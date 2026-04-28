package com.pao.laboratory09.exercise2;

import com.pao.laboratory09.exercise1.TipTranzactie;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class Main {
    private static final String OUTPUT_FILE = "output/lab09_ex2.bin";
    private static final int RECORD_SIZE = 32;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String firstLine = br.readLine();
        if (firstLine == null || firstLine.trim().isEmpty()) {
            return;
        }

        int n = Integer.parseInt(firstLine.trim());

        File file = new File(OUTPUT_FILE);
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
            for (int i = 0; i < n; i++) {
                String line = br.readLine();

                while (line != null && line.trim().isEmpty()) {
                    line = br.readLine();
                }

                String[] parts = line.trim().split("\\s+");

                int id = Integer.parseInt(parts[0]);
                double suma = Double.parseDouble(parts[1]);
                String data = parts[2];
                TipTranzactie tip = TipTranzactie.valueOf(parts[3]);

                byte[] record = createRecord(id, suma, data, tip);
                dos.write(record);
            }
        }

        try (RandomAccessFile raf = new RandomAccessFile(OUTPUT_FILE, "rw")) {
            String commandLine;

            while ((commandLine = br.readLine()) != null) {
                commandLine = commandLine.trim();

                if (commandLine.isEmpty()) {
                    continue;
                }

                String[] parts = commandLine.split("\\s+");
                String command = parts[0];

                switch (command) {
                    case "READ": {
                        int idx = Integer.parseInt(parts[1]);
                        System.out.println(readRecord(raf, idx));
                        break;
                    }

                    case "UPDATE": {
                        int idx = Integer.parseInt(parts[1]);
                        String status = parts[2];

                        raf.seek((long) idx * RECORD_SIZE + 23);
                        raf.write(statusToByte(status));

                        System.out.println("Updated [" + idx + "]: " + status);
                        break;
                    }

                    case "PRINT_ALL": {
                        for (int idx = 0; idx < n; idx++) {
                            System.out.println(readRecord(raf, idx));
                        }
                        break;
                    }

                    default:
                        break;
                }
            }
        }
    }

    private static byte[] createRecord(int id, double suma, String data, TipTranzactie tip) {
        byte[] record = new byte[RECORD_SIZE];

        ByteBuffer buffer = ByteBuffer.wrap(record);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        buffer.putInt(id);
        buffer.putDouble(suma);

        byte[] dataBytes = data.getBytes(StandardCharsets.US_ASCII);
        for (int i = 0; i < 10; i++) {
            if (i < dataBytes.length) {
                record[12 + i] = dataBytes[i];
            } else {
                record[12 + i] = (byte) ' ';
            }
        }

        record[22] = tipToByte(tip);
        record[23] = 0; // PENDING

        // bytes 24-31 rămân 0 implicit

        return record;
    }

    private static String readRecord(RandomAccessFile raf, int idx) throws IOException {
        byte[] record = new byte[RECORD_SIZE];

        raf.seek((long) idx * RECORD_SIZE);
        raf.readFully(record);

        ByteBuffer buffer = ByteBuffer.wrap(record);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        int id = buffer.getInt();
        double suma = buffer.getDouble();

        String data = new String(record, 12, 10, StandardCharsets.US_ASCII).trim();

        TipTranzactie tip = byteToTip(record[22]);
        String status = byteToStatus(record[23]);

        return String.format(
                Locale.US,
                "[%d] id=%d data=%s tip=%s suma=%.2f RON status=%s",
                idx, id, data, tip, suma, status
        );
    }

    private static byte tipToByte(TipTranzactie tip) {
        if (tip == TipTranzactie.CREDIT) {
            return 0;
        }

        return 1;
    }

    private static TipTranzactie byteToTip(byte value) {
        if (value == 0) {
            return TipTranzactie.CREDIT;
        }

        return TipTranzactie.DEBIT;
    }

    private static byte statusToByte(String status) {
        switch (status) {
            case "PENDING":
                return 0;
            case "PROCESSED":
                return 1;
            case "REJECTED":
                return 2;
            default:
                return 0;
        }
    }

    private static String byteToStatus(byte value) {
        switch (value) {
            case 0:
                return "PENDING";
            case 1:
                return "PROCESSED";
            case 2:
                return "REJECTED";
            default:
                return "PENDING";
        }
    }
}