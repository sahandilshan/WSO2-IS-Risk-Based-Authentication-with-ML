package com.wso2.custom.risk.auth.function.dao;

import org.bson.Document;

import java.util.Optional;

public interface UserProfileDao {

    Optional<Document> getPreviousUserLoginDataByUserId(String userId);

    void UpdateUserLoginData(Document userProfile);

    void createUserDocument(Document userProfile);


}
