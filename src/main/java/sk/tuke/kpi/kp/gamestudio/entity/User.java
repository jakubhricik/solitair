package sk.tuke.kpi.kp.gamestudio.entity;

import javax.persistence.*;


@Entity
@NamedQuery( name = "User.resetUser",
        query = "DELETE FROM User")
@NamedQuery( name = "User.isUserRegistered",
        query = "select u from User u where u.login =: login")
@NamedQuery( name = "User.isLogCorrect",
        query = "select u from User u where u.login =: login AND u.password =: password")
@NamedQuery( name = "User.getUser",
        query = "select u from User u where u.login =: login AND u.password =: password")
@NamedQuery( name = "User.changeLogin",
        query = "update User u set u.login=:newLogin where u.login=:login")
@NamedQuery( name = "User.changePassword",
        query = "update User u set u.password=:newPassword where u.login=:login and u.password=:password")
@Table(name = "Player")
public class User {

    @Id
    @GeneratedValue
    private int ident;

    private String login;
    private String password;

    public User(){}

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public String toString() {
        return "User{" +
                "ident=" + ident +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
