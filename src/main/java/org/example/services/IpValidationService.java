package org.example.services;

import jakarta.servlet.http.HttpServletRequest;
import org.example.model.IpInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class IpValidationService {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    private static final List<String> BLOCKED_COUNTRIES = List.of("China","Spain","United States");
    private static final List<String> BLOCKED_ISPS = List.of("Amazon","Google","Microsoft");
    public static final String FIELDS = "query,status,isp,country,countryCode,message";

    // could add the base ip for validation to the application.yaml
    public IpValidationService(RestTemplate restTemplate, @Value("${ip.api.url:http://ip-api.com/json/}")String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    //only allowing X-Forwarded-For for testing purposes.
    public String extractClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if(forwardedFor != null && !forwardedFor.isEmpty()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    public IpInfo validateIp(String ip) {
        String url =
                UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment(ip)
                .queryParam("fields", FIELDS)
                .toUriString();

        ResponseEntity<IpInfo> response = restTemplate.getForEntity(url,IpInfo.class);

        IpInfo ipInfo = response.getBody();
        if (ipInfo == null || !"success".equalsIgnoreCase(ipInfo.getStatus())) {
            throw new SecurityException("Failed to validate IP: " + ipInfo.getMessage());
        }
        if (BLOCKED_COUNTRIES.contains(ipInfo.getCountry())) {
            throw new SecurityException("Blocked IP from restricted country: " + ipInfo.getCountry());
        }
        for (String blockedIsp : BLOCKED_ISPS) {
            if (ipInfo.getIsp() != null && ipInfo.getIsp().toLowerCase().contains(blockedIsp.toLowerCase())) {
                throw new SecurityException("Blocked IP from restricted ISP: " + ipInfo.getIsp());
            }
        }
        return ipInfo;
    }
}
