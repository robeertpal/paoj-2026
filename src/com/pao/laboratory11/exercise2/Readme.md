# Exercise 2 - Raportare stream avansata

## Scenariu

Pornind de la rezultatele din exercitiul 1, trebuie construit un modul de raportare analitica pentru tranzactii, cu agregari pe luni, conturi si canale.

## Import din exercitiul 1

Refoloseste cel putin un tip din exercitiul 1 (de exemplu modelul tranzactiei sau enum-uri asociate) prin import explicit.

## Cerinta

Construieste rapoarte deterministe folosind Stream API:
- agregare pe luna;
- agregare pe cont;
- clasament canale.

## Structura inputului

- `N`
- N linii tranzactii
- `Q`
- Q comenzi:
  - `REPORT_MONTH <yyyy-MM>`
  - `REPORT_ACCOUNT <accountId>`
  - `TOP_CHANNELS <k>`

## Structura outputului

Fiecare comanda are output fix, ordonat determinist, cu doua zecimale pentru valori monetare.

## Exemplu

Input:
```
4
1 200.00 2026-05-01 RO WEB A1
2 300.00 2026-05-01 RO ATM A1
3 50.00 2026-05-10 NL APP A2
4 90.00 2026-06-02 RO WEB A1
2
REPORT_MONTH 2026-05
TOP_CHANNELS 2
```

Output (exemplu orientativ):
```
MONTH 2026-05 total=550.00 count=3
WEB 2
ATM 1
```

## Hint-uri

- Foloseste `groupingBy` cu colectori downstream.
- Stabileste o ordine finala clara pentru ties.
- Nu combina logica de parsing cu logica de raportare.
