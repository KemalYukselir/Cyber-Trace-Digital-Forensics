package kent45.digitalforensics.controller;

import kent45.digitalforensics.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestPageController {

    @Autowired
    private TestService testService;

    @GetMapping("/test")
    public ModelAndView test(@RequestParam(name="name", required=false, defaultValue="World") String name) {
        var testData = testService.getTestData();

        return new ModelAndView("test")
                .addObject("name", name)
                .addObject("data", testData);
    }
}
