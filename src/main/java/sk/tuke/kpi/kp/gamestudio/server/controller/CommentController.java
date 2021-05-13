package sk.tuke.kpi.kp.gamestudio.server.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.kpi.kp.gamestudio.entity.Comment;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.CommentService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/comment")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class CommentController {

    @Autowired
    private CommentService commentService;

    @RequestMapping("/addComment")
    public String saveScore(@RequestParam(required = false) String url,
                            @RequestParam String game,
                            @RequestParam String player,
                            @RequestParam String commentText) {

        if (game != null && player != null && commentText.length() > 3 && commentText.length() < 255) {
            Comment comment = new Comment(game, player, commentText, new Date());
            commentService.addComment(comment);
        }

        if (url != null) return "redirect:" + url;
        else return "redirect:/";
    }


    @RequestMapping(value = "/getComments", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getComments(@RequestParam String game,
                              @RequestParam(required = false) Integer max,
                              @RequestParam(required = false) Integer offset) {
        StringBuilder sb = new StringBuilder();
        List<Comment> comments = commentService.getComments(game);
        int numOfComments = 10;
        int commentOffset = 0;
        int numberOfStartingComment = 0;

        try {
            if (max != null)
                numOfComments = max;
            if (offset != null)
                commentOffset = offset;

        } catch (Exception e) {
            //toto znamena ze neprisli parametre
        }


        numberOfStartingComment = commentOffset * numOfComments;

        for (Comment comment : comments) {
            if (numOfComments > 0 && numberOfStartingComment <= 0) {
                sb.append("<div class='comment'>\n");

                sb.append("<h2>").append(comment.getPlayer()).append("</h2>\n");
                sb.append("<i>").append(comment.getCommentedOn()).append("</i>\n");
                sb.append("<p>").append(comment.getComment()).append("</p>\n");

                sb.append("</div>\n");
                numOfComments--;
            }
            numberOfStartingComment--;
        }
        return sb.toString();
    }

}
