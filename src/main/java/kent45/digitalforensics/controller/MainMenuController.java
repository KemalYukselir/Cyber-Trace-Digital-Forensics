package kent45.digitalforensics.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller("/mainMenu")
public class MainMenuController {

    @GetMapping
    public ModelAndView mainMenu() {
        return new ModelAndView("mainMenu");
    }
}
