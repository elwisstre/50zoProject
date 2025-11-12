package com.example.demomvc.Model;

public class Card {
    private final String symbol; // A,2,...,K
    private final String suit;   // corazon, pica, diamante, trebol
    private final int value;

    public Card(String symbol, String suit) {
        this.symbol = symbol.toUpperCase();
        this.suit = suit.toLowerCase();
        this.value = assignValue(symbol);
    }

    private int assignValue(String symbol) {
        switch (symbol.toUpperCase()) {
            case "A": return 1;
            case "J":
            case "Q":
            case "K": return -10;
            case "9": return 0;
            case "10": return 10;
            default:
                try {
                    return Integer.parseInt(symbol);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid card symbol: " + symbol);
                }
        }
    }

    public String getSymbol() { return symbol; }

    public String getSuit() { return suit; }

    public int getValue(boolean asTenForAce) {
        if (symbol.equals("A") && asTenForAce) return 10;
        return value;
    }

    public String getImageFileName() {
        return symbol + "_" + suit + ".png";
    }

    @Override
    public String toString() {
        return symbol + " de " + suit;
    }
}
