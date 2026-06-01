package com.pao.proiect.aibilet.model;

public abstract class Eveniment implements Comparable<Eveniment> {
    protected int id;
    protected String titlu;
    protected String descriere;
    protected CategorieEveniment categorie;
    protected String dataOraInceput;
    protected String dataOraFinal;
    protected StatusEveniment status;
    protected int locatieId;
    protected int organizatorId;
    protected String numeOrganizatieOrganizator;

    protected TipBiletEveniment[] tipuriBilete;
    protected int numarTipuriBilete;

    protected Eveniment(int id, String titlu, String descriere, CategorieEveniment categorie, String dataOraInceput, String dataOraFinal, StatusEveniment status, int locatieId, int organizatorId, String numeOrganizatieOrganizator) {
        if (titlu == null) {
            throw new IllegalArgumentException("Titlul nu poate fi null.");
        }
        if (descriere == null) {
            throw new IllegalArgumentException("Descrierea nu poate fi null.");
        }
        if (categorie == null) {
            throw new IllegalArgumentException("Categoria nu poate fi null.");
        }
        if (dataOraInceput == null) {
            throw new IllegalArgumentException("Data si ora de inceput nu poate fi null.");
        }
        if (dataOraFinal == null) {
            throw new IllegalArgumentException("Data si ora de final nu poate fi null.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Statusul nu poate fi null.");
        }

        this.id = id;
        this.titlu = titlu;
        this.descriere = descriere;
        this.categorie = categorie;
        this.dataOraInceput = dataOraInceput;
        this.dataOraFinal = dataOraFinal;
        this.status = status;
        this.locatieId = locatieId;
        this.organizatorId = organizatorId;
        setNumeOrganizatieOrganizator(numeOrganizatieOrganizator);

        this.tipuriBilete = new TipBiletEveniment[20];
        this.numarTipuriBilete = 0;
    }

    public abstract boolean esteCuLocuri();

    public abstract int getDisponibilitate();

    public void seteazaTipuriBilete(TipBiletEveniment[] tipuri, int numarTipuri) {
        if (tipuri == null) {
            throw new IllegalArgumentException("Tipul nu poate fi null.");
        }

        if (numarTipuri < 0 || numarTipuri > tipuri.length) {
            throw new IllegalArgumentException("Numarul de tipuri este invalid.");
        }

        this.tipuriBilete = new TipBiletEveniment[numarTipuri];
        this.numarTipuriBilete = numarTipuri;

        for (int i = 0; i < numarTipuri; i++) {
            this.tipuriBilete[i] = tipuri[i];
        }
    }

    public TipBiletEveniment[] getTipuriBilete() {
        TipBiletEveniment[] copie = new TipBiletEveniment[numarTipuriBilete];

        for (int i = 0; i < numarTipuriBilete; i++) {
            copie[i] = tipuriBilete[i];
        }

        return copie;
    }

    public int getNumarTipuriBilete() {
        return numarTipuriBilete;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitlu() {
        return titlu;
    }

    public void setTitlu(String titlu) {
        if (titlu == null) {
            throw new IllegalArgumentException("Titlul nu poate fi null.");
        }
        this.titlu = titlu;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        if (descriere == null) {
            throw new IllegalArgumentException("Descrierea nu poate fi null.");
        }
        this.descriere = descriere;
    }

    public CategorieEveniment getCategorie() {
        return categorie;
    }

    public void setCategorie(CategorieEveniment categorie) {
        if (categorie == null) {
            throw new IllegalArgumentException("Categoria nu poate fi null.");
        }
        this.categorie = categorie;
    }

    public String getDataOraInceput() {
        return dataOraInceput;
    }

    public void setDataOraInceput(String dataOraInceput) {
        if (dataOraInceput == null) {
            throw new IllegalArgumentException("Data si ora de inceput nu poate fi null.");
        }
        this.dataOraInceput = dataOraInceput;
    }

    public String getDataOraFinal() {
        return dataOraFinal;
    }

    public void setDataOraFinal(String dataOraFinal) {
        if (dataOraFinal == null) {
            throw new IllegalArgumentException("Data si ora de final nu poate fi null.");
        }
        this.dataOraFinal = dataOraFinal;
    }

    public StatusEveniment getStatus() {
        return status;
    }

    public void setStatus(StatusEveniment status) {
        if (status == null) {
            throw new IllegalArgumentException("Statusul nu poate fi null.");
        }
        this.status = status;
    }

    public int getLocatieId() {
        return locatieId;
    }

    public void setLocatieId(int locatieId) {
        this.locatieId = locatieId;
    }

    public int getOrganizatorId() {
        return organizatorId;
    }

    public String getNumeOrganizatieOrganizator() {
        return numeOrganizatieOrganizator;
    }

    public void setNumeOrganizatieOrganizator(String numeOrganizatieOrganizator) {
        if (numeOrganizatieOrganizator == null) {
            throw new IllegalArgumentException("Numele organizatiei nu poate fi null.");
        }
        this.numeOrganizatieOrganizator = numeOrganizatieOrganizator.trim();
    }

    @Override
    public int compareTo(Eveniment other) {
        int comparareInceput = this.dataOraInceput.compareTo(other.dataOraInceput);

        if (comparareInceput != 0) {
            return comparareInceput;
        }

        int comparareFinal = this.dataOraFinal.compareTo(other.dataOraFinal);

        if (comparareFinal != 0) {
            return comparareFinal;
        }

        if (this.id < other.id) {
            return -1;
        }
        if (this.id > other.id) {
            return 1;
        }

        return 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "\n" +
                "   ID: " + id + "\n" +
                "   Titlu: " + titlu + "\n" +
                "   Descriere: " + descriere + "\n" +
                "   Categorie: " + categorie + "\n" +
                "   Inceput: " + dataOraInceput + "\n" +
                "   Final: " + dataOraFinal + "\n" +
                "   Organizat de: " + numeOrganizatieOrganizator + "\n" +
                "   Disponibilitate: " + getDisponibilitate() + " bilete\n" +
                "   Status: " + status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Eveniment)) {
            return false;
        }

        Eveniment eveniment = (Eveniment) o;

        return id == eveniment.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}