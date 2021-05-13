package sk.tuke.kpi.kp.gamestudio.entity;


import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

//    public static final String SELECT = "SELECT game, player, comment, commentedOn FROM comment WHERE game = ? LIMIT 10";

@Entity
@NamedQuery(name = "Comment.getLastComments",
        query = "SELECT c FROM Comment c WHERE c.game=:game ORDER BY c.commentedOn DESC")

@NamedQuery(name = "Comment.resetComment",
        query = "DELETE FROM Comment")

public class Comment {

    @Id
    @GeneratedValue
    private int ident;

    private String game;

    private String player;

    private String comment;

    private Date commentedOn;

    public Comment() {
    }

    public Comment(String game, String player, String comment, Date commentedOn) throws IllegalArgumentException {
        this.game = game;
        this.player = player;
        setComment(comment);
        this.commentedOn = commentedOn;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        if (comment.length() > 1024) {
            throw new IllegalArgumentException("Comment is too long.");
        } else if (comment.length() == 0) {
            throw new IllegalArgumentException("There is no comment.");
        }
        this.comment = comment;
    }

    public Date getCommentedOn() {
        return commentedOn;
    }

    public void setCommentedOn(Date commentedOn) {
        this.commentedOn = commentedOn;
    }

    public int getIdent() {
        return ident;
    }

    public void setIdent(int ident) {
        this.ident = ident;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "game='" + game + '\'' +
                ", player='" + player + '\'' +
                ", comment='" + comment + '\'' +
                ", commentedOn=" + commentedOn +
                '}';
    }
}
