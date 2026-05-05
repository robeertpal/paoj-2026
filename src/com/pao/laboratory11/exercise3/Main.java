package com.pao.laboratory11.exercise3;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Transaction> transactions = List.of(
                new Transaction(1, new BigDecimal("1200.00"), LocalDate.parse("2026-05-01"), "RO", "WEB"),
                new Transaction(2, new BigDecimal("300.00"), LocalDate.parse("2026-05-01"), "RO", "ATM"),
                new Transaction(3, new BigDecimal("50.00"), LocalDate.parse("2026-05-10"), "NL", "APP"),
                new Transaction(4, new BigDecimal("90.00"), LocalDate.parse("2026-06-02"), "RO", "WEB"),
                new Transaction(5, new BigDecimal("7000.00"), LocalDate.parse("2026-06-11"), "CN", "MOBILE"),
                new Transaction(6, new BigDecimal("7000.00"), LocalDate.parse("2026-06-12"), "BR", "WEB"),
                new Transaction(7, new BigDecimal("450.75"), LocalDate.parse("2026-07-03"), "DE", "POS"),
                new Transaction(8, new BigDecimal("999.99"), LocalDate.parse("2026-07-10"), "RO", "APP")
        );

        Snapshot snapshot = transactions.stream()
                .collect(CustomCollectors.toSnapshot(5));

        System.out.println("1. Total amount");
        System.out.println("Total: " + formatAmount(snapshot.getTotalAmount()) + " RON");

        System.out.println();

        System.out.println("2. Top transactions from immutable snapshot");
        snapshot.getTopTransactions().forEach(System.out::println);

        System.out.println();

        System.out.println("3. Count by country");
        snapshot.getCountByCountry()
                .entrySet()
                .stream()
                .sorted(
                        Comparator
                                .<Map.Entry<String, Long>>comparingLong(Map.Entry::getValue)
                                .reversed()
                                .thenComparing(Map.Entry::getKey)
                )
                .forEach(entry ->
                        System.out.println(entry.getKey() + ": " + entry.getValue())
                );

        System.out.println();

        System.out.println("4. Count by channel");
        snapshot.getCountByChannel()
                .entrySet()
                .stream()
                .sorted(
                        Comparator
                                .<Map.Entry<String, Long>>comparingLong(Map.Entry::getValue)
                                .reversed()
                                .thenComparing(Map.Entry::getKey)
                )
                .forEach(entry ->
                        System.out.println(entry.getKey() + ": " + entry.getValue())
                );

        System.out.println();

        System.out.println("5. Total by month");
        snapshot.getTotalByMonth()
                .forEach((month, total) ->
                        System.out.println(month + ": " + formatAmount(total) + " RON")
                );
    }

    private static String formatAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static final class CustomCollectors {
        private static Collector<Transaction, ?, Snapshot> toSnapshot(int topN) {
            class Agg {
                private final Map<String, Long> countByCountry = new HashMap<>();
                private final Map<String, Long> countByChannel = new HashMap<>();
                private final Map<String, BigDecimal> totalByMonth = new HashMap<>();
                private final List<Transaction> transactions = new ArrayList<>();
                private BigDecimal totalAmount = BigDecimal.ZERO;

                private void add(Transaction transaction) {
                    countByCountry.merge(transaction.getCountry(), 1L, Long::sum);
                    countByChannel.merge(transaction.getChannel(), 1L, Long::sum);

                    String month = transaction.getDate().toString().substring(0, 7);
                    totalByMonth.merge(month, transaction.getAmount(), BigDecimal::add);

                    totalAmount = totalAmount.add(transaction.getAmount());
                    transactions.add(transaction);
                }

                private Agg combine(Agg other) {
                    other.countByCountry.forEach((country, count) ->
                            countByCountry.merge(country, count, Long::sum)
                    );

                    other.countByChannel.forEach((channel, count) ->
                            countByChannel.merge(channel, count, Long::sum)
                    );

                    other.totalByMonth.forEach((month, total) ->
                            totalByMonth.merge(month, total, BigDecimal::add)
                    );

                    totalAmount = totalAmount.add(other.totalAmount);
                    transactions.addAll(other.transactions);

                    return this;
                }

                private Snapshot finish(int topN) {
                    List<Transaction> topTransactions = transactions.stream()
                            .sorted(Transaction.BY_AMOUNT_DESC_DATE_ASC_ID_ASC)
                            .limit(topN)
                            .collect(Collectors.toList());

                    Map<String, BigDecimal> sortedTotalByMonth = totalByMonth.entrySet()
                            .stream()
                            .sorted(Map.Entry.comparingByKey())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (a, b) -> a,
                                    LinkedHashMap::new
                            ));

                    return new Snapshot(
                            countByCountry,
                            countByChannel,
                            sortedTotalByMonth,
                            totalAmount,
                            topTransactions
                    );
                }
            }

            return Collector.of(
                    Agg::new,
                    Agg::add,
                    Agg::combine,
                    agg -> agg.finish(topN)
            );
        }
    }

    private static final class Snapshot {
        private final Map<String, Long> countByCountry;
        private final Map<String, Long> countByChannel;
        private final Map<String, BigDecimal> totalByMonth;
        private final BigDecimal totalAmount;
        private final List<Transaction> topTransactions;

        private Snapshot(
                Map<String, Long> countByCountry,
                Map<String, Long> countByChannel,
                Map<String, BigDecimal> totalByMonth,
                BigDecimal totalAmount,
                List<Transaction> topTransactions
        ) {
            this.countByCountry = Collections.unmodifiableMap(new HashMap<>(countByCountry));
            this.countByChannel = Collections.unmodifiableMap(new HashMap<>(countByChannel));
            this.totalByMonth = Collections.unmodifiableMap(new LinkedHashMap<>(totalByMonth));
            this.totalAmount = totalAmount;
            this.topTransactions = List.copyOf(topTransactions);
        }

        public Map<String, Long> getCountByCountry() {
            return countByCountry;
        }

        public Map<String, Long> getCountByChannel() {
            return countByChannel;
        }

        public Map<String, BigDecimal> getTotalByMonth() {
            return totalByMonth;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public List<Transaction> getTopTransactions() {
            return topTransactions;
        }
    }

    private static final class Transaction {
        private static final Comparator<Transaction> BY_AMOUNT_DESC_DATE_ASC_ID_ASC =
                Comparator
                        .comparing(Transaction::getAmount, Comparator.reverseOrder())
                        .thenComparing(Transaction::getDate)
                        .thenComparingInt(Transaction::getId);

        private final int id;
        private final BigDecimal amount;
        private final LocalDate date;
        private final String country;
        private final String channel;

        private Transaction(
                int id,
                BigDecimal amount,
                LocalDate date,
                String country,
                String channel
        ) {
            this.id = id;
            this.amount = amount;
            this.date = date;
            this.country = country;
            this.channel = channel;
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

        @Override
        public String toString() {
            return String.format(
                    "[%d] amount=%s date=%s country=%s channel=%s",
                    id,
                    formatAmount(amount),
                    date,
                    country,
                    channel
            );
        }
    }
}