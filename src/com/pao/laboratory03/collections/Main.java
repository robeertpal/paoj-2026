package com.pao.laboratory03.collections;

import java.util.*;

/**
 * Exercițiul 1 — Colecții: HashMap și TreeMap
 *
 * Creează în acest main:
 *
 * PARTEA A — HashMap (frecvența cuvintelor)
 * 1. Declară un array de String-uri:
 *    String[] words = {"java", "python", "java", "c++", "python", "java", "rust", "c++", "go"};
 * 2. Creează un HashMap<String, Integer> care contorizează de câte ori apare fiecare cuvânt.
 *    - Parcurge array-ul și folosește put() + getOrDefault() pentru a incrementa contorul.
 * 3. Afișează map-ul.
 * 4. Verifică dacă există cheia "rust" cu containsKey().
 * 5. Afișează DOAR cheile (keySet()), apoi DOAR valorile (values()).
 * 6. Parcurge map-ul cu entrySet() și afișează "cheia -> valoarea" pentru fiecare intrare.
 *
 * PARTEA B — TreeMap (sortare automată)
 * 7. Creează un TreeMap<String, Integer> din același HashMap (constructor cu argument).
 * 8. Afișează TreeMap-ul — observă ordinea alfabetică a cheilor.
 * 9. Folosește firstKey() și lastKey() pentru a afișa prima și ultima cheie.
 *
 * PARTEA C — Map cu obiecte
 * 10. Creează un HashMap<String, List<String>> care asociază materii cu liste de studenți.
 *     Exemplu: "PAOJ" -> ["Ana", "Mihai", "Ion"], "BD" -> ["Ana", "Elena"]
 * 11. Afișează toți studenții de la materia "PAOJ".
 * 12. Adaugă un student nou la "BD" și afișează lista actualizată.
 */
public class Main {
    public static void main(String[] args) {

        System.out.println("=== PARTEA A: HashMap — frecvența cuvintelor ===");

        String[] words = {"java", "python", "java", "c++", "python", "java", "rust", "c++", "go"};

        HashMap<String, Integer> frequencyMap = new HashMap<>();

        for (String word : words) {
            frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
        }

        System.out.println("Frecvență: " + frequencyMap);
        System.out.println("Conține 'rust'? " + frequencyMap.containsKey("rust"));
        System.out.println("Chei: " + frequencyMap.keySet());
        System.out.println("Valori: " + frequencyMap.values());

        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        System.out.println("\n=== PARTEA B: TreeMap — sortare automată ===");

        TreeMap<String, Integer> sortedMap = new TreeMap<>(frequencyMap);

        System.out.println("Sortat: " + sortedMap);
        System.out.println("Prima cheie: " + sortedMap.firstKey());
        System.out.println("Ultima cheie: " + sortedMap.lastKey());

        System.out.println("\n=== PARTEA C: Map cu obiecte ===");

        HashMap<String, List<String>> subjects = new HashMap<>();
        subjects.put("PAOJ", new ArrayList<>(Arrays.asList("Ana", "Mihai", "Ion")));
        subjects.put("BD", new ArrayList<>(Arrays.asList("Ana", "Elena")));

        System.out.println("Studenți la PAOJ: " + subjects.get("PAOJ"));

        subjects.get("BD").add("George");
        System.out.println("Studenți la BD (actualizat): " + subjects.get("BD"));
    }
}