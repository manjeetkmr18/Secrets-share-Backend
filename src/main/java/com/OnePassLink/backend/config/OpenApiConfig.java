package com.OnePassLink.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${app.title:OnePassLink API}")
    private String appTitle;

    @Value("${app.description:Secure one-time secret sharing service with zero-knowledge architecture}")
    private String appDescription;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(appTitle)
                        .version(appVersion)
                        .description(appDescription + "\n\n" +
                                "## Security Model\n" +
                                "- **Zero-knowledge**: Secrets are encrypted client-side before being sent to the server\n" +
                                "- **One-time access**: Secrets are automatically deleted after first read\n" +
                                "- **URL fragments**: Encryption keys are stored in URL fragments (#key) and never reach the server\n" +
                                "- **TTL expiry**: All secrets have configurable time-to-live limits\n\n" +
                                "## Usage Flow\n" +
                                "1. Client generates random encryption key\n" +
                                "2. Client encrypts secret with AES-GCM\n" +
                                "3. Client posts ciphertext to `/api/secrets`\n" +
                                "4. Server returns secret ID\n" +
                                "5. Share URL: `https://app/s/{id}#<key>`\n" +
                                "6. Recipient visits URL, client decrypts with fragment key\n" +
                                "7. First GET request deletes the secret atomically")
                        .contact(new Contact()
                                .name("OnePassLink")
                                .url("https://github.com/onepasslink")
                                .email("support@onepasslink.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development server"),
                        new Server()
                                .url("https://api.onepasslink.com")
                                .description("Production server")));
    }
}
