package com.wso2.custom.risk.auth.function;

import com.wso2.custom.risk.auth.function.util.ConfigReader;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {

    private final static  String DEVICE = "device";
    private final static  String BROWSER = "browser";
    private final static  String PLATFORM = "platform";
    private final static  String IP_ADDRESS = "ip";
    private final static String LANGUAGE = "lang";

    private final String userId;
    private String ipAddress;
    private String device;
    private String platform;
    private String browser;
    private String language;

    private UserProfile(String userId) {

        this.userId = userId;
    }

    public Document getDocumentFromObject() {

        Document userDocument = new Document();
        userDocument.append("user_id", userId)
                .append(IP_ADDRESS, ipAddress)
                .append(DEVICE, device)
                .append(PLATFORM, platform)
                .append(BROWSER, browser)
                .append("language", language);
        return userDocument;
    }

    public List<NameValuePair> compareWithPreviousLoginData(Document previousLoginData) {

        List<NameValuePair> payload = new ArrayList<>();
        String previousDevice = previousLoginData.getString(DEVICE);
        payload.add(new BasicNameValuePair(DEVICE, compareAttributes(this.device, previousDevice)));

        String previousPlatform = previousLoginData.getString(PLATFORM);
        payload.add(new BasicNameValuePair(PLATFORM, compareAttributes(this.platform, previousPlatform)));

        String previousBrowser = previousLoginData.getString(BROWSER);
        payload.add(new BasicNameValuePair(BROWSER, compareAttributes(this.browser, previousBrowser)));

        String previousLanguage = previousLoginData.getString(LANGUAGE);
        payload.add(new BasicNameValuePair(LANGUAGE, compareAttributes(this.language, previousLanguage)));

        /*
         * If ip saved enabled?
         *   Get IP details
         *   Compare it with current
         * Get
         * */
        if (ConfigReader.canSaveIpAddress()) {
            String previousIp = previousLoginData.getString(IP_ADDRESS);
            payload.add(new BasicNameValuePair(IP_ADDRESS, compareAttributes(this.ipAddress, previousIp)));
            return payload;
        }
        payload.add(new BasicNameValuePair(IP_ADDRESS, "0")); // To ignore the IP attribute.
        return payload;
    }

    private String compareAttributes(String current, String previous) {

        if (StringUtils.equalsIgnoreCase(current, previous)) {
            return "0";
        }
        return "1";
    }

    public static class UserProfileBuilder {

        private final String userId;
        private String ipAddress;
        private String device;
        private String platform;
        private String browser;
        private String language;

        public UserProfileBuilder(String userId) {

            this.userId = userId;
        }

        public UserProfileBuilder setIpAddress(String ipAddress) {

            this.ipAddress = ipAddress;
            return this;
        }

        public UserProfileBuilder setBrowser(String browser) {
            this.browser = browser;
            return this;
        }

        public UserProfileBuilder setDevice(String device) {
            this.device = device;
            return this;
        }

        public UserProfileBuilder setPlatform(String platform) {
            this.platform = platform;
            return this;
        }

        public UserProfileBuilder setLanguage(String language) {
            this.language = language;
            return this;
        }

        public UserProfile build() {

            UserProfile userProfile = new UserProfile(this.userId);
            userProfile.browser = this.browser;
            userProfile.ipAddress = this.ipAddress;
            userProfile.device = this.device;
            userProfile.language = this.language;
            userProfile.platform = this.platform;
            return userProfile;
        }
    }

}
