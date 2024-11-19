package indy.blackjack.game;

import indy.blackjack.utils.ASCII;
import indy.blackjack.card.Card;
import indy.blackjack.card.Value;
import indy.blackjack.utils.Colors;

import java.util.*;

import static indy.blackjack.main.Main.Game;

public class Player {
    private final String name;
    private final List<Card> cards = new ArrayList<>();
    private int value = 0;
    private boolean folded = false;
    private double balance = 1000;
    private int bet;
    private boolean split = false;
    private Player parent = null;
    private Player left = null;
    private Player right = null;

    public Player(Player parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public Player(String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    public boolean folded() {
        return this.folded;
    }

    public List<Card> cards() {
        return this.cards;
    }

    public int value() {
        return this.value;
    }

    public int bet() {
        return this.bet;
    }

    public boolean is_split() {
        return this.split;
    }

    public double balance() {
        if(this.parent != null) return parent.balance();
        return this.balance;
    }

    public void showoff() {
        if(this.split) {
            this.left.showoff();
            this.right.showoff();
            return;
        }

        final String color = (this.value > 21 ? Colors.RED : ((this.value == 21 && this.cards.size() == 2) || this.cards.size() == 6 ? Colors.GREEN : Colors.WHITE));
        System.out.println(
                Colors.WHITE + this.name + (this.name.endsWith("s") ? "'" : "'s") + " cards (" +
                color + (this.value < 21 && this.cards.size() == 6 ? "Charlie" : (this.value == 21 && this.cards.size() == 2 ? "Blackjack" : this.value)) + Colors.WHITE + "):"
        );
        if(!this.folded) {
            Card.print(this.cards);
        } else System.out.println(Colors.WHITE + ASCII.FOLDED);
    }

    private void hand_value() {
        int val = 0, ace = 0;
        for(Card card : this.cards) {
            if(card.value().equals(Value.Ace)) ace++;
            else val += Card.VALUES.get(card.value());
        }
        while(ace-- > 0) {
            if(val + 11 > 21 || ace > 1) val++;
            else val += 11;
        }

        this.value = val;
    }

    public void turn() {
        if(this.folded) return;

        if(this.parent == null) {
            System.out.println();
            System.out.println(Colors.WHITE + this.name + (this.name.endsWith("s") ? "'" : "'s") + " balance: " + this.balance() + "$ (" + (this.balance + this.bet) + "$). ");
        }

        if(this.split) {
            this.left.turn();
            this.right.turn();
            return;
        }

        System.out.println(Colors.WHITE + this.name + (this.name.endsWith("s") ? "'" : "'s") + " turn. ");
        System.out.println(Colors.WHITE + this.name + (this.name.endsWith("s") ? "'" : "'s") + " bet: " + this.bet + "$. ");
        for(Player p : Game.players()) p.showoff();
        Game.dealer().showoff();

        String action;
        Set<String> exit = Set.of("fold", "f", "stand", "pass", "quit", "q");
        do {
            System.out.print(Colors.WHITE + "Action (fold, hit, double down, split, stand, quit): ");
            Scanner input = new Scanner(System.in);
            action = input.nextLine();

            switch(action) {
                case "fold", "f" -> this.fold();
                case "hit", "h" -> this.hit();
                case "double down", "dd" -> this.double_down();
                case "split", "s" -> this.split();
                case "quit", "q" -> this.quit();
            }
        } while(!exit.contains(action) && this.value <= 21 && !this.split);
    }

    public void check_win() {
        if(this.split) {
            this.left.check_win();
            this.right.check_win();
            return;
        }

        Colors.set(Colors.RED);
        if(this.value > 21) System.out.println("Player " + this.name + " lost against dealer. Current balance: " + this.balance() + "$.");
        else if(this.cards.size() < 6 && Game.dealer().cards().size() == 6 && Game.dealer().value() <= 21)
            System.out.println("Player " + this.name + " lost against dealer. Current balance: " + this.balance() + "$.");
        else if(this.cards.size() < 6 && this.value <= Game.dealer().value() && Game.dealer().value() <= 21)
            System.out.println("Player " + this.name + " lost against dealer. Current balance: " + this.balance() + "$.");
        else {
            final double val = this.value == 21 && this.cards.size() == 2 && !this.split ? 2.5*this.bet : 2*this.bet;
            this.add_balance(val);

            Colors.set(Colors.WHITE);
            System.out.println("Player " + this.name + " won against dealer. (" + Colors.GREEN + "+" + val + "$" + Colors.WHITE + "). Current balance: " + this.balance() + "$.");
        }
        Colors.set(Colors.WHITE);
    }

    public void add_card(Card card) {
        this.cards.add(card);
        this.hand_value();
    }

    public void take_card() {
        this.add_card(Game.take_card());
    }

    public void clear_cards() {
        this.cards.clear();
        this.value = 0;
        this.split = false;
        this.left = null;
        this.right = null;
    }

    public void add_balance(double amount) {
        if(this.parent != null) this.parent.add_balance(amount);
        else this.balance += amount;
    }

    public void bet(int bet) {
        this.bet = bet;
        this.add_balance(-bet);
    }

    private void hit() {
        this.take_card();
        this.showoff();
    }

    private void double_down() {
        this.hit();
        this.bet *= 2;
    }

    private void fold() {
        this.clear_cards();
        this.folded = true;
        this.add_balance((double) this.bet / 2);
    }

    private void quit() {
        Game.players().remove(this);
    }

    private void split() {
        if(this.split || this.cards.size() != 2 || this.bet > this.balance()) return;
        if(!this.cards.get(0).value().equals(this.cards.get(1).value())) return;

        this.left = new Player(this, this.name + " (Left Hand)");
        this.right = new Player(this, this.name + " (Right Hand)");
        this.left.add_card(this.cards.remove(0));
        this.right.add_card(this.cards.remove(0));
        this.add_balance(this.bet);
        this.left.bet(this.bet);
        this.right.bet(this.bet);
        this.left.take_card();
        this.right.take_card();
        this.split = true;

        this.left.turn();
        this.right.turn();
    }
}
