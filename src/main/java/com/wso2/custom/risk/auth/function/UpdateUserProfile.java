package com.wso2.custom.risk.auth.function;

import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsServletRequest;
import org.wso2.carbon.identity.application.authentication.framework.exception.UserIdNotFoundException;
import org.wso2.carbon.utils.ServerException;

public interface UpdateUserProfile {

    void updateUserProfile(JsAuthenticationContext context, JsServletRequest request);
}
