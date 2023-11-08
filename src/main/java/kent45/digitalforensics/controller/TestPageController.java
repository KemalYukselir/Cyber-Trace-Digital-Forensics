package kent45.digitalforensics.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class TestPageController {
    @GetMapping("/test")
    public String test(@RequestParam(name="name", required=false, defaultValue="World") String name,
                       Model model) {
        model.addAttribute("name", name);
        return "test";
    }
}
