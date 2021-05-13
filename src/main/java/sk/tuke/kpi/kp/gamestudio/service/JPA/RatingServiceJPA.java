package sk.tuke.kpi.kp.gamestudio.service.JPA;

import com.sun.istack.NotNull;
import sk.tuke.kpi.kp.gamestudio.entity.Rating;
import sk.tuke.kpi.kp.gamestudio.service.exceptions.RatingException;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.RatingService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Transactional
public class RatingServiceJPA implements RatingService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating) throws RatingException {
        if (rating == null) throw new RatingException("Rating null parameter");
        updateRating(rating);
    }

    private void updateRating(@NotNull Rating rating) {
        int oldRating = getRating(rating.getGame(), rating.getPlayer());
        if (oldRating != 0) {
            entityManager.createNamedQuery("Rating.updateRating")
                    .setParameter("game", rating.getGame())
                    .setParameter("player", rating.getPlayer())
                    .executeUpdate();
        }
        entityManager.persist(rating);
    }


    @Override
    public int getAverageRating(String game) throws RatingException {
        try {
            return (int) Math.round((double) entityManager.createNamedQuery("Rating.getAverageRating")
                    .setParameter("game", game).getSingleResult());
        } catch (Exception e) {
            throw new RatingException("This game is not Rated jet");
        }
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        try {
            return (int) entityManager.createNamedQuery("Rating.getRatingByName")
                    .setParameter("game", game)
                    .setParameter("player", player)
                    .getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }


    @Override
    public void reset() throws RatingException {
        entityManager.createNamedQuery("Rating.resetRating").executeUpdate();
    }
}
