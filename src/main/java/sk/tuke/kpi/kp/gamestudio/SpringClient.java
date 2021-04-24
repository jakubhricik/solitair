package sk.tuke.kpi.kp.gamestudio;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.client.RestTemplate;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.consoleUi.ConsoleUi;
import sk.tuke.kpi.kp.gamestudio.game.solitaire.core.GameBoard;
import sk.tuke.kpi.kp.gamestudio.service.REST.CommentServiceRestClient;
import sk.tuke.kpi.kp.gamestudio.service.REST.RatingServiceRestClient;
import sk.tuke.kpi.kp.gamestudio.service.REST.ScoreServiceRestClient;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.CommentService;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.RatingService;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.ScoreService;

@SpringBootApplication
@Configuration
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
        pattern = "sk.tuke.kpi.kp.gamestudio.server.*"))
public class SpringClient {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringClient.class).web(WebApplicationType.NONE).run(args);
    }

    @Bean
    public CommandLineRunner runner(ConsoleUi ui, GameBoard gameBoard ) {
        return args -> ui.play(gameBoard);
    }

    @Bean
    public ConsoleUi consoleUI() {
        return new ConsoleUi();
    }

    @Bean
    public GameBoard gameBoard() {
        return new GameBoard();
    }

    @Bean
    public ScoreService scoreService() {
//        return new ScoreServiceJPA();
        return new ScoreServiceRestClient();
    }


    @Bean
    public RatingService ratingService() {
//        return new RatingServiceJPA();
        return new RatingServiceRestClient();
    }

    @Bean
    public CommentService commentService() {
//        return new CommentServiceJPA();
        return new CommentServiceRestClient();
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
