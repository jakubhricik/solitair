package sk.tuke.kpi.kp.gamestudio.service.JPA;

import sk.tuke.kpi.kp.gamestudio.entity.User;
import sk.tuke.kpi.kp.gamestudio.service.exceptions.UserException;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.UserService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Transactional
public class UserServiceJPA implements UserService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addUser(User user) throws UserException {
        if(!isUserRegistered(user.getLogin())){
            entityManager.persist(user);
        }
    }

    @Override
    public boolean isUserRegistered(String login) throws UserException {
        try{
            return entityManager.createNamedQuery("User.isUserRegistered")
                    .setParameter("login", login)
                    .getSingleResult() != null;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean isLogCorrect(String login, String password) throws UserException {
        try{
            return entityManager.createNamedQuery("User.isLogCorrect")
                    .setParameter("login", login)
                    .setParameter("password", password)
                    .getSingleResult() != null;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean changeLogin(String login, String newLogin) throws UserException {
        try{
            entityManager.createNamedQuery("User.changeLogin")
                    .setParameter("newLogin", newLogin)
                    .setParameter("login", login)
                    .executeUpdate();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean changePassword(String login, String password, String newPassword) throws UserException {
        try{
            entityManager.createNamedQuery("User.changePassword")
                    .setParameter("newPassword", newPassword)
                    .setParameter("login", login)
                    .setParameter("password", password)
                    .executeUpdate();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public User getUser(String login, String password) throws UserException {
        try {
            return  (User) entityManager.createNamedQuery("User.getUser")
                    .setParameter("login", login)
                    .setParameter("password", password)
                    .getSingleResult();
        }catch (Exception e){
            return null;
        }
    }


    @Override
    public void reset() throws UserException {
        entityManager.createNamedQuery("User.resetUser").executeUpdate();
    }
}
