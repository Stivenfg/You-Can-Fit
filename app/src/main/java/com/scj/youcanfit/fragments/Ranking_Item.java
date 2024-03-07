package com.scj.youcanfit.fragments;

public class Ranking_Item {
    private String playerName;
    private int score;
    public Ranking_Item(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }
    public String getPlayerName() {
        return playerName;
    }
    public int getScore() {
        return score;
    }

}

