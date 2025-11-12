package com.example.demomvc.Model;

public class Card {
    private final String symbol; // A,2,3,...,K
    private final int value;     // valor numérico según reglas

    public Card(String symbol) {
        this.symbol = symbol.toUpperCase();
        this.value = assignValue(symbol);
    }

    private int assignValue(String symbol) {
        switch (symbol.toUpperCase()) {
            case "A": return 1;   // el valor de 10 se decide en la jugada
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

    public int getValue(boolean asTenForAce) {
        if (symbol.equals("A") && asTenForAce) return 10;
        return value;
    }

    @Override
    public String toString() { return symbol; }
}
