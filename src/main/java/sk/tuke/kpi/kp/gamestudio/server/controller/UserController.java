package sk.tuke.kpi.kp.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.kpi.kp.gamestudio.entity.User;
import sk.tuke.kpi.kp.gamestudio.service.interfaces.UserService;


@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class UserController {

    @Autowired
    private UserService userService;
    private User loggedUser = null;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/login")
    public String login(@RequestParam(required = false) String url, String login, String password) {
        if (userService.isLogCorrect(login, password)) {
            loggedUser = new User(login, password);
        }
        if (url != null) return "redirect:" + url;
        else return "redirect:/";
    }

    @RequestMapping("/register")
    public String register(@RequestParam(required = false) String url, String login, String password) {

        if(login.length() > 3 && password.length() > 6){
            if (! userService.isUserRegistered(login)) {
                loggedUser = new User(login, password);
                userService.addUser(loggedUser);
            }
        }

        if(url != null) return "redirect:"+url;
        else return "redirect:/";
    }

    @RequestMapping(value ="/getUser" , produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getUser( @RequestParam String login, @RequestParam(required = false) String password){
        User user = null;
        try{
            if (password != null)
                user = userService.getUser(login, password);
            else {
                if(userService.isUserRegistered(login))
                    return login;
            }
        }catch(Exception e){
            if(userService.isUserRegistered(login))
                return login;
        }

        if (user == null) return "null";
        return user.toString();
    }


    @RequestMapping("/changeUserName")
    public String changeUserName(@RequestParam(required = false) String url, String currentLogin, String currentPassword, String newLogin) {

        if(newLogin.length() > 3 ){
            if (userService.isLogCorrect(currentLogin, currentPassword)) {
                if (userService.changeLogin(currentLogin, newLogin)){
                    loggedUser = new User(newLogin, currentPassword);
                }
            }
        }

        if(url != null) return "redirect:"+url;
        else return "redirect:/";
    }

    @RequestMapping("/changeUserPassword")
    public String changeUserPassword(@RequestParam(required = false) String url, String currentLogin, String currentPassword, String newPassword) {

        if(newPassword.length() > 6 ){
            if (userService.isLogCorrect(currentLogin, currentPassword)) {
                if (userService.changePassword(currentLogin,currentPassword,newPassword)){
                    loggedUser = new User(currentLogin, newPassword);
                }
            }
        }

        if(url != null) return "redirect:"+url;
        else return "redirect:/";
    }


    @RequestMapping("/logout")
    public String logout(@RequestParam(required = false) String url) {
        loggedUser = null;
        if(url != null) return "redirect:"+url;
        else return "redirect:/";
    }

    public boolean isLogged() {
        return loggedUser != null;
    }

    public User loggedUser(){
        return loggedUser;
    }

}
