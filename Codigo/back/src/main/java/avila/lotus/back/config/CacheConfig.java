package avila.lotus.back.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching // Ativa cache no Spring
public class CacheConfig {

  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager("passwordResetCodes");
    cacheManager.setCaffeine(Caffeine.newBuilder()
        .expireAfterWrite(2, TimeUnit.MINUTES) // Expira em 2 minutos
        .maximumSize(100) // Até 100 códigos armazenados
        .recordStats() // Ativa registro das estatísticas para métricas
    );
    return cacheManager;
  }
}
