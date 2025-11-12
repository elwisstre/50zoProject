package com.example.demomvc.Model;

import java.util.*;

public class Deck {
    private final List<Card> cards = new ArrayList<>();
    private final Random random = new Random();

    public Deck() { reset(); }

    public void reset() {
        cards.clear();
        String[] symbols = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] suits = {"corazon", "pica", "diamante", "trebol"};

        for (String s : symbols) {
            for (String suit : suits) {
                cards.add(new Card(s, suit));
            }
        }
        shuffle();
    }


    public void shuffle() { Collections.shuffle(cards, random); }

    public Card drawCard() {
        if (cards.isEmpty()) return null;
        return cards.remove(0);
    }

    public void addToBottom(List<Card> list) { cards.addAll(list); }

    public int size() { return cards.size(); }
}
