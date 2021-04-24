package sk.tuke.kpi.kp.gamestudio.service.JDBC;

import org.junit.jupiter.api.Test;
import sk.tuke.kpi.kp.gamestudio.entity.Rating;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.RatingService;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
public class RatingServiceTest {

    private static final String GAME = "solitaire";
    private Random random = new Random();

    private RatingServiceJDBC createService() {
        return new RatingServiceJDBC();
    }

    @Test
    public void testReset(){
        RatingService service = createService();
        service.reset();
        assertEquals(0, service.getAverageRating(GAME));
    }

    @Test
    public void testSetRating(){
        RatingService service = createService();
        service.reset();
        Date date = new Date();
        service.setRating(new Rating(GAME,"player", 3, date));

        int rating = service.getRating(GAME, "player");

        assertEquals(3, rating);
    }

    @Test
    public void testAverageRating(){
        RatingService service = createService();
        service.reset();
        int [] randomNumbers = new int[10];

        for (int i = 0; i < 10; i++){
            randomNumbers[i] = random.nextInt(4) + 1;
            service.setRating(new Rating(GAME, "player", randomNumbers[i], new Date()));
        }

        int average = (int) Math.round(Arrays.stream(randomNumbers).average().getAsDouble());
        assertEquals(average, service.getAverageRating(GAME) );
    }

    @Test
    public void testRating(){
        RatingService service = createService();
        service.reset();
        int rating = 1;
        int rating2 = 5;
        Date date1 = new Date(0, Calendar.NOVEMBER, 10,11,20,40);
        Date date2 = new Date(0, Calendar.DECEMBER,10,10,10,10);

        service.setRating(new Rating(GAME, "player", rating, date1));
        service.setRating(new Rating(GAME, "player", rating2, date2));

        assertEquals(rating2, service.getRating(GAME, "player"));

    }
}
