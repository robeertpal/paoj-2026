# Baza de date Oracle pentru aibilet

Acest director contine scripturile SQL pentru pasul 1 al Etapei II PAO/PAOJ. Scripturile sunt scrise pentru Oracle Database si pot fi rulate in Oracle SQL Developer.

## Structura

- `utilizatori` modeleaza toate tipurile de utilizatori intr-o singura tabela, prin coloana `rol`: `CLIENT`, `ORGANIZATOR`, `ADMIN`, `AGENT_CHECK_IN`.
- `locatii` pastreaza locatiile si indica prin `suporta_locuri` daca pot gazdui evenimente cu locuri.
- `evenimente` pastreaza evenimentele cu si fara locuri prin coloana `tip_eveniment`: `SEATED` pentru evenimente cu locuri si `STANDING` pentru evenimente fara locuri.
- `tipuri_bilete_eveniment` pastreaza tipurile de bilete, preturile si stocurile disponibile pentru fiecare eveniment. Pentru evenimente fara locuri, disponibilitatea se calculeaza din aceasta tabela.
- `locuri_eveniment` pastreaza locurile pentru evenimentele cu locuri, impreuna cu tipul de bilet si statusul locului.
- `bilete` pastreaza biletele emise, codul unic al biletului, statusul si optional codul locului.
- `comenzi` si `comanda_bilete` modeleaza comenzile si relatia many-to-many dintre comenzi si bilete.
- `agent_event_assignments` pastreaza asignarile agentilor de check-in la evenimente.

## Ordine de rulare

1. Ruleaza `schema_oracle.sql`.
2. Ruleaza `seed_oracle.sql`.

`schema_oracle.sql` contine blocuri `DROP TABLE` la inceput, deci poate fi rulat de mai multe ori in acelasi schema Oracle. `seed_oracle.sql` trebuie rulat dupa schema, deoarece insereaza date de test pe structura proaspat creata.

## Conectare din Java

Datele de conectare la baza de date nu sunt hardcodate in codul Java. Aplicatia le citeste din `db.properties`, prin clasele `DatabaseConfig` si `DatabaseConnection`.

Aplicatia Java se conecteaza la Oracle prin JDBC, folosind driverul Oracle JDBC din `lib/ojdbc11.jar`. Repository-urile JDBC singleton implementeaza operatiile CRUD pentru entitatile principale si folosesc scripturile din acest director pentru structura relationala si datele initiale de test.
