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
        while(this.players.stream().noneMatch(Player::folded));
    }

    public void round() {
        this.curr_round();

        Scanner scanner = new Scanner(System.in);
        for(Player player : this.players) {
            System.out.print(Colors.WHITE + "Enter " + player.name() + (player.name().endsWith("s") ? "'" : "'s") + " bet: ");
            int bet = scanner.nextInt();
            scanner.nextLine();

            while(bet < 2 || bet > 1000) {
                System.out.print("Your bet must be higher than 2$ and lower than 1000$. New bet: ");
                bet = scanner.nextInt();
                scanner.nextLine();
            }

            player.bet(bet);
        }

        for(Player player : this.players) {
            if(this.deck.empty()) this.shuffle();
            player.turn();
        }
        this.dealer.cards().get(0).reverse(false);
        this.dealer.hand_value();
        while(this.dealer.value() <= 16) this.dealer.take_card();

        for(Player player : players) {
            // TODO: check all player's hands
            System.out.print(Colors.RED);
            if(player.value() > 21) System.out.println("Dealer beats " + player.name() + "."); // Player loses
            else if(this.dealer().cards().size() == 6 && this.dealer.value() <= 21) System.out.println("Dealer beats " + player.name() + "."); // Player loses
            else if((player.cards().size() < 6) && (this.dealer.cards().size() == 2 && this.dealer.value() == 21)) System.out.println("Dealer beats " + player.name() + "."); // Player loses
            else if(player.value() <= this.dealer().value() && this.dealer.value() <= 21) System.out.println("Dealer beats " + player.name() + "."); // Player loses
            else {
                System.out.print(Colors.WHITE);
                final double val = player.value() == 21 && player.cards().size() == 2 && !player.is_split() ? 2.5*player.bet() : 2*player.bet();
                player.add_balance(val);
                System.out.println(player.name() + " wins (" + Colors.GREEN + "+" + val + "$" + Colors.WHITE + "). Current balance: " + player.balance() + "$.");
            }
            System.out.print(Colors.WHITE);
        }

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
        System.out.print(Colors.WHITE);

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
        return this.deck.pop();
    }
}
