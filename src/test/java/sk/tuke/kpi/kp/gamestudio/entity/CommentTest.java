package sk.tuke.kpi.kp.gamestudio.entity;

import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class CommentTest {

    private final String GAME = "solitaire";

    private final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_";
    private final SecureRandom RANDOM = new SecureRandom();

    public String randomString(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; ++i) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }


    @Test
    public void bigComment(){
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> new Comment(GAME, "player", randomString(2000), new Date()) ,
                "Expected new Comment() to throw IllegalArgumentException, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Comment is too long."));
    }

    @Test
    public void smallComment(){
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> new Comment(GAME, "player", "" , new Date()) ,
                "Expected new Comment() to throw IllegalArgumentException, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("There is no comment."));
    }



}
