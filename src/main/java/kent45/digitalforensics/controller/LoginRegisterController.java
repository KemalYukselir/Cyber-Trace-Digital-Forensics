package kent45.digitalforensics.controller;

import kent45.digitalforensics.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginRegisterController {

    @Autowired
    private DatabaseService databaseService;

    @GetMapping("/register")
    public ModelAndView register() {
        return new ModelAndView("register")
                .addObject("failedRegister", false);
    }

    @PostMapping("/register")
    // @RequestParam("The name of the input in the html file")
    public ModelAndView register(@RequestParam("username") String username) {

        // Attempts to create a user
        if (databaseService.createUser(username)) {
            // Return the loginSuccess modelAndView
            return new ModelAndView("redirect:/dashboard");
        }

        // If cannot create a new user (Username taken) redirect back to register
        return new ModelAndView("register")
                .addObject("failedRegister", true);
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login")
                .addObject("failedLogin", false);
    }

    @PostMapping("/login")
    // @RequestParam("The name of the input in the html file")
    public ModelAndView login(@RequestParam("username") String username) {

        // Attempts to create a user
        if (databaseService.checkLogin(username)) {
            // Return the loginSuccess modelAndView
            return new ModelAndView("redirect:/dashboard");
        }

        // If cannot create a new user (Username taken) redirect back to register
        return new ModelAndView("login")
                .addObject("failedLogin", true)
                // Re-add username and password so they persist
                .addObject("username", username);
    }
}
