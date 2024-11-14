package indy.blackjack.game;

import indy.blackjack.card.Card;
import indy.blackjack.card.Value;
import indy.blackjack.utils.Colors;

import java.util.ArrayList;
import java.util.List;

import static indy.blackjack.main.Main.Game;

public class Dealer {
    private final List<Card> cards;
    private int value = 0;

    public Dealer() {
        this.cards = new ArrayList<>();
    }

    public int value() {
        return this.value;
    }

    public List<Card> cards() {
        return this.cards;
    }

    public void showoff() {
        final String color = (this.value > 21 ? Colors.RED : (this.value == 21 ? Colors.GREEN : Colors.WHITE));
        System.out.println(
                Colors.WHITE + "Dealer's cards (" +
                        color + (this.value == 21 && this.cards.size() == 2 ? "Blackjack" : this.value) + Colors.WHITE + "):"
        );
        Card.print(this.cards);
    }

    public void hand_value() {
        int val = 0, ace = 0;
        for(Card card : this.cards) {
            if(card.reverse()) continue;
            if(card.value().equals(Value.Ace)) ace++;
            else val += Card.VALUES.get(card.value());
        }
        while(ace-- > 0) {
            if(val + 11 > 21 || ace > 1) val++;
            else val += 11;
        }

        this.value = val;
    }

    public void take_card() {
        Card card = Game.take_card();
        if(this.cards.isEmpty()) card.reverse(true);

        this.cards.add(card);
        this.hand_value();
    }

    public void clear_cards() {
        this.cards.clear();
        this.value = 0;
    }
}
