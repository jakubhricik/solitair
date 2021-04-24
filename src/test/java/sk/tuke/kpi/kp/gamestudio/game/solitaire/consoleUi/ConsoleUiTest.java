package sk.tuke.kpi.kp.gamestudio.game.solitaire.consoleUi;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConsoleUiTest {
    @Test
    public void testNullGameBoard(){
        ConsoleUi consoleUi = new ConsoleUi();
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                ()-> consoleUi.play(null),
                "Expected play(null) to throw IllegalArgumentException, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Cannot initialize game with null gameBoard!"));
    }
}
