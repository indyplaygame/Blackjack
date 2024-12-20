package indy.blackjack.card;

import indy.blackjack.utils.ASCII;
import indy.blackjack.utils.Colors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Card {
    public static final Map<Suit, String> SYMBOLS = new HashMap<>();
    public static final Map<Value, Integer> VALUES = new HashMap<>();
    static {
        VALUES.put(Value.Two, 2);
        VALUES.put(Value.Three, 3);
        VALUES.put(Value.Four, 4);
        VALUES.put(Value.Five, 5);
        VALUES.put(Value.Six, 6);
        VALUES.put(Value.Seven, 7);
        VALUES.put(Value.Eight, 8);
        VALUES.put(Value.Nine, 9);
        VALUES.put(Value.Ten, 10);
        VALUES.put(Value.Jack, 10);
        VALUES.put(Value.Queen, 10);
        VALUES.put(Value.King, 10);
    }

    static {
        SYMBOLS.put(Suit.Club, "♣");
        SYMBOLS.put(Suit.Spade, "♠");
        SYMBOLS.put(Suit.Diamond, "♦");
        SYMBOLS.put(Suit.Heart, "♥");
    }

    private final Value value;
    private final Suit suit;
    private boolean reverse = false;

    /**
     * Constructs a new Card object with the given value and suit.
     *
     * @param value The value of the card. It can be one of the following:
     *              Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen, King, Ace.
     * @param suit  The suit of the card. It can be one of the following:
     *              Club, Spade, Diamond, Heart.
    */
    public Card(Value value, Suit suit) {
        this.value = value;
        this.suit = suit;
    }

    public boolean reverse() {
        return this.reverse;
    }

    public Value value() {
        return this.value;
    }

    public Suit suit() {
        return this.suit;
    }

    public void reverse(boolean reverse) {
        this.reverse = reverse;
    }

    /**
     * Returns the ASCII representation of the card.
     *
     * @return A string representing the card in ASCII format. If the card is reversed,
     *         it returns the ASCII representation of the back of the card.
     *         The ASCII representation is color-coded based on the card's suit.
    */
    public String ascii() {
        return this.reverse ? ASCII.CARD_REVERSE : (switch(this.value) {
            case Two -> ASCII.CARD_TWO;
            case Three -> ASCII.CARD_THREE;
            case Four -> ASCII.CARD_FOUR;
            case Five -> ASCII.CARD_FIVE;
            case Six -> ASCII.CARD_SIX;
            case Seven -> ASCII.CARD_SEVEN;
            case Eight -> ASCII.CARD_EIGHT;
            case Nine -> ASCII.CARD_NINE;
            case Ten -> ASCII.CARD_TEN;
            case Jack -> ASCII.CARD_JACK;
            case Queen -> ASCII.CARD_QUEEN;
            case King -> ASCII.CARD_KING;
            case Ace -> ASCII.CARD_ACE;
        }).replaceAll("x", SYMBOLS.get(this.suit));
    }

    /**
     * Creates a standard deck of 52 playing cards.
     *
     * @return An array of Card objects representing a full deck of 52 playing cards.
     *         The deck is ordered by value and suit, with each suit (Club, Spade, Diamond, Heart)
     *         followed by each value (Two, Three, ..., King, Ace).
    */
    public static Card[] deck() {
        List<Card> deck = new ArrayList<>();
        for(Value value : Value.values())
            for(Suit suit : Suit.values()) deck.add(new Card(value, suit));

        return deck.toArray(new Card[0]);
    }

    /**
     * Prints a list of cards in ASCII format.
     *
     * @param cards A list of Card objects to be printed.
    */
    public static void print(List<Card> cards) {
        int n = ASCII.CARD_TWO.split("\n").length;
        List<List<String>> rows = new ArrayList<>();
        for(int i = 0; i < n; i++) rows.add(new ArrayList<>());

        for(Card card : cards) {
            String color = card.suit.equals(Suit.Club) || card.suit.equals(Suit.Spade) || card.reverse ? Colors.WHITE : Colors.RED;
            String[] ascii = card.ascii().split("\n");

            for(int i = 0; i < n; i++) rows.get(i).add(color + ascii[i]);
        }

        for(List<String> row : rows) {
            StringBuilder res = new StringBuilder();
            for(String str : row) res.append(str).append("  ");

            System.out.println(res);
        }
    }

    /**
     * Prints the ASCII representation of the card to the console.
     * If the card is reversed, it prints the ASCII representation of the back of the card.
    */
    public void print() {
        String color = this.suit.equals(Suit.Club) || this.suit.equals(Suit.Spade) || this.reverse ? Colors.WHITE : Colors.RED;
        System.out.println(color + this.ascii());
    }
}
