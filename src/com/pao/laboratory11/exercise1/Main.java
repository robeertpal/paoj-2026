package com.pao.laboratory11.exercise1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Predicate;

public class Main {
    private static final BigDecimal AMOUNT_THRESHOLD = new BigDecimal("1000.00");

    private static final Set<String> RISKY_COUNTRIES = Set.of("NG", "RU", "UA", "CN", "BR");
    private static final Set<String> SUSPICIOUS_CHANNELS = Set.of("WEB", "MOBILE");

    private static final Predicate<Transaction> AMOUNT_OVER_THRESHOLD =
            tx -> tx.getAmount().compareTo(AMOUNT_THRESHOLD) > 0;

    private static final Predicate<Transaction> COUNTRY_IN_RISK =
            tx -> RISKY_COUNTRIES.contains(tx.getCountry());

    private static final Predicate<Transaction> CHANNEL_SUSPICIOUS =
            tx -> SUSPICIOUS_CHANNELS.contains(tx.getChannel());

    private static final Predicate<Transaction> FLAGGED_RULE =
            AMOUNT_OVER_THRESHOLD
                    .or(COUNTRY_IN_RISK)
                    .or(CHANNEL_SUSPICIOUS);

    private static final Comparator<Transaction> RISK_COMPARATOR = Comparator
            .comparingInt(Transaction::getScore).reversed()
            .thenComparing(Transaction::getAmount, Comparator.reverseOrder())
            .thenComparing(Transaction::getDate)
            .thenComparingInt(Transaction::getId);

    public static void main(String[] args) {
        FastScanner scanner = new FastScanner(System.in);

        int n = scanner.nextInt();

        List<Transaction> transactions = new ArrayList<>(n);
        Map<Integer, Transaction> transactionsById = new HashMap<>();

        for (int i = 0; i < n; i++) {
            int id = scanner.nextInt();
            BigDecimal amount = new BigDecimal(scanner.next());
            LocalDate date = LocalDate.parse(scanner.next());
            String country = scanner.next();
            String channel = scanner.next();

            int score = computeScore(amount, country, channel);

            Transaction temporaryTransaction = new Transaction(
                    id,
                    amount,
                    date,
                    country,
                    channel,
                    score,
                    Verdict.ALLOW
            );

            Verdict verdict = FLAGGED_RULE.test(temporaryTransaction)
                    ? Verdict.FLAG
                    : Verdict.ALLOW;

            Transaction transaction = new Transaction(
                    id,
                    amount,
                    date,
                    country,
                    channel,
                    score,
                    verdict
            );

            transactions.add(transaction);
            transactionsById.put(id, transaction);
        }

        int q = scanner.nextInt();
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < q; i++) {
            String command = scanner.next();

            switch (command) {
                case "CHECK": {
                    int id = scanner.nextInt();
                    Transaction transaction = transactionsById.get(id);

                    if (transaction == null) {
                        output.append("CHECK ")
                                .append(id)
                                .append(" => NOT_FOUND\n");
                    } else {
                        output.append("CHECK ")
                                .append(id)
                                .append(" => ")
                                .append(transaction.getVerdict())
                                .append(" score=")
                                .append(transaction.getScore())
                                .append('\n');
                    }

                    break;
                }

                case "LIST_FLAGGED": {
                    transactions.stream()
                            .filter(tx -> tx.getVerdict() == Verdict.FLAG)
                            .sorted(RISK_COMPARATOR)
                            .forEach(tx -> output.append(formatFullLine(tx)).append('\n'));

                    break;
                }

                case "TOP_RISK": {
                    int k = scanner.nextInt();
                    appendTopRisk(transactions, k, output);
                    break;
                }

                default:
                    break;
            }
        }

        System.out.print(output);
    }

    private static int computeScore(BigDecimal amount, String country, String channel) {
        return computeAmountScore(amount)
                + computeCountryScore(country)
                + computeChannelScore(channel)
                + computeBonusFraudPattern();
    }

    private static int computeAmountScore(BigDecimal amount) {
        int buckets = amount
                .divide(new BigDecimal("1000"), 0, RoundingMode.DOWN)
                .intValue();

        return Math.min(50, buckets * 10);
    }

    private static int computeCountryScore(String country) {
        return RISKY_COUNTRIES.contains(country) ? 20 : 0;
    }

    private static int computeChannelScore(String channel) {
        switch (channel) {
            case "WEB":
                return 22;
            case "MOBILE":
            case "APP":
                return 10;
            case "ATM":
                return 5;
            case "POS":
                return 3;
            default:
                return 0;
        }
    }

    private static int computeBonusFraudPattern() {
        return 0;
    }

    private static void appendTopRisk(
            List<Transaction> transactions,
            int k,
            StringBuilder output
    ) {
        if (k <= 0 || transactions.isEmpty()) {
            return;
        }

        int limit = Math.min(k, transactions.size());

        PriorityQueue<Transaction> heap = new PriorityQueue<>(
                limit + 1,
                RISK_COMPARATOR.reversed()
        );

        for (Transaction transaction : transactions) {
            heap.offer(transaction);

            if (heap.size() > limit) {
                heap.poll();
            }
        }

        List<Transaction> result = new ArrayList<>(heap);
        result.sort(RISK_COMPARATOR);

        for (Transaction transaction : result) {
            output.append(formatFullLine(transaction)).append('\n');
        }
    }

    private static String formatFullLine(Transaction transaction) {
        return String.format(
                "[%d] %s score=%d amount=%s date=%s country=%s channel=%s",
                transaction.getId(),
                transaction.getVerdict(),
                transaction.getScore(),
                formatAmount(transaction.getAmount()),
                transaction.getDate(),
                transaction.getCountry(),
                transaction.getChannel()
        );
    }

    private static String formatAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private enum Verdict {
        ALLOW,
        FLAG
    }

    private static final class Transaction {
        private final int id;
        private final BigDecimal amount;
        private final LocalDate date;
        private final String country;
        private final String channel;
        private final int score;
        private final Verdict verdict;

        private Transaction(
                int id,
                BigDecimal amount,
                LocalDate date,
                String country,
                String channel,
                int score,
                Verdict verdict
        ) {
            this.id = id;
            this.amount = amount;
            this.date = date;
            this.country = country;
            this.channel = channel;
            this.score = score;
            this.verdict = verdict;
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

        public int getScore() {
            return score;
        }

        public Verdict getVerdict() {
            return verdict;
        }
    }

    private static final class FastScanner {
        private final java.io.InputStream inputStream;
        private final byte[] buffer = new byte[1 << 16];

        private int pointer = 0;
        private int length = 0;

        private FastScanner(java.io.InputStream inputStream) {
            this.inputStream = inputStream;
        }

        private int read() {
            if (pointer >= length) {
                try {
                    length = inputStream.read(buffer);
                    pointer = 0;
                } catch (java.io.IOException e) {
                    return -1;
                }

                if (length <= 0) {
                    return -1;
                }
            }

            return buffer[pointer++];
        }

        private String next() {
            StringBuilder builder = new StringBuilder();
            int c;

            do {
                c = read();
            } while (c <= ' ' && c != -1);

            while (c > ' ') {
                builder.append((char) c);
                c = read();
            }

            return builder.toString();
        }

        private int nextInt() {
            return Integer.parseInt(next());
        }
    }
}