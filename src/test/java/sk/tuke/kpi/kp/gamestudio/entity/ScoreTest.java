package sk.tuke.kpi.kp.gamestudio.entity;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreTest {

    private final String GAME = "solitaire";

    @Test
    public void testConstructor(){
        Date date = new Date();
        Score score = new Score(GAME, "player", 10, date);

        assertEquals(GAME, score.getGame());
        assertEquals("player", score.getPlayer());
        assertEquals(10, score.getPoints());
        assertEquals(date, score.getPlayedOn());
    }

    @Test
    public void testSettersAndGetters(){
        Date date = new Date();
        Score score = new Score("mines", "jakub", 100, new Date());

        score.setGame(GAME);
        score.setPlayer("player");
        score.setPoints(10);
        score.setPlayedOn(date);

        assertEquals(GAME, score.getGame());
        assertEquals("player", score.getPlayer());
        assertEquals(10, score.getPoints());
        assertEquals(date, score.getPlayedOn());
    }
}
