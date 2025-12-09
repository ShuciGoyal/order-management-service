package com.example.oms.config;

import java.sql.SQLException;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Start H2's built-in web console server on a separate port for local development.
 * Controlled by spring.h2.console.enabled (reuse existing property).
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.h2.console", name = "enabled", havingValue = "true")
public class H2WebServerStarter {

    private static final Logger log = LoggerFactory.getLogger(H2WebServerStarter.class);

    @Value("${spring.h2.console.port:8082}")
    private int h2Port;

    private Server webServer;

    @PostConstruct
    public void start() {
        // Start the H2 WebServer (console) on a separate port if possible.
        // Don't fail application startup if the port is already in use — log and proceed.
        try {
            webServer = Server.createWebServer("-webPort", String.valueOf(h2Port), "-tcpAllowOthers");
            webServer.start();
            log.info("Started H2 web console on port {}", h2Port);
        } catch (SQLException e) {
            // BindException or other SQL-related startup problems should not block the app or tests.
            log.warn("Unable to start H2 web console on port {}: {}", h2Port, e.getMessage());
            webServer = null;
        } catch (RuntimeException e) {
            log.warn("Unexpected error while starting H2 web console: {}", e.getMessage());
            webServer = null;
        }
    }

    @PreDestroy
    public void stop() {
        if (webServer != null) {
            webServer.stop();
        }
    }
}
