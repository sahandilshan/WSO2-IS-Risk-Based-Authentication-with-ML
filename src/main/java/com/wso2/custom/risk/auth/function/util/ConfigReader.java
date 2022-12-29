package com.wso2.custom.risk.auth.function.util;

import org.apache.commons.lang.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties;

    private ConfigReader() {

    }


    public static void readProperties() {

        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
            // Load a properties file.
            properties = new Properties();
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static Optional<String> getProperty(String propertyName) {

        String property = properties.getProperty(propertyName);
        if (StringUtils.isNotEmpty(property)) {
            return Optional.of(property);
        }
        return Optional.empty();
    }

    public static Optional<String> getDBUri() {

        return getProperty("mongo.uri");
    }

    public static Optional<String> getDbUser() {

        return getProperty("mongo.user");
    }

    public static Optional<String> getDBPassword() {

        return getProperty("mongo.password");
    }

    public static String getDBHost() {

        return properties.getProperty("mongo.host", "localhost");
    }

    public static String getDBPort() {

        return properties.getProperty("mongo.port", "27017");
    }

    public static boolean isSSLEnabled() {

        String property = properties.getProperty("mongo.ssl_enabled", "false");
        return Boolean.parseBoolean(property);
    }

    public static boolean canSaveIpAddress() {

        String property = properties.getProperty("user.save_ip_address", "false");
        return Boolean.parseBoolean(property);
    }

    public static String getServerHostName() {

        return properties.getProperty("server.host.name", "localhost");
    }

    public static String getServerPort() {

        return properties.getProperty("server.port", "5000");
    }
}
