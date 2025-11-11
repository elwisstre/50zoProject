package com.example.demomvc.Model;

public class Player {
    private String name;
    private String avatarPath;
    private int score;
    private boolean isBot;

    // Constructor para bots
    public Player(String name, String avatarPath, boolean isBot) {
        this.name = name;
        this.avatarPath = avatarPath;
        this.isBot = isBot;
        this.score = 0; // Inicializar la puntuación a cero
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public int getScore() {
        return score;
    }

    public boolean isBot() {
        return isBot;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int points) {
        this.score += points;
    }
}

