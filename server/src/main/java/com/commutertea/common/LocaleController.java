package com.commutertea.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/locale")
public class LocaleController {

    private final IpLocaleService ipLocaleService;

    public LocaleController(IpLocaleService ipLocaleService) {
        this.ipLocaleService = ipLocaleService;
    }

    @GetMapping("/detect")
    public Map<String, String> detect(HttpServletRequest request) {
        String language = ipLocaleService.detectLanguage(
                resolveClientIp(request),
                request.getHeader("Accept-Language")
        );
        return Map.of("language", language);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String real = request.getHeader("X-Real-IP");
        if (real != null && !real.isBlank()) {
            return real.trim();
        }
        return request.getRemoteAddr();
    }
}
