package kent45.digitalforensics.controller;

import kent45.digitalforensics.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainMenuController {

    private final DatabaseService databaseService;

    @Autowired
    public MainMenuController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @GetMapping("/mainMenu")
    public ModelAndView mainMenu() {
        return new ModelAndView("mainMenu");
    }

    @GetMapping("/leaderboard")
    public ModelAndView leaderboard() {
        return new ModelAndView("leaderboard")
                .addObject("highScores", databaseService.getLeaderboardData());
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
