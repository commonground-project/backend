package tw.commonground.backend.configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

//@Configuration
//@EnableCaching
public class CachingConfig {

//    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }

}
