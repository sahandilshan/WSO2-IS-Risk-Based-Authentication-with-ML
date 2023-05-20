# Risk-Based Authentication with Machine Learning

This repo introduce a Risk-Based adaptive authentication flow for WSO2 Identity Server 6.0.0 with Machine Learning.

## Architecture
![image](https://github.com/sahandilshan/WSO2-IS-Risk-Based-Authentication-with-ML/assets/32576163/90c956ff-edcf-45e3-ac31-bf4479c0c640)

1. The Identity Server initiates the custom Adaptive authentication function (getRiskScoreFromContext).
2. The Risk Score Calculator (Custom Adaptive Auth function) retrieves the Login Data from MongoDB, if available.
3. The Risk Score Calculator sends a request to the Python server, providing the login data for analysis by the ML model to determine the Risk Score of the user login.
4. After analyzing the Risk Score, the Python server sends a response back to the Risk Score Calculator.
5. The Risk Score Calculator communicates the Risk Score back to the Identity Server.

### Prerequisites
* Python 3.9
* MongoDB
* Identity Server

### Setup Python Server (ML Model)

### Setup Database (MongoDB)

### Setup Risk-Based Authentication configuration

### How to use the custom adaptive authentication script

Sample Script
```js
// Start of Risk-Based Authentication with Machine Learning

var onLoginRequest = function(context) {
  doLogin(context);
};

var doLogin = function(context) {
   executeStep(1, {
       onSuccess : function(context){
           var riskScore = getRiskScoreFromContext(context, context.request);
           Log.info("#### Risk Score #####" + riskScore);
           if (riskScore >= 1) {
               executeStep(2, {
                   onSuccess: function(context) {
                       updateUserRiskProfile(context, context.request);
                   }
               });
           } else {
               updateUserRiskProfile(context, context.request);
           }
           
           
       },
       onFail : function(context) {
           // Retry the login..
           doLogin(context);
       }
   });
};


// End of Risk-Based Authentication with Machine Learning
```

