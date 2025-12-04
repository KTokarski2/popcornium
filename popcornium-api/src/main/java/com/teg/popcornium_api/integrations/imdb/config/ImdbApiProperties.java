package com.teg.popcornium_api.integrations.imdb.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "imdb.api")
public class ImdbApiProperties {
    private String baseUrl;
    private int timeout = 5000;
}
