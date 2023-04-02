package com.wso2.custom.risk.auth.function.util;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConfigReader {
    private static Map<String, String> configMap = new HashMap<>();

    private ConfigReader() {

    }


    public static void loadProperties() {

        // DB properties.
        configMap.put("mongo.uri", "mongodb://localhost:27017");

        // Server properties.
        configMap.put("server.uri", "http://localhost:5000");

        // User properties.
        configMap.put("user.save_ip_address", "true");
    }

    public static Optional<String> getProperty(String propertyName) {

        if (configMap.containsKey(propertyName)) {
            return Optional.of(configMap.get(propertyName));
        }
        return Optional.empty();
    }

    public static String getProperty(String propertyName, String defaultValue) {

        if (configMap.containsKey(propertyName)) {
            return configMap.get(propertyName);
        }
        return defaultValue;
    }

    public static Optional<String> getDBUri() {

        return getProperty("mongo.uri");
    }

    public static boolean canSaveIpAddress() {

        String property = getProperty("user.save_ip_address", "false");
        return Boolean.parseBoolean(property);
    }

    public static String getServerUri() {

        return getProperty("server.uri", "https://localhost:5000");
    }
}
