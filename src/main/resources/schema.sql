CREATE TABLE IF NOT EXISTS request_log (
    id              UUID   PRIMARY KEY,
    request_uri     VARCHAR(512)  NOT NULL,
    request_ts      TIMESTAMP     NOT NULL,
    response_code   INT           NOT NULL,
    ip_address      VARCHAR(64)   NOT NULL,
    country_code    VARCHAR(8)    NOT NULL,
    isp             VARCHAR(256)  NOT NULL,
    time_lapsed_ms  BIGINT        NOT NULL
    );