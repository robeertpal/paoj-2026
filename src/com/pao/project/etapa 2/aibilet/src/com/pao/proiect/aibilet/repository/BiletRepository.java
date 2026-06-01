package com.pao.proiect.aibilet.repository;

import com.pao.proiect.aibilet.config.DatabaseConnection;
import com.pao.proiect.aibilet.model.Bilet;
import com.pao.proiect.aibilet.model.BiletCuLoc;
import com.pao.proiect.aibilet.model.BiletFaraLoc;
import com.pao.proiect.aibilet.model.Client;
import com.pao.proiect.aibilet.model.CodBilet;
import com.pao.proiect.aibilet.model.StatusBilet;
import com.pao.proiect.aibilet.model.Utilizator;
import com.pao.proiect.aibilet.model.dto.BiletClientView;
import com.pao.proiect.aibilet.service.AuditService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BiletRepository implements Repository<Bilet, Integer> {
    private static BiletRepository instance;

    private final EvenimentRepository evenimentRepository;
    private final UtilizatorRepository utilizatorRepository;

    private BiletRepository() {
        this.evenimentRepository = EvenimentRepository.getInstance();
        this.utilizatorRepository = UtilizatorRepository.getInstance();
    }

    public static synchronized BiletRepository getInstance() {
        if (instance == null) {
            instance = new BiletRepository();
        }

        return instance;
    }

    @Override
    public Optional<Bilet> findById(Integer id) {
        String sql = "SELECT id, tip, cod_bilet, eveniment_id, client_id, seat_code, tip_bilet, pret, status " +
                "FROM bilete WHERE id = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Bilet bilet = mapRowToBilet(resultSet);
                    AuditService.getInstance().logAction("read_bilet_by_id");
                    return Optional.of(bilet);
                }
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea biletului cu id-ul " + id + ".", e);
        }
    }

    @Override
    public List<Bilet> findAll() {
        String sql = "SELECT id, tip, cod_bilet, eveniment_id, client_id, seat_code, tip_bilet, pret, status " +
                "FROM bilete ORDER BY id";
        List<Bilet> bilete = new ArrayList<Bilet>();

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                bilete.add(mapRowToBilet(resultSet));
            }

            AuditService.getInstance().logAction("read_all_bilete");
            return bilete;
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea listei de bilete.", e);
        }
    }

    @Override
    public Bilet create(Bilet entity) {
        String sql = "INSERT INTO bilete (tip, cod_bilet, eveniment_id, client_id, seat_code, tip_bilet, pret, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection()) {
            verificaRelatii(connection, entity);

            try (
                    PreparedStatement statement = connection.prepareStatement(sql, new String[]{"ID"})
            ) {
                setBiletParameters(statement, entity);

                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new IllegalStateException("Crearea biletului nu a inserat niciun rand.");
                }

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setId(generatedKeys.getInt(1));
                        AuditService.getInstance().logAction("create_bilet");
                        return entity;
                    }
                }

                throw new IllegalStateException("Crearea biletului nu a returnat id-ul generat.");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la crearea biletului.", e);
        }
    }

    @Override
    public void save(Bilet entity) {
        create(entity);
    }

    @Override
    public void update(Bilet entity) {
        String sql = "UPDATE bilete SET tip = ?, cod_bilet = ?, eveniment_id = ?, client_id = ?, seat_code = ?, " +
                "tip_bilet = ?, pret = ?, status = ? WHERE id = ?";

        try (Connection connection = getConnection()) {
            verificaRelatii(connection, entity);

            try (
                    PreparedStatement statement = connection.prepareStatement(sql)
            ) {
                setBiletParameters(statement, entity);
                statement.setInt(9, entity.getId());

                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new IllegalStateException("Nu exista bilet cu id-ul " + entity.getId() + " pentru actualizare.");
                }

                AuditService.getInstance().logAction("update_bilet");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la actualizarea biletului cu id-ul " + entity.getId() + ".", e);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM bilete WHERE id = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);
            boolean deleted = statement.executeUpdate() > 0;
            if (deleted) {
                AuditService.getInstance().logAction("delete_bilet");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la stergerea biletului cu id-ul " + id + ".", e);
        }
    }

    public List<BiletClientView> findBileteClientCuEvenimentSiLocatie(int clientId) {
        String sql = "SELECT b.id AS bilet_id, b.cod_bilet, b.status AS status_bilet, b.pret, " +
                "e.titlu AS titlu_eveniment, l.denumire AS nume_locatie, l.oras, " +
                "le.cod AS cod_loc, le.rand, le.coloana " +
                "FROM bilete b " +
                "JOIN evenimente e ON b.eveniment_id = e.id " +
                "JOIN locatii l ON e.locatie_id = l.id " +
                "LEFT JOIN locuri_eveniment le ON b.eveniment_id = le.eveniment_id AND b.seat_code = le.cod " +
                "WHERE b.client_id = ? " +
                "ORDER BY e.data_ora_inceput, b.id";
        List<BiletClientView> bilete = new ArrayList<BiletClientView>();

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, clientId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Integer rand = null;
                    int randValue = resultSet.getInt("rand");
                    if (!resultSet.wasNull()) {
                        rand = Integer.valueOf(randValue);
                    }

                    Integer coloana = null;
                    int coloanaValue = resultSet.getInt("coloana");
                    if (!resultSet.wasNull()) {
                        coloana = Integer.valueOf(coloanaValue);
                    }

                    bilete.add(new BiletClientView(
                            resultSet.getInt("bilet_id"),
                            resultSet.getString("cod_bilet"),
                            resultSet.getString("status_bilet"),
                            resultSet.getDouble("pret"),
                            resultSet.getString("titlu_eveniment"),
                            resultSet.getString("nume_locatie"),
                            resultSet.getString("oras"),
                            resultSet.getString("cod_loc"),
                            rand,
                            coloana
                    ));
                }
            }

            AuditService.getInstance().logAction("join_bilete_client_eveniment_locatie");
            return bilete;
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea biletelor clientului cu eveniment si locatie.", e);
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

    private void verificaRelatii(Bilet bilet) {
        if (!evenimentRepository.findById(bilet.getEvenimentId()).isPresent()) {
            throw new IllegalStateException("Evenimentul cu id-ul " + bilet.getEvenimentId() + " nu exista.");
        }

        Optional<Utilizator> utilizator = utilizatorRepository.findById(bilet.getClientId());
        if (!utilizator.isPresent()) {
            throw new IllegalStateException("Clientul cu id-ul " + bilet.getClientId() + " nu exista.");
        }

        if (!(utilizator.get() instanceof Client)) {
            throw new IllegalStateException("Utilizatorul cu id-ul " + bilet.getClientId() + " nu este client.");
        }
    }

    private void verificaRelatii(Connection connection, Bilet bilet) throws SQLException {
        String sqlEveniment = "SELECT COUNT(*) AS total FROM evenimente WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sqlEveniment)) {
            statement.setInt(1, bilet.getEvenimentId());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next() || resultSet.getInt("total") == 0) {
                    throw new IllegalStateException("Evenimentul cu id-ul " + bilet.getEvenimentId() + " nu exista.");
                }
            }
        }

        String sqlClient = "SELECT rol FROM utilizatori WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sqlClient)) {
            statement.setInt(1, bilet.getClientId());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new IllegalStateException("Clientul cu id-ul " + bilet.getClientId() + " nu exista.");
                }

                if (!"CLIENT".equals(resultSet.getString("rol"))) {
                    throw new IllegalStateException("Utilizatorul cu id-ul " + bilet.getClientId() + " nu este client.");
                }
            }
        }
    }

    private void setBiletParameters(PreparedStatement statement, Bilet bilet) throws SQLException {
        statement.setString(1, bilet instanceof BiletCuLoc ? "CU_LOC" : "FARA_LOC");
        statement.setString(2, bilet.getCodBilet().getValoare());
        statement.setInt(3, bilet.getEvenimentId());
        statement.setInt(4, bilet.getClientId());
        statement.setString(5, getSeatCode(bilet));
        statement.setString(6, bilet.getTipBilet());
        statement.setDouble(7, bilet.getPret());
        statement.setString(8, bilet.getStatus().name());
    }

    private String getSeatCode(Bilet bilet) {
        if (bilet instanceof BiletCuLoc) {
            return ((BiletCuLoc) bilet).getSeatCode();
        }

        return null;
    }

    private Bilet mapRowToBilet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String tip = resultSet.getString("tip");
        CodBilet codBilet = new CodBilet(resultSet.getString("cod_bilet"));
        int evenimentId = resultSet.getInt("eveniment_id");
        int clientId = resultSet.getInt("client_id");
        String seatCode = resultSet.getString("seat_code");
        String tipBilet = resultSet.getString("tip_bilet");
        double pret = resultSet.getDouble("pret");
        StatusBilet status = StatusBilet.valueOf(resultSet.getString("status"));

        if ("CU_LOC".equals(tip)) {
            return new BiletCuLoc(id, codBilet, evenimentId, clientId, seatCode, tipBilet, pret, status);
        }

        if ("FARA_LOC".equals(tip)) {
            return new BiletFaraLoc(id, codBilet, evenimentId, clientId, tipBilet, pret, status);
        }

        throw new IllegalStateException("Tip de bilet necunoscut in tabela bilete: " + tip);
    }
}
