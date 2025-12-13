package com.teg.popcornium_api.integrations.wikipedia.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "wikipedia.api")
public class WikipediaApiProperties {
    private String baseUrl = "https://pl.wikipedia.org/w/api.php";
    private int searchLimit = 5;
}
