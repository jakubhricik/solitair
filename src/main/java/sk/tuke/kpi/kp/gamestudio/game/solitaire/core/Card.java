package sk.tuke.kpi.kp.gamestudio.game.solitaire.core;

import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.enums.Rank;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.enums.Suit;

import java.io.Serializable;

public class Card implements Serializable {

    private final Rank rank;
    private final Suit suit;
    private boolean isFacingUp;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
        this.isFacingUp = false;
    }

    public Rank getRank() {
        if (isFacingUp) {
            return rank;
        } else throw new IllegalStateException("Card Facing down");
    }

    public Suit getSuit() {
        if (isFacingUp) {
            return suit;
        } else throw new IllegalStateException("Card Facing down");
    }

    public boolean isFacingUp() {
        return isFacingUp;
    }

    public void setFacingUp(boolean facingUp) {
        isFacingUp = facingUp;
    }


    @Override
    public String toString() {
        if (isFacingUp) {
            return " " + rank.getRankShortcut() + " " + suit.getSuitCharacter();
        } else return " X X";
    }
}
