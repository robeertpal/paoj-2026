package com.pao.proiect.aibilet.repository;

import com.pao.proiect.aibilet.config.DatabaseConnection;
import com.pao.proiect.aibilet.model.AgentCheckIn;
import com.pao.proiect.aibilet.model.Utilizator;
import com.pao.proiect.aibilet.service.AuditService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AgentEventAssignmentRepository implements Repository<AgentEventAssignmentRepository.AgentEventAssignment, String> {
    private static AgentEventAssignmentRepository instance;

    private final UtilizatorRepository utilizatorRepository;
    private final EvenimentRepository evenimentRepository;

    private AgentEventAssignmentRepository() {
        this.utilizatorRepository = UtilizatorRepository.getInstance();
        this.evenimentRepository = EvenimentRepository.getInstance();
    }

    public static synchronized AgentEventAssignmentRepository getInstance() {
        if (instance == null) {
            instance = new AgentEventAssignmentRepository();
        }

        return instance;
    }

    public AgentEventAssignment createAssignment(int agentId, int evenimentId) {
        verificaRelatii(agentId, evenimentId);

        if (exists(agentId, evenimentId)) {
            throw new IllegalStateException("Asignarea agentului " + agentId + " la evenimentul " + evenimentId + " exista deja.");
        }

        String sql = "INSERT INTO agent_event_assignments (agent_id, eveniment_id, assigned_at) VALUES (?, ?, SYSTIMESTAMP)";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, agentId);
            statement.setInt(2, evenimentId);
            statement.executeUpdate();

            AgentEventAssignment assignment = findOne(agentId, evenimentId).get();
            AuditService.getInstance().logAction("create_agent_event_assignment");
            return assignment;
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la crearea asignarii agent-eveniment.", e);
        }
    }

    @Override
    public AgentEventAssignment create(AgentEventAssignment entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Asignarea agent-eveniment nu poate fi null.");
        }

        return createAssignment(entity.getAgentId(), entity.getEvenimentId());
    }

    @Override
    public void save(AgentEventAssignment entity) {
        create(entity);
    }

    @Override
    public Optional<AgentEventAssignment> findById(String id) {
        int[] ids = parseCompositeId(id);
        return findOne(ids[0], ids[1]);
    }

    @Override
    public List<AgentEventAssignment> findAll() {
        String sql = "SELECT agent_id, eveniment_id, assigned_at FROM agent_event_assignments ORDER BY agent_id, eveniment_id";
        List<AgentEventAssignment> assignments = new ArrayList<AgentEventAssignment>();

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                assignments.add(mapRowToAssignment(resultSet));
            }

            AuditService.getInstance().logAction("read_all_agent_event_assignments");
            return assignments;
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea asignarilor agent-eveniment.", e);
        }
    }

    @Override
    public void update(AgentEventAssignment entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Asignarea agent-eveniment nu poate fi null.");
        }

        verificaRelatii(entity.getAgentId(), entity.getEvenimentId());

        String sql = "UPDATE agent_event_assignments SET assigned_at = assigned_at WHERE agent_id = ? AND eveniment_id = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, entity.getAgentId());
            statement.setInt(2, entity.getEvenimentId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new IllegalStateException("Nu exista asignarea agent-eveniment pentru actualizare.");
            }

            AuditService.getInstance().logAction("update_agent_event_assignment");
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la actualizarea asignarii agent-eveniment.", e);
        }
    }

    public List<AgentEventAssignment> findByAgentId(int agentId) {
        String sql = "SELECT agent_id, eveniment_id, assigned_at FROM agent_event_assignments " +
                "WHERE agent_id = ? ORDER BY eveniment_id";
        List<AgentEventAssignment> assignments = new ArrayList<AgentEventAssignment>();

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, agentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    assignments.add(mapRowToAssignment(resultSet));
                }
            }

            AuditService.getInstance().logAction("read_assignments_by_agent");
            return assignments;
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea asignarilor pentru agentul " + agentId + ".", e);
        }
    }

    public List<AgentEventAssignment> findByEvenimentId(int evenimentId) {
        String sql = "SELECT agent_id, eveniment_id, assigned_at FROM agent_event_assignments " +
                "WHERE eveniment_id = ? ORDER BY agent_id";
        List<AgentEventAssignment> assignments = new ArrayList<AgentEventAssignment>();

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, evenimentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    assignments.add(mapRowToAssignment(resultSet));
                }
            }

            AuditService.getInstance().logAction("read_assignments_by_eveniment");
            return assignments;
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la citirea asignarilor pentru evenimentul " + evenimentId + ".", e);
        }
    }

    public boolean exists(int agentId, int evenimentId) {
        boolean exists = findOne(agentId, evenimentId).isPresent();
        AuditService.getInstance().logAction("check_agent_event_assignment_exists");
        return exists;
    }

    public boolean deleteAssignment(int agentId, int evenimentId) {
        String sql = "DELETE FROM agent_event_assignments WHERE agent_id = ? AND eveniment_id = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, agentId);
            statement.setInt(2, evenimentId);
            boolean deleted = statement.executeUpdate() > 0;
            if (deleted) {
                AuditService.getInstance().logAction("delete_agent_event_assignment");
            }

            return deleted;
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la stergerea asignarii agent-eveniment.", e);
        }
    }

    @Override
    public void delete(String id) {
        int[] ids = parseCompositeId(id);
        deleteAssignment(ids[0], ids[1]);
    }

    private Optional<AgentEventAssignment> findOne(int agentId, int evenimentId) {
        String sql = "SELECT agent_id, eveniment_id, assigned_at FROM agent_event_assignments " +
                "WHERE agent_id = ? AND eveniment_id = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, agentId);
            statement.setInt(2, evenimentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToAssignment(resultSet));
                }
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Eroare la verificarea asignarii agent-eveniment.", e);
        }
    }

    private void verificaRelatii(int agentId, int evenimentId) {
        Optional<Utilizator> utilizator = utilizatorRepository.findById(agentId);
        if (!utilizator.isPresent()) {
            throw new IllegalStateException("Agentul cu id-ul " + agentId + " nu exista.");
        }

        if (!(utilizator.get() instanceof AgentCheckIn)) {
            throw new IllegalStateException("Utilizatorul cu id-ul " + agentId + " nu este agent check-in.");
        }

        if (!evenimentRepository.findById(evenimentId).isPresent()) {
            throw new IllegalStateException("Evenimentul cu id-ul " + evenimentId + " nu exista.");
        }
    }

    private int[] parseCompositeId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID-ul asignarii trebuie sa aiba formatul agentId:evenimentId.");
        }

        String[] parts = id.trim().split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("ID-ul asignarii trebuie sa aiba formatul agentId:evenimentId.");
        }

        try {
            return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID-ul asignarii trebuie sa contina doua numere intregi.", e);
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

    private AgentEventAssignment mapRowToAssignment(ResultSet resultSet) throws SQLException {
        Timestamp assignedAt = resultSet.getTimestamp("assigned_at");

        return new AgentEventAssignment(
                resultSet.getInt("agent_id"),
                resultSet.getInt("eveniment_id"),
                assignedAt == null ? null : assignedAt.toLocalDateTime().toString()
        );
    }

    public static class AgentEventAssignment {
        private final int agentId;
        private final int evenimentId;
        private final String assignedAt;

        public AgentEventAssignment(int agentId, int evenimentId, String assignedAt) {
            this.agentId = agentId;
            this.evenimentId = evenimentId;
            this.assignedAt = assignedAt;
        }

        public int getAgentId() {
            return agentId;
        }

        public int getEvenimentId() {
            return evenimentId;
        }

        public String getAssignedAt() {
            return assignedAt;
        }

        @Override
        public String toString() {
            return "AgentEventAssignment\n" +
                    "  Agent ID: " + agentId + "\n" +
                    "  Eveniment ID: " + evenimentId + "\n" +
                    "  Assigned at: " + assignedAt;
        }
    }
}
