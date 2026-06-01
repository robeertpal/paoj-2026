package com.pao.proiect.aibilet.repository;

import com.pao.proiect.aibilet.config.DatabaseConnection;
import com.pao.proiect.aibilet.model.Eveniment;
import com.pao.proiect.aibilet.model.EvenimentCuLocuri;
import com.pao.proiect.aibilet.model.LocEveniment;
import com.pao.proiect.aibilet.model.StatusLoc;
import com.pao.proiect.aibilet.model.TipBiletEveniment;
import com.pao.proiect.aibilet.service.AuditService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LocEvenimentRepository implements Repository<LocEveniment, Integer> {
    private static LocEvenimentRepository instance;

    private final EvenimentRepository evenimentRepository;
    private final TipBiletEvenimentRepository tipBiletEvenimentRepository;

    private LocEvenimentRepository() {
        this.evenimentRepository = EvenimentRepository.getInstance();
        this.tipBiletEvenimentRepository = TipBiletEvenimentRepository.getInstance();
    }

    public static synchronized LocEvenimentRepository getInstance() {
        if (instance == null) {
            instance = new LocEvenimentRepository();
        }

        return instance;
    }

    @Override
    public Optional<LocEveniment> findById(Integer id) {
        String sql = "SELECT id, eveniment_id, rand, coloana, cod, tip_bilet, status FROM locuri_eveniment WHERE id = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    LocEveniment loc = mapRowToLocEveniment(resultSet);
                    AuditService.getInstance().logAction("read_loc_eveniment_by_id");
                    return Optional.of(loc);
                }
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea locului cu id-ul " + id + ".", e);
        }
    }

    @Override
    public List<LocEveniment> findAll() {
        String sql = "SELECT id, eveniment_id, rand, coloana, cod, tip_bilet, status " +
                "FROM locuri_eveniment ORDER BY eveniment_id, rand, coloana";
        List<LocEveniment> locuri = new ArrayList<LocEveniment>();

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                locuri.add(mapRowToLocEveniment(resultSet));
            }

            AuditService.getInstance().logAction("read_all_locuri_eveniment");
            return locuri;
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea listei de locuri.", e);
        }
    }

    public List<LocEveniment> findByEvenimentId(Integer evenimentId) {
        String sql = "SELECT id, eveniment_id, rand, coloana, cod, tip_bilet, status " +
                "FROM locuri_eveniment WHERE eveniment_id = ? ORDER BY rand, coloana";
        List<LocEveniment> locuri = new ArrayList<LocEveniment>();

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, evenimentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    locuri.add(mapRowToLocEveniment(resultSet));
                }
            }

            AuditService.getInstance().logAction("read_locuri_by_eveniment");
            return locuri;
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea locurilor pentru evenimentul " + evenimentId + ".", e);
        }
    }

    public Map<Integer, List<LocEveniment>> findAllGroupedByEvenimentId() {
        String sql = "SELECT id, eveniment_id, rand, coloana, cod, tip_bilet, status " +
                "FROM locuri_eveniment ORDER BY eveniment_id, rand, coloana";
        Map<Integer, List<LocEveniment>> rezultat = new HashMap<Integer, List<LocEveniment>>();

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                LocEveniment loc = mapRowToLocEveniment(resultSet);
                Integer evenimentId = loc.getEvenimentId();
                List<LocEveniment> locuri = rezultat.get(evenimentId);

                if (locuri == null) {
                    locuri = new ArrayList<LocEveniment>();
                    rezultat.put(evenimentId, locuri);
                }

                locuri.add(loc);
            }

            AuditService.getInstance().logAction("read_locuri_grouped_by_eveniment");
            return rezultat;
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea locurilor grupate dupa eveniment.", e);
        }
    }

    public Optional<LocEveniment> findByEvenimentIdAndCod(Integer evenimentId, String cod) {
        String sql = "SELECT id, eveniment_id, rand, coloana, cod, tip_bilet, status " +
                "FROM locuri_eveniment WHERE eveniment_id = ? AND cod = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, evenimentId);
            statement.setString(2, cod);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    LocEveniment loc = mapRowToLocEveniment(resultSet);
                    AuditService.getInstance().logAction("read_loc_eveniment_by_cod");
                    return Optional.of(loc);
                }
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea locului " + cod + " pentru evenimentul " + evenimentId + ".", e);
        }
    }

    @Override
    public LocEveniment create(LocEveniment entity) {
        verificaRelatii(entity);

        String sql = "INSERT INTO locuri_eveniment (eveniment_id, rand, coloana, cod, tip_bilet, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, new String[]{"ID"})
        ) {
            setLocEvenimentParameters(statement, entity);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new IllegalStateException("Crearea locului nu a inserat niciun rand.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getInt(1));
                    AuditService.getInstance().logAction("create_loc_eveniment");
                    return entity;
                }
            }

            throw new IllegalStateException("Crearea locului nu a returnat id-ul generat.");
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la crearea locului. Verifica unicitatea codului si pozitiei in eveniment.", e);
        }
    }

    @Override
    public void save(LocEveniment entity) {
        create(entity);
    }

    public List<LocEveniment> createAll(List<LocEveniment> locuri) {
        if (locuri == null || locuri.isEmpty()) {
            return new ArrayList<LocEveniment>();
        }

        verificaRelatiiPentruBatch(locuri);

        String sql = "INSERT INTO locuri_eveniment (eveniment_id, rand, coloana, cod, tip_bilet, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            for (int i = 0; i < locuri.size(); i++) {
                setLocEvenimentParameters(statement, locuri.get(i));
                statement.addBatch();
            }

            statement.executeBatch();
            AuditService.getInstance().logAction("create_all_locuri_eveniment");
            return locuri;
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la crearea locurilor. Verifica unicitatea codurilor si pozitiilor in eveniment.", e);
        }
    }

    @Override
    public void update(LocEveniment entity) {
        verificaRelatii(entity);

        String sql = "UPDATE locuri_eveniment SET eveniment_id = ?, rand = ?, coloana = ?, cod = ?, " +
                "tip_bilet = ?, status = ? WHERE id = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            setLocEvenimentParameters(statement, entity);
            statement.setInt(7, entity.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new IllegalStateException("Nu exista loc cu id-ul " + entity.getId() + " pentru actualizare.");
            }

            AuditService.getInstance().logAction("update_loc_eveniment");
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la actualizarea locului cu id-ul " + entity.getId() + ".", e);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM locuri_eveniment WHERE id = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);
            boolean deleted = statement.executeUpdate() > 0;
            if (deleted) {
                AuditService.getInstance().logAction("delete_loc_eveniment");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la stergerea locului cu id-ul " + id + ".", e);
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

    private void verificaRelatii(LocEveniment loc) {
        Optional<Eveniment> eveniment = evenimentRepository.findById(loc.getEvenimentId());
        if (!eveniment.isPresent()) {
            throw new IllegalStateException("Evenimentul cu id-ul " + loc.getEvenimentId() + " nu exista.");
        }

        if (!(eveniment.get() instanceof EvenimentCuLocuri)) {
            throw new IllegalStateException("Evenimentul cu id-ul " + loc.getEvenimentId() + " nu este eveniment cu locuri.");
        }

        if (!existaTipBiletPentruEveniment(loc.getEvenimentId(), loc.getTipBilet())) {
            throw new IllegalStateException("Tipul de bilet " + loc.getTipBilet() + " nu exista pentru evenimentul " + loc.getEvenimentId() + ".");
        }
    }

    private void verificaRelatiiPentruBatch(List<LocEveniment> locuri) {
        int evenimentId = locuri.get(0).getEvenimentId();
        Optional<Eveniment> eveniment = evenimentRepository.findById(evenimentId);

        if (!eveniment.isPresent()) {
            throw new IllegalStateException("Evenimentul cu id-ul " + evenimentId + " nu exista.");
        }

        if (!(eveniment.get() instanceof EvenimentCuLocuri)) {
            throw new IllegalStateException("Evenimentul cu id-ul " + evenimentId + " nu este eveniment cu locuri.");
        }

        List<TipBiletEveniment> tipuri = tipBiletEvenimentRepository.findByEvenimentId(evenimentId);

        for (int i = 0; i < locuri.size(); i++) {
            LocEveniment loc = locuri.get(i);

            if (loc.getEvenimentId() != evenimentId) {
                throw new IllegalStateException("Toate locurile din batch trebuie sa apartina aceluiasi eveniment.");
            }

            if (!existaTipBiletInLista(tipuri, loc.getTipBilet())) {
                throw new IllegalStateException("Tipul de bilet " + loc.getTipBilet() + " nu exista pentru evenimentul " + loc.getEvenimentId() + ".");
            }
        }
    }

    private boolean existaTipBiletPentruEveniment(int evenimentId, String tipBilet) {
        List<TipBiletEveniment> tipuri = tipBiletEvenimentRepository.findByEvenimentId(evenimentId);
        return existaTipBiletInLista(tipuri, tipBilet);
    }

    private boolean existaTipBiletInLista(List<TipBiletEveniment> tipuri, String tipBilet) {
        for (int i = 0; i < tipuri.size(); i++) {
            if (tipuri.get(i).getNume().equals(tipBilet)) {
                return true;
            }
        }

        return false;
    }

    private void setLocEvenimentParameters(PreparedStatement statement, LocEveniment loc) throws SQLException {
        statement.setInt(1, loc.getEvenimentId());
        statement.setInt(2, loc.getRand());
        statement.setInt(3, loc.getColoana());
        statement.setString(4, loc.getCod());
        statement.setString(5, loc.getTipBilet());
        statement.setString(6, loc.getStatus().name());
    }

    private LocEveniment mapRowToLocEveniment(ResultSet resultSet) throws SQLException {
        int evenimentId = resultSet.getInt("eveniment_id");

        return new LocEveniment(
                resultSet.getInt("id"),
                evenimentId,
                resultSet.getInt("rand"),
                resultSet.getInt("coloana"),
                resultSet.getString("cod"),
                resultSet.getString("tip_bilet"),
                StatusLoc.valueOf(resultSet.getString("status"))
        );
    }
}
