package tw.commonground.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BackendApplication {
    /**
     * Handles requests to the root URL ("/") and returns a greeting message.
     *
     * @return A string greeting message.
     */
    @RequestMapping("/")
    public String home() {
        return "Hello Docker World";
    }

    /**
     * The entry point of the application.
     *
     * @param args command-line arguments (if any)
     */
    public static void main(final String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
