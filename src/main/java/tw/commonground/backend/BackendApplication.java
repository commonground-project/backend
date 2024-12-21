package tw.commonground.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SuppressWarnings("HideUtilityClassConstructor")
public class BackendApplication {
    /**
     * The entry point of the application.
     *
     * @param args command-line arguments (if any)
     */
    public static void main(final String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
