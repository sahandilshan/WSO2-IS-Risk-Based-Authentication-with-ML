package com.wso2.custom.risk.auth.function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.custom.risk.auth.function.dao.UserProfileDaoImp;
import com.wso2.custom.risk.auth.function.util.ConfigReader;
import com.wso2.custom.risk.auth.function.util.RiskScoreUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bson.Document;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsServletRequest;
import org.wso2.carbon.identity.application.authentication.framework.exception.UserIdNotFoundException;
import org.wso2.carbon.utils.ServerException;

import java.io.IOException;
import java.rmi.UnexpectedException;
import java.util.List;
import java.util.Optional;

public class GetRiskScoreImpl implements GetRiskScore {

    private static final Log log = LogFactory.getLog(GetRiskScoreImpl.class);

    @Override
    public int getRiskScoreFromContext(JsAuthenticationContext context, JsServletRequest request) throws ServerException {

        String userId;
        try {
            userId = RiskScoreUtil.getUserIdFromContext(context);
        } catch (UserIdNotFoundException e) {
            log.debug("User ID not found, hence setting the risk score to a higher value.");
            return 1;
        }

        UserProfile userProfile = RiskScoreUtil.getUserProfileFromHttpRequest(userId, request.getWrapped().getWrapped());
        Optional<Document> previousUserLoginData = UserProfileDaoImp.getInstance().getPreviousUserLoginDataByUserId(userId);
        if (previousUserLoginData.isEmpty()) {
            UserProfileDaoImp.getInstance().createUserDocument(userProfile.getDocumentFromObject());
            return 1;
        }
        try {
            HttpResponse response = sendHttpPostRequest(previousUserLoginData.get(), userProfile);
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(responseString).getAsJsonObject();
            JsonElement jsonElement = jsonObject.get("risk-score");
            return Integer.parseInt(jsonElement.getAsString());

        } catch (IOException e) {
            throw new ServerException("An error occurred while connecting to the server to calculate the risk-score.");
        }
    }

    private HttpResponse sendHttpPostRequest(Document previousLoginData, UserProfile userProfile) throws IOException {

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String uri = ConfigReader.getServerUri() + "/calculate_risk_score";
            HttpPost httpPost = new HttpPost(uri);

            List<NameValuePair> nameValuePairs = userProfile.compareWithPreviousLoginData(previousLoginData);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

            CloseableHttpResponse response = httpclient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new UnexpectedException("An error has occurred while calculation the risk-score.");
            }
            return response;
        }
    }

}
