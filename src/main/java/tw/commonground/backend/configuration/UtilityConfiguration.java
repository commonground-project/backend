package tw.commonground.backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class UtilityConfiguration {
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
