package sk.tuke.kpi.kp.gamestudio.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.tuke.kpi.kp.gamestudio.service.JPA.CommentServiceJPA;
import sk.tuke.kpi.kp.gamestudio.service.JPA.RatingServiceJPA;
import sk.tuke.kpi.kp.gamestudio.service.JPA.ScoreServiceJPA;
import sk.tuke.kpi.kp.gamestudio.service.JPA.UserServiceJPA;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.CommentService;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.RatingService;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.ScoreService;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.UserService;

@SpringBootApplication
@Configuration
@EntityScan("sk.tuke.kpi.kp.gamestudio.entity")
public class GameStudioServer {
    public static void main(String[] args) {
        SpringApplication.run(GameStudioServer.class, args);
    }

    @Bean
    public ScoreService scoreService() {
        return new ScoreServiceJPA();
    }

    @Bean
    public CommentService commentService() {
        return new CommentServiceJPA();
    }

    @Bean
    public RatingService ratingService() {
        return new RatingServiceJPA();
    }

    @Bean
    public UserService userService() {
        return new UserServiceJPA();
    }
}
