package com.pao.proiect.aibilet.repository;

import com.pao.proiect.aibilet.config.DatabaseConnection;
import com.pao.proiect.aibilet.model.Locatie;
import com.pao.proiect.aibilet.service.AuditService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LocatieRepository implements Repository<Locatie, Integer> {
    private static LocatieRepository instance;

    private LocatieRepository() {
    }

    public static synchronized LocatieRepository getInstance() {
        if (instance == null) {
            instance = new LocatieRepository();
        }

        return instance;
    }

    @Override
    public Optional<Locatie> findById(Integer id) {
        String sql = "SELECT id, denumire, oras, adresa, suporta_locuri FROM locatii WHERE id = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Locatie locatie = mapRowToLocatie(resultSet);
                    AuditService.getInstance().logAction("read_locatie_by_id");
                    return Optional.of(locatie);
                }
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea locatiei cu id-ul " + id + ".", e);
        }
    }

    @Override
    public List<Locatie> findAll() {
        String sql = "SELECT id, denumire, oras, adresa, suporta_locuri FROM locatii ORDER BY id";
        List<Locatie> locatii = new ArrayList<Locatie>();

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                locatii.add(mapRowToLocatie(resultSet));
            }

            AuditService.getInstance().logAction("read_all_locatii");
            return locatii;
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea listei de locatii.", e);
        }
    }

    @Override
    public Locatie create(Locatie entity) {
        String sql = "INSERT INTO locatii (denumire, oras, adresa, suporta_locuri) VALUES (?, ?, ?, ?)";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, new String[]{"ID"})
        ) {
            setLocatieParameters(statement, entity);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new IllegalStateException("Crearea locatiei nu a inserat niciun rand.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getInt(1));
                    AuditService.getInstance().logAction("create_locatie");
                    return entity;
                }
            }

            throw new IllegalStateException("Crearea locatiei nu a returnat id-ul generat.");
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la crearea locatiei.", e);
        }
    }

    @Override
    public void save(Locatie entity) {
        create(entity);
    }

    @Override
    public void update(Locatie entity) {
        String sql = "UPDATE locatii SET denumire = ?, oras = ?, adresa = ?, suporta_locuri = ? WHERE id = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            setLocatieParameters(statement, entity);
            statement.setInt(5, entity.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new IllegalStateException("Nu exista locatie cu id-ul " + entity.getId() + " pentru actualizare.");
            }

            AuditService.getInstance().logAction("update_locatie");
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la actualizarea locatiei cu id-ul " + entity.getId() + ".", e);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM locatii WHERE id = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);
            boolean deleted = statement.executeUpdate() > 0;
            if (deleted) {
                AuditService.getInstance().logAction("delete_locatie");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la stergerea locatiei cu id-ul " + id + ".", e);
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

    private void setLocatieParameters(PreparedStatement statement, Locatie locatie) throws SQLException {
        statement.setString(1, locatie.getDenumire());
        statement.setString(2, locatie.getOras());
        statement.setString(3, locatie.getAdresa());
        statement.setInt(4, locatie.isSuportaLocuri() ? 1 : 0);
    }

    private Locatie mapRowToLocatie(ResultSet resultSet) throws SQLException {
        return new Locatie(
                resultSet.getInt("id"),
                resultSet.getString("denumire"),
                resultSet.getString("oras"),
                resultSet.getString("adresa"),
                resultSet.getInt("suporta_locuri") == 1
        );
    }
}
