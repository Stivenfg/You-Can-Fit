package com.scj.youcanfit.fragments;

public class Ranking_Item {
    private String playerName;
    private int score;

    private int position;

    public Ranking_Item(String playerName, int score, int position) {
        this.playerName = playerName;
        this.score = score;
        this.position = position;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public int getPosition() {return position;}
}

