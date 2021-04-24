package sk.tuke.kpi.kp.gamestudio.game.solitaire.core;

import org.junit.jupiter.api.Test;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.enums.Rank;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.enums.Suit;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class CardTest {
    Random random = new Random();

    private Rank getRandomRank(){
        return Rank.values()[ random.nextInt(Rank.values().length) ];
    }

    private Suit getRandomSuit() {
        return Suit.values()[ random.nextInt(Suit.values().length)];
    }


    @Test
    public void rankCheck(){
        Rank rank = getRandomRank();
        Suit suit = getRandomSuit();
        Card card = new Card(rank,suit);

        assertFalse(card.isFacingUp());
        card.setFacingUp(true);
        assertTrue(card.isFacingUp());

        assertEquals(rank, card.getRank());
        assertEquals(suit, card.getSuit());

    }

    @Test
    public void cardFacingDown(){
        Card card = new Card(getRandomRank(), getRandomSuit());

        assertFalse(card.isFacingUp());

        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                card::getRank,
                "Expected getRank() to throw IllegalStateException, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Card Facing down"));

        thrown = assertThrows(
                IllegalStateException.class,
                card::getSuit,
                "Expected getRank() to throw IllegalStateException, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Card Facing down"));
    }


}
