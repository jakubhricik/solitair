package sk.tuke.kpi.kp.gamestudio.game.solitaire.core.enums;

public enum Suit {

    CLUBS(0, '♣'),
    SPADES(0, '♠'),
    DIAMONDS(1, '♦'),
    HEARTS(1, '♥');

    /**
     * COLOR:  0 == black
     *         1 == red
     */
    private final int color;
    private final char suit_character;

    Suit(int color, char suit_character) {
        this.color = color;
        this.suit_character = suit_character;
    }



    public int getColor() {
        return color;
    }

    public char getSuitCharacter() {
        return suit_character;
    }
}
