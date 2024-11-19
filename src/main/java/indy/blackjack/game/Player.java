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

    /**
     * Constructs a new Player object with a parent player and a given name.
     * This constructor is used when creating split players.
     *
     * @param parent The parent player from which this player is split.
     * @param name The name of the player.
    */
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

    /**
     * Returns the current balance of the player. If the player is a split player,
     * it retrieves the balance from the parent player.
     *
     * @return The current balance of the player.
    */
    public double balance() {
        if(this.parent != null) return parent.balance();
        return this.balance;
    }

    /**
     * Displays the player's cards and their current status.
     * If the player is split, it calls the showoff method for each split player.
     * If the player has folded, it prints a folded message.
    */
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

    /**
     * Calculates and updates the current value of the player's hand.
     * The hand value is determined by summing the values of the cards,
     * considering aces as either 1 or 11. If the sum exceeds 21 and there
     * are aces, the value of the aces is reduced to 1 until the sum is
     * less than or equal to 21.
    */
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

    /**
     * Performs a player's turn in the game.
     * If the player has already folded, the function returns immediately.
     * If the player has split, it calls the turn method for each hand.
    */
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

    /**
     * Determines the outcome of the player's hand against the dealer's hand.
     * If the player has split, it calls the check_win method for each hand.
     * The function prints a message indicating the outcome and updates the player's balance accordingly.
    */
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

    /**
     * Adds a card to the player's hand and updates the hand value.
     *
     * @param card The card to be added to the player's hand.
    */
    public void add_card(Card card) {
        this.cards.add(card);
        this.hand_value();
    }

    /**
     * Draws a card from the deck and adds it to the player's hand.
     * After adding the card, the player's hand value is updated.
     */
    public void take_card() {
        this.add_card(Game.take_card());
    }

    /**
     * Clears the player's hand by removing all cards, resetting the hand value,
     * and resetting the split status. If the player has split, it also
     * clears the hands of the left and right hands.
     */
    public void clear_cards() {
        this.cards.clear();
        this.value = 0;
        this.split = false;
        this.left = null;
        this.right = null;
    }

    /**
     * Adds a specified amount to the player's balance. If the player has a parent,
     * the amount is added to the parent player's balance.
     *
     * @param amount The amount to be added to the player's balance.
     */
    public void add_balance(double amount) {
        if(this.parent != null) this.parent.add_balance(amount);
        else this.balance += amount;
    }

    /**
     * This method allows a player to place a bet.
     * The bet amount is subtracted from the player's balance.
     *
     * @param bet The amount to be bet. It must be a positive integer and less than or equal to the player's current balance.
     * @throws IllegalArgumentException If the bet amount is not a positive integer or exceeds the player's current balance.
     */
    public void bet(int bet) {
        if (bet <= 0)
            throw new IllegalArgumentException("Bet amount must be a positive integer.");
        if (bet > this.balance())
            throw new IllegalArgumentException("Bet amount exceeds player's current balance.");

        this.bet = bet;
        this.add_balance(-bet);
    }

    /**
     * Performs a hit action for the player.
     * This method adds a card to the player's hand from the deck and then displays the player's updated hand.
    */
    private void hit() {
        this.take_card();
        this.showoff();
    }

    /**
     * Performs a double down action for the player.
     * This method doubles the player's bet, adds a card to the player's hand from the deck,
     * and then displays the player's updated hand.
    */
    private void double_down() {
        this.hit();
        this.bet *= 2;
    }

    /**
     * Performs a fold action for the player.
     * This method clears the player's hand and returns half of player's bet to them.
    */
    private void fold() {
        this.clear_cards();
        this.folded = true;
        this.add_balance((double) this.bet / 2);
    }

    /**
     * Performs a quit action for the player.
     * This method removes the player from the game.
    */
    private void quit() {
        Game.players().remove(this);
    }

    /**
     * Performs a split action for the player.
     * This method checks if the player can split their hand, creates two new players (left and right hands),
     * removes the cards from the original hand, adds them to the new players, doubles the bet, and deals
     * two additional cards to each new player. Finally, it starts the turns for the left and right hands.
    */
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
