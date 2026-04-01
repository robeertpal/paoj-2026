# Exercițiul 1 — Sistem simplu de tracking pentru comenzi eCommerce

> **Pachet:** `com.pao.laboratory07.exercise1`
> **Timp estimat:** ~35 min · **Teste automate:** da (`Test.java`)

---

## Scop

Acest exercițiu te familiarizează cu structura laboratorului și testarea automată. Vei implementa un sistem simplu de urmărire a comenzilor pentru un magazin online, folosind un singur fișier `Main.java` și un enum pentru stări.

---

## Cerință generală

Creează în pachetul `com.pao.laboratory07.exercise1`:
- un enum `StareComanda` cu stările posibile ale unei comenzi;
- un `Main.java` care citește comenzile de la tastatură, procesează tranzițiile și afișează rezultatele conform cerințelor.

---

## Stările comenzii

O comandă poate fi în una din următoarele stări:
- `PLASATA` (comanda a fost plasată)
- `PROCESATA` (comanda este procesată)
- `EXPEDIATA` (comanda a fost expediată)
- `LIVRATA` (comanda a fost livrată — stare finală)
- `ANULATA` (comanda a fost anulată — stare finală)

---

## Input și Output

- Prima linie: starea inițială (`PLASATA`, `PROCESATA`, `EXPEDIATA`, `LIVRATA`, `ANULATA`)
- Următoarele linii: comenzi (`next`, `cancel`, `undo`) până la `QUIT`
- Pentru fiecare comandă, afișează noua stare sau un mesaj special dacă nu se poate face tranziția

---

## Partea A — Tranziții de bază

- Comenzi acceptate: `next`, `QUIT`
- Tranziții:
  - `PLASATA` → `PROCESATA` → `EXPEDIATA` → `LIVRATA` (stare finală)
  - Orice altă comandă nu schimbă starea
- La fiecare pas, afișează starea curentă

**Exemplu:**
```
Input:           Output:
PLASATA          PLASATA
next             PROCESATA
next             EXPEDIATA
next             LIVRATA
QUIT
```

---

## Partea B — Anulare și stări finale

- Comandă suplimentară: `cancel`
- Dacă primești `cancel` din orice stare non-finală, treci la `ANULATA`
- Dacă ești într-o stare finală (`LIVRATA` sau `ANULATA`), orice comandă (`next`, `cancel`) afișează: `Comanda este in stare finala.`
- Comenzile continuă să fie citite până la `QUIT`, dar sunt ignorate

**Exemplu:**
```
Input:           Output:
LIVRATA          LIVRATA
next             Comanda este in stare finala.
cancel           Comanda este in stare finala.
QUIT
```

---

## Partea C — Undo (revenire la starea anterioară)

- Comandă suplimentară: `undo`
- `undo` revine la starea anterioară (dacă există una, altfel rămâne la starea curentă)
- `undo` funcționează și dacă starea curentă este finală (ieși din starea finală!)
- La `undo` nu se afișează mesaj de stare finală, ci doar noua stare

**Exemplu:**
```
Input:           Output:
PLASATA          PLASATA
next             PROCESATA
next             EXPEDIATA
undo             PROCESATA
undo             PLASATA
QUIT
```

---

## Hints
- Folosește un enum pentru stări și metode simple pentru tranziții
- Pentru `undo`, poți folosi o listă sau un stack pentru a ține istoricul stărilor
- Nu folosi funcționalități avansate — păstrează codul clar și simplu

---

## Cum rulezi testele

Deschide `Test.java` și apasă **Run** în IntelliJ.
Directorul de lucru trebuie să fie rădăcina proiectului (`paoj-2026/`).
