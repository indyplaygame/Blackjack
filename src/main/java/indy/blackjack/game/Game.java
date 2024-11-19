package indy.blackjack.game;

import indy.blackjack.card.Card;
import indy.blackjack.utils.ASCII;
import indy.blackjack.utils.Colors;

import java.util.*;

public class Game {
    private static final List<Card> CARDS = new ArrayList<>(List.of(Card.deck()));

    private final Dealer dealer = new Dealer();
    private final List<Player> players = new ArrayList<>();
    private final Stack<Card> deck;

    private int round = 1;

    /**
     * Constructs a new Game instance. Initializes the deck as a Stack and shuffles it
     * to prepare for the game.
    */
    public Game() {
        this.deck = new Stack<>();
        this.shuffle();
    }

    public Dealer dealer() {
        return this.dealer;
    }

    public List<Player> players() {
        return this.players;
    }

    /**
     * Initializes a new game with the specified number of players.
     * Prompts each player to enter their name, creates a new {@link Player} object for each player,
     * deals two cards to each player and the dealer, and starts the game.
     *
     * @param players the number of players participating in the game
    */
    public void start(int players){
        Scanner scanner = new Scanner(System.in);
        for(int i = 0; i < players; i++) {
            System.out.print(Colors.WHITE + "Enter player's name: ");
            String name = scanner.nextLine();

            this.players.add(new Player(name));
        }

        this.deal();
        this.deal();

        do this.round();
        while(!this.players.isEmpty());
    }

    /**
     * Executes a single round of the Blackjack game. This includes prompting each player to place a bet,
     * allowing each player to take their turn, and then having the dealer take their turn. After all turns
     * are completed, the results are evaluated to determine winners. The round concludes with a display of
     * results and preparation for the next round.
    */
    public void round() {
        this.curr_round();

        Scanner scanner = new Scanner(System.in);
        for(Player player : this.players) {
            System.out.print(Colors.WHITE + "Enter " + player.name() + (player.name().endsWith("s") ? "'" : "'s") + " bet (" + player.balance() + "$): ");
            int bet = scanner.nextInt();
            scanner.nextLine();

            while(bet < 2 || bet > player.balance()) {
                System.out.print("Your bet must be higher than 2$ and lower than " + player.balance() + "$. New bet: ");
                bet = scanner.nextInt();
                scanner.nextLine();
            }

            player.bet(bet);
        }

        for(Player player : new ArrayList<>( this.players)) {
            if(this.deck.empty()) this.shuffle();
            player.turn();
        }
        this.dealer.cards().get(0).reverse(false);
        this.dealer.hand_value();
        while(this.dealer.value() <= 16) this.dealer.take_card();

        for(Player player : players) player.check_win();

        for(Player player : players) {
            player.showoff();
            player.clear_cards();
        }
        this.dealer.showoff();
        this.dealer.clear_cards();

        this.shuffle();
        this.deal();
        this.deal();

        System.out.print(Colors.WHITE + "Press Enter to start next round...");
        scanner.nextLine();
        scanner.nextLine();

        this.round++;
    }

    /**
     * Displays the current round number using ASCII art.
     * This method creates a visual representation of the round number by combining
     * ASCII art for the word "ROUND" with ASCII digits representing the current round number.
     * The resulting ASCII art is then printed to the console.
    */
    public void curr_round() {
        Colors.set(Colors.WHITE);

        int n = ASCII.ROUND.split("\n").length;
        List<List<String>> rows = new ArrayList<>();
        for(int i = 0; i < n; i++) rows.add(new ArrayList<>());

        List<String> digits = this.digits();
        Collections.reverse(digits);

        String[] round_ascii = ASCII.ROUND.split("\n");
        for(int i = 0; i < n; i++) rows.get(i).add(round_ascii[i] + "   ");

        for(String digit : digits) {
            String[] digit_rows = digit.split("\n");
            for(int i = 0; i < n; i++) rows.get(i).add(digit_rows[i]);
        }

        for(List<String> row : rows) {
            StringBuilder res = new StringBuilder();
            for(String str : row) res.append(str);

            System.out.println(res);
        }
    }

    /**
     * Converts the current round number into a list of ASCII art representations of its digits.
     * This method breaks down the round number into individual digits and maps each digit
     * to its corresponding ASCII art representation.
     *
     * @return A List of Strings, where each String is an ASCII art representation of a digit
     *         from the current round number. The digits are in reverse order (least significant
     *         digit first).
     * @throws IllegalStateException if an unexpected digit value is encountered (which should
     *         never happen with valid integer division and modulo operations)
    */
    private List<String> digits() {
        int curr_round = round;
        List<String> digits = new ArrayList<>();
        while(curr_round > 0) {
            digits.add((switch(curr_round % 10) {
                case 0 -> ASCII.ZERO;
                case 1 -> ASCII.ONE;
                case 2 -> ASCII.TWO;
                case 3 -> ASCII.THREE;
                case 4 -> ASCII.FOUR;
                case 5 -> ASCII.FIVE;
                case 6 -> ASCII.SIX;
                case 7 -> ASCII.SEVEN;
                case 8 -> ASCII.EIGHT;
                case 9 -> ASCII.NINE;
                default -> throw new IllegalStateException("Unexpected value: " + curr_round % 10);
            }));
            curr_round /= 10;
        }
        return digits;
    }

    /**
     * This method clears the current deck, shuffles the original list of cards,
     * and then adds the shuffled cards to the deck. This ensures that the deck
     * is in a random order for each new game.
    */
    public void shuffle() {
        this.deck.clear();
        Collections.shuffle(CARDS);
        this.deck.addAll(CARDS);
    }

    /**
     * Deals a card to each player and the dealer.
     * It calls the {@link Dealer#take_card()} method to deal a card to the dealer,
     * and then iterates over the list of players to deal a card to each player.
    */
    public void deal() {
        this.dealer.take_card();
        for(Player player : players) player.take_card();
    }

    /**
     * Deals a card from the deck to the next available player or the dealer.
     * If the deck is empty, it shuffles the original list of cards and repopulates the deck.
     *
     * @return The card that was dealt from the deck.
    */
    public Card take_card() {
        if(this.deck.empty()) this.shuffle();
        return this.deck.pop();
    }
}
