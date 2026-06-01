package com.pao.proiect.aibilet.repository;

import com.pao.proiect.aibilet.config.DatabaseConnection;
import com.pao.proiect.aibilet.model.Admin;
import com.pao.proiect.aibilet.model.AgentCheckIn;
import com.pao.proiect.aibilet.model.Client;
import com.pao.proiect.aibilet.model.Organizator;
import com.pao.proiect.aibilet.model.Utilizator;
import com.pao.proiect.aibilet.service.AuditService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UtilizatorRepository implements Repository<Utilizator, Integer> {
    private static UtilizatorRepository instance;

    private UtilizatorRepository() {
    }

    public static synchronized UtilizatorRepository getInstance() {
        if (instance == null) {
            instance = new UtilizatorRepository();
        }

        return instance;
    }

    @Override
    public Optional<Utilizator> findById(Integer id) {
        String sql = "SELECT id, rol, username, parola, nume, prenume, email, telefon, nume_organizatie " +
                "FROM utilizatori WHERE id = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Utilizator utilizator = mapRowToUtilizator(resultSet);
                    AuditService.getInstance().logAction("read_utilizator_by_id");
                    return Optional.of(utilizator);
                }
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea utilizatorului cu id-ul " + id + ".", e);
        }
    }

    @Override
    public List<Utilizator> findAll() {
        String sql = "SELECT id, rol, username, parola, nume, prenume, email, telefon, nume_organizatie " +
                "FROM utilizatori ORDER BY id";
        List<Utilizator> utilizatori = new ArrayList<Utilizator>();

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                utilizatori.add(mapRowToUtilizator(resultSet));
            }

            AuditService.getInstance().logAction("read_all_utilizatori");
            return utilizatori;
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea listei de utilizatori.", e);
        }
    }

    @Override
    public Utilizator create(Utilizator entity) {
        String sql = "INSERT INTO utilizatori " +
                "(rol, username, parola, nume, prenume, email, telefon, nume_organizatie) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, new String[]{"ID"})
        ) {
            setUtilizatorParameters(statement, entity);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new IllegalStateException("Crearea utilizatorului nu a inserat niciun rand.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getInt(1));
                    AuditService.getInstance().logAction("create_utilizator");
                    return entity;
                }
            }

            throw new IllegalStateException("Crearea utilizatorului nu a returnat id-ul generat.");
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la crearea utilizatorului.", e);
        }
    }

    @Override
    public void save(Utilizator entity) {
        create(entity);
    }

    @Override
    public void update(Utilizator entity) {
        String sql = "UPDATE utilizatori SET rol = ?, username = ?, parola = ?, nume = ?, prenume = ?, " +
                "email = ?, telefon = ?, nume_organizatie = ? WHERE id = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            setUtilizatorParameters(statement, entity);
            statement.setInt(9, entity.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new IllegalStateException("Nu exista utilizator cu id-ul " + entity.getId() + " pentru actualizare.");
            }

            AuditService.getInstance().logAction("update_utilizator");
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la actualizarea utilizatorului cu id-ul " + entity.getId() + ".", e);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM utilizatori WHERE id = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);
            boolean deleted = statement.executeUpdate() > 0;
            if (deleted) {
                AuditService.getInstance().logAction("delete_utilizator");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la stergerea utilizatorului cu id-ul " + id + ".", e);
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

    private void setUtilizatorParameters(PreparedStatement statement, Utilizator utilizator) throws SQLException {
        statement.setString(1, utilizator.getRol().name());
        statement.setString(2, utilizator.getUsername());
        statement.setString(3, utilizator.getParola());
        statement.setString(4, utilizator.getNume());
        statement.setString(5, utilizator.getPrenume());
        statement.setString(6, utilizator.getEmail());
        statement.setString(7, utilizator.getTelefon());
        statement.setString(8, getNumeOrganizatie(utilizator));
    }

    private String getNumeOrganizatie(Utilizator utilizator) {
        if (utilizator instanceof Organizator) {
            return ((Organizator) utilizator).getOrganizatiiCaText();
        }

        return null;
    }

    private Utilizator mapRowToUtilizator(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String rol = resultSet.getString("rol");
        String username = resultSet.getString("username");
        String parola = resultSet.getString("parola");
        String nume = resultSet.getString("nume");
        String prenume = resultSet.getString("prenume");
        String email = resultSet.getString("email");
        String telefon = resultSet.getString("telefon");
        String numeOrganizatie = resultSet.getString("nume_organizatie");

        if ("ADMIN".equals(rol)) {
            return new Admin(id, username, parola, nume, prenume, email, telefon);
        }

        if ("CLIENT".equals(rol)) {
            return new Client(id, username, parola, nume, prenume, email, telefon);
        }

        if ("ORGANIZATOR".equals(rol)) {
            return new Organizator(id, username, parola, nume, prenume, email, telefon, numeOrganizatie);
        }

        if ("AGENT_CHECK_IN".equals(rol)) {
            return new AgentCheckIn(id, username, parola, nume, prenume, email, telefon);
        }

        throw new IllegalStateException("Rol necunoscut in tabela utilizatori: " + rol);
    }
}
