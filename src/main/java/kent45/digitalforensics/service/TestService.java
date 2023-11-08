package kent45.digitalforensics.service;

import org.springframework.stereotype.Service;

import java.util.List;

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
