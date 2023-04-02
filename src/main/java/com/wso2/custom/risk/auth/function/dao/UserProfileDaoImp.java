package com.wso2.custom.risk.auth.function.dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.wso2.custom.risk.auth.function.util.ConfigReader;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Optional;

public class UserProfileDaoImp implements UserProfileDao {

    private static volatile UserProfileDaoImp instance;
    private MongoClient mongoClient;
    private MongoDatabase riskAuthDatabase;
    private MongoCollection<Document> userAttributeCollection;

    private final static String DATABASE_NAME = "WSO2_IS_RISK_AUTH";
    private final static String COLLECTION_NAME = "USER_ATTR";



    private UserProfileDaoImp () {

    }

    public static UserProfileDaoImp getInstance() {

        if (instance == null) {
            synchronized (UserProfileDaoImp.class) {
                if (instance == null) {
                    instance = new UserProfileDaoImp();
                }
            }
        }
        return instance;
    }

    @Override
    public Optional<Document> getPreviousUserLoginDataByUserId(String userId) {

        try {
            createConnection();
            Document userDocument = userAttributeCollection.find(new Document("user_id", userId)).first();
            if (userDocument != null) {
                return Optional.of(userDocument);
            }
            return Optional.empty();
        } finally {
            mongoClient.close();
        }
    }

    @Override
    public void updateUserLoginData(String userId, Document userProfile) {

        try {
            createConnection();
            Bson filter = Filters.eq("userId", userId);
            userAttributeCollection.replaceOne(filter, userProfile);
        } finally {
            mongoClient.close();
        }
    }

    @Override
    public void createUserDocument(Document userProfile) {

        try {
            createConnection();
            userAttributeCollection.insertOne(userProfile);
        } finally {
            mongoClient.close();
        }
    }

    private void createConnection() {

        Optional<String> dbUriOptional = ConfigReader.getDBUri();
        if (dbUriOptional.isPresent()) {
            this.mongoClient = MongoClients.create(dbUriOptional.get());
        } else {
            throw new UnsupportedOperationException("URI is required to create the connection with the MongoDB.");
            // TODO add the support to just create the mongo connection with host, port, username & password
            //  (without the mongoURI).
        }
        this.riskAuthDatabase = mongoClient.getDatabase(DATABASE_NAME);
        this.userAttributeCollection = riskAuthDatabase.getCollection(COLLECTION_NAME);
    }
}
