
# Exercise 1 - Motor de reguli antifrauda

## Scenariu

Construim un motor de reguli pentru tranzactii bancare. Pentru fiecare tranzactie se calculeaza un verdict (`ALLOW`/`FLAG`) si un scor de risc determinist.

## Cerinta (revizuită, specificații exacte)

Scop: implementează un pipeline de reguli folosind compoziție de predicate astfel încât mai multe condiții să poată fi evaluate fără duplicare de cod. Readme-ul acesta conține valorile exacte și formatele necesare pentru o implementare reproducibilă.

### Specificații principale (valori exacte)

- Prag sumă: AMOUNT_THRESHOLD = 1000.00
- Țări cu risc (RISKY_COUNTRIES): { "NG", "RU", "UA", "CN", "BR" }
- Canale considerate suspecte (SUSPICIOUS_CHANNELS): { "WEB", "MOBILE" }

Poți modifica aceste constante în cod, dar implementarea trebuie să folosească predicate reușite și compoziție pentru a defini regula finală.

## Format input (detaliat)

- Linia 1: N (int, 1 <= N <= 200000)
- Următoarele N linii: fiecare tranzacție în format:
  id amount date country channel
  - id: int (unic)
  - amount: zecimal cu 2 zecimale (ex: 1200.50)
  - date: YYYY-MM-DD
  - country: 2-letter uppercase (ex: RO)
  - channel: uppercase string (ex: WEB, ATM, APP)
- Următoarea linie: Q (int, 1 <= Q <= 200000)
- Următoarele Q linii: comenzi, una per linie:
  - CHECK <id>
  - LIST_FLAGGED
  - TOP_RISK <k>

Notă: input invalid (format greșit) poate fi considerat eroare; pentru laborator, presupunem că inputul respectă formatul.

## Reguli elementare (Partea A — valori exacte)

- amount > AMOUNT_THRESHOLD
- country ∈ RISKY_COUNTRIES
- channel ∈ SUSPICIOUS_CHANNELS

Implementați fiecare ca `Predicate<Transaction>`.

## Compoziție reguli și verdict (Partea B)

Exemplu de regulă finală (configurabilă):

flaggedRule = amountOverThreshold.or(countryInRisk).or(channelSuspicious)

Dacă flaggedRule.test(tx) == true => verdict = FLAG, altfel => ALLOW

Observație: folosiți .and(), .or(), .negate() pentru a permite ușurința modificărilor.

## Calcul scor de risc (Partea C — exact)

Scor = amountScore + countryScore + channelScore + bonusFraudPattern

Componente:
- amountScore = min(50, floor(amount / 1000) * 10)
  - Ex: 1200.50 => floor(1.2005)=1 => 10; 7000.00 => floor(7)=7*10=70 => cap la 50
- countryScore = 20 dacă country ∈ RISKY_COUNTRIES, altfel 0
- channelScore:
  - WEB -> 22
  - MOBILE, APP -> 10
  - ATM -> 5
  - POS -> 3
  - other -> 0
- bonusFraudPattern = 5 (opțional — rămâne 0 dacă nu se aplică reguli combinate speciale)

Toate valorile sunt întregi; scorul final este integer >= 0.

## Comparator și tie-breakers (detaliat)

Ordine folosită pentru `LIST_FLAGGED` și `TOP_RISK`:
1. score (desc)
2. amount (desc)
3. date (asc) — tranzacțiile mai vechi înainte
4. id (asc)

Aceasta asigură ordine deterministă în caz de egalitate la nivelul scorului.

## Format output (exact)

- CHECK <id> => <ALLOW|FLAG> score=<value>
  - ex: CHECK 1 => FLAG score=72
  - Dacă id inexistent: CHECK <id> => NOT_FOUND
- LIST_FLAGGED:
  - Listează toate tranzacțiile cu verdict FLAG, câte o linie, în ordinea comparatorului:
    [<id>] FLAG score=<value> amount=<amount> date=<date> country=<country> channel=<channel>
  - Minimal (dacă preferi linie scurtă): [<id>] FLAG score=<value>
- TOP_RISK <k>:
  - Primele k tranzacții (indiferent de verdict), ordonate conform comparatorului de mai sus.
  - Format linie: [<id>] <ALLOW|FLAG> score=<value> amount=<amount> date=<date> country=<country> channel=<channel>
  - Dacă k > N, returnează primele N fără eroare.

## Exemple complete

Input:
```
3
1 1200.50 2026-05-01 RO WEB
2 90.00 2026-05-01 NL ATM
3 7000.00 2026-05-02 RO APP
3
CHECK 1
LIST_FLAGGED
TOP_RISK 2
```

Folosind RISKY_COUNTRIES={NG,RU,UA,CN,BR} și regulile de mai sus:
- tx1: amountScore=10, countryScore=0 (RO nu este în listă), channelScore=22 => score=32 => FLAG (canal WEB)
- tx2: amountScore=0, countryScore=0, channelScore=5 => score=5 => ALLOW
- tx3: amountScore=50, countryScore=0, channelScore=10 => score=60 => FLAG

Output:
```
CHECK 1 => FLAG score=32
[3] FLAG score=60 amount=7000.00 date=2026-05-02 country=RO channel=APP
[1] FLAG score=32 amount=1200.50 date=2026-05-01 country=RO channel=WEB
[3] FLAG score=60 amount=7000.00 date=2026-05-02 country=RO channel=APP
[1] FLAG score=32 amount=1200.50 date=2026-05-01 country=RO channel=WEB
```

Notă: valorile depind de lista `RISKY_COUNTRIES` și de ponderile alese; exemplele de mai sus sunt consistente cu specificațiile din acest readme.

## Cazuri margină și recomandări de implementare

- Parsare sume: folosește BigDecimal pentru precizie sau double păstrând 2 zecimale la afișare.
- Date: parsează cu LocalDate (format strict YYYY-MM-DD).
- Performanță: pentru `TOP_RISK k` folosește un heap de dimensiune k sau partial sort dacă N mare.
- Testare: scrie teste unitare pentru reguli elementare, compunerea lor, comparator și comenzile CHECK/LIST/TOP.

## Hint-uri

- Definește reguli elementare ca `Predicate<Transaction>`.
- Evită duplicarea codului; compune predicate.
- Păstrează comparatorul într-un singur loc (factory/constantă).

Dacă vrei, pot actualiza și codul exemplu (Java) conform acestor specificații și adăuga teste și input/output de verificare.

## Teorie și explicații (ce înseamnă fiecare cerință)

Scopul acestei secțiuni este să ofere contextul teoretic și recomandări de implementare pentru conceptele cerute în laborator: ce înseamnă "pipeline de reguli", de ce folosim `Predicate<Transaction>`, cum se compun regulile, cum se proiectează comparatorul și cum se calculează scorul într-un mod reprodus și testabil.

- Pipeline de reguli
  - Definiție: un pipeline de reguli este o secvență sau un arbore de predicate/pasi de evaluare prin care trece o tranzacție pentru a obține un verdict și/sau un scor. Fiecare pas e o funcție pură (fără efecte secundare) care evaluează o proprietate a tranzacției.
  - Avantaje:
    - separare a responsabilităților (fiecare regulă este independentă)
    - testabilitate ușoară (teste unitare pentru fiecare predicate)
    - ușurință la compunere (putem combina reguli simple în reguli complexe fără duplicare)

- Predicate<Transaction>
  - Concept: `Predicate<Transaction>` (sau orice interfață funcțională echivalentă) reprezintă o funcție Transaction -> boolean.
  - Motiv: Java 8+ oferă metode default `.and()`, `.or()`, `.negate()` pentru a compune predicate, deci implementarea devine concisă și declarativă.
  - Implementare recomandată: creează o clasă `Rules` sau `TransactionPredicates` cu predicate statice publice, de ex. `Predicate<Transaction> amountOverThreshold(BigDecimal t)`.

- Compoziție (and/or/negate)
  - Exemple:
    - `Predicate<Transaction> suspicious = amountOverThreshold(AMOUNT_THRESHOLD).or(countryInRisk).or(channelSuspicious);`
    - `Predicate<Transaction> highValueAndRisky = amountOverThreshold(10000).and(countryInRisk);`
  - Observație: compunerea nu trebuie să implementeze efecte; doar combină predicatele existente.

- Calcularea scorului (determinism și reproducibilitate)
  - Scorul trebuie să fie determinist: pentru aceleași inputuri rezultatul e același. De aceea folosim reguli integer fixe (nicio valoare aleatorie sau dependență de timp).
  - Recomandarea: encapsulează funcția de calcul `int computeScore(Transaction tx)` într-o clasă `RiskScorer` sau metodă statică. Astfel poți testa ușor și poți schimba politica de scor fără a modifica pipeline-ul de reguli.

- Comparatorul (tie-breakers determinist)
  - Motiv: atunci când facem `LIST_FLAGGED` sau `TOP_RISK`, rezultatul trebuie să fie stabil între rulări. Comparatorul propus (score desc, amount desc, date asc, id asc) furnizează acest lucru.
  - Implementare: folosește `Comparator.comparingInt(Transaction::getScore).reversed()` și apoi `thenComparing(Transaction::getAmount).reversed()` etc., având grijă la tipuri (BigDecimal comparat cu `compareTo`).

- Structura entităților
  - Transaction: clasa trebuie să conțină cel puțin: id (int), amount (BigDecimal), date (LocalDate), country (String), channel (String), score (int) sau un mod de a-l obține la cerere.
  - Recomandare: calculează score la parsare și păstrează-l într-un câmp final/immutabil în obiect pentru eficiență.

- Pipeline-ul efectiv (fluxul aplicației)
  - Pași:
    1. Citește și parsează tranzacțiile; validează formatele.
    2. Pentru fiecare tranzacție calculează scorul (folosește `RiskScorer`).
    3. Determină verdictul aplicând regula compusă `flaggedRule`.
    4. Păstrează tranzacțiile într-o structură (listă) și un map `id -> transaction` pentru CHECK rapid.
    5. Răspunde comenzilor:
       - `CHECK id`: caută în map și afișează verdict+scor sau NOT_FOUND
       - `LIST_FLAGGED`: filtrează WHERE verdict==FLAG și sortează cu comparator-ul
       - `TOP_RISK k`: sortează (sau folosește heap) toate tranzacțiile după comparator și afișează primele k

- Performance și complexitate
  - Parsing: O(N)
  - Calcul scor/verdict: O(N)
  - `LIST_FLAGGED`: sortarea completă a subsetului flagged: O(F log F) unde F = numărul flagged
  - `TOP_RISK k`: recomandat heap O(N log k) sau partial sort O(N + k log N) în funcție de librărie.

- Testare recomandată
  - Unit tests pentru:
    - predicate simple (amount, country, channel)
    - compunerea predicate
    - RiskScorer pentru valori limită (ex: amount=1000,999.99, exact multipli de 1000)
    - Comparator pentru cazuri de tie (același score)
  - Integration tests pentru input/output sesiuni (folosește fișiere de test cu exemple)

## Exemple de fragmente Java (orientativ)

Transaction model (simplificat):

```java
public final class Transaction {
    private final int id;
    private final BigDecimal amount;
    private final LocalDate date;
    private final String country;
    private final String channel;
    private final int score;

    // constructor, getters
}
```

Predicate și compunere (exemplu):

```java
Predicate<Transaction> amountOver(BigDecimal threshold) {
    return tx -> tx.getAmount().compareTo(threshold) > 0;
}

Predicate<Transaction> countryIn(Set<String> risky) {
    return tx -> risky.contains(tx.getCountry());
}

Predicate<Transaction> suspiciousChannel = countryInRisk.or(amountOver(threshold)).or(channelSuspicious);
```

Comparator (exemplu):

```java
Comparator<Transaction> cmp = Comparator
    .comparingInt(Transaction::getScore).reversed()
    .thenComparing(Transaction::getAmount, (a,b) -> b.compareTo(a)) // amount desc
    .thenComparing(Transaction::getDate) // asc
    .thenComparingInt(Transaction::getId);
```

Computare scor (orientativ):

```java
int computeAmountScore(BigDecimal amount) {
    int buckets = amount.divide(new BigDecimal("1000"), 0, RoundingMode.DOWN).intValue();
    return Math.min(50, buckets * 10);
}
```

Aceste fragmente sunt orientative; implementarea finală trebuie să trateze erorile de parsare, validările și formatarea output-ului exact așa cum este specificat mai sus.

---

Am adăugat explicațiile teoretice și exemplele orientative. Spune-mi dacă vrei să includ și un fișier sursă Java complet (implementare minimală + teste) în repo; pot crea pachetul `com.pao.laboratory11.exercise1.impl` cu clasele necesare și câteva teste de integrare.
