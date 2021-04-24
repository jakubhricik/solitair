package sk.tuke.kpi.kp.gamestudio.game.solitaire.core;

import org.junit.jupiter.api.Test;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.enums.Rank;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.enums.StackType;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.enums.Suit;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

public class CardStackTest {

    private final int cardCount;
    private final CardStack cardStack;

    public CardStackTest() {
        Random randomGenerator = new Random();
        cardCount = randomGenerator.nextInt(10);
        cardStack = new CardStack(StackType.STOCK);
    }

    @Test
    public void peekEmptyStack(){

        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                cardStack::peek,
                "Expected peek() to throw IllegalStateException, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Cannot peek, card stock is empty"));
    }


    @Test
    public void popEmptyStack(){

        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                cardStack::pop,
                "Expected pop() to throw IllegalStateException, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Cannot pop, card stock is empty"));
    }

    @Test
    public void checkSize(){
        for (int i = 0; i<cardCount; i++){
            cardStack.push(new Card(Rank.ACE, Suit.CLUBS));
        }

        assertEquals(cardCount, cardStack.getSize(), "Card stack was initialized incorrectly - " +
                "a different amount of cards was counted in the card stack than amount given in the constructor.");
    }

    @Test
    public void pushIncorrectObject(){

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> cardStack.push(null),
                "Expected push() to throw IllegalArgumentException, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Illegal Card added to stack"));
    }




}
