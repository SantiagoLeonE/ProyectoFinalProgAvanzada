package co.edu.uniquindio.gestionacademica.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IAConfig {

    //Bean que Spring AI usa para comunicarse con Groq
    @Bean
    @ConditionalOnProperty(name = "app.ia.habilitada", havingValue = "true")
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}
