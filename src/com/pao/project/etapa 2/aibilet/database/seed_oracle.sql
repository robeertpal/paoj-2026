-- Date initiale pentru testare in Oracle SQL Developer.
-- Se ruleaza dupa schema_oracle.sql.

INSERT INTO utilizatori (id, rol, username, parola, nume, prenume, email, telefon, nume_organizatie)
VALUES (1, 'ADMIN', 'admin', 'admin', 'Pal', 'Robert-Attila', 'robert@aibilet.ro', '0265283188', NULL);

INSERT INTO utilizatori (id, rol, username, parola, nume, prenume, email, telefon, nume_organizatie)
VALUES (2, 'ORGANIZATOR', 'andrei', 'andrei', 'Munteanu', 'Andrei', 'andrei@untold.com', '0752172618', 'UNTOLD');

INSERT INTO utilizatori (id, rol, username, parola, nume, prenume, email, telefon, nume_organizatie)
VALUES (3, 'ORGANIZATOR', 'david', 'david', 'Ardelean', 'David', 'david@themotans.ro', '0757628732', 'THE MOTANS');

INSERT INTO utilizatori (id, rol, username, parola, nume, prenume, email, telefon, nume_organizatie)
VALUES (4, 'CLIENT', 'angelo', 'angelo', 'Eremia', 'Angelo', 'angelo@icloud.com', '076282732', NULL);

INSERT INTO utilizatori (id, rol, username, parola, nume, prenume, email, telefon, nume_organizatie)
VALUES (5, 'AGENT_CHECK_IN', 'alex', 'alex', 'Moldovan', 'Alex', 'alex@gmail.com', '077672176', NULL);

INSERT INTO utilizatori (id, rol, username, parola, nume, prenume, email, telefon, nume_organizatie)
VALUES (6, 'CLIENT', 'robi', 'robi', 'Pal', 'Robert-Attila', 'robe.rt@icloud.com', '0743592928', NULL);

INSERT INTO locatii (id, denumire, oras, adresa, suporta_locuri)
VALUES (1, 'Stadionul Cluj Arena', 'Cluj-Napoca', 'Aleea Stadionului', 0);

INSERT INTO locatii (id, denumire, oras, adresa, suporta_locuri)
VALUES (2, 'Sala Palatului', 'Bucuresti', 'Strada Ion Campineanu 28', 1);

INSERT INTO evenimente (
    id, tip_eveniment, titlu, descriere, categorie, data_ora_inceput, data_ora_final,
    status, locatie_id, organizator_id, nume_organizatie_organizator
) VALUES (
    1,
    'STANDING',
    'UNTOLD',
    'Festivalul numarul 3 in lume incepe un nou capitol, alaturi de cei mai mari artisti si DJ.',
    'FESTIVAL',
    TO_TIMESTAMP('2026-08-06 00:00:00', 'YYYY-MM-DD HH24:MI:SS'),
    TO_TIMESTAMP('2026-08-09 00:00:00', 'YYYY-MM-DD HH24:MI:SS'),
    'PROGRAMAT',
    1,
    2,
    'UNTOLD'
);

INSERT INTO evenimente (
    id, tip_eveniment, titlu, descriere, categorie, data_ora_inceput, data_ora_final,
    status, locatie_id, organizator_id, nume_organizatie_organizator
) VALUES (
    2,
    'SEATED',
    'The Motans - Grand Concert',
    'Concert live The Motans cu cele mai cunoscute piese, intr-un spectacol dedicat fanilor.',
    'CONCERT',
    TO_TIMESTAMP('2026-06-14 20:00:00', 'YYYY-MM-DD HH24:MI:SS'),
    TO_TIMESTAMP('2026-06-14 22:30:00', 'YYYY-MM-DD HH24:MI:SS'),
    'PROGRAMAT',
    2,
    3,
    'THE MOTANS'
);

INSERT INTO tipuri_bilete_eveniment (id, eveniment_id, nume, pret, stoc_total, stoc_disponibil)
VALUES (1, 1, 'General Access', 999.99, 20000, 20000);

INSERT INTO tipuri_bilete_eveniment (id, eveniment_id, nume, pret, stoc_total, stoc_disponibil)
VALUES (2, 1, 'VIP', 1999.99, 2500, 2498);

INSERT INTO tipuri_bilete_eveniment (id, eveniment_id, nume, pret, stoc_total, stoc_disponibil)
VALUES (3, 2, 'VIP', 250.00, 20, 18);

INSERT INTO tipuri_bilete_eveniment (id, eveniment_id, nume, pret, stoc_total, stoc_disponibil)
VALUES (4, 2, 'STANDARD', 150.00, 68, 68);

INSERT INTO locuri_eveniment (id, eveniment_id, rand, coloana, cod, tip_bilet, status)
VALUES (1, 2, 0, 0, 'A1', 'VIP', 'LIBER');

INSERT INTO locuri_eveniment (id, eveniment_id, rand, coloana, cod, tip_bilet, status)
VALUES (2, 2, 0, 1, 'A2', 'VIP', 'LIBER');

INSERT INTO locuri_eveniment (id, eveniment_id, rand, coloana, cod, tip_bilet, status)
VALUES (3, 2, 0, 2, 'A3', 'VIP', 'LIBER');

INSERT INTO locuri_eveniment (id, eveniment_id, rand, coloana, cod, tip_bilet, status)
VALUES (4, 2, 0, 3, 'A4', 'VIP', 'LIBER');

INSERT INTO locuri_eveniment (id, eveniment_id, rand, coloana, cod, tip_bilet, status)
VALUES (5, 2, 0, 4, 'A5', 'VIP', 'VANDUT');

INSERT INTO locuri_eveniment (id, eveniment_id, rand, coloana, cod, tip_bilet, status)
VALUES (6, 2, 0, 5, 'A6', 'VIP', 'VANDUT');

INSERT INTO locuri_eveniment (id, eveniment_id, rand, coloana, cod, tip_bilet, status)
VALUES (7, 2, 0, 6, 'A7', 'VIP', 'LIBER');

INSERT INTO locuri_eveniment (id, eveniment_id, rand, coloana, cod, tip_bilet, status)
VALUES (8, 2, 1, 0, 'B1', 'STANDARD', 'LIBER');

INSERT INTO locuri_eveniment (id, eveniment_id, rand, coloana, cod, tip_bilet, status)
VALUES (9, 2, 1, 1, 'B2', 'STANDARD', 'LIBER');

INSERT INTO locuri_eveniment (id, eveniment_id, rand, coloana, cod, tip_bilet, status)
VALUES (10, 2, 1, 2, 'B3', 'STANDARD', 'LIBER');

INSERT INTO bilete (id, tip, cod_bilet, eveniment_id, client_id, seat_code, tip_bilet, pret, status)
VALUES (1, 'FARA_LOC', 'UN-1024', 1, 4, NULL, 'VIP', 1999.99, 'FOLOSIT');

INSERT INTO bilete (id, tip, cod_bilet, eveniment_id, client_id, seat_code, tip_bilet, pret, status)
VALUES (2, 'FARA_LOC', 'UN-1025', 1, 4, NULL, 'VIP', 1999.99, 'PLATIT');

INSERT INTO bilete (id, tip, cod_bilet, eveniment_id, client_id, seat_code, tip_bilet, pret, status)
VALUES (3, 'CU_LOC', 'TH-1024', 2, 4, 'A5', 'VIP', 250.00, 'PLATIT');

INSERT INTO bilete (id, tip, cod_bilet, eveniment_id, client_id, seat_code, tip_bilet, pret, status)
VALUES (4, 'CU_LOC', 'TH-1025', 2, 4, 'A6', 'VIP', 250.00, 'PLATIT');

INSERT INTO comenzi (id, client_id, total, created_at)
VALUES (1, 4, 3999.98, TO_TIMESTAMP('2026-04-26 08:34:52.359943', 'YYYY-MM-DD HH24:MI:SS.FF'));

INSERT INTO comenzi (id, client_id, total, created_at)
VALUES (2, 4, 250.00, TO_TIMESTAMP('2026-04-26 08:36:15.547465', 'YYYY-MM-DD HH24:MI:SS.FF'));

INSERT INTO comenzi (id, client_id, total, created_at)
VALUES (3, 4, 250.00, TO_TIMESTAMP('2026-04-26 08:36:15.552233', 'YYYY-MM-DD HH24:MI:SS.FF'));

INSERT INTO comanda_bilete (comanda_id, bilet_id)
VALUES (1, 1);

INSERT INTO comanda_bilete (comanda_id, bilet_id)
VALUES (1, 2);

INSERT INTO comanda_bilete (comanda_id, bilet_id)
VALUES (2, 3);

INSERT INTO comanda_bilete (comanda_id, bilet_id)
VALUES (3, 4);

INSERT INTO agent_event_assignments (agent_id, eveniment_id, assigned_at)
VALUES (5, 1, TO_TIMESTAMP('2026-04-26 08:00:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO agent_event_assignments (agent_id, eveniment_id, assigned_at)
VALUES (5, 2, TO_TIMESTAMP('2026-04-26 08:00:00', 'YYYY-MM-DD HH24:MI:SS'));

COMMIT;
