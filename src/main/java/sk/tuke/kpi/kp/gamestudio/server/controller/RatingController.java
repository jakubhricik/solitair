package sk.tuke.kpi.kp.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.kpi.kp.gamestudio.entity.Rating;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.RatingService;

import java.util.Date;

@Controller
@RequestMapping("/rating")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @RequestMapping(value = "/getAverageRating", produces = MediaType.TEXT_HTML_VALUE )
    @ResponseBody
    public String getAverageRating(@RequestParam String game) {
        StringBuilder sb = new StringBuilder();
        int countOfStars = 5;
        for (int filledStar = 0; filledStar < ratingService.getAverageRating(game); filledStar++){
            sb.append("<span class='star filedStar'> \n");
            sb.append("☆");
            sb.append("</span> \n");
            countOfStars--;
        }
        while(countOfStars != 0){
            sb.append("<span class='star'> \n");
            sb.append("☆");
            sb.append("</span> \n");
            countOfStars--;
        }
        return sb.toString();
    }

    @RequestMapping("/rateGame")
    public String raeGame(@RequestParam(required = false) String url,
                          @RequestParam String game,
                          @RequestParam String player,
                          @RequestParam int rating){
        Rating gameRating = new Rating(game, player, rating, new Date());
        ratingService.setRating(gameRating);

        if(url != null) return "redirect:"+url;
        else return "redirect:/";
    }

    @RequestMapping("/getRating")
    @ResponseBody
    public int getRating(@RequestParam String game, @RequestParam String player){
        return ratingService.getRating(game, player);
    }
}
