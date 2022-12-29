package com.wso2.custom.risk.auth.function.util;

import com.wso2.custom.risk.auth.function.UserProfile;

import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.UserIdNotFoundException;
import org.wso2.carbon.identity.core.model.UserAgent;
import org.wso2.carbon.identity.core.util.IdentityUtil;

import javax.servlet.http.HttpServletRequest;

public class RiskScoreUtil {

    private final static String USER_AGENT_HEADER_NAME = "user-agent";

    private RiskScoreUtil() {


    }

    public static String getUserIdFromContext(JsAuthenticationContext context) throws UserIdNotFoundException {

        return context.getContext().getLastAuthenticatedUser().getUserId();
    }

    public static UserProfile getUserProfileFromHttpRequest(String userId, HttpServletRequest httpServletRequest) {

        String userAgentHeader = httpServletRequest.getHeader(USER_AGENT_HEADER_NAME);
        UserAgent userAgent = new UserAgent(userAgentHeader);
        return new UserProfile.UserProfileBuilder(userId)
                .setIpAddress(IdentityUtil.getClientIpAddress(httpServletRequest))
                .setBrowser(userAgent.getBrowser())
                .setDevice(userAgent.getDevice())
                .setPlatform(userAgent.getPlatform())
                .setLanguage(httpServletRequest.getLocale().getLanguage())
                .build();
    }

}
