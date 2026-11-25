package com.commutertea.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** 根据客户端 IP 推断国家，再映射到 App 支持的语言；失败时降级用 Accept-Language。 */
@Service
public class IpLocaleService {

    private static final Logger log = LoggerFactory.getLogger(IpLocaleService.class);

    private static final String DEFAULT_LANGUAGE = "zh";
    private static final Set<String> SUPPORTED = Set.of("zh", "en", "ja", "ko", "es", "fr", "de", "pt");

    private static final Map<String, String> COUNTRY_TO_LANGUAGE = Map.ofEntries(
            Map.entry("CN", "zh"), Map.entry("HK", "zh"), Map.entry("MO", "zh"), Map.entry("TW", "zh"),
            Map.entry("JP", "ja"),
            Map.entry("KR", "ko"),
            Map.entry("ES", "es"), Map.entry("MX", "es"), Map.entry("AR", "es"), Map.entry("CL", "es"),
            Map.entry("CO", "es"), Map.entry("PE", "es"), Map.entry("VE", "es"), Map.entry("EC", "es"),
            Map.entry("GT", "es"), Map.entry("BO", "es"), Map.entry("PY", "es"), Map.entry("UY", "es"),
            Map.entry("CR", "es"), Map.entry("DO", "es"), Map.entry("NI", "es"), Map.entry("HN", "es"),
            Map.entry("SV", "es"), Map.entry("PA", "es"),
            Map.entry("FR", "fr"), Map.entry("BE", "fr"), Map.entry("LU", "fr"),
            Map.entry("DE", "de"), Map.entry("AT", "de"), Map.entry("CH", "de"),
            Map.entry("BR", "pt"), Map.entry("PT", "pt")
    );

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String detectLanguage(String ip, String acceptLanguage) {
        // 当前阶段统一使用中文；需要按 IP 分发时恢复下面的块注释逻辑即可。
        return DEFAULT_LANGUAGE;
        /*
        if (ip != null && !isPrivate(ip)) {
            String country = queryCountry(ip);
            if (country != null) {
                String language = COUNTRY_TO_LANGUAGE.get(country.toUpperCase(Locale.ROOT));
                if (language != null) {
                    return language;
                }
            }
        }
        return fromAcceptLanguage(acceptLanguage);
        */
    }

    private String queryCountry(String ip) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://ip-api.com/json/" + ip + "?fields=status,countryCode"))
                    .timeout(Duration.ofSeconds(2))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode body = objectMapper.readTree(response.body());
            if ("success".equals(body.path("status").asText())) {
                return body.path("countryCode").asText();
            }
        } catch (Exception error) {
            log.debug("IP 定位失败，降级用 Accept-Language: {}", error.getMessage());
        }
        return null;
    }

    private String fromAcceptLanguage(String header) {
        if (header == null || header.isBlank()) {
            return DEFAULT_LANGUAGE;
        }
        for (String part : header.split(",")) {
            String code = part.split(";")[0].trim().replace('_', '-').toLowerCase(Locale.ROOT);
            if (SUPPORTED.contains(code)) {
                return code;
            }
            String prefix = code.split("-")[0];
            if (SUPPORTED.contains(prefix)) {
                return prefix;
            }
        }
        return DEFAULT_LANGUAGE;
    }

    private boolean isPrivate(String ip) {
        return ip.startsWith("127.") || ip.startsWith("10.") || ip.startsWith("192.168.")
                || ip.equals("0:0:0:0:0:0:0:1") || ip.equals("::1")
                || (ip.startsWith("172.") && isPrivate172(ip));
    }

    private boolean isPrivate172(String ip) {
        try {
            int second = Integer.parseInt(ip.split("\\.")[1]);
            return second >= 16 && second <= 31;
        } catch (Exception error) {
            return false;
        }
    }
}
