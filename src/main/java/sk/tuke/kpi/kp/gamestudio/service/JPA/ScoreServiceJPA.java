package sk.tuke.kpi.kp.gamestudio.service.JPA;

import sk.tuke.kpi.kp.gamestudio.entity.Score;
import sk.tuke.kpi.kp.gamestudio.service.exceptions.ScoreException;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.ScoreService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
public class ScoreServiceJPA implements ScoreService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addScore(Score score) throws ScoreException {
        entityManager.persist(score);
    }

    @Override
    public List<Score> getTopScores(String game) throws ScoreException {
        return entityManager.createNamedQuery("Score.getTopScores")
                .setParameter("game", game).setMaxResults(10).getResultList();
    }

    @Override
    public void reset() {
        entityManager.createNamedQuery("Score.resetScores").executeUpdate();
    }
}
