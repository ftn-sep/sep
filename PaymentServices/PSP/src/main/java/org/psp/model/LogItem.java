package org.psp.model;


import jakarta.persistence.Id;
import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor
@Timestamp("timestamp")
public class LogItem {
    @Id
    private String id;

    @Field(name = "time")
    private Date timestamp;

    @Field(name = "text")
    private String value;

    @Field(name = "class")
    private String className;

    @Field(name = "url")
    private String requestURI;

    @Field(name = "level")
    private LogLevel logLevel;

    @Field(name = "params")
    private String params;

    @Field(name = "ip")
    private String ip;

    public LogItem(String value, String className, String requestURI, LogLevel logLevel, String params, String remoteAddr) {
        this.value = value;
        this.className = className;
        this.requestURI = requestURI;
        this.logLevel = logLevel;
        this.params = params;
        LocalDateTime localDateTime = LocalDateTime.now();  // Replace with your LocalDateTime object
        ZoneId zoneId = ZoneId.systemDefault();  // or specify the desired time zone
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        Instant instant = zonedDateTime.toInstant();
        this.timestamp = Date.from(instant);//LocalDateTime.now();
        this.ip = remoteAddr;
    }
}