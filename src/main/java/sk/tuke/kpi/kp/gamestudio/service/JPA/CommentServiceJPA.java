package sk.tuke.kpi.kp.gamestudio.service.JPA;

import sk.tuke.kpi.kp.gamestudio.entity.Comment;
import sk.tuke.kpi.kp.gamestudio.service.exceptions.CommentException;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.CommentService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
public class CommentServiceJPA implements CommentService {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public void addComment(Comment comment) throws CommentException {
        entityManager.persist(comment);
    }

    @Override
    public List<Comment> getComments(String game) throws CommentException {
        return  entityManager.createNamedQuery("Comment.getLastComments")
                .setParameter("game", game).setMaxResults(10).getResultList();
    }

    @Override
    public void reset() throws CommentException {
        entityManager.createNamedQuery("Comment.resetComment").executeUpdate();
    }
}
