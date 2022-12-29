package com.wso2.custom.risk.auth.function;

import com.wso2.custom.risk.auth.function.dao.UserProfileDaoImp;
import com.wso2.custom.risk.auth.function.util.RiskScoreUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsServletRequest;
import org.wso2.carbon.identity.application.authentication.framework.exception.UserIdNotFoundException;

public class UpdateUserProfileImpl implements UpdateUserProfile {

    private static final Log log = LogFactory.getLog(UpdateUserProfileImpl.class);

    @Override
    public void updateUserProfile(JsAuthenticationContext context, JsServletRequest request) {

        String userId;
        try {
            userId = RiskScoreUtil.getUserIdFromContext(context);
            UserProfile userProfile = RiskScoreUtil.getUserProfileFromHttpRequest(userId,
                    request.getWrapped().getWrapped());
            UserProfileDaoImp.getInstance().updateUserLoginData(userId, userProfile.getDocumentFromObject());
        } catch (UserIdNotFoundException e) {
            log.debug("User ID not found, Hence user profile won't be updated with the latest login information's.");
        }

    }
}
