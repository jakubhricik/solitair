package sk.tuke.kpi.kp.gamestudio.service.JDBC;

import org.springframework.http.ResponseEntity;
import sk.tuke.kpi.kp.gamestudio.entity.Rating;
import sk.tuke.kpi.kp.gamestudio.service.ConnectionConstantsJDBC;
import sk.tuke.kpi.kp.gamestudio.service.exceptions.RatingException;
import sk.tuke.kpi.kp.gamestudio.service.exceptions.ScoreException;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.RatingService;

import java.sql.*;

public class RatingServiceJDBC implements RatingService {

    public static final String INSERT = "INSERT INTO rating (game, player, rating, ratingOn) VALUES (?, ?, ?, ?)";
    public static final String SELECT_RATING = "SELECT rating FROM rating WHERE game = ? and player = ? ORDER BY ratingOn DESC";
    public static final String SELECT_AVERAGE = "SELECT AVG(rating) FROM rating WHERE game = ?";
    public static final String DELETE = "DELETE FROM rating";


    @Override
    public void setRating(Rating rating) throws RatingException {
        try (Connection connection = DriverManager.getConnection(ConnectionConstantsJDBC.URL, ConnectionConstantsJDBC.USER, ConnectionConstantsJDBC.PASSWORD);
             PreparedStatement statement = connection.prepareStatement(INSERT)
        ) {
            statement.setString(1, rating.getGame());
            statement.setString(2, rating.getPlayer());
            statement.setInt(3, rating.getRating());
            statement.setTimestamp(4, new Timestamp(rating.getRatingOn().getTime()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RatingException("Problem inserting rating", e);
        }
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        try (Connection connection = DriverManager.getConnection(ConnectionConstantsJDBC.URL, ConnectionConstantsJDBC.USER, ConnectionConstantsJDBC.PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT_AVERAGE);
        ) {
            statement.setString(1, game);
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                return (int) Math.round(rs.getDouble(1));
            }
        } catch (SQLException e) {
            throw new ScoreException("Problem selecting average rating", e);
        }
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        try (Connection connection = DriverManager.getConnection(ConnectionConstantsJDBC.URL, ConnectionConstantsJDBC.USER, ConnectionConstantsJDBC.PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT_RATING);
        ) {
            statement.setString(1, game);
            statement.setString(2, player);
            try(ResultSet rs = statement.executeQuery()){
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new ScoreException("Problem selecting player score", e);
        }
    }

    @Override
    public void reset() throws RatingException {
        try (Connection connection = DriverManager.getConnection(ConnectionConstantsJDBC.URL, ConnectionConstantsJDBC.USER, ConnectionConstantsJDBC.PASSWORD);
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(DELETE);
        } catch (SQLException e) {
            throw new RatingException("Problem deleting rating", e);
        }
    }
}
