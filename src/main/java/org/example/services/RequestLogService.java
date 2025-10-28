package org.example.services;

import org.example.model.RequestLog;
import org.example.repository.RequestLogRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RequestLogService {
    public final RequestLogRepository requestLogRepository;

    public RequestLogService(RequestLogRepository requestLogRepository) {
        this.requestLogRepository = requestLogRepository;
    }

    public void saveLog(String requestUri, String ipAddress, String countryCode, String isp, int responseCode, long duration){
       RequestLog requestLog = new RequestLog();
        requestLog.setRequestId(UUID.randomUUID().toString());
        requestLog.setRequestUri(requestUri);
        requestLog.setIpAddress(ipAddress);
        requestLog.setCountryCode(countryCode);
        requestLog.setIsp(isp);
        requestLog.setResponseCode(responseCode);
        requestLog.setTimestamp(Instant.now());
        requestLog.setTimeLapsedMs(duration);
        requestLogRepository.save(requestLog);
    }
}
