package org.example.repository;

import org.example.model.RequestLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;

@Repository
public class RequestLogRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public RequestLogRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void save(RequestLog requestLog){
        String sql = """
                INSERT INTO request_log
                (id, request_uri, request_ts, response_code, ip_address, country_code, isp, time_lapsed_ms)
                VALUES(:id, :request_uri, :request_ts, :response_code, :ip_address, :country_code, :isp, :time_lapsed_ms)
                """;
        var params = new MapSqlParameterSource()
                .addValue("id", requestLog.getRequestId())
                .addValue("request_uri", requestLog.getRequestUri())
                .addValue("request_ts", Timestamp.from(requestLog.getTimestamp()))
                .addValue("response_code", requestLog.getResponseCode())
                .addValue("ip_address", requestLog.getIpAddress())
                .addValue("country_code", requestLog.getCountryCode())
                .addValue("isp", requestLog.getIsp())
                .addValue("time_lapsed_ms", requestLog.getTimeLapsedMs());
        jdbcTemplate.update(sql, params);
    }

}
