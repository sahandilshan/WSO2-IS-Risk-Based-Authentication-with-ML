# Risk-Based Authentication with Machine Learning

This repo introduce a Risk-Based adaptive authentication flow for WSO2 Identity Server 6.0.0 with Machine Learning.

## Architecture
<todo></todo>


### Prerequisites


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

