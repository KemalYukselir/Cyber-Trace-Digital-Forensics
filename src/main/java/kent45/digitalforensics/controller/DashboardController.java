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
import java.util.Random;

@Controller
public class DashboardController {

    final static int TEST_SCENARIO = 1;

    private final DatabaseService databaseService;

    private ArrayList<Integer> scenarioQueue = new ArrayList<>();
    private ScenarioJson currentScenario; 

    @Autowired
    public DashboardController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @GetMapping("/dashboard")
    public ModelAndView getDashboard() {
        if (scenarioQueue.size() == 0) {
            scenarioQueue = databaseService.scenarioQueue();
        }
        currentScenario = databaseService.getScenario(scenarioQueue.remove(0));

        return new ModelAndView("dashboard")
                .addObject("scenario", currentScenario)
                // .addObject("loggedInUser", databaseService.getLoggedInUser())
                .addObject("getUsersCurrentScore", databaseService.getUsersCurrentScore(databaseService.getLoggedInUser()));
    }

    @PostMapping("/judgement/guilty")
    public String judgement_guilty(@RequestParam("userMultiplier") int userMultiplier,
                                   RedirectAttributes redirectAttributes) {
        var userScore = databaseService.getUsersCurrentScore(databaseService.getLoggedInUser());
        var newScore = currentScenario.isGuilty() ? userScore + (userMultiplier * currentScenario.difficulty()) : userScore - (userMultiplier);

        redirectAttributes.addFlashAttribute("judgement", "guilty");
        redirectAttributes.addFlashAttribute("isGuilty", currentScenario.isGuilty());
        redirectAttributes.addFlashAttribute("userScore", databaseService.updateUsersCurrentScore(databaseService.getLoggedInUser(), newScore));

        return "redirect:/dashboard";
    }
    
    @PostMapping("/judgement/innocent")
    public String judgement_innocent(@RequestParam("userMultiplier") int userMultiplier,
                                           RedirectAttributes redirectAttributes) {
        var userScore = databaseService.getUsersCurrentScore(databaseService.getLoggedInUser());
        var newScore = !currentScenario.isGuilty() ? userScore + (userMultiplier * currentScenario.difficulty()) : userScore - (userMultiplier);

        redirectAttributes.addFlashAttribute("judgement", "innocent");
        redirectAttributes.addFlashAttribute("isGuilty", currentScenario.isGuilty());
        redirectAttributes.addFlashAttribute("userScore", databaseService.updateUsersCurrentScore(databaseService.getLoggedInUser(), newScore));

        return "redirect:/dashboard";
    }
}
