package com.wso2.custom.risk.auth.function;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
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


    private final static String MONGO_DB_URI = "mongodb://localhost:27017";
    private final static String DATABASE_NAME = "WSO2_IS_RISK_AUTH";
    private final static String COLLECTION_NAME = "USER_ATTR";

    private MongoCollection<Document> userAttributeCollection;

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

        MongoClient mongoClient = createMongoClient();
        MongoDatabase riskAuthDatabase = mongoClient.getDatabase(DATABASE_NAME);
        this.userAttributeCollection = riskAuthDatabase.getCollection(COLLECTION_NAME);

        Optional<Document> userDocument = getUserDocument(userId);
        if (userDocument.isEmpty()) {
            userAttributeCollection.insertOne(userProfile.getDocumentFromObject());
            return 1;
        }

        /*
        * 1. Get attributes as a as List<NameValuePair> params = new ArrayList<NameValuePair>();.
        * 2. Create http post and send it.
        * 3. Return Risk Score.
        * */


        mongoClient.close();
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



    private MongoClient createMongoClient() {

        return MongoClients.create(MONGO_DB_URI);
    }

    private Optional<Document> getUserDocument(String userId) {

        Document userDocument = userAttributeCollection.find(new Document("user_id", userId)).first();
        if (userDocument != null) {
            return Optional.of(userDocument);
        }
        return Optional.empty();
    }

    // TODO: Need to introduce another function to update the user Attributes after successful login.

    private int getHourOfDay() {

        DateTime dt = new DateTime();
        return dt.getHourOfDay();
    }


    private HttpPost sendHttpPostRequest(HttpServletRequest httpServletRequest) {

        String userAgentHeader = httpServletRequest.getHeader(USER_AGENT_HEADER_NAME);
        Locale locale = httpServletRequest.getLocale();
        UserAgent userAgent = new UserAgent(userAgentHeader);
        String userDevice = userAgent.getDevice();
        String userBrowser = userAgent.getBrowser();
        String userPlatform = userAgent.getPlatform();
        String clientIpAddress = IdentityUtil.getClientIpAddress(httpServletRequest);
        int hourOfTheDay = getHourOfDay();

        HttpPost post = new HttpPost("http://jakarata.apache.org/");
        return post;
    }



}
