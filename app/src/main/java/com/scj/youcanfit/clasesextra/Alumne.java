package com.scj.youcanfit.clasesextra;

public class Alumne {
    String nom,email,sexo,edat,dataNaixement,foto,institut;

    public Alumne() {
    }

    public Alumne(String nom, String email, String sexo, String edat, String dataNaixement, String foto, String institut) {
        this.nom = nom;
        this.email = email;
        this.sexo = sexo;
        this.edat = edat;
        this.dataNaixement = dataNaixement;
        this.foto = foto;
        this.institut = institut;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEdat() {
        return edat;
    }

    public void setEdat(String edat) {
        this.edat = edat;
    }

    public String getDataNaixement() {
        return dataNaixement;
    }

    public void setDataNaixement(String dataNaixement) {
        this.dataNaixement = dataNaixement;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getInstitut() {
        return institut;
    }

    public void setInstitut(String institut) {
        this.institut = institut;
    }

    @Override
    public String toString() {
        return "Alumne{" +
                "nom='" + nom + '\'' +
                ", email='" + email + '\'' +
                ", sexo='" + sexo + '\'' +
                ", edat='" + edat + '\'' +
                ", dataNaixement='" + dataNaixement + '\'' +
                ", foto='" + foto + '\'' +
                ", institut='" + institut + '\'' +
                '}';
    }
}
