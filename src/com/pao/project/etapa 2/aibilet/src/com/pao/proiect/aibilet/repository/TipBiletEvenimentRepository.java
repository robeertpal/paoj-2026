package com.pao.proiect.aibilet.repository;

import com.pao.proiect.aibilet.config.DatabaseConnection;
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

public class TipBiletEvenimentRepository implements Repository<TipBiletEveniment, Integer> {
    private static TipBiletEvenimentRepository instance;

    private final EvenimentRepository evenimentRepository;

    private TipBiletEvenimentRepository() {
        this.evenimentRepository = EvenimentRepository.getInstance();
    }

    public static synchronized TipBiletEvenimentRepository getInstance() {
        if (instance == null) {
            instance = new TipBiletEvenimentRepository();
        }

        return instance;
    }

    @Override
    public Optional<TipBiletEveniment> findById(Integer id) {
        String sql = "SELECT id, eveniment_id, nume, pret, stoc_total, stoc_disponibil " +
                "FROM tipuri_bilete_eveniment WHERE id = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    TipBiletEveniment tipBilet = mapRowToTipBilet(resultSet);
                    AuditService.getInstance().logAction("read_tip_bilet_eveniment_by_id");
                    return Optional.of(tipBilet);
                }
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea tipului de bilet cu id-ul " + id + ".", e);
        }
    }

    @Override
    public List<TipBiletEveniment> findAll() {
        String sql = "SELECT id, eveniment_id, nume, pret, stoc_total, stoc_disponibil " +
                "FROM tipuri_bilete_eveniment ORDER BY eveniment_id, id";
        List<TipBiletEveniment> tipuri = new ArrayList<TipBiletEveniment>();

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                tipuri.add(mapRowToTipBilet(resultSet));
            }

            AuditService.getInstance().logAction("read_all_tipuri_bilete_eveniment");
            return tipuri;
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea listei de tipuri de bilete.", e);
        }
    }

    public List<TipBiletEveniment> findByEvenimentId(Integer evenimentId) {
        String sql = "SELECT id, eveniment_id, nume, pret, stoc_total, stoc_disponibil " +
                "FROM tipuri_bilete_eveniment WHERE eveniment_id = ? ORDER BY id";
        List<TipBiletEveniment> tipuri = new ArrayList<TipBiletEveniment>();

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, evenimentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    tipuri.add(mapRowToTipBilet(resultSet));
                }
            }

            AuditService.getInstance().logAction("read_tipuri_bilete_by_eveniment");
            return tipuri;
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea tipurilor de bilete pentru evenimentul " + evenimentId + ".", e);
        }
    }

    public Map<Integer, List<TipBiletEveniment>> findAllGroupedByEvenimentId() {
        String sql = "SELECT id, eveniment_id, nume, pret, stoc_total, stoc_disponibil " +
                "FROM tipuri_bilete_eveniment ORDER BY eveniment_id, id";
        Map<Integer, List<TipBiletEveniment>> rezultat = new HashMap<Integer, List<TipBiletEveniment>>();

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                TipBiletEveniment tip = mapRowToTipBilet(resultSet);
                Integer evenimentId = tip.getEvenimentId();
                List<TipBiletEveniment> tipuri = rezultat.get(evenimentId);

                if (tipuri == null) {
                    tipuri = new ArrayList<TipBiletEveniment>();
                    rezultat.put(evenimentId, tipuri);
                }

                tipuri.add(tip);
            }

            AuditService.getInstance().logAction("read_tipuri_bilete_grouped_by_eveniment");
            return rezultat;
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea tipurilor de bilete grupate dupa eveniment.", e);
        }
    }

    @Override
    public TipBiletEveniment create(TipBiletEveniment entity) {
        verificaEveniment(entity.getEvenimentId());

        String sql = "INSERT INTO tipuri_bilete_eveniment (eveniment_id, nume, pret, stoc_total, stoc_disponibil) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, new String[]{"ID"})
        ) {
            setTipBiletParameters(statement, entity);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new IllegalStateException("Crearea tipului de bilet nu a inserat niciun rand.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getInt(1));
                    AuditService.getInstance().logAction("create_tip_bilet_eveniment");
                    return entity;
                }
            }

            throw new IllegalStateException("Crearea tipului de bilet nu a returnat id-ul generat.");
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la crearea tipului de bilet. Verifica unicitatea perechii (eveniment_id, nume).", e);
        }
    }

    @Override
    public void save(TipBiletEveniment entity) {
        create(entity);
    }

    @Override
    public void update(TipBiletEveniment entity) {
        verificaEveniment(entity.getEvenimentId());

        String sql = "UPDATE tipuri_bilete_eveniment SET eveniment_id = ?, nume = ?, pret = ?, " +
                "stoc_total = ?, stoc_disponibil = ? WHERE id = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            setTipBiletParameters(statement, entity);
            statement.setInt(6, entity.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new IllegalStateException("Nu exista tip de bilet cu id-ul " + entity.getId() + " pentru actualizare.");
            }

            AuditService.getInstance().logAction("update_tip_bilet_eveniment");
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la actualizarea tipului de bilet cu id-ul " + entity.getId() + ".", e);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM tipuri_bilete_eveniment WHERE id = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);
            boolean deleted = statement.executeUpdate() > 0;
            if (deleted) {
                AuditService.getInstance().logAction("delete_tip_bilet_eveniment");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la stergerea tipului de bilet cu id-ul " + id + ".", e);
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

    private void verificaEveniment(int evenimentId) {
        if (!evenimentRepository.findById(evenimentId).isPresent()) {
            throw new IllegalStateException("Evenimentul cu id-ul " + evenimentId + " nu exista.");
        }
    }

    private void setTipBiletParameters(PreparedStatement statement, TipBiletEveniment tipBilet) throws SQLException {
        statement.setInt(1, tipBilet.getEvenimentId());
        statement.setString(2, tipBilet.getNume());
        statement.setDouble(3, tipBilet.getPret());
        statement.setInt(4, tipBilet.getStocTotal());
        statement.setInt(5, tipBilet.getStocDisponibil());
    }

    private TipBiletEveniment mapRowToTipBilet(ResultSet resultSet) throws SQLException {
        int evenimentId = resultSet.getInt("eveniment_id");

        return new TipBiletEveniment(
                resultSet.getInt("id"),
                evenimentId,
                resultSet.getString("nume"),
                resultSet.getDouble("pret"),
                resultSet.getInt("stoc_total"),
                resultSet.getInt("stoc_disponibil")
        );
    }
}
