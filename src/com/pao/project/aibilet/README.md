<p align="center">
  <img src="logo.png" alt="aibilet logo" width="160"/>
</p>

<h1 align="center">aibilet</h1>

<p align="center">
  <em>Platformă de e-ticketing pentru evenimente culturale și de divertisment</em>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-11%2B-orange?style=flat-square&logo=java" alt="Java"/>
  <img src="https://img.shields.io/badge/Persistenta-CSV-blue?style=flat-square" alt="CSV"/>
  <img src="https://img.shields.io/badge/Interfata-CLI-lightgrey?style=flat-square" alt="CLI"/>
  <img src="https://img.shields.io/badge/PAO-Proiect-darkgreen?style=flat-square" alt="PAO"/>
</p>

---

## Cuprins

1. [Prezentare generală](#1-prezentare-generală)
2. [Structura proiectului](#2-structura-proiectului)
3. [Acțiuni și interogări disponibile în sistem](#3-acțiuni-și-interogări-disponibile-în-sistem)
4. [Tipuri de obiecte din domeniu](#4-tipuri-de-obiecte-din-domeniu)
5. [Arhitectura orientată pe obiecte](#5-arhitectura-orientată-pe-obiecte)
6. [Excepții personalizate](#6-excepții-personalizate)
7. [Colecții și structuri de date](#7-colecții-și-structuri-de-date)
8. [Servicii Singleton](#8-servicii-singleton)
9. [Persistența datelor](#9-persistența-datelor)
10. [Instrucțiuni de compilare și rulare](#10-instrucțiuni-de-compilare-și-rulare)
11. [Conturi de test](#11-conturi-de-test)

---

## 1. Prezentare generală

**aibilet** este o aplicație Java de tip **Command-Line Interface** care modelează o platformă de e-ticketing pentru evenimente culturale, sportive și de divertisment. Aplicația permite administrarea evenimentelor, locațiilor, utilizatorilor, biletelor și comenzilor, oferind un flux complet pentru publicarea evenimentelor, achiziționarea biletelor și validarea acestora la intrare.

Sistemul gestionează mai multe categorii de utilizatori, fiecare având responsabilități și operații specifice:

| Rol | Responsabilități principale |
|-----|-----------------------------|
| **Administrator** | Administrează utilizatorii și locațiile disponibile în platformă |
| **Organizator** | Creează, modifică și anulează evenimente |
| **Client** | Consultă evenimentele disponibile și achiziționează bilete |
| **Agent Check-In** | Validează biletele la intrarea în eveniment |

Datele aplicației sunt salvate în fișiere CSV, astfel încât informațiile introduse să fie păstrate între rulări succesive ale programului.

---

## 2. Structura proiectului

Proiectul este organizat în pachete logice, conform cerințelor Etapei I:

```text
aibilet/
├── logo.png
├── README.md
│
├── locations.csv
├── users.csv
├── events.csv
├── event_seats.csv
├── event_ticket_types.csv
├── tickets.csv
├── orders.csv
├── agent_event_assignments.csv
│
└── src/com/pao/proiect/aibilet/
    ├── Main.java
    │
    ├── model/
    │   ├── Utilizator.java
    │   ├── Admin.java
    │   ├── Client.java
    │   ├── Organizator.java
    │   ├── Agent.java
    │   ├── AgentCheckIn.java
    │   ├── Eveniment.java
    │   ├── EvenimentCuLocuri.java
    │   ├── EvenimentFaraLocuri.java
    │   ├── HartaLocuri.java
    │   ├── LocEveniment.java
    │   ├── Bilet.java
    │   ├── BiletCuLoc.java
    │   ├── BiletFaraLoc.java
    │   ├── CodBilet.java
    │   ├── Comanda.java
    │   ├── Locatie.java
    │   ├── TipBiletEveniment.java
    │   ├── CategorieEveniment.java
    │   ├── StatusEveniment.java
    │   ├── StatusBilet.java
    │   ├── StatusLoc.java
    │   └── RolUtilizator.java
    │
    ├── service/
    │   ├── UtilizatorService.java
    │   ├── EvenimentService.java
    │   ├── LocatieService.java
    │   ├── TicketService.java
    │   ├── TicketingService.java
    │   └── ComandaService.java
    │
    └── exception/
        ├── AutentificareEsuataException.java
        ├── EntitateInexistentaException.java
        ├── LocIndisponibilException.java
        └── OperatieNepermisaException.java
```

---

## 3. Acțiuni și interogări disponibile în sistem

Sistemul permite următoarele acțiuni și interogări principale:

### 1. Autentificarea unui utilizator

Un utilizator existent se poate autentifica folosind datele sale de acces. În funcție de rolul utilizatorului, aplicația afișează meniul corespunzător.

### 2. Înregistrarea unui utilizator nou

Platforma permite înregistrarea utilizatorilor noi, în special a clienților și organizatorilor, direct din interfața aplicației.

### 3. Adăugarea unei locații

Administratorul poate introduce locații noi în sistem, precizând informații precum denumirea, orașul, adresa și capacitatea.

### 4. Listarea locațiilor disponibile

Sistemul poate afișa toate locațiile existente, astfel încât acestea să poată fi asociate ulterior unor evenimente.

### 5. Publicarea unui eveniment cu locuri numerotate

Organizatorul poate crea un eveniment cu locuri numerotate. Pentru acest tip de eveniment se generează o hartă de locuri reprezentată printr-o matrice bidimensională.

### 6. Publicarea unui eveniment fără locuri numerotate

Organizatorul poate crea evenimente cu acces general, precum festivaluri sau evenimente în aer liber, pentru care se gestionează doar numărul de bilete disponibile pe fiecare categorie.

### 7. Modificarea unui eveniment existent

Organizatorul poate modifica informațiile asociate unui eveniment publicat de el, precum titlul, descrierea, data, tipurile de bilete sau prețurile.

### 8. Anularea unui eveniment

Un eveniment poate fi marcat drept anulat. Statusul acestuia este actualizat și salvat persistent.

### 9. Căutarea și filtrarea evenimentelor

Clienții pot consulta evenimentele disponibile prin mai multe criterii:

- listarea tuturor evenimentelor;
- căutarea după titlu;
- filtrarea după oraș;
- filtrarea după categorie;
- afișarea cronologică a evenimentelor.

### 10. Achiziționarea unui bilet cu loc

Clientul poate selecta un eveniment cu locuri numerotate, poate consulta harta locurilor și poate alege un loc disponibil. În urma achiziției se emite un bilet cu un cod unic.

### 11. Achiziționarea unui bilet fără loc

Pentru evenimentele fără locuri numerotate, clientul poate selecta tipul de bilet dorit și numărul de bilete, în limita stocului disponibil.

### 12. Vizualizarea comenzilor unui client

Clientul poate consulta comenzile plasate și biletele asociate acestora.

### 13. Asignarea unui agent de check-in la un eveniment

Organizatorul poate desemna un agent de check-in pentru unul sau mai multe evenimente.

### 14. Validarea unui bilet la intrare

Agentul de check-in introduce codul biletului, iar sistemul verifică existența acestuia, statusul biletului și dreptul agentului de a valida accesul la evenimentul respectiv.

### 15. Administrarea utilizatorilor

Administratorul poate lista, adăuga și șterge utilizatori din sistem.

---

## 4. Tipuri de obiecte din domeniu

Aplicația modelează următoarele tipuri principale de obiecte:

| # | Clasă | Tip | Descriere |
|---|-------|-----|-----------|
| 1 | `Utilizator` | abstractă | Clasa de bază pentru utilizatorii platformei |
| 2 | `Admin` | concretă | Utilizator cu drepturi administrative |
| 3 | `Client` | concretă | Utilizator care achiziționează bilete |
| 4 | `Organizator` | concretă | Utilizator care creează și administrează evenimente |
| 5 | `Agent` | abstractă | Clasă de bază pentru agenții platformei |
| 6 | `AgentCheckIn` | concretă | Agent responsabil cu validarea biletelor |
| 7 | `Eveniment` | abstractă | Clasa de bază pentru evenimente |
| 8 | `EvenimentCuLocuri` | concretă | Eveniment cu hartă de locuri numerotate |
| 9 | `EvenimentFaraLocuri` | concretă | Eveniment cu acces general, fără locuri numerotate |
| 10 | `HartaLocuri` | concretă | Reprezentarea matriceală a locurilor unui eveniment |
| 11 | `LocEveniment` | concretă | Un loc individual din cadrul unei hărți de locuri |
| 12 | `Bilet` | abstractă | Clasa de bază pentru biletele emise |
| 13 | `BiletCuLoc` | concretă | Bilet asociat unui loc specific |
| 14 | `BiletFaraLoc` | concretă | Bilet fără loc individual alocat |
| 15 | `CodBilet` | imutabilă | Identificator unic pentru un bilet |
| 16 | `Comanda` | concretă | Reprezintă o comandă efectuată de un client |
| 17 | `Locatie` | concretă | Reprezintă spațiul în care se desfășoară un eveniment |
| 18 | `TipBiletEveniment` | concretă | Definește prețul și stocul unui tip de bilet |

---

## 5. Arhitectura orientată pe obiecte

Aplicația respectă principiile programării orientate pe obiecte: încapsulare, moștenire, abstractizare și polimorfism.

### 5.1 Încapsulare

Clasele de domeniu folosesc atribute `private` sau `protected`, iar accesul la acestea se realizează prin metode publice de tip getter și setter, acolo unde modificarea este permisă.

Exemplu conceptual:

```java
private String titlu;

public String getTitlu() {
    return titlu;
}

public void setTitlu(String titlu) {
    this.titlu = titlu;
}
```

### 5.2 Ierarhii de moștenire

Proiectul conține mai multe ierarhii de clase.

#### Ierarhia utilizatorilor

```text
Utilizator
├── Admin
├── Client
├── Organizator
└── Agent
    └── AgentCheckIn
```

Această ierarhie are cel puțin două niveluri și permite tratarea polimorfică a utilizatorilor platformei.

#### Ierarhia evenimentelor

```text
Eveniment
├── EvenimentCuLocuri
└── EvenimentFaraLocuri
```

Această ierarhie permite definirea unui comportament comun pentru toate evenimentele și particularizarea lui în funcție de tipul evenimentului.

#### Ierarhia biletelor

```text
Bilet
├── BiletCuLoc
└── BiletFaraLoc
```

Această structură diferențiază biletele pentru evenimente cu locuri numerotate de biletele pentru evenimente cu acces general.

### 5.3 Clase abstracte

Proiectul utilizează clase abstracte pentru entitățile care definesc comportament comun, dar care nu trebuie instanțiate direct.

Exemple:

- `Utilizator`
- `Agent`
- `Eveniment`
- `Bilet`

Clasa `Utilizator` definește metoda abstractă:

```java
public abstract RolUtilizator getRol();
```

Astfel, fiecare subclasă concretă este obligată să specifice rolul său în sistem.

Clasa `Eveniment` definește comportament comun pentru evenimente și permite implementări diferite pentru calculul disponibilității:

```java
public abstract boolean esteCuLocuri();

public abstract int getDisponibilitate();
```

### 5.4 Clasă imutabilă

Clasa `CodBilet` este o clasă imutabilă. Aceasta este declarată `final`, are atribut final, nu oferă metode setter și este inițializată complet prin constructor.

```java
public final class CodBilet {
    private final String valoare;

    public CodBilet(String valoare) {
        if (valoare == null || valoare.trim().isEmpty()) {
            throw new IllegalArgumentException("Codul biletului nu poate fi null sau gol.");
        }
        this.valoare = valoare;
    }

    public String getValoare() {
        return valoare;
    }

    @Override
    public String toString() {
        return valoare;
    }
}
```

Această clasă este folosită pentru reprezentarea codului unic al unui bilet.

### 5.5 Metode suprascrise

Mai multe clase suprascriu metodele `toString()`, `equals()` și `hashCode()`, pentru o reprezentare clară a obiectelor și pentru compararea corectă a instanțelor.

| Clasă | `toString()` | `equals()` | `hashCode()` |
|-------|:------------:|:----------:|:------------:|
| `Utilizator` | da | da | da |
| `Eveniment` | da | da | da |
| `Locatie` | da | da | da |
| `CodBilet` | da | da | da |
| `Comanda` | da | — | — |

Prin aceste suprascrieri, obiectele pot fi afișate, comparate și folosite în colecții într-un mod controlat.

---

## 6. Excepții personalizate

Proiectul definește excepții proprii pentru tratarea situațiilor specifice domeniului aplicației.

| Excepție | Scop |
|----------|------|
| `AutentificareEsuataException` | Semnalează date de autentificare incorecte |
| `EntitateInexistentaException` | Semnalează că o entitate căutată nu există |
| `LocIndisponibilException` | Semnalează imposibilitatea rezervării unui loc |
| `OperatieNepermisaException` | Semnalează o acțiune neautorizată sau incompatibilă |

Excepțiile sunt aruncate în clasele de serviciu și tratate în `Main.java`, astfel încât programul să nu se oprească necontrolat în cazul unor date invalide.

Exemplu:

```java
try {
    Bilet bilet = ticketingService.cumparaBiletCuLoc(clientId, evenimentId, codLoc);
    System.out.println("Biletul a fost cumpărat cu succes.");
} catch (LocIndisponibilException e) {
    System.out.println("Eroare: " + e.getMessage());
} catch (OperatieNepermisaException e) {
    System.out.println("Eroare: " + e.getMessage());
} catch (EntitateInexistentaException e) {
    System.out.println("Eroare: " + e.getMessage());
}
```

---

## 7. Colecții și structuri de date

Proiectul utilizează mai multe structuri de date pentru gestionarea entităților.

### 7.1 Liste

În anumite operații, obiectele sunt adăugate în liste pentru sortare, filtrare sau afișare.

Exemplu: în `EvenimentService`, evenimentele sunt adăugate într-un `ArrayList`, apoi sunt sortate cronologic.

```java
List<Eveniment> lista = new ArrayList<>();

for (int i = 0; i < numarEvenimente; i++) {
    lista.add(evenimente[i]);
}

Collections.sort(lista);
```

### 7.2 Map pentru indexare

Serviciile folosesc structuri de tip `Map` pentru indexarea rapidă a entităților după identificatori sau câmpuri unice.

Exemple:

- `Map<Integer, Utilizator>` pentru indexarea utilizatorilor după ID;
- `Map<String, Utilizator>` pentru indexarea utilizatorilor după username.

Acest tip de colecție permite căutarea eficientă a utilizatorilor în cadrul operațiilor de autentificare și administrare.

### 7.3 Colecție sortată / sortare prin Comparable

Clasa `Eveniment` implementează interfața `Comparable<Eveniment>`, ceea ce permite sortarea evenimentelor după data de început, data de final și ID.

```java
@Override
public int compareTo(Eveniment other) {
    int cmp = this.dataOraInceput.compareTo(other.dataOraInceput);
    if (cmp != 0) {
        return cmp;
    }

    cmp = this.dataOraFinal.compareTo(other.dataOraFinal);
    if (cmp != 0) {
        return cmp;
    }

    return Integer.compare(this.id, other.id);
}
```

Astfel, evenimentele afișate utilizatorilor sunt prezentate într-o ordine cronologică.

### 7.4 Tablouri și matrice

Proiectul utilizează și tablouri pentru stocarea internă a entităților, precum și matrice bidimensionale pentru reprezentarea locurilor dintr-o sală.

Exemplu:

```java
private final Eveniment[] evenimente = new Eveniment[1000];
private int numarEvenimente = 0;
```

Pentru evenimentele cu locuri, harta este modelată printr-o matrice:

```java
private LocEveniment[][] locuri;
```

Această abordare este potrivită pentru reprezentarea rândurilor și coloanelor dintr-o sală.

---

## 8. Servicii Singleton

Operațiile principale ale sistemului sunt grupate în clase de serviciu. Fiecare serviciu este implementat folosind pattern-ul Singleton, având constructor privat și o metodă statică `getInstance()`.

Exemplu:

```java
public class EvenimentService {
    private static final EvenimentService INSTANCE = new EvenimentService();

    private EvenimentService() {
        load();
    }

    public static EvenimentService getInstance() {
        return INSTANCE;
    }
}
```

Serviciile principale ale aplicației sunt:

| Serviciu | Responsabilitate |
|----------|------------------|
| `UtilizatorService` | Gestionarea utilizatorilor, autentificare și asignarea agenților |
| `EvenimentService` | Gestionarea evenimentelor și a datelor asociate acestora |
| `LocatieService` | Gestionarea locațiilor |
| `TicketService` | Emiterea, căutarea și validarea biletelor |
| `ComandaService` | Crearea și listarea comenzilor |
| `TicketingService` | Orchestrarea fluxului complet de cumpărare a biletelor |

Fiecare serviciu expune operații de tip adăugare, ștergere, căutare și listare, în funcție de responsabilitatea sa.

---

## 9. Persistența datelor

Persistența este realizată prin fișiere CSV, folosind separatorul `;`. La pornirea aplicației, serviciile încarcă datele existente, iar după operațiile care modifică starea sistemului, datele sunt salvate automat.

Fișierele principale utilizate sunt:

| Fișier | Conținut |
|--------|----------|
| `users.csv` | utilizatorii platformei |
| `locations.csv` | locațiile disponibile |
| `events.csv` | evenimentele publicate |
| `event_seats.csv` | locurile asociate evenimentelor cu locuri numerotate |
| `event_ticket_types.csv` | tipurile de bilete asociate evenimentelor |
| `tickets.csv` | biletele emise |
| `orders.csv` | comenzile plasate |
| `agent_event_assignments.csv` | asignările agenților la evenimente |

Exemplu de structură pentru `users.csv`:

```text
tip;id;username;parola;nume;prenume;email;telefon;extra
ORGANIZATOR;2;robert.events;parola;Robert;Popescu;robert@example.com;0722000000;Events SRL|Demo Org
AGENT_CHECK_IN;3;agent.ion;parola;Ion;Vasile;ion@example.com;0733000000;1,2,3
```

Exemplu de structură pentru `events.csv`:

```text
id;tip;titlu;descriere;categorie;dataInceput;dataFinal;status;locatieId;organizatorId;numeOrganizatie
1;CU_LOCURI;Rock Legends;Concert rock;CONCERT;2026-06-15 20:00;2026-06-15 23:00;PROGRAMAT;1;2;Events SRL
```

Exemplu de structură pentru `event_seats.csv`:

```text
evenimentId;rand;coloana;cod;tipBilet;status
1;0;0;A1;VIP;LIBER
1;0;1;A2;STANDARD;VANDUT
```

---

## 10. Instrucțiuni de compilare și rulare

### Cerințe

Pentru rularea aplicației este necesară o versiune de Java 11 sau mai nouă.

### Compilare din terminal

Din directorul rădăcină al proiectului:

```bash
find src -name "*.java" | xargs javac -d out
```

### Rulare din terminal

```bash
java -cp out com.pao.proiect.aibilet.Main
```

### Rulare din IntelliJ IDEA

1. Deschideți directorul proiectului în IntelliJ IDEA.
2. Marcați directorul `src` ca **Sources Root**.
3. Rulați clasa `Main.java`.

### Observație privind fișierele CSV

Fișierele CSV sunt citite și scrise în directorul de lucru curent al aplicației. În cazul rulării din IntelliJ IDEA, acesta este de obicei directorul rădăcină al proiectului.

---

## 11. Conturi de test

Dacă fișierele CSV sunt deja prezente, pot fi utilizate conturile existente din `users.csv`.

Dacă fișierele nu există sau sunt goale, aplicația permite crearea unor conturi noi din meniul de înregistrare. Pentru un cont de administrator, se poate introduce manual o linie corespunzătoare în fișierul `users.csv`, folosind tipul `ADMIN`.

---

<p align="center">
  <sub>
    Proiect realizat în cadrul cursului <strong>Programare Avansată pe Obiecte</strong> de <strong>Pal Robert-Attila</strong>.
  </sub>
</p>