package sk.tuke.kpi.kp.gamestudio.server.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.kpi.kp.gamestudio.entity.Score;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.ScoreService;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/score")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class ScoreController {

    @Autowired
    private ScoreService scoreService;


    @RequestMapping("/saveScore")
    public String saveScore(@RequestParam(required = false) String url,
                            @RequestParam String game,
                            @RequestParam String player,
                            @RequestParam int points) {

        Score newScore = new Score(game, player, points, new Date());
        scoreService.addScore(newScore);

        if (url != null) return "redirect:" + url;
        else return "redirect:/";
    }


    @RequestMapping(value = "/getTopScores", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getTopScores(@RequestParam String game) {
        StringBuilder sb = new StringBuilder();
        List<Score> topScores = scoreService.getTopScores(game);
        int numberOfRow = 1;

        sb.append("<table class=\"table table-hover table-striped table-bordered\">");
        sb.append("<caption>TOP PLAYERS</caption>\n");
        sb.append("<thead class=\"table-dark\" >\n<tr>");
        sb.append("<th scope=\"col\">#</th>\n" +
                "<th scope=\"col\">Player</th>\n" +
                "<th scope=\"col\">Points</th>\n" +
                "<th scope=\"col\">Date</th>");
        sb.append("</tr>\n</thead>");

        sb.append("<tbody>");

        for (Score score : topScores) {
            sb.append("<tr>");
            sb.append("<th scope=\"row\">").append(numberOfRow).append("</th>");
            sb.append("<td>").append(score.getPlayer()).append("</td>");
            sb.append("<td>").append(score.getPoints()).append("</td>");
            sb.append("<td>").append(score.getPlayedOn()).append("</td>");
            sb.append("</tr>");
            numberOfRow++;
        }
        sb.append("</tbody>");
        sb.append("</table>");

        return sb.toString();
    }
}
