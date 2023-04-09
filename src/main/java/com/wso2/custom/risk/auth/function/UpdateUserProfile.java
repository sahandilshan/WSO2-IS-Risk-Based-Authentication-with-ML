package com.wso2.custom.risk.auth.function;

import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsServletRequest;

@FunctionalInterface
public interface UpdateUserProfile {

    void updateUserRiskProfile(JsAuthenticationContext context, JsServletRequest request);
}
