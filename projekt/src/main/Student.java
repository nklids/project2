package main;

import java.util.ArrayList;
import java.util.List;

public abstract class Student {
    protected int id;
    protected String jmeno;
    protected String prijmeni;
    protected int rokNarozeni;
    protected List<Integer> znamky = new ArrayList<>();

    public Student(int id, String jmeno, String prijmeni, int rokNarozeni) {
        this.id = id;
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.rokNarozeni = rokNarozeni;
    }

    public void pridejZnamku(int znamka) {
        znamky.add(znamka);
    }

    public double getStudijniPrumer() {
        return znamky.stream().mapToInt(i -> i).average().orElse(0.0);
    }

    public int getId() {
        return id;
    }

    public String getPrijmeni() {
        return prijmeni;
    }

    public abstract void spustDovednost();

    @Override
    public String toString() {
        return String.format("ID: %d, %s %s, Rok: %d, Průměr: %.2f", id, jmeno, prijmeni, rokNarozeni, getStudijniPrumer());
    }

    public List<Integer> getZnamky() {
        return znamky;
    }

    public String getJmeno() {
        return jmeno;
    }

    public int getRokNarozeni() {
        return rokNarozeni;
    }
}

