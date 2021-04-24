package sk.tuke.kpi.kp.gamestudio.service.JDBC;

import sk.tuke.kpi.kp.gamestudio.entity.Score;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.ScoreService;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreServiceTest {


    private static final String GAME = "solitaire";

    private ScoreService createService() {
        return new ScoreServiceJDBC();
    }

    @Test
    public void testReset(){
        ScoreService service = createService();
        service.reset();
        assertEquals(0, service.getTopScores(GAME).size());
    }

    @Test
    public void testAddScore() {
        ScoreService service = createService();
        service.reset();
        Date date = new Date();
        service.addScore(new Score(GAME, "Jakub", 200, date));

        List<Score> scores = service.getTopScores(GAME);

        assertEquals(1, scores.size());

        assertEquals(GAME, scores.get(0).getGame());
        assertEquals("Jakub", scores.get(0).getPlayer());
        assertEquals(200, scores.get(0).getPoints());
        assertEquals(date, scores.get(0).getPlayedOn());
    }

    @Test
    public void testAddScore3() {
        ScoreService service = createService();
        service.reset();
        Date date = new Date();
        service.addScore(new Score(GAME, "p1", 200, date));
        service.addScore(new Score(GAME, "p2", 400, date));
        service.addScore(new Score(GAME, "p3", 100, date));

        List<Score> scores = service.getTopScores(GAME);

        assertEquals(3, scores.size());

        assertEquals(GAME, scores.get(0).getGame());
        assertEquals("p2", scores.get(0).getPlayer());
        assertEquals(400, scores.get(0).getPoints());
        assertEquals(date, scores.get(0).getPlayedOn());

        assertEquals(GAME, scores.get(1).getGame());
        assertEquals("p1", scores.get(1).getPlayer());
        assertEquals(200, scores.get(1).getPoints());
        assertEquals(date, scores.get(1).getPlayedOn());

        assertEquals(GAME, scores.get(2).getGame());
        assertEquals("p3", scores.get(2).getPlayer());
        assertEquals(100, scores.get(2).getPoints());
        assertEquals(date, scores.get(2).getPlayedOn());
    }

    @Test
    public void testAddScore10() {
        ScoreService service = createService();
        for (int i = 0; i < 20; i++)
            service.addScore(new Score(GAME, "player", 200, new Date()));
        assertEquals(10, service.getTopScores(GAME).size());
    }

    @Test
    public void testPersistence() {
        ScoreService service = createService();
        service.reset();
        service.addScore(new Score(GAME, "player", 200, new Date()));

        service = createService();
        assertEquals(1, service.getTopScores(GAME).size());
    }



}
