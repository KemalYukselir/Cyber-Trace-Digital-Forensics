package kent45.digitalforensics.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GameOverController {

    @GetMapping("/gameOver")
    public ModelAndView gameOver() {
        return new ModelAndView("gameOver");
    }
}
