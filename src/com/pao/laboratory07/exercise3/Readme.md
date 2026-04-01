# Exercițiul 3 (BONUS) — Analiză avansată și workflow-uri automate pentru comenzi eCommerce

> **Pachet:** `com.pao.laboratory07.exercise3`
> **Timp estimat:** ~45 min · **Teste automate:** nu (demonstrație în `Main.java`)

---

## Scop și context

Acest exercițiu bonus extinde sistemul de tracking al comenzilor eCommerce cu funcționalități avansate de analiză, raportare și automatizare a fluxurilor de lucru. Vei integra și extinde ierarhia de comenzi, adăugând statistici, rapoarte și acțiuni automate (ex: notificări, procesări în masă, generare de rapoarte periodice).

---

## Cerințe generale

- Integrează și extinde funcționalitățile de la exercițiul 2 (Comanda, tipuri de comenzi, enumuri, interfețe, polimorfism etc.).
- Demonstrează în `Main.java` (cu output clar și comentarii) următoarele:
  1. Gruparea comenzilor după tip și afișarea pentru fiecare tip a mediei valorilor.
  2. Identificarea și afișarea comenzilor cu valoare peste media tuturor comenzilor.
  3. Pentru fiecare comandă, afișează și dacă este specială (unde e cazul).
  4. Permite filtrarea comenzilor după un prag de valoare dat ca argument.
  5. Permite sortarea comenzilor după client, apoi după valoare (criteriu compus).
  6. Implementează o funcționalitate de workflow automat: de exemplu, procesarea automată a tuturor precomenzilor cu data de livrare în trecut sau generarea unui raport sumar pentru fiecare tip de comandă.
  7. Demonstrează tratarea cazurilor limită și a erorilor (input invalid, tip necunoscut, date lipsă etc.).
- Outputul trebuie să fie clar, structurat și să acopere toate cerințele, cu comentarii explicative în cod.

---

## Exemple de funcționalități avansate (poți alege sau combina):
- Rapoarte periodice (ex: total comenzi/lună, top clienți, etc.)
- Undo/redo pentru operații pe comenzi
- Notificări automate pentru precomenzi cu livrare depășită
- Integrare cu un sistem de validare a datelor (ex: validare date client, valoare pozitivă, etc.)
- Exportul datelor într-un format structurat (ex: CSV, JSON)

---

## Demonstrație și evaluare

- Nu există input/output fix sau test automat.
- Toate funcționalitățile trebuie să fie vizibile și comentate în outputul din `Main.java`.
- Se acordă 0.5% bonus la nota finală dacă exercițiul este complet și demonstrat corect.
- Poate fi folosit pentru a atinge nota maximă la laborator.

---

## Hints
- Folosește excepții pentru tratarea cazurilor limită și a erorilor.
- Comentează clar fiecare secțiune de output și cod.
- Poți folosi orice funcționalitate Java relevantă (stream-uri, colecții, etc.).
- Fii creativ: poți adăuga orice funcționalitate avansată relevantă pentru sistemul de comenzi eCommerce.
