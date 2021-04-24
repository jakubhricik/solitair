package sk.tuke.kpi.kp.gamestudio.game.solitaire.core;

import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.enums.StackType;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class GameBoardTest {

    private final GameBoard gameBoard;
    private final Random randomGenerator = new Random();

    public GameBoardTest() {
        gameBoard = new GameBoard();
    }

    @Test
    public void dealAllCards(){
        while(!gameBoard.getStock().isEmpty()){
            gameBoard.deal();
        }

        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                gameBoard::deal,
                "Expected deal() to throw IllegalStateException, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Stock is empty."));
    }

    @Test
    public void getWrongPileFoundation(){
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> gameBoard.getFoundations(10),
                "Expected getFoundations() to throw IllegalArgumentException, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Illegal pile num"));

        thrown = assertThrows(
                IllegalArgumentException.class,
                () -> gameBoard.getFoundations(-1),
                "Expected getFoundations() to throw IllegalArgumentException, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Illegal pile num"));
    }

    @Test
    public void getWrongPileTableau(){
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> gameBoard.getTableau(10),
                "Expected getTableau() to throw IllegalArgumentException, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Illegal pile num"));

        thrown = assertThrows(
                IllegalArgumentException.class,
                () -> gameBoard.getTableau(-1),
                "Expected getTableau() to throw IllegalArgumentException, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Illegal pile num"));
    }

    @Test
    public void moveCardWrongCombinations(){
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> gameBoard.moveCard(StackType.STOCK, 1, StackType.TABLEAU, 1),
                "Expected moveCard() to throw IllegalArgumentException, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Illegal argument combination"));

        thrown = assertThrows(
                IllegalArgumentException.class,
                () -> gameBoard.moveCard(StackType.TABLEAU, 1, StackType.STOCK, 1),
                "Expected moveCard() to throw IllegalArgumentException, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Illegal argument combination"));

        thrown = assertThrows(
                IllegalArgumentException.class,
                () -> gameBoard.moveCard(StackType.TABLEAU, 1, StackType.TALON, 1),
                "Expected moveCard() to throw IllegalArgumentException, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Illegal argument combination"));
    }

    @Test
    public void moveCardWrongPileNumber(){
        gameBoard.deal();

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> gameBoard.moveCard(StackType.TALON, 1, StackType.TABLEAU,randomGenerator.nextInt()+6),
                "Expected moveCard() to throw IllegalArgumentException, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Illegal argument in"));


        thrown = assertThrows(
                IllegalArgumentException.class,
                () -> gameBoard.moveCard(StackType.TALON, 1, StackType.FOUNDATION, randomGenerator.nextInt()+4),
                "Expected moveCard() to throw IllegalArgumentException, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Illegal argument in"));

        thrown = assertThrows(
                IllegalArgumentException.class,
                () -> gameBoard.moveCard(StackType.FOUNDATION, randomGenerator.nextInt()+4, StackType.TABLEAU, 1),
                "Expected moveCard() to throw IllegalArgumentException, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Illegal argument in"));

        thrown = assertThrows(
                IllegalArgumentException.class,
                () -> gameBoard.moveCard(StackType.TABLEAU, randomGenerator.nextInt()+6, StackType.FOUNDATION,1),
                "Expected moveCard() to throw IllegalArgumentException, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Illegal argument in"));
    }

    @Test
    public void reloadStockTest(){
        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                gameBoard::reloadStock,
                "Expected reloadStock() to throw IllegalStateException, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Stock is not empty"));
    }

    @Test
    public void testQuickSaveLoad(){
        int talonStartingSize = gameBoard.getTalon().getSize();
        int stockStartingSize = gameBoard.getStock().getSize();

        gameBoard.quickSave();
        gameBoard.deal();
        gameBoard.deal();
        gameBoard.quickLoad();

        assertEquals(talonStartingSize, gameBoard.getTalon().getSize());
        assertEquals(stockStartingSize, gameBoard.getStock().getSize());
    }
}
