package kent45.digitalforensics.controller;

import kent45.digitalforensics.service.DatabaseService;
import kent45.digitalforensics.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

// @Controller annotation is used to make the @GetMapping work
@Controller
public class TestPageController {

    // Rather than creating a service using new TestService etc, Autowiring does the
    // instantiating in the background and will give the controller a fully made service.
    // Works the same as having a constructor which takes an already made class.
    // TestPageController(TestService testService) {...
    @Autowired
    private TestService testService;

    @Autowired
    private DatabaseService databaseService;

    // @GetMapping(X) Maps the www.bla.com/X to the method below
    @GetMapping("/test")
    // RequestParam(name="Y") is for optional param like www.bla.com/X?Y=hello
    public ModelAndView test(@RequestParam(name="name", required=false, defaultValue="World") String name) {
        // Controllers should only be used to map an endpoint and give data to it.
        // Data processing and getting etc. should be done in services.
        var testData = testService.getTestData();

        // ModelAndView("Z") will map to the Z.html in the templates folder
        return new ModelAndView("test")
                // Data is passed to the template using attributes, with a name and the data.
                .addObject("name", name)
                .addObject("data", testData);
    }

    @GetMapping("/register")
    public ModelAndView register() {
        return new ModelAndView("register")
                .addObject("failedRegister", false);
    }

    @PostMapping("/register")
    // @RequestParam("The name of the input in the html file")
    public ModelAndView register(@RequestParam("userName") String userName,
                                 @RequestParam("passWord") String passWord) {

        // Attempts to create a user
        if (databaseService.createUser(userName, passWord)) {
            // Return the loginSuccess modelAndView
            return new ModelAndView("loginSuccess");
        }

        // If cannot create a new user (Username taken) redirect back to register
        return new ModelAndView("register")
                .addObject("failedRegister", true);
    }
}
