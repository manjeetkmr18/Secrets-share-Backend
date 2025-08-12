package com.OnePassLink.backend.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ActuatorConfig {

    // Configuration is handled via application.yml
    // This class exists for future custom actuator beans if needed

    // Health endpoint is enabled by default
    // Prometheus endpoint is enabled via management.endpoints.web.exposure.include=health,prometheus
    // All other endpoints are disabled for security
}
