package sk.tuke.kpi.kp.gamestudio.service.interfaces;


import sk.tuke.kpi.kp.gamestudio.entity.User;
import sk.tuke.kpi.kp.gamestudio.service.exceptions.UserException;


public interface UserService {
    void addUser(User user) throws UserException;
    boolean isUserRegistered(String login) throws UserException;
    boolean isLogCorrect(String login, String password) throws UserException;
    boolean changeLogin(String login, String newLogin) throws UserException;
    boolean changePassword(String login, String password, String newPassword) throws UserException;
    User getUser(String login, String password) throws UserException;
    void reset() throws UserException;
}
