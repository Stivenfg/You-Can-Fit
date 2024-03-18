package com.scj.youcanfit.clasesextra;

public class PuntosAlumne {
    String nom,edat,sexe,punts;

    public PuntosAlumne() {
    }

    public PuntosAlumne(String nom, String edat, String sexe, String punts) {
        this.nom = nom;
        this.edat = edat;
        this.sexe = sexe;
        this.punts = punts;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEdat() {
        return edat;
    }

    public void setEdat(String edat) {
        this.edat = edat;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getPunts() {
        return punts;
    }

    public void setPunts(String punts) {
        this.punts = punts;
    }

    @Override
    public String toString() {
        return "PuntosAlumne{" +
                "nom='" + nom + '\'' +
                ", edat='" + edat + '\'' +
                ", sexe='" + sexe + '\'' +
                ", punts='" + punts + '\'' +
                '}';
    }
}
