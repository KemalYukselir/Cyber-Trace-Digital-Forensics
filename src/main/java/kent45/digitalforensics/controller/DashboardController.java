package kent45.digitalforensics.controller;

import kent45.digitalforensics.model.Scenario;
import kent45.digitalforensics.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DashboardController {

    final static int TEST_SCENARIO = 1;

    private final DatabaseService databaseService;

    @Autowired
    public DashboardController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @GetMapping("/dashboard")
    public ModelAndView getDashboard() {
        Scenario scenario = databaseService.getScenario(TEST_SCENARIO);

        return new ModelAndView("dashboard")
                .addObject("scenario", scenario);
    }

    @GetMapping("/judgement")
    public ModelAndView getJudgementScreen() {
        return new ModelAndView("judgement");
    }
}
