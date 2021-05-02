package sk.tuke.kpi.kp.gamestudio.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.tuke.kpi.kp.gamestudio.entity.User;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.UserService;

@RestController
@RequestMapping("/api/user")
public class UserServiceRest {
    @Autowired
    private UserService userService;

    @GetMapping("/{login}")
    public boolean isUserRegistered(@PathVariable String login){
        return userService.isUserRegistered(login);
    }

    @GetMapping("/{login}/{password}")
    public boolean getRating(@PathVariable String login, @PathVariable String password){
        return userService.isLogCorrect(login,password);
    }

    @PostMapping
    public void addUser(@RequestBody User user){
        userService.addUser(user);
    }

}
