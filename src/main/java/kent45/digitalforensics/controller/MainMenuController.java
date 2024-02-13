package kent45.digitalforensics.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainMenuController {

    @GetMapping("/mainMenu")
    public ModelAndView mainMenu() {
        return new ModelAndView("mainMenu");
    }

    @GetMapping("/leaderboard")
    public ModelAndView leaderboard() {
        return new ModelAndView("leaderboard");
    }

    @GetMapping("/tutorial")
    public ModelAndView tutorial() {
        return new ModelAndView("tutorial");
    }

    @GetMapping("/credits")
    public ModelAndView credits() {
        return new ModelAndView("credits");
    }
}
