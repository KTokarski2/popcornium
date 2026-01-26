package com.teg.popcornium_api.integrations.azureopenai.config;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureOpenAiConfig {

    @Bean
    public OpenAIAsyncClient openAIAsyncClient(
            @Value("${spring.ai.azure.openai.endpoint}") String endpoint,
            @Value("${spring.ai.azure.openai.api-key}") String apiKey) {

        return new OpenAIClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(apiKey))
                .buildAsyncClient();
    }
}