package com.pao.proiect.aibilet.repository;

import com.pao.proiect.aibilet.config.DatabaseConnection;
import com.pao.proiect.aibilet.model.CategorieEveniment;
import com.pao.proiect.aibilet.model.Eveniment;
import com.pao.proiect.aibilet.model.EvenimentCuLocuri;
import com.pao.proiect.aibilet.model.EvenimentFaraLocuri;
import com.pao.proiect.aibilet.model.HartaLocuri;
import com.pao.proiect.aibilet.model.LocEveniment;
import com.pao.proiect.aibilet.model.Organizator;
import com.pao.proiect.aibilet.model.StatusEveniment;
import com.pao.proiect.aibilet.model.Utilizator;
import com.pao.proiect.aibilet.model.dto.EvenimentVanzariView;
import com.pao.proiect.aibilet.service.AuditService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EvenimentRepository implements Repository<Eveniment, Integer> {
    private static final DateTimeFormatter DB_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter APP_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static EvenimentRepository instance;

    private final LocatieRepository locatieRepository;
    private final UtilizatorRepository utilizatorRepository;

    private EvenimentRepository() {
        this.locatieRepository = LocatieRepository.getInstance();
        this.utilizatorRepository = UtilizatorRepository.getInstance();
    }

    public static synchronized EvenimentRepository getInstance() {
        if (instance == null) {
            instance = new EvenimentRepository();
        }

        return instance;
    }

    @Override
    public Optional<Eveniment> findById(Integer id) {
        String sql = "SELECT id, tip_eveniment, titlu, descriere, categorie, data_ora_inceput, data_ora_final, " +
                "status, locatie_id, organizator_id, nume_organizatie_organizator FROM evenimente WHERE id = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Eveniment eveniment = mapRowToEveniment(resultSet);
                    AuditService.getInstance().logAction("read_eveniment_by_id");
                    return Optional.of(eveniment);
                }
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea evenimentului cu id-ul " + id + ".", e);
        }
    }

    @Override
    public List<Eveniment> findAll() {
        String sql = "SELECT id, tip_eveniment, titlu, descriere, categorie, data_ora_inceput, data_ora_final, " +
                "status, locatie_id, organizator_id, nume_organizatie_organizator FROM evenimente ORDER BY id";
        List<Eveniment> evenimente = new ArrayList<Eveniment>();

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                evenimente.add(mapRowToEveniment(resultSet));
            }

            AuditService.getInstance().logAction("read_all_evenimente");
            return evenimente;
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea listei de evenimente.", e);
        }
    }

    @Override
    public Eveniment create(Eveniment entity) {
        verificaRelatii(entity);

        String sql = "INSERT INTO evenimente " +
                "(tip_eveniment, titlu, descriere, categorie, data_ora_inceput, data_ora_final, status, " +
                "locatie_id, organizator_id, nume_organizatie_organizator) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, new String[]{"ID"})
        ) {
            setEvenimentParameters(statement, entity);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new IllegalStateException("Crearea evenimentului nu a inserat niciun rand.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getInt(1));
                    AuditService.getInstance().logAction("create_eveniment");
                    return entity;
                }
            }

            throw new IllegalStateException("Crearea evenimentului nu a returnat id-ul generat.");
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la crearea evenimentului.", e);
        }
    }

    @Override
    public void save(Eveniment entity) {
        create(entity);
    }

    @Override
    public void update(Eveniment entity) {
        verificaRelatii(entity);

        String sql = "UPDATE evenimente SET tip_eveniment = ?, titlu = ?, descriere = ?, categorie = ?, " +
                "data_ora_inceput = ?, data_ora_final = ?, status = ?, locatie_id = ?, organizator_id = ?, " +
                "nume_organizatie_organizator = ? WHERE id = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            setEvenimentParameters(statement, entity);
            statement.setInt(11, entity.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new IllegalStateException("Nu exista eveniment cu id-ul " + entity.getId() + " pentru actualizare.");
            }

            AuditService.getInstance().logAction("update_eveniment");
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la actualizarea evenimentului cu id-ul " + entity.getId() + ".", e);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM evenimente WHERE id = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);
            boolean deleted = statement.executeUpdate() > 0;
            if (deleted) {
                AuditService.getInstance().logAction("delete_eveniment");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la stergerea evenimentului cu id-ul " + id + ".", e);
        }
    }

    public List<EvenimentVanzariView> findEvenimenteCuNumarBileteVandute() {
        String sql = "SELECT e.id AS eveniment_id, e.titlu AS titlu_eveniment, " +
                "COUNT(b.id) AS bilete_vandute, COALESCE(SUM(b.pret), 0) AS venit_total " +
                "FROM evenimente e " +
                "LEFT JOIN bilete b ON e.id = b.eveniment_id AND b.status <> 'ANULAT' " +
                "GROUP BY e.id, e.titlu " +
                "ORDER BY bilete_vandute DESC, e.titlu";
        List<EvenimentVanzariView> evenimente = new ArrayList<EvenimentVanzariView>();

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                evenimente.add(new EvenimentVanzariView(
                        resultSet.getInt("eveniment_id"),
                        resultSet.getString("titlu_eveniment"),
                        resultSet.getInt("bilete_vandute"),
                        resultSet.getDouble("venit_total")
                ));
            }

            AuditService.getInstance().logAction("join_evenimente_bilete_vandute");
            return evenimente;
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea raportului de vanzari pe evenimente.", e);
        }
    }

    private Connection getConnection() {
        try {
            return DatabaseConnection.getInstance().getConnection();
        } catch (IOException e) {
            throw new IllegalStateException("Nu s-a putut citi configuratia bazei de date.", e);
        } catch (SQLException e) {
            throw new IllegalStateException("Nu s-a putut deschide conexiunea la baza de date.", e);
        }
    }

    private void verificaRelatii(Eveniment eveniment) {
        if (!locatieRepository.findById(eveniment.getLocatieId()).isPresent()) {
            throw new IllegalStateException("Locatia cu id-ul " + eveniment.getLocatieId() + " nu exista.");
        }

        Optional<Utilizator> utilizator = utilizatorRepository.findById(eveniment.getOrganizatorId());
        if (!utilizator.isPresent()) {
            throw new IllegalStateException("Organizatorul cu id-ul " + eveniment.getOrganizatorId() + " nu exista.");
        }

        if (!(utilizator.get() instanceof Organizator)) {
            throw new IllegalStateException("Utilizatorul cu id-ul " + eveniment.getOrganizatorId() + " nu este organizator.");
        }
    }

    private void setEvenimentParameters(PreparedStatement statement, Eveniment eveniment) throws SQLException {
        statement.setString(1, eveniment.esteCuLocuri() ? "SEATED" : "STANDING");
        statement.setString(2, eveniment.getTitlu());
        statement.setString(3, eveniment.getDescriere());
        statement.setString(4, eveniment.getCategorie().name());
        statement.setTimestamp(5, parseTimestamp(eveniment.getDataOraInceput()));
        statement.setTimestamp(6, parseTimestamp(eveniment.getDataOraFinal()));
        statement.setString(7, eveniment.getStatus().name());
        statement.setInt(8, eveniment.getLocatieId());
        statement.setInt(9, eveniment.getOrganizatorId());
        statement.setString(10, eveniment.getNumeOrganizatieOrganizator());
    }

    private Eveniment mapRowToEveniment(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String tipEveniment = resultSet.getString("tip_eveniment");
        String titlu = resultSet.getString("titlu");
        String descriere = resultSet.getString("descriere");
        CategorieEveniment categorie = CategorieEveniment.valueOf(resultSet.getString("categorie"));
        String dataOraInceput = formatTimestamp(resultSet.getTimestamp("data_ora_inceput"));
        String dataOraFinal = formatTimestamp(resultSet.getTimestamp("data_ora_final"));
        StatusEveniment status = StatusEveniment.valueOf(resultSet.getString("status"));
        int locatieId = resultSet.getInt("locatie_id");
        int organizatorId = resultSet.getInt("organizator_id");
        String numeOrganizatieOrganizator = resultSet.getString("nume_organizatie_organizator");

        if ("SEATED".equals(tipEveniment)) {
            return new EvenimentCuLocuri(
                    id,
                    titlu,
                    descriere,
                    categorie,
                    dataOraInceput,
                    dataOraFinal,
                    status,
                    locatieId,
                    organizatorId,
                    numeOrganizatieOrganizator,
                    new HartaLocuri(new LocEveniment[0][0])
            );
        }

        if ("STANDING".equals(tipEveniment)) {
            return new EvenimentFaraLocuri(
                    id,
                    titlu,
                    descriere,
                    categorie,
                    dataOraInceput,
                    dataOraFinal,
                    status,
                    locatieId,
                    organizatorId,
                    numeOrganizatieOrganizator,
                    0,
                    0
            );
        }

        throw new IllegalStateException("Tip de eveniment necunoscut in tabela evenimente: " + tipEveniment);
    }

    private Timestamp parseTimestamp(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Data evenimentului nu poate fi null.");
        }

        String trimmedValue = value.trim();
        DateTimeFormatter[] formatters = new DateTimeFormatter[]{
                APP_DATE_FORMAT,
                DB_DATE_FORMAT,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
        };

        for (int i = 0; i < formatters.length; i++) {
            try {
                return Timestamp.valueOf(LocalDateTime.parse(trimmedValue, formatters[i]));
            } catch (DateTimeParseException e) {
                // incercam formatul urmator
            }
        }

        try {
            return Timestamp.valueOf(trimmedValue);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Format invalid pentru data evenimentului: " + value, e);
        }
    }

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }

        return timestamp.toLocalDateTime().format(APP_DATE_FORMAT);
    }
}
