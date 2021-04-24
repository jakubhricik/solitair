package sk.tuke.kpi.kp.gamestudio.entity;


import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;



@Entity
@NamedQuery( name = "Rating.getAverageRating",
        query = "SELECT AVG(r.rating) FROM Rating r WHERE r.game=:game")
@NamedQuery( name = "Rating.getRatingByName",
        query = "SELECT r.rating FROM Rating r WHERE r.game=:game AND r.player=:player")
@NamedQuery( name = "Rating.updateRating",
        query = "DELETE FROM Rating r WHERE r.game=:game AND r.player=:player")
@NamedQuery( name = "Rating.resetRating",
        query = "DELETE FROM Rating")

public class Rating {

    @Id
    @GeneratedValue
    private int ident;

    private String game;

    private String player;

    private int rating;

    private Date ratingOn;

    public Rating(){}

    public Rating(String game, String player, int rating, Date ratingOn) {
        this.game = game;
        this.player = player;
        setRating(rating);
        this.ratingOn = ratingOn;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if(rating > 5) this.rating = 5;
        else this.rating = Math.max(rating, 1);
    }

    public Date getRatingOn() {
        return ratingOn;
    }

    public void setRatingOn(Date ratingOn) {
        this.ratingOn = ratingOn;
    }

    public int getIdent() { return ident; }

    public void setIdent(int ident) { this.ident = ident; }

    @Override
    public String toString() {
        return "Rating{" +
                "game='" + game + '\'' +
                ", player='" + player + '\'' +
                ", rating=" + rating +
                ", playedOn=" + ratingOn +
                '}';
    }
}
