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

        final String color = (this.value > 21 ? Colors.RED : (this.value == 21 ? Colors.GREEN : Colors.WHITE));
        System.out.println(
                Colors.WHITE + this.name + (this.name.endsWith("s") ? "'" : "'s") + " cards (" +
                color + (this.value == 21 && this.cards.size() == 2 ? "Blackjack" : this.value) + Colors.WHITE + "):"
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
        do {
            System.out.print(Colors.WHITE + "Action (fold, hit, double down, split, stand): ");
            Scanner input = new Scanner(System.in);
            action = input.nextLine();

            switch(action) {
                case "fold", "f":
                    this.fold();
                    break;
                case "hit", "h":
                    this.hit();
                    break;
                case "double down", "dd":
                    this.double_down();
                    break;
                case "split", "s":
                    this.split();
                    break;
            }
        } while(!(action.equals("stand") || action.equals("pass")) && this.value <= 21);
    }

    public void add_card(Card card) {
        this.cards.add(card);
        this.hand_value();
    }

    public void take_card() {
        this.cards.add(Game.take_card());
        this.hand_value();
    }

    public void clear_cards() {
        this.cards.clear();
        this.value = 0;
    }

    public void add_balance(double amount) {
        if(this.parent != null) this.parent.add_balance(amount);
        else this.balance += amount;
    }

    public void bet(int bet) {
        this.bet = bet;
        this.balance -= bet;
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
    }

    private void split() {
        if(this.split || this.cards.size() != 2 || this.bet > this.balance()) return;
        if(!this.cards.get(0).value().equals(this.cards.get(1).value())) return;

        this.left = new Player(this, this.name + " (Left Hand)");
        this.right = new Player(this, this.name + " (Right Hand)");
        this.left.add_card(this.cards.remove(0));
        this.right.add_card(this.cards.remove(1));
        this.left.bet(this.bet);
        this.right.bet(this.bet);
        this.split = true;
    }
}
