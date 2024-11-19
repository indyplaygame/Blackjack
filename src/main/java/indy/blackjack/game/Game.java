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

    public void shuffle() {
        this.deck.clear();
        Collections.shuffle(CARDS);
        this.deck.addAll(CARDS);
    }

    public void deal() {
        this.dealer.take_card();
        for(Player player : players) player.take_card();
    }

    public Card take_card() {
        if(this.deck.empty()) this.shuffle();
        return this.deck.pop();
    }
}
