package com.pao.laboratory11.exercise2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int n = scanner.nextInt();
        List<TransactionReport> transactions = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            int id = scanner.nextInt();
            BigDecimal amount = new BigDecimal(scanner.next());
            LocalDate date = LocalDate.parse(scanner.next());
            String country = scanner.next();
            String channel = scanner.next();
            String accountId = scanner.next();

            transactions.add(new TransactionReport(
                    id,
                    amount,
                    date,
                    country,
                    channel,
                    accountId
            ));
        }

        int q = scanner.nextInt();

        for (int i = 0; i < q; i++) {
            String command = scanner.next();

            switch (command) {
                case "REPORT_MONTH": {
                    String month = scanner.next();
                    printMonthReport(transactions, month);
                    break;
                }

                case "REPORT_ACCOUNT": {
                    String accountId = scanner.next();
                    printAccountReport(transactions, accountId);
                    break;
                }

                case "TOP_CHANNELS": {
                    int k = scanner.nextInt();
                    printTopChannels(transactions, k);
                    break;
                }

                default:
                    break;
            }
        }

        scanner.close();
    }

    private static void printMonthReport(List<TransactionReport> transactions, String month) {
        List<TransactionReport> filtered = transactions.stream()
                .filter(t -> t.getMonth().equals(month))
                .collect(Collectors.toList());

        BigDecimal total = filtered.stream()
                .map(TransactionReport::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.printf(
                "MONTH %s total=%s count=%d%n",
                month,
                formatAmount(total),
                filtered.size()
        );
    }

    private static void printAccountReport(List<TransactionReport> transactions, String accountId) {
        List<TransactionReport> filtered = transactions.stream()
                .filter(t -> t.getAccountId().equals(accountId))
                .collect(Collectors.toList());

        BigDecimal total = filtered.stream()
                .map(TransactionReport::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.printf(
                "ACCOUNT %s total=%s count=%d%n",
                accountId,
                formatAmount(total),
                filtered.size()
        );
    }

    private static void printTopChannels(List<TransactionReport> transactions, int k) {
        Map<String, Long> channelCounts = transactions.stream()
                .collect(Collectors.groupingBy(
                        TransactionReport::getChannel,
                        Collectors.counting()
                ));

        channelCounts.entrySet()
                .stream()
                .sorted(
                        Comparator
                                .<Map.Entry<String, Long>>comparingLong(Map.Entry::getValue)
                                .reversed()
                                .thenComparing(Map.Entry::getKey)
                )
                .limit(k)
                .forEach(entry ->
                        System.out.println(entry.getKey() + " " + entry.getValue())
                );
    }

    private static String formatAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static final class TransactionReport {
        private final int id;
        private final BigDecimal amount;
        private final LocalDate date;
        private final String country;
        private final String channel;
        private final String accountId;

        private TransactionReport(
                int id,
                BigDecimal amount,
                LocalDate date,
                String country,
                String channel,
                String accountId
        ) {
            this.id = id;
            this.amount = amount;
            this.date = date;
            this.country = country;
            this.channel = channel;
            this.accountId = accountId;
        }

        public int getId() {
            return id;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public LocalDate getDate() {
            return date;
        }

        public String getCountry() {
            return country;
        }

        public String getChannel() {
            return channel;
        }

        public String getAccountId() {
            return accountId;
        }

        public String getMonth() {
            return date.toString().substring(0, 7);
        }
    }
}