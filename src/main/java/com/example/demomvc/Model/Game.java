package com.example.demomvc.Model;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents the main game logic for the "Cincuentazo" card game.
 * Handles player turns, deck management, table sum, elimination rules,
 * and automatic bot turns using multithreading.
 *
 * @author Lina Cosme, Stiven Diosa
 */
public class Game {

    private final List<Player> players = new ArrayList<>();
    private final Deck deck = new Deck();
    private final List<Card> tablePile = new ArrayList<>();
    private int tableSum = 0;
    private int currentPlayerIndex = 0;
    private boolean gameOver = false;

    private final Random random = new Random();

    private Consumer<Card> onCardPlayed;


    /**
     * Initializes the game with 1 human and N bots.
     *
     * @param numBots Number of bot players (1–3)
     */
    public Game(int numBots) {
        // Human player
        players.add(new Player("You", "/com/example/demomvc/PlayerBotIcons/human.png", false));

        // Bot players
        for (int i = 1; i <= numBots; i++) {
            players.add(new Player("Bot " + i, "/com/example/demomvc/PlayerBotIcons/jugador" + i + ".png", true));
        }

        startGame();
    }

    /**
     * Prepares the game by dealing initial cards and setting the first table card.
     */
    private void startGame() {
        // Give 4 cards to each player
        for (Player p : players) {
            for (int i = 0; i < 4; i++) {
                Card c = deck.drawCard();
                if (c != null) p.addCardToHand(c);
            }
        }

        // Draw a first card that does NOT make the sum negative
        Card startCard;
        do {
            startCard = deck.drawCard();
        } while (startCard.getValue(false) < 0); // skip J, Q, K

        tablePile.add(startCard);
        tableSum = computeCardValue(startCard, 0);

        System.out.println("Game started. Table sum = " + tableSum);
        currentPlayerIndex = 0;
    }

    public Player getHumanPlayer() {
        if (players.isEmpty()) return null;
        return players.get(0); // asumimos que el humano se creó en índice 0
    }

    /**
     * Calculates how the card affects the total table sum based on game rules.
     * Supports negative effects for J, Q, K, and proper value selection for A.
     */
    private int computeCardValue(Card card, int currentSum) {
        String sym = card.getSymbol();

        // Ace special case (1 or 10)
        if (sym.equals("A")) {
            int addTen = currentSum + 10;
            int addOne = currentSum + 1;
            // Siempre escoge el que no pase de 50
            return (addTen <= 50) ? addTen : addOne;
        }

        // Cualquier otra carta, solo suma su valor (puede ser negativo)
        return currentSum + card.getValue(false);
    }

    /**
     * Plays a card for the human player (by index in hand).
     */
    public synchronized boolean playHumanCard(int index) {
        if (gameOver || currentPlayer().isBot()) return false;
        Player human = currentPlayer();

        if (index < 0 || index >= human.getHand().size()) return false;
        Card selected = human.getHand().get(index);

        int newSum = computeCardValue(selected, tableSum);

        if (selected.getValue(false) > 0 && newSum > 50) {
            System.out.println("That card exceeds 50. Invalid move.");
            return false;
        }

        // actualiza la suma
        tableSum = newSum;
        notifySumChanged(); // notifica cambio en la suma

        tablePile.add(selected);
        human.removeCard(selected);

        System.out.println(human.getName() + " played " + selected + ". Sum = " + tableSum);

        // 🔥 Notifica al controlador para mostrar la carta en el centro
        if (onCardPlayed != null) {
            onCardPlayed.accept(selected);
        }

        drawReplacementCard(human);
        nextTurn();
        return true;
    }

    public synchronized boolean playHumanCard(Card selected) {
        if (gameOver || currentPlayer().isBot()) return false;
        Player human = currentPlayer();

        if (!human.getHand().contains(selected)) {
            System.out.println("[DEBUG] Attempted to play a Card object not present in human's hand: " + selected);
            return false;
        }

        int newSum = computeCardValue(selected, tableSum);
        if (selected.getValue(false) > 0 && newSum > 50) {
            System.out.println("That card exceeds 50. Invalid move.");
            return false;
        }

        tableSum = newSum;
        tablePile.add(selected);
        human.removeCard(selected);

        System.out.println(human.getName() + " played " + selected + ". Sum = " + tableSum);

        // 🔥 Notifica al controlador para mostrar la carta en el centro
        if (onCardPlayed != null) {
            onCardPlayed.accept(selected);
        }

        drawReplacementCard(human);
        nextTurn();
        return true;
    }


    /**
     * Handles turn rotation and triggers bot actions if needed.
     */
    private void nextTurn() {
        if (checkForEliminations()) {
            if (checkGameOver()) return;
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

        Player current = currentPlayer();
        if (current.isBot()) {
            new Thread(() -> botPlay(current)).start();
        }
    }

    /**
     * Logic for a bot's automatic play, using sleep to simulate time delay.
     */
    private void botPlay(Player bot) {
        try {
            Thread.sleep(2000 + random.nextInt(2000)); // 2–4 seconds

            Optional<Card> playable = bot.getHand().stream()
                    .filter(c -> computeCardValue(c, tableSum) <= 50)
                    .findFirst();

            if (playable.isPresent()) {
                Card chosen = playable.get();
                tableSum = computeCardValue(chosen, tableSum);

                // Actualiza el label de la UI con la nueva suma
                notifySumChanged();

                tablePile.add(chosen);
                bot.removeCard(chosen);
                System.out.println(bot.getName() + " played " + chosen + ". Sum = " + tableSum);

                drawReplacementCard(bot);
            } else {
                System.out.println(bot.getName() + " cannot play and is eliminated!");
                bot.setEliminated(true);
                deck.addToBottom(bot.getHand());
                bot.getHand().clear();
            }

            if (!checkGameOver()) nextTurn();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Gives one new card from the deck to a player (if available).
     */
    private void drawReplacementCard(Player player) {
        Card newCard = deck.drawCard();
        if (newCard == null) {
            recycleTablePile();
            newCard = deck.drawCard();
        }
        if (newCard != null) player.addCardToHand(newCard);
    }

    /**
     * Recycles all but the top card from the table back into the deck.
     */
    private void recycleTablePile() {
        if (tablePile.size() > 1) {
            List<Card> toRecycle = new ArrayList<>(tablePile.subList(0, tablePile.size() - 1));
            tablePile.retainAll(Collections.singletonList(tablePile.get(tablePile.size() - 1)));
            deck.addToBottom(toRecycle);
            deck.shuffle();
        }
    }

    /**
     * Checks if any player has no playable cards and eliminates them.
     */
    private boolean checkForEliminations() {
        for (Player p : new ArrayList<>(players)) {
            if (!p.isEliminated()) {
                boolean hasPlayable = p.getHand().stream()
                        .anyMatch(c -> computeCardValue(c, tableSum) <= 50);
                if (!hasPlayable) {
                    System.out.println(p.getName() + " eliminated (no playable cards)");
                    p.setEliminated(true);
                    deck.addToBottom(p.getHand());
                    p.getHand().clear();
                }
            }
        }
        players.removeIf(Player::isEliminated);
        return true;
    }

    /**
     * Checks if the game is over and announces the winner.
     */
    private boolean checkGameOver() {
        long alive = players.stream().filter(p -> !p.isEliminated()).count();
        if (alive <= 1) {
            gameOver = true;
            Player winner = players.stream().filter(p -> !p.isEliminated()).findFirst().orElse(null);
            if (winner != null)
                System.out.println("🏆 " + winner.getName() + " wins the game! 🏆");
            else
                System.out.println("No winner (draw)");
            return true;
        }
        return false;
    }

    public Player currentPlayer() { return players.get(currentPlayerIndex); }
    public int getTableSum() { return tableSum; }
    public boolean isGameOver() { return gameOver; }

    // Callback para avisar al controlador cuando cambie la suma
    private Runnable onSumChanged;

    public void setOnSumChanged(Runnable callback) {
        this.onSumChanged = callback;
    }

    private void notifySumChanged() {
        if (onSumChanged != null) {
            javafx.application.Platform.runLater(onSumChanged);
        }
    }

    public void setOnCardPlayed(Consumer<Card> callback) {
        this.onCardPlayed = callback;
    }


}
