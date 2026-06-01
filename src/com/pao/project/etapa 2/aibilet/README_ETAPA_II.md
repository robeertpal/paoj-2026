# Etapa II - Persistenta JDBC si Audit CSV

Proiectul `aibilet` este o aplicatie Java CLI pentru e-ticketing. In Etapa II, persistenta datelor principale este realizata exclusiv in Oracle Database folosind JDBC.

CSV-urile vechi de date au fost eliminate din proiect. Singurul CSV pastrat este `audit.csv`, folosit de `AuditService` pentru jurnalizarea actiunilor.

## Cerinte Acoperite

- Baza de date relationala.
- Conectare la baza de date prin JDBC.
- CRUD pentru cel putin 4 clase.
- Repository-uri singleton pentru citire si scriere.
- Audit CSV pentru actiuni, cu formatul `nume_actiune,timestamp`.

## Baza De Date

Proiectul foloseste Oracle Database local, administrat prin Oracle SQL Developer.

Scripturile SQL se afla in directorul `database`:

- `schema.sql` in radacina proiectului, pentru conformitatea cu baremul
- `database/schema_oracle.sql`
- `database/seed_oracle.sql`

Ordinea de rulare in Oracle SQL Developer este:

1. `schema_oracle.sql`
2. `seed_oracle.sql`

## Configurare JDBC

Conectarea la baza de date se face prin clasa `DatabaseConnection`, pe baza proprietatilor incarcate din `db.properties`.

Driverul folosit este `ojdbc11.jar`.

Fisierul `resources/db.properties` contine configuratia ceruta pentru Etapa II. Clasa `DatabaseConfig` cauta configuratia in classpath, apoi in `resources/db.properties`, apoi foloseste ca fallback `db.properties` din radacina proiectului. Valorile `db.url`, `db.user` si `db.password` nu sunt hardcodate in codul Java. Pentru configurare exista si fisierul:

- `database/db.properties.example`

## Tabele Principale

Schema Oracle include urmatoarele tabele principale:

- `utilizatori`
- `locatii`
- `evenimente`
- `tipuri_bilete_eveniment`
- `locuri_eveniment`
- `bilete`
- `comenzi`
- `comanda_bilete`
- `agent_event_assignments`

## Repository-uri JDBC

Au fost implementate repository-uri JDBC singleton pentru entitatile principale si auxiliare:

- `LocatieRepository`
- `UtilizatorRepository`
- `EvenimentRepository`
- `TipBiletEvenimentRepository`
- `LocEvenimentRepository`
- `BiletRepository`
- `ComandaRepository`
- `AgentEventAssignmentRepository`

Repository-urile folosesc `PreparedStatement`, `try-with-resources` si mapari explicite intre randurile din Oracle si modelele Java.

Interfata generica oficiala este implementata in `src/com/pao/proiect/aibilet/repository/Repository.java`:

```java
public interface Repository<T, ID> {
    T create(T entity);
    void save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void update(T entity);
    void delete(ID id);
}
```

Repository-urile concrete implementeaza aceasta interfata unica. Operatia de stergere este expusa prin `delete(ID id)`.

## Servicii Migrate Pe JDBC

Serviciile principale folosesc repository-uri JDBC:

- `LocatieService`
- `UtilizatorService`
- `EvenimentService`
- `TicketService`
- `ComandaService`

`TicketingService` orchestreaza acum serviciile migrate pe JDBC si nu mai gestioneaza direct persistenta.

## CRUD Implementat

Proiectul implementeaza CRUD pentru mai mult de 4 clase:

- `Locatie`
- `Utilizator`
- `Eveniment`
- `TipBiletEveniment`
- `LocEveniment`
- `Bilet`
- `Comanda`
- `AgentEventAssignment`

Pentru `AgentEventAssignment`, tabela are cheie primara compusa si operatii specifice de creare, cautare si stergere a asignarilor.

## Interogari SQL Cu JOIN

Proiectul include interogari JDBC cu `JOIN` mapate in DTO-uri:

- `BiletRepository.findBileteClientCuEvenimentSiLocatie(int clientId)` returneaza `BiletClientView` si foloseste `bilete JOIN evenimente JOIN locatii LEFT JOIN locuri_eveniment`.
- `ComandaRepository.findComenziClientCuNumarBilete(int clientId)` returneaza `ComandaClientView` si foloseste `comenzi JOIN comanda_bilete`.
- `EvenimentRepository.findEvenimenteCuNumarBileteVandute()` returneaza `EvenimentVanzariView` si foloseste `evenimente LEFT JOIN bilete`.

Aceste metode sunt expuse prin service-uri si sunt apelabile din meniurile aplicatiei: `Biletele mele`, `Comenzile mele` si raportul de vanzari din meniul admin.

## Audit CSV

Auditul este implementat prin clasa Singleton `AuditService`. Metoda `logAction` este `synchronized`, iar fisierul `audit.csv` este scris in mod append, fara suprascriere la fiecare rulare.

Fisierul generat este:

- `audit.csv`

Formatul fisierului este:

```csv
nume_actiune,timestamp
```

Auditul este scris dupa operatii JDBC reusite. Pot aparea loguri indirecte, deoarece repository-urile se apeleaza intre ele pentru validari si mapari. In plus, actiunile principale apelate din meniurile aplicatiei sunt logate explicit, astfel incat in `audit.csv` apar actiuni clare de utilizator precum `login`, `inregistrare_client`, `afiseaza_toate_evenimentele`, `cauta_eveniment_dupa_titlu`, `filtreaza_evenimente_dupa_oras`, `cumpara_bilet_cu_loc`, `afiseaza_biletele_mele`, `afiseaza_comenzile_mele`, `schimba_parola`, `adauga_eveniment_fara_locuri`, `modifica_eveniment`, `anuleaza_eveniment`, `sterge_eveniment`, `raport_vanzari_evenimente` si `asigneaza_agent_checkin`.

## Tranzactie JDBC Explicita

Operatia de creare comanda din `ComandaRepository.create(Comanda entity)` este executata intr-o tranzactie JDBC explicita. Aceasta insereaza o inregistrare in tabela `comenzi` si una sau mai multe inregistrari in tabela `comanda_bilete`. Metoda foloseste `connection.setAutoCommit(false)` inaintea operatiilor, `commit()` dupa succes si `rollback()` la eroare. La final, starea initiala `autoCommit` este restaurata.

## Clase De Test

Clasele manuale de test `*Test.java` au fost folosite doar pentru verificare locala in timpul dezvoltarii si au fost eliminate din varianta finala a proiectului.

Functionalitatile cerute pentru Etapa II sunt demonstrabile din `Main.java`, prin meniurile CLI ale aplicatiei:

- meniul principal;
- meniul client;
- meniul organizator;
- meniul admin;
- meniul agent check-in.

Verificarea finala a fost realizata prin rularea efectiva a operatiilor principale din aplicatie, inclusiv autentificare, inregistrare, listare/cautare/filtrare evenimente, cumparare bilete, afisare bilete/comenzi prin JOIN, raport vanzari, tranzactie comanda si audit CSV.

## Fluxuri Testate Din Main

Au fost testate fluxuri reale din aplicatia CLI:

- Login utilizatori din baza de date.
- Listare evenimente din baza de date.
- Cumparare bilet fara loc.
- Cumparare bilet cu loc.
- Creare comanda.
- Actualizare stoc pentru evenimente fara loc.
- Actualizare loc `VANDUT` / `LIBER` pentru evenimente cu locuri.
- Validare bilet la check-in.
- Listare comenzi si bilete din baza de date.

## Observatii Tehnice

- Proiectul foloseste JDBC simplu, fara Spring sau Hibernate.
- Interogarile folosesc `PreparedStatement`.
- Gestionarea resurselor se face cu `try-with-resources`.
- Fluxurile principale ale aplicatiei folosesc Oracle Database ca sursa de date.
- `audit.csv` este singurul fisier CSV pastrat si este scris exclusiv de `AuditService`.
- Parolele reale si datele sensibile de conectare nu trebuie incluse in documentatie sau publicate in repository.

## Compilare Si Rulare

Compilare:

```bash
find src -name '*.java' -print > /tmp/aibilet_sources.txt && javac -cp lib/ojdbc11.jar -d out @/tmp/aibilet_sources.txt
```

Rulare:

```bash
java -cp out:lib/ojdbc11.jar com.pao.proiect.aibilet.Main
```
