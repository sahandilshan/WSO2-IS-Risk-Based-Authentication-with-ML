package com.wso2.custom.risk.auth.function;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.wso2.custom.risk.auth.function.dao.UserProfileDaoImp;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpPost;
import org.bson.Document;
import org.joda.time.DateTime;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsServletRequest;
import org.wso2.carbon.identity.application.authentication.framework.exception.UserIdNotFoundException;
import org.wso2.carbon.identity.application.authentication.framework.store.SessionDataStore;
import org.wso2.carbon.identity.core.model.UserAgent;
import org.wso2.carbon.identity.core.util.IdentityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

public class GetRiskScoreImpl implements GetRiskScore {

    private static final Log log = LogFactory.getLog(SessionDataStore.class);
    private final static String USER_AGENT_HEADER_NAME = "user-agent";
    private UserProfile userProfile;

    @Override
    public int getRiskScoreFromContext(JsAuthenticationContext context, JsServletRequest request) {

        String userId;
        try {
            userId = context.getContext().getLastAuthenticatedUser().getUserId();
        } catch (UserIdNotFoundException e) {
            log.debug("User ID not found, hence setting the risk score to a higher value.");
            return 1;
        }

        initUserAttributes(userId, request.getWrapped().getWrapped());
        Optional<Document> previousUserLoginData = UserProfileDaoImp.getInstance().getPreviousUserLoginDataByUserId(userId);
        if (previousUserLoginData.isEmpty()) {
            UserProfileDaoImp.getInstance().createUserDocument(userProfile.getDocumentFromObject());
            return 1;
        }

        /*
        * 1. Get attributes as a as List<NameValuePair> params = new ArrayList<NameValuePair>();.
        * 2. Create http post and send it.
        * 3. Return Risk Score.
        * */

        return 0;
    }

    private void initUserAttributes(String userId, HttpServletRequest request) {

        String userAgentHeader = request.getHeader(USER_AGENT_HEADER_NAME);
        UserAgent userAgent = new UserAgent(userAgentHeader);
        userProfile = new UserProfile.UserProfileBuilder(userId)
                .setIpAddress(IdentityUtil.getClientIpAddress(request))
                .setBrowser(userAgent.getBrowser())
                .setDevice(userAgent.getDevice())
                .setPlatform(userAgent.getPlatform())
                .setLanguage(request.getLocale().getLanguage())
                .build();
    }

    // TODO: Need to introduce another function to update the user Attributes after successful login.


    private HttpPost sendHttpPostRequest(Document previousLoginData) {

        return null;
    }



}
