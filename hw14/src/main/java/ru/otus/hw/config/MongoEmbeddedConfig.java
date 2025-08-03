package ru.otus.hw.config;

import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion;
import de.flapdoodle.embed.mongo.distribution.Version;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoEmbeddedConfig {

    @Bean
    public IFeatureAwareVersion embeddedMongoVersion() {
        return Version.V6_0_4;
    }
}
