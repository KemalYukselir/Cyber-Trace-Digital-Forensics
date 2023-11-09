package kent45.digitalforensics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class DigitalForensicsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DigitalForensicsApplication.class, args);
	}

}
