package org.example.model;

import java.time.Instant;

public class RequestLog {
    private String requestId;
    private String requestUri;
    private Instant timestamp;
    private int responseCode;
    private String ipAddress;
    private String countryCode;
    private String isp;
    private long timeLapsedMs;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    public long getTimeLapsedMs() {
        return timeLapsedMs;
    }

    public void setTimeLapsedMs(long timeLapsedMs) {
        this.timeLapsedMs = timeLapsedMs;
    }
}
