package sk.tuke.kpi.kp.gamestudio.service.REST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import sk.tuke.kpi.kp.gamestudio.entity.Comment;
import sk.tuke.kpi.kp.gamestudio.service.exceptions.CommentException;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.CommentService;

import java.util.Arrays;
import java.util.List;

public class CommentServiceRestClient implements CommentService {
    @Value("${remote.server.api}/comment")
    private String url;

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public void addComment(Comment comment) throws CommentException {
        restTemplate.postForEntity(url, comment, Comment.class);
    }

    @Override
    public List<Comment> getComments(String game) throws CommentException {
        return Arrays.asList(restTemplate.getForEntity(url + "/" + game, Comment[].class).getBody());
    }

    @Override
    public void reset() throws CommentException {
        throw new UnsupportedOperationException("Not supported via web service");
    }
}
