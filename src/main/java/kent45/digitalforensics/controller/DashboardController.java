package kent45.digitalforensics.controller;

import kent45.digitalforensics.model.ScenarioJson;
import kent45.digitalforensics.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

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
                .addObject("scenario", currentScenario);
    }

    @GetMapping("/judgement")
    public ModelAndView getJudgementScreen() {
        return new ModelAndView("judgement");
    }

    @PostMapping("/judgement/guilty")
    public ModelAndView judgement_guilty() {
        return new ModelAndView("judgementScenario")
                .addObject("judgement", "guilty")
                .addObject("isGuilty", currentScenario.isGuilty());
    }

    @PostMapping("/judgement/innocent")
    public ModelAndView judgement_innocent() {
        return new ModelAndView("judgementScenario")
                .addObject("judgement", "innocent")
                .addObject("isGuilty", currentScenario.isGuilty());
    }
}
