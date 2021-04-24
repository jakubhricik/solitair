package sk.tuke.kpi.kp.gamestudio.service.JDBC;

import org.junit.jupiter.api.Test;
import sk.tuke.kpi.kp.gamestudio.entity.Comment;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.CommentService;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommentServiceTest {

    private final String GAME = "solitaire";

    private CommentServiceJDBC createService() {
        return new CommentServiceJDBC();
    }

    @Test
    public void testReset(){
        CommentService service = createService();
        service.reset();
        assertEquals(0, service.getComments(GAME).size());
    }

    @Test
    public void testAddComment(){
        CommentService service = createService();
        service.reset();
        Date date = new Date();
        service.addComment(new Comment(GAME, "player", "some comment", date));

        List<Comment> comments = service.getComments(GAME);

        assertEquals(1, comments.size());

        assertEquals(GAME, comments.get(0).getGame());
        assertEquals("player", comments.get(0).getPlayer());
        assertEquals("some comment", comments.get(0).getComment());
        assertEquals(date, comments.get(0).getCommentedOn());
    }

    @Test
    public void testAddComment3(){
        CommentService service = createService();
        service.reset();
        Date date = new Date();
        service.addComment(new Comment(GAME, "player1", "some comment1", date));
        service.addComment(new Comment(GAME, "player2", "some comment2", date));
        service.addComment(new Comment(GAME, "player3", "some comment3", date));

        List<Comment> comments = service.getComments(GAME);

        assertEquals(3, comments.size());

        assertEquals(GAME, comments.get(0).getGame());
        assertEquals("player1", comments.get(0).getPlayer());
        assertEquals("some comment1", comments.get(0).getComment());
        assertEquals(date, comments.get(0).getCommentedOn());

        assertEquals(GAME, comments.get(1).getGame());
        assertEquals("player2", comments.get(1).getPlayer());
        assertEquals("some comment2", comments.get(1).getComment());
        assertEquals(date, comments.get(1).getCommentedOn());

        assertEquals(GAME, comments.get(2).getGame());
        assertEquals("player3", comments.get(2).getPlayer());
        assertEquals("some comment3", comments.get(2).getComment());
        assertEquals(date, comments.get(2).getCommentedOn());
    }

    @Test
    public void testAddComment13(){
        CommentService service = createService();
        service.reset();
        for(int i = 0; i < 13; i++)
            service.addComment(new Comment(GAME, "player1", "some comment1", new Date()));
        assertEquals(10, service.getComments(GAME).size());
    }
}
