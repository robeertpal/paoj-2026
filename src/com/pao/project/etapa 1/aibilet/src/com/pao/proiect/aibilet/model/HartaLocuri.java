package com.pao.proiect.aibilet.model;

import com.pao.proiect.aibilet.exception.LocIndisponibilException;

public class HartaLocuri {
    private final LocEveniment[][] locuri;

    public HartaLocuri(LocEveniment[][] locuri) {
        if (locuri == null) {
            throw new IllegalArgumentException("Locurile nu pot fi null.");
        }

        this.locuri = locuri;
    }

    public static HartaLocuri genereazaUniforma(int randuri, int coloane, String tipBilet) {
        LocEveniment[][] matrice = new LocEveniment[randuri][coloane];

        for (int i = 0; i < randuri; i++) {
            for (int j = 0; j < coloane; j++) {
                char literaRand = (char) ('A' + i);
                String cod = literaRand + "" + (j + 1);
                matrice[i][j] = new LocEveniment(i, j, cod, tipBilet, StatusLoc.LIBER);
            }
        }

        return new HartaLocuri(matrice);
    }

    public void seteazaTipPentruLoc(String codLoc, String tipBilet) throws LocIndisponibilException {
        LocEveniment loc = cautaLocDupaCod(codLoc);
        loc.setTipBilet(tipBilet);
    }

    public void seteazaLocInexistent(String codLoc) throws LocIndisponibilException {
        LocEveniment loc = cautaLocInclusivInexistente(codLoc);
        loc.setStatus(StatusLoc.INEXISTENT);
    }

    public LocEveniment cautaLocDupaCod(String codLoc) throws LocIndisponibilException {
        LocEveniment loc = cautaLocInclusivInexistente(codLoc);

        if (loc.getStatus() == StatusLoc.INEXISTENT) {
            throw new LocIndisponibilException("Locul " + codLoc + " nu exista.");
        }

        return loc;
    }

    private LocEveniment cautaLocInclusivInexistente(String codLoc) throws LocIndisponibilException {
        if (codLoc == null || codLoc.trim().length() == 0) {
            throw new LocIndisponibilException("Codul locului este invalid.");
        }

        String cod = codLoc.trim();

        for (int i = 0; i < locuri.length; i++) {
            for (int j = 0; j < locuri[i].length; j++) {
                LocEveniment loc = locuri[i][j];

                if (loc == null) {
                    continue;
                }

                if (loc.getCod().equalsIgnoreCase(cod)) {
                    return loc;
                }
            }
        }

        throw new LocIndisponibilException("Locul " + codLoc + " nu exista.");
    }

    public LocEveniment getLoc(int rand, int coloana) throws LocIndisponibilException {
        if (rand < 0 || rand >= locuri.length || coloana < 0 || coloana >= locuri[rand].length) {
            throw new LocIndisponibilException("Pozitia selectata nu exista.");
        }

        LocEveniment loc = locuri[rand][coloana];

        if (loc == null || loc.getStatus() == StatusLoc.INEXISTENT) {
            throw new LocIndisponibilException("Pozitia selectata nu exista.");
        }

        return loc;
    }

    public LocEveniment vinde(int rand, int coloana) throws LocIndisponibilException {
        LocEveniment loc = getLoc(rand, coloana);

        if (loc.getStatus() == StatusLoc.VANDUT) {
            throw new LocIndisponibilException("Locul " + loc.getCod() + " este deja vandut.");
        }

        loc.setStatus(StatusLoc.VANDUT);
        return loc;
    }

    public int numarLocuriDisponibile() {
        int count = 0;

        for (int i = 0; i < locuri.length; i++) {
            for (int j = 0; j < locuri[i].length; j++) {
                LocEveniment loc = locuri[i][j];

                if (loc != null && loc.esteDisponibil()) {
                    count++;
                }
            }
        }

        return count;
    }

    public LocEveniment[] toateLocurile() {
        int total = 0;

        for (int i = 0; i < locuri.length; i++) {
            for (int j = 0; j < locuri[i].length; j++) {
                LocEveniment loc = locuri[i][j];

                if (loc != null && loc.getStatus() != StatusLoc.INEXISTENT) {
                    total++;
                }
            }
        }

        LocEveniment[] rezultat = new LocEveniment[total];
        int index = 0;

        for (int i = 0; i < locuri.length; i++) {
            for (int j = 0; j < locuri[i].length; j++) {
                LocEveniment loc = locuri[i][j];

                if (loc != null && loc.getStatus() != StatusLoc.INEXISTENT) {
                    rezultat[index] = loc;
                    index++;
                }
            }
        }

        return rezultat;
    }

    public String afisareCompacta() {
        StringBuilder sb = new StringBuilder();
        sb.append("Harta locurilor\n");
        sb.append("Legenda: [ S ] loc liber (prima litera din tip), [ X ] loc vandut, spatiu gol = loc inexistent\n");

        if (locuri.length == 0) {
            sb.append("(nu exista locuri)");
            return sb.toString();
        }

        sb.append("\n");
        int latimeHarta = 4 + getColoane() * 6;
        sb.append(centreaza("SCENA", latimeHarta)).append("\n");
        sb.append("\n");

        sb.append("    ");
        for (int col = 0; col < getColoane(); col++) {
            sb.append(completeazaDreapta(String.valueOf(col + 1), 6));
        }
        sb.append("\n");

        for (int i = 0; i < locuri.length; i++) {
            char etichetaRand = '?';
            if (i < 26) {
                etichetaRand = (char) ('A' + i);
            }

            sb.append(etichetaRand).append("   ");

            for (int j = 0; j < locuri[i].length; j++) {
                LocEveniment loc = locuri[i][j];

                if (loc == null || loc.getStatus() == StatusLoc.INEXISTENT) {
                    sb.append(completeazaDreapta("      ", 6));
                } else if (loc.getStatus() == StatusLoc.LIBER) {
                    sb.append(completeazaDreapta(formatAfisareLocLiber(loc), 6));
                } else {
                    sb.append(completeazaDreapta("[ X ]", 6));
                }

            }

            sb.append("\n");
        }

        return sb.toString();
    }

    private String formatAfisareLocLiber(LocEveniment loc) {
        char initiala = initialaTipBilet(loc.getTipBilet());

        if (initiala == ' ') {
            return "[   ]";
        }

        return "[ " + initiala + " ]";
    }

    private char initialaTipBilet(String tipBilet) {
        if (tipBilet == null) {
            return ' ';
        }

        String tip = tipBilet.trim();

        if (tip.length() == 0 || tip.equalsIgnoreCase("NEALOCAT")) {
            return ' ';
        }

        for (int i = 0; i < tip.length(); i++) {
            char c = Character.toUpperCase(tip.charAt(i));

            if (c >= 'A' && c <= 'Z') {
                return c;
            }
        }

        return Character.toUpperCase(tip.charAt(0));
    }

    private String centreaza(String text, int latime) {
        if (text.length() >= latime) {
            return text;
        }

        int spatiiStanga = (latime - text.length()) / 2;
        String rezultat = "";

        for (int i = 0; i < spatiiStanga; i++) {
            rezultat = rezultat + " ";
        }

        rezultat = rezultat + text;

        return rezultat;
    }

    private String completeazaDreapta(String text, int lungime) {
        String rezultat = text;

        while (rezultat.length() < lungime) {
            rezultat = rezultat + " ";
        }

        return rezultat;
    }

    public int getRanduri() {
        return locuri.length;
    }

    public int getColoane() {
        if (locuri.length == 0) {
            return 0;
        }

        return locuri[0].length;
    }

    public LocEveniment[][] getLocuri() {
        return locuri;
    }
}