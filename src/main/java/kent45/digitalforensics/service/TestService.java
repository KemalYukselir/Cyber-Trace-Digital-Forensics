package kent45.digitalforensics.service;

import org.springframework.stereotype.Service;

import java.util.List;

// @Service annotation is used to for the autowiring in the Controller.
@Service
public class TestService {

    public List<String> getTestData() {
        return List.of(
                "This",
                "is",
                "test",
                "data"
            );
    }
}
