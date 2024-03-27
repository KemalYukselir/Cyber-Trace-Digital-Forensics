package kent45.digitalforensics.controller;

import kent45.digitalforensics.model.ScenarioJson;
import kent45.digitalforensics.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Controller
public class DashboardController {

    private final DatabaseService databaseService;

    private ArrayList<Integer> scenarioQueue;
    private ScenarioJson currentScenario; 

    @Autowired
    public DashboardController(DatabaseService databaseService) {
        this.databaseService = databaseService;
        this.scenarioQueue = databaseService.scenarioQueue();
    }

    @GetMapping("/dashboard")
    public ModelAndView getDashboard(@RequestParam("fromMainMenu") boolean fromMainMenu) {
        if (fromMainMenu) {
            // Reset scenario queue and current score
            scenarioQueue = databaseService.scenarioQueue();
            databaseService.updateUsersCurrentScore(0);
        }

        if (scenarioQueue.size() == 0) {
            return gameOver();
        }

        currentScenario = databaseService.getScenario(scenarioQueue.remove(0));

        return new ModelAndView("dashboard")
                .addObject("scenario", currentScenario)
                .addObject("getUsersCurrentScore", databaseService.getUsersCurrentScore())
                .addObject("getUsersLogin", databaseService.getLoggedInUser());
    }

    @PostMapping("/judgement/guilty")
    public String judgement_guilty(@RequestParam("userMultiplier") int userMultiplier,
                                   @RequestParam("timeLeft") int timeLeft,
                                   RedirectAttributes redirectAttributes) {
        var correctJudgment = currentScenario.isGuilty();
        var userScore = databaseService.getUsersCurrentScore();
        var newScore = correctJudgment ? userScore + (userMultiplier * currentScenario.difficulty()) : userScore - (userMultiplier);
        databaseService.updateUsersCurrentScore(newScore);
        databaseService.updateGamePlayStats(correctJudgment, 300000 - timeLeft); // 5 minutes - time left

        // Redirects to the dashboard to make sure the url updates back to dashboard with attributes similar to MAV object
        redirectAttributes.addFlashAttribute("judgement", "guilty");
        redirectAttributes.addFlashAttribute("isGuilty", currentScenario.isGuilty());

        return "redirect:/dashboard?fromMainMenu=false";
    }

    @PostMapping("/judgement/innocent")
    public String judgement_innocent(@RequestParam("userMultiplier") int userMultiplier,
                                     @RequestParam("timeLeft") int timeLeft,
                                     RedirectAttributes redirectAttributes) {
        var correctJudgment = !currentScenario.isGuilty();
        var userScore = databaseService.getUsersCurrentScore();
        var newScore = correctJudgment ? userScore + (userMultiplier * currentScenario.difficulty()) : userScore - (userMultiplier);
        databaseService.updateUsersCurrentScore(newScore);
        databaseService.updateGamePlayStats(correctJudgment, 300000 - timeLeft); // 5 minutes - time left

        // Redirects to the dashboard to make sure the url updates back to dashboard with attributes similar to MAV object
        redirectAttributes.addFlashAttribute("judgement", "innocent");
        redirectAttributes.addFlashAttribute("isGuilty", currentScenario.isGuilty());

        return "redirect:/dashboard?fromMainMenu=false";
    }

    @GetMapping("/gameOver")
    public ModelAndView gameOver() {
        databaseService.setUsersHighScore();

        return new ModelAndView("gameOver")
                .addObject("gamePlayStats", databaseService.getGamePlayStats());

    }
}
