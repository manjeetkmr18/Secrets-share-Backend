package com.OnePassLink.backend.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class RootController {

    @GetMapping("/")
    public RedirectView home() {
        return new RedirectView("/swagger-ui/");
    }

    @GetMapping("/health")
    @ResponseBody
    public String health() {
        return "OnePassLink API is running! Available endpoints:\n" +
               "- Swagger UI: /swagger-ui/\n" +
               "- API Docs: /v3/api-docs\n" +
               "- Health Check: /actuator/health\n" +
               "- API Endpoints: /api/secrets";
    }

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "Application is running successfully!";
    }

    @GetMapping("/debug")
    @ResponseBody
    public String debug() {
        return "Available Swagger UI paths to try:\n" +
               "- /swagger-ui.html\n" +
               "- /swagger-ui/index.html\n" +
               "- /swagger-ui/\n" +
               "- /v3/api-docs (JSON)\n" +
               "- /v3/api-docs/swagger-config\n" +
               "\nTry these URLs one by one to find which one works!";
    }
}
