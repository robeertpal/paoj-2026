package com.pao.proiect.aibilet.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class AuditService {
    private static final AuditService INSTANCE = new AuditService();
    private static final String NUME_FISIER = "audit.csv";
    private static final String HEADER = "nume_actiune,timestamp";

    private AuditService() {
    }

    public static AuditService getInstance() {
        return INSTANCE;
    }

    public synchronized void logAction(String actionName) {
        if (actionName == null || actionName.trim().isEmpty()) {
            System.err.println("Audit esuat: numele actiunii nu poate fi gol.");
            return;
        }

        File auditFile = new File(NUME_FISIER);
        boolean needsHeader = !auditFile.exists() || auditFile.length() == 0;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(auditFile, true))) {
            if (needsHeader) {
                writer.write(HEADER);
                writer.newLine();
            }

            writer.write(escapeCsv(actionName.trim()));
            writer.write(",");
            writer.write(LocalDateTime.now().toString());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Audit esuat pentru actiunea '" + actionName + "': " + e.getMessage());
        }
    }

    private String escapeCsv(String value) {
        if (value.indexOf(',') >= 0 || value.indexOf('"') >= 0 || value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }
}
