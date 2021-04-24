package sk.tuke.kpi.kp.gamestudio.entity;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class RatingTest {

    private final String GAME = "solitaire";
    Random random = new Random();

    @Test
    public void testConstructor(){
        Date date = new Date();
        Rating rating = new Rating(GAME, "player", 4, date);

        assertEquals(GAME, rating.getGame());
        assertEquals("player", rating.getPlayer());
        assertEquals(4, rating.getRating());
        assertEquals(date, rating.getRatingOn());
    }

    @Test
    public void testBadRating(){
        Date date = new Date();
        int overRating = random.nextInt(5) + 5;
        int underRating = random.nextInt(4) - 5;

        Rating rating = new Rating(GAME, "player", overRating, date);
        assertEquals(5, rating.getRating());

        rating = new Rating(GAME, "player", underRating, date);
        assertEquals(1, rating.getRating());
    }
}
