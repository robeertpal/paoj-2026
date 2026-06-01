package com.pao.proiect.aibilet.repository;

import com.pao.proiect.aibilet.config.DatabaseConnection;
import com.pao.proiect.aibilet.model.Client;
import com.pao.proiect.aibilet.model.Comanda;
import com.pao.proiect.aibilet.model.Utilizator;
import com.pao.proiect.aibilet.model.dto.ComandaClientView;
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

public class ComandaRepository implements Repository<Comanda, Integer> {
    private static final DateTimeFormatter DB_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static ComandaRepository instance;

    private final UtilizatorRepository utilizatorRepository;
    private final BiletRepository biletRepository;

    private ComandaRepository() {
        this.utilizatorRepository = UtilizatorRepository.getInstance();
        this.biletRepository = BiletRepository.getInstance();
    }

    public static synchronized ComandaRepository getInstance() {
        if (instance == null) {
            instance = new ComandaRepository();
        }

        return instance;
    }

    @Override
    public Optional<Comanda> findById(Integer id) {
        String sql = "SELECT id, client_id, total, created_at FROM comenzi WHERE id = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Comanda comanda = mapRowToComanda(connection, resultSet);
                    AuditService.getInstance().logAction("read_comanda_by_id");
                    return Optional.of(comanda);
                }
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea comenzii cu id-ul " + id + ".", e);
        }
    }

    @Override
    public List<Comanda> findAll() {
        String sql = "SELECT id, client_id, total, created_at FROM comenzi ORDER BY id";
        List<Comanda> comenzi = new ArrayList<Comanda>();

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                comenzi.add(mapRowToComanda(connection, resultSet));
            }

            AuditService.getInstance().logAction("read_all_comenzi");
            return comenzi;
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea listei de comenzi.", e);
        }
    }

    @Override
    public Comanda create(Comanda entity) {
        String sql = "INSERT INTO comenzi (client_id, total, created_at) VALUES (?, ?, ?)";

        try (Connection connection = getConnection()) {
            boolean autoCommitInitial = connection.getAutoCommit();
            try {
                connection.setAutoCommit(false);

                try (
                        PreparedStatement statement = connection.prepareStatement(sql, new String[]{"ID"})
                ) {
                    verificaRelatii(connection, entity);

                    statement.setInt(1, entity.getClientId());
                    statement.setDouble(2, entity.getTotal());
                    statement.setTimestamp(3, parseTimestamp(entity.getTimestamp()));

                    int affectedRows = statement.executeUpdate();
                    if (affectedRows == 0) {
                        throw new IllegalStateException("Crearea comenzii nu a inserat niciun rand.");
                    }

                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            entity.setId(generatedKeys.getInt(1));
                        } else {
                            throw new IllegalStateException("Crearea comenzii nu a returnat id-ul generat.");
                        }
                    }

                    insereazaRelatiiBilete(connection, entity.getId(), entity.getTicketIds());
                    connection.commit();
                    AuditService.getInstance().logAction("create_comanda");
                    return entity;
                }
            } catch (SQLException | RuntimeException e) {
                rollback(connection, e);
                throw e;
            } finally {
                restaureazaAutoCommit(connection, autoCommitInitial);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Nu s-a putut crea comanda. Tranzactia a fost anulata.", e);
        }
    }

    @Override
    public void save(Comanda entity) {
        create(entity);
    }

    @Override
    public void update(Comanda entity) {
        String sql = "UPDATE comenzi SET total = ? WHERE id = ?";

        try (Connection connection = getConnection()) {
            boolean autoCommitInitial = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (
                    PreparedStatement statement = connection.prepareStatement(sql)
            ) {
                verificaRelatii(connection, entity);

                statement.setDouble(1, entity.getTotal());
                statement.setInt(2, entity.getId());

                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new IllegalStateException("Nu exista comanda cu id-ul " + entity.getId() + " pentru actualizare.");
                }

                stergeRelatiiBilete(connection, entity.getId());
                insereazaRelatiiBilete(connection, entity.getId(), entity.getTicketIds());
                connection.commit();
                connection.setAutoCommit(autoCommitInitial);
                AuditService.getInstance().logAction("update_comanda");
            } catch (SQLException | RuntimeException e) {
                rollback(connection);
                connection.setAutoCommit(autoCommitInitial);
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la actualizarea comenzii cu id-ul " + entity.getId() + ".", e);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM comenzi WHERE id = ?";

        try (Connection connection = getConnection()) {
            boolean autoCommitInitial = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (
                    PreparedStatement statement = connection.prepareStatement(sql)
            ) {
                stergeRelatiiBilete(connection, id);
                statement.setInt(1, id);

                boolean deleted = statement.executeUpdate() > 0;
                connection.commit();
                connection.setAutoCommit(autoCommitInitial);
                if (deleted) {
                    AuditService.getInstance().logAction("delete_comanda");
                }
            } catch (SQLException | RuntimeException e) {
                rollback(connection);
                connection.setAutoCommit(autoCommitInitial);
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la stergerea comenzii cu id-ul " + id + ".", e);
        }
    }

    public List<ComandaClientView> findComenziClientCuNumarBilete(int clientId) {
        String sql = "SELECT c.id AS comanda_id, c.created_at, c.total, COUNT(cb.bilet_id) AS numar_bilete " +
                "FROM comenzi c " +
                "JOIN comanda_bilete cb ON c.id = cb.comanda_id " +
                "WHERE c.client_id = ? " +
                "GROUP BY c.id, c.created_at, c.total " +
                "ORDER BY c.created_at DESC";
        List<ComandaClientView> comenzi = new ArrayList<ComandaClientView>();

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, clientId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    comenzi.add(new ComandaClientView(
                            resultSet.getInt("comanda_id"),
                            formatTimestamp(resultSet.getTimestamp("created_at")),
                            resultSet.getDouble("total"),
                            resultSet.getInt("numar_bilete")
                    ));
                }
            }

            AuditService.getInstance().logAction("join_comenzi_client_numar_bilete");
            return comenzi;
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea comenzilor clientului cu numar de bilete.", e);
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

    private void verificaRelatii(Comanda comanda) {
        Optional<Utilizator> utilizator = utilizatorRepository.findById(comanda.getClientId());
        if (!utilizator.isPresent()) {
            throw new IllegalStateException("Clientul cu id-ul " + comanda.getClientId() + " nu exista.");
        }

        if (!(utilizator.get() instanceof Client)) {
            throw new IllegalStateException("Utilizatorul cu id-ul " + comanda.getClientId() + " nu este client.");
        }

        int[] ticketIds = comanda.getTicketIds();
        for (int i = 0; i < ticketIds.length; i++) {
            if (!biletRepository.findById(ticketIds[i]).isPresent()) {
                throw new IllegalStateException("Biletul cu id-ul " + ticketIds[i] + " nu exista.");
            }
        }
    }

    private void verificaRelatii(Connection connection, Comanda comanda) throws SQLException {
        String sqlClient = "SELECT rol FROM utilizatori WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sqlClient)) {
            statement.setInt(1, comanda.getClientId());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new IllegalStateException("Clientul cu id-ul " + comanda.getClientId() + " nu exista.");
                }

                if (!"CLIENT".equals(resultSet.getString("rol"))) {
                    throw new IllegalStateException("Utilizatorul cu id-ul " + comanda.getClientId() + " nu este client.");
                }
            }
        }

        String sqlBilet = "SELECT COUNT(*) AS total FROM bilete WHERE id = ?";
        int[] ticketIds = comanda.getTicketIds();

        try (PreparedStatement statement = connection.prepareStatement(sqlBilet)) {
            for (int i = 0; i < ticketIds.length; i++) {
                statement.setInt(1, ticketIds[i]);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next() || resultSet.getInt("total") == 0) {
                        throw new IllegalStateException("Biletul cu id-ul " + ticketIds[i] + " nu exista.");
                    }
                }
            }
        }
    }

    private Comanda mapRowToComanda(Connection connection, ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int clientId = resultSet.getInt("client_id");
        double total = resultSet.getDouble("total");
        String timestamp = formatTimestamp(resultSet.getTimestamp("created_at"));

        int[] ticketIds = incarcaTicketIds(connection, id);

        return new Comanda(id, clientId, ticketIds, total, timestamp);
    }

    private int[] incarcaTicketIds(Connection connection, int comandaId) throws SQLException {
        String sql = "SELECT bilet_id FROM comanda_bilete WHERE comanda_id = ? ORDER BY bilet_id";
        List<Integer> ids = new ArrayList<Integer>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, comandaId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ids.add(resultSet.getInt("bilet_id"));
                }
            }
        }

        int[] ticketIds = new int[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            ticketIds[i] = ids.get(i);
        }

        return ticketIds;
    }

    private void insereazaRelatiiBilete(Connection connection, int comandaId, int[] ticketIds) throws SQLException {
        String sql = "INSERT INTO comanda_bilete (comanda_id, bilet_id) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < ticketIds.length; i++) {
                statement.setInt(1, comandaId);
                statement.setInt(2, ticketIds[i]);
                statement.executeUpdate();
            }
        }
    }

    private void stergeRelatiiBilete(Connection connection, int comandaId) throws SQLException {
        String sql = "DELETE FROM comanda_bilete WHERE comanda_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, comandaId);
            statement.executeUpdate();
        }
    }

    private Timestamp parseTimestamp(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Timestamp-ul comenzii nu poate fi null.");
        }

        String trimmedValue = value.trim();
        DateTimeFormatter[] formatters = new DateTimeFormatter[]{
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                DB_DATE_FORMAT
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
            throw new IllegalArgumentException("Format invalid pentru timestamp-ul comenzii: " + value, e);
        }
    }

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }

        return timestamp.toLocalDateTime().toString();
    }

    private void rollback(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new IllegalStateException("Rollback-ul tranzactiei a esuat.", e);
        }
    }

    private void rollback(Connection connection, Exception cause) {
        try {
            connection.rollback();
        } catch (SQLException e) {
            cause.addSuppressed(e);
        }
    }

    private void restaureazaAutoCommit(Connection connection, boolean autoCommitInitial) throws SQLException {
        connection.setAutoCommit(autoCommitInitial);
    }
}
