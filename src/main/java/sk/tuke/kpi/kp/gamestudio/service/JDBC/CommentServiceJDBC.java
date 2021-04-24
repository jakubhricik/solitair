package sk.tuke.kpi.kp.gamestudio.service.JDBC;

import sk.tuke.kpi.kp.gamestudio.entity.Comment;
import sk.tuke.kpi.kp.gamestudio.service.ConnectionConstantsJDBC;
import sk.tuke.kpi.kp.gamestudio.service.exceptions.CommentException;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.CommentService;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentServiceJDBC implements CommentService {

    public static final String INSERT = "INSERT INTO comment (game, player, comment, commentedOn) VALUES (?, ?, ?, ?)";
    public static final String SELECT = "SELECT game, player, comment, commentedOn FROM comment WHERE game = ? LIMIT 10";
    public static final String DELETE = "DELETE FROM comment";


    @Override
    public void addComment(Comment comment) throws CommentException {
        try (Connection connection = DriverManager.getConnection(ConnectionConstantsJDBC.URL, ConnectionConstantsJDBC.USER, ConnectionConstantsJDBC.PASSWORD);
             PreparedStatement statement = connection.prepareStatement(INSERT)
        ) {
            statement.setString(1, comment.getGame());
            statement.setString(2, comment.getPlayer());
            statement.setString(3, comment.getComment());
            statement.setTimestamp(4, new Timestamp(comment.getCommentedOn().getTime()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new CommentException("Problem inserting comment", e);
        }
    }

    @Override
    public List<Comment> getComments(String game) throws CommentException {
        try (Connection connection = DriverManager.getConnection(ConnectionConstantsJDBC.URL, ConnectionConstantsJDBC.USER, ConnectionConstantsJDBC.PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT)
        ) {
            statement.setString(1, game);
            try (ResultSet rs = statement.executeQuery()) {
                List<Comment> comments = new ArrayList<>();
                while (rs.next()) {
                    comments.add(new Comment(rs.getString(1), rs.getString(2), rs.getString(3), rs.getTimestamp(4)));
                }
                return comments;
            }
        } catch (SQLException e) {
            throw new CommentException("Problem selecting comment", e);
        }
    }

    @Override
    public void reset() throws CommentException {
        try (Connection connection = DriverManager.getConnection(ConnectionConstantsJDBC.URL, ConnectionConstantsJDBC.USER, ConnectionConstantsJDBC.PASSWORD);
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(DELETE);
        } catch (SQLException e) {
            throw new CommentException("Problem deleting comment", e);
        }
    }
}
