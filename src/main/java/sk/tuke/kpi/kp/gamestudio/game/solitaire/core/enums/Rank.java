package sk.tuke.kpi.kp.gamestudio.game.solitaire.core.enums;

public enum Rank {
    ACE(1, 'A'),
    TWO(2, '2'),
    THREE(3, '3'),
    FOUR(4, '4'),
    FIVE(5, '5'),
    SIX(6, '6'),
    SEVEN(7, '7'),
    EIGHT(8,'8'),
    NINE(9,'9'),
    TEN(10,'⒑'),
    JACK(11,'J'),
    QUEEN(12,'Q'),
    KING(13,'K');

    /**
     * VALUE of the card
     * number from 1 to 13
     */
    private final int value;
    private final char rankShortcut;

    Rank(int value, char rankShortcut) {
        this.value = value;
        this.rankShortcut = rankShortcut;
    }

    public int getValue() {
        return this.value;
    }

    public char getRankShortcut() {
        return rankShortcut;
    }
}
