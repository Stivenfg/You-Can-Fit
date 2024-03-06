package com.scj.youcanfit;

import java.util.Date;

public class Exercicis {

    private Date dataFi;
    private Date dataInici;
    private String descripcio;
    private Date marcaDeTiempo;
    private String numRondes;

    private  int repeticions;
    private String tipusExercicis;
    private String urlVideo;


    public Date getDataFi() {
        return dataFi;
    }

    public void setDataFi(Date dataFi) {
        this.dataFi = dataFi;
    }

    public Date getDataInici() {
        return dataInici;
    }

    public void setDataInici(Date dataInici) {
        this.dataInici = dataInici;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }

    public Date getMarcaDeTiempo() {
        return marcaDeTiempo;
    }

    public void setMarcaDeTiempo(Date marcaDeTiempo) {
        this.marcaDeTiempo = marcaDeTiempo;
    }

    public String getNumRondes() {
        return numRondes;
    }

    public void setNumRondes(String numRondes) {
        this.numRondes = numRondes;
    }

    public int getRepeticions() {
        return repeticions;
    }

    public void setRepeticions(int repeticions) {
        this.repeticions = repeticions;
    }

    public String getTipusExercicis() {
        return tipusExercicis;
    }

    public void setTipusExercicis(String tipusExercicis) {
        this.tipusExercicis = tipusExercicis;
    }

    public String getUrlVideo() {
        return urlVideo;
    }

    public void setUrlVideo(String urlVideo) {
        this.urlVideo = urlVideo;
    }

    public Exercicis(Date dataFi, Date dataInici, String descripcio, Date marcaDeTiempo, String numRondes, int repeticions, String tipusExercicis, String urlVideo) {
        this.dataFi = dataFi;
        this.dataInici = dataInici;
        this.descripcio = descripcio;
        this.marcaDeTiempo = marcaDeTiempo;
        this.numRondes = numRondes;
        this.repeticions = repeticions;
        this.tipusExercicis = tipusExercicis;
        this.urlVideo = urlVideo;
    }

    public Exercicis() {
    }
}
