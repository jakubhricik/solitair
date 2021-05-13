package sk.tuke.kpi.kp.gamestudio.server.webservice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.tuke.kpi.kp.gamestudio.entity.Rating;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.RatingService;

@RestController
@RequestMapping("/api/rating")
public class RatingServiceRest {

    @Autowired
    private RatingService ratingService;

    @GetMapping("/{game}")
    public int getAverageRating(@PathVariable String game) {
        return ratingService.getAverageRating(game);
    }

    @GetMapping("/{game}/{player}")
    public int getRating(@PathVariable String game, @PathVariable String player) {
        return ratingService.getRating(game, player);
    }

    @PostMapping
    public void addRating(@RequestBody Rating rating) {
        ratingService.setRating(rating);
    }

}
