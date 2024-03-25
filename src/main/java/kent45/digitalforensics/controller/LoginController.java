package kent45.digitalforensics.controller;

import kent45.digitalforensics.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

    private final DatabaseService databaseService;

    @Autowired
    public LoginController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }


    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @PostMapping("/login")
    // @RequestParam("The name of the input in the html file")
    public ModelAndView login(@RequestParam("username") String username) {

        databaseService.addUser(username);
        databaseService.logInUser(username);
        databaseService.updateUsersCurrentScore(0);
        return new ModelAndView("redirect:/mainMenu");
    }
}
