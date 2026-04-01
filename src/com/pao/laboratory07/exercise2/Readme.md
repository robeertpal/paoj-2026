# Exercițiul 2 — Sistem avansat de comenzi eCommerce: ierarhii, interfețe, procesare

> **Pachet:** `com.pao.laboratory07.exercise2`
> **Timp estimat:** ~35 min · **Teste automate:** da (`Test.java`)

---

## Teste automate și structură

- Pentru fiecare parte există teste automate în directoarele:
  - `tests/partA/` (ex: `1.in`, `1.out`, ...)
  - `tests/partB/` (ex: `1.in`, `1.out`, ...)
  - `tests/partC/` (ex: `1.in`, `1.out`, ...)
- Fiecare test are un fișier de input (`.in`) și unul de output (`.out`).
- Testele acoperă cazuri normale și limită. Rularea testelor se face cu `Test.java`.

---

## Scenariul general

Extindem sistemul de tracking al comenzilor pentru magazinul online, pornind de la enum-ul și structura din exercițiul 1. Acum, comenzile pot fi de mai multe tipuri (standard, precomandă, abonament), fiecare cu reguli și date suplimentare. Vei folosi sealed classes, interfețe și enumuri pentru a modela ierarhia și procesarea comenzilor.

---

## Structura inputului (valabilă pentru toate părțile)

- Prima linie: numărul de comenzi
- Următoarele linii: fiecare comandă pe o linie, format:
  - `STANDARD <id> <client> <valoare>`
  - `PRECOMANDA <id> <client> <valoare> <data_livrare>`
  - `ABONAMENT <id> <client> <valoare> <nr_luni>`

---

# Partea A — Ierarhie sealed și procesare de bază

> Teste pentru această parte: `tests/partA/`

**Cerință:**
- Creează o sealed class `Comanda` cu câmpuri comune (`id`, `client`, `valoare`) și o metodă abstractă `procesare()`.
- Creează subclase pentru fiecare tip: `ComandaStandard`, `Precomanda`, `ComandaAbonament`.
- În `Main.java`, citește comenzile, creează obiectele potrivite și apelează `procesare()` pentru fiecare, afișând un mesaj simplu cu tipul și datele comenzii.

**Exemplu input:**
```
3
STANDARD 1001 Popescu 250.0
PRECOMANDA 1002 Ionescu 400.0 2026-05-10
ABONAMENT 1003 Georgescu 120.0 6
```

**Exemplu output:**
```
STANDARD: 1001 Popescu, valoare: 250.00 lei
PRECOMANDA: 1002 Ionescu, valoare: 400.00 lei, livrare: 2026-05-10
ABONAMENT: 1003 Georgescu, valoare: 120.00 lei, luni: 6
```

**Hint:**
- Folosește sealed classes pentru a restricționa ierarhia.
- Folosește polimorfismul pentru a apela `procesare()` pe fiecare tip.

---

# Partea B — Interfață, tipuri speciale și reguli suplimentare

> Teste pentru această parte: `tests/partB/`

**Cerință:**
- Creează o interfață `ActiuneComanda` cu metodele:
  - `void proceseaza()` — procesează comanda conform regulilor tipului
  - `void afiseaza()` — afișează detalii despre comandă
  - `String tipComanda()` — returnează tipul comenzii
  - `default boolean esteSpeciala() { return false; }` — doar pentru precomenzi și abonamente returnează true
- Fă ca toate clasele de comenzi să implementeze această interfață.
- În `Main.java`, după procesare, afișează doar comenzile speciale (precomenzi și abonamente).

**Exemplu input:**
```
4
STANDARD 1001 Popescu 250.0
PRECOMANDA 1002 Ionescu 400.0 2026-05-10
ABONAMENT 1003 Georgescu 120.0 6
STANDARD 1004 Enache 300.0
```

**Exemplu output:**
```
PRECOMANDA: 1002 Ionescu, valoare: 400.00 lei, livrare: 2026-05-10
ABONAMENT: 1003 Georgescu, valoare: 120.00 lei, luni: 6
```

**Hint:**
- Folosește metoda `esteSpeciala()` pentru filtrare.
- Poți apela `afiseaza()` pentru output.

---

# Partea C — Sortare, filtrare și statistici

> Teste pentru această parte: `tests/partC/`

**Cerință:**
- Sortează comenzile descrescător după valoare.
- Afișează comanda cu valoarea maximă.
- Pentru fiecare tip de comandă, afișează suma valorilor și numărul de comenzi de acel tip.

**Exemplu input:**
```
6
STANDARD 1001 Popescu 250.0
PRECOMANDA 1002 Ionescu 400.0 2026-05-10
ABONAMENT 1003 Georgescu 120.0 6
STANDARD 1004 Enache 300.0
PRECOMANDA 1005 Vasilescu 500.0 2026-06-01
ABONAMENT 1006 Pop 90.0 12
```

**Exemplu output:**
```
PRECOMANDA: 1005 Vasilescu, valoare: 500.00 lei, livrare: 2026-06-01
PRECOMANDA: 1002 Ionescu, valoare: 400.00 lei, livrare: 2026-05-10
STANDARD: 1004 Enache, valoare: 300.00 lei
STANDARD: 1001 Popescu, valoare: 250.00 lei
ABONAMENT: 1006 Pop, valoare: 90.00 lei, luni: 12
ABONAMENT: 1003 Georgescu, valoare: 120.00 lei, luni: 6

Comanda cu valoarea maximă: PRECOMANDA: 1005 Vasilescu, valoare: 500.00 lei, livrare: 2026-06-01

Sume și număr comenzi pe tip:
STANDARD: suma = 550.00 lei, număr = 2
PRECOMANDA: suma = 900.00 lei, număr = 2
ABONAMENT: suma = 210.00 lei, număr = 2
```

**Hint:**
- Poți folosi un comparator pentru sortare.
- Folosește un map pentru statistici pe tip.

---

## Cum rulezi testele

Deschide `Test.java` și apasă **Run** în IntelliJ.
Directorul de lucru trebuie să fie rădăcina proiectului (`paoj-2026/`).
