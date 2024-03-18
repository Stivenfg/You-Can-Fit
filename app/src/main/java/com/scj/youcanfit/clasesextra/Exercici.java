package com.scj.youcanfit.clasesextra;

public class Exercici {
    String dataInici,dataFi,marcaDeTemps,nomExercici,series,repeticions,tipusExercici,urlVideo,valor;

    public Exercici() {
    }

    public Exercici(String dataInici, String dataFi, String marcaDeTemps, String nomExercici, String series, String repeticions, String tipusExercici, String urlVideo, String valor) {
        this.dataInici = dataInici;
        this.dataFi = dataFi;
        this.marcaDeTemps = marcaDeTemps;
        this.nomExercici = nomExercici;
        this.series = series;
        this.repeticions = repeticions;
        this.tipusExercici = tipusExercici;
        this.urlVideo = urlVideo;
        this.valor = valor;
    }

    public Exercici(String dataInici, String dataFi, String marcaDeTemps, String nomExercici, String series, String repeticions, String tipusExercici, String valor) {
        this.dataInici = dataInici;
        this.dataFi = dataFi;
        this.marcaDeTemps = marcaDeTemps;
        this.nomExercici = nomExercici;
        this.series = series;
        this.repeticions = repeticions;
        this.tipusExercici = tipusExercici;
        this.valor = valor;
    }

    public String getDataInici() {
        return dataInici;
    }

    public void setDataInici(String dataInici) {
        this.dataInici = dataInici;
    }

    public String getDataFi() {
        return dataFi;
    }

    public void setDataFi(String dataFi) {
        this.dataFi = dataFi;
    }

    public String getMarcaDeTemps() {
        return marcaDeTemps;
    }

    public void setMarcaDeTemps(String marcaDeTemps) {
        this.marcaDeTemps = marcaDeTemps;
    }

    public String getNomExercici() {
        return nomExercici;
    }

    public void setNomExercici(String nomExercici) {
        this.nomExercici = nomExercici;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getRepeticions() {
        return repeticions;
    }

    public void setRepeticions(String repeticions) {
        this.repeticions = repeticions;
    }

    public String getTipusExercici() {
        return tipusExercici;
    }

    public void setTipusExercici(String tipusExercici) {
        this.tipusExercici = tipusExercici;
    }

    public String getUrlVideo() {
        return urlVideo;
    }

    public void setUrlVideo(String urlVideo) {
        this.urlVideo = urlVideo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Exercici{" +
                "dataInici='" + dataInici + '\'' +
                ", dataFi='" + dataFi + '\'' +
                ", marcaDeTemps='" + marcaDeTemps + '\'' +
                ", nomExercici='" + nomExercici + '\'' +
                ", series='" + series + '\'' +
                ", repeticions='" + repeticions + '\'' +
                ", tipusExercici='" + tipusExercici + '\'' +
                ", urlVideo='" + urlVideo + '\'' +
                ", valor='" + valor + '\'' +
                '}';
    }
}
