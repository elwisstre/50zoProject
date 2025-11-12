package com.example.demomvc.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the Cincuentazo game.
 * Each player can be human or bot, has an avatar and a hand of cards.
 *
 * @author Lina Cosme, Stiven Diosa
 */
public class Player {

    private String name;
    private String avatarPath;
    private boolean isBot;
    private boolean eliminated = false;

    private List<Card> hand = new ArrayList<>();

    public Player(String name, String avatarPath, boolean isBot) {
        this.name = name;
        this.avatarPath = avatarPath;
        this.isBot = isBot;
    }

    /** Returns the player's hand of cards. */
    public List<Card> getHand() {
        return hand;
    }

    /** Adds a new card to the player's hand. */
    public void addCardToHand(Card card) {
        hand.add(card);
    }

    /** Removes a card from the player's hand. */
    public void removeCard(Card card) {
        hand.remove(card);
    }

    /** Returns true if this player is a bot. */
    public boolean isBot() {
        return isBot;
    }

    /** Returns the player's name. */
    public String getName() {
        return name;
    }

    /** Returns the player's avatar path. */
    public String getAvatarPath() {
        return avatarPath;
    }

    /** Checks if the player has been eliminated. */
    public boolean isEliminated() {
        return eliminated;
    }

    /** Sets whether the player is eliminated. */
    public void setEliminated(boolean eliminated) {
        this.eliminated = eliminated;
    }
}
