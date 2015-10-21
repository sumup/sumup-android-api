#SumUp Android Payment API

This documentation is intended for app versions 1.55.0 and up.

##I. Getting Started
* Create a SumUp account and get an affiliate key <a href="https://me.sumup.com/integration-tools" target="_blank">here</a>

##II. How to call the Payment API

* **From your app**  
  * [Option 1](#api-helper) - Use our API Helper library for easy integration
  * [Option 2](#uri-call) - Use a URI to call the API 
* **From your mobile website** [Payment API - Web](#payment-api---web)

The sample app provided in this repository can be used as a reference.

<a href="https://sumup.com/integration" target="_blank">Full SumUp API Documentation</a>

##API Helper

#####1. Add the repository to your gradle dependencies
```groovy
allprojects {
   repositories {
      maven { url 'https://maven.sumup.com/releases' }
   }
}
```

#####2. Add the dependency to a module 
```groovy
compile 'com.sumup:merchant-api:1.0'
```

#####3. Provide a callback activity
```xml
	<activity android:name="com.example.ResultActivity"  android:label="Payment Result">
	  <intent-filter>
	    <action android:name="com.example.ResultActivity"></action>
	    <category android:name="android.intent.category.DEFAULT"></category>
	    <category android:name="android.intent.category.BROWSABLE"></category>
	  </intent-filter>
	</activity>
```

#####4. Make a payment
```java
    SumUpPayment payment = SumUpPayment.builder()
            //mandatory parameters
            // Your affiliate key is bound to the applicationID entered in the SumUp dashboard at https://me.sumup.com/integration-tools
            .affiliateKey("YOUR_AFFILIATE_KEY")
            .productAmount(1.23)
            .currency(SumUpPayment.Currency.EUR)
            // optional: add details
            .productTitle("Taxi Ride")
            .receiptEmail("customer@mail.com")
            .receiptSMS("+3531234567890")
            // optional: Add metadata
            .addAdditionalInfo("AccountId", "taxi0334")
            .addAdditionalInfo("From", "Paris")
            .addAdditionalInfo("To", "Berlin")
            //optional: foreign transaction ID, must be unique!
            .foreignTransactionId(UUID.randomUUID().toString())  // can not exceed 128 chars
            .build();

    SumUpAPI.openPaymentActivity(MainActivity.this, ResponseActivity.class, payment);
```

##URI call

#####1. Provide a callback activity
```xml
    <activity
          android:name="com.example.URLResponseActivity"
          android:label="Payment Result">
            <intent-filter>
                <action android:name="android.intent.action.VIEW"/>
                <action android:name="com.example.URLResponseActivity"/>
                <category android:name="android.intent.category.DEFAULT"/>
                <category android:name="android.intent.category.BROWSABLE"/>
                <!-- Provide your own scheme here and reference it when you make a payment -->
                 <data
                   android:scheme="mycallbackscheme"
                   android:host="result"/>
            </intent-filter>
        </activity>
```

#####2. Make a  payment
```java
  Intent payIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                        "sumupmerchant://pay/1.0"
                                + "?affiliate-key="YOUR_AFFILIATE_KEY""
                                + "&app-id=com.example.myapp"
                                + "&amount=1.23"
                                + "&currency=EUR"
                                + "&title=Taxi Ride"
                                + "&receipt-mobilephone=+3531234567890"
                                + "&receipt-email=customer@mail.com"
                                + "&foreign-tx-id=" + UUID.randomUUID().toString()
                                + "&callback=mycallbackscheme://result"));

                startActivityForResult(payIntent, 0);
```

The result is received as a URI in the callback activity intent: 

```java
Uri result = getIntent().getData()
```

Success:
```
mycallbackscheme://result?smp-status=success&smp-message=Transaction%20successful.&smp-receipt-sent=false&smp-tx-code=123ABC&foreign-tx-id=0558637a-b73c-43ad-b358-f93cb909251x
```

Failure:
```
mycallbackscheme://result?smp-status=failed&smp-failure-cause=transaction-failed&smp-message=Transaction%20failed.&smp-receipt-sent=false&smp-tx-code=123ABC&foreign-tx-id=05c14c86-a7a0-49c5-a1ec-acb168f5198x
```

###Payment API - Web

Put a link onto your website

```
<a href="sumupmerchant://pay/1.0?affiliate-key=7ca84f17-84a5-4140-8df6-6ebeed8540fc&app-id=com.example.myapp&amount=1.23&currency=EUR&title=Taxi Ride&receipt-mobilephone=+3531234567890&receipt-email=customer@mail.com&callback=http://example.com/myapp/mycallback">Start SumUp Payment</a>
```

Make sure that the callback URL you provide is correct and controlled by you.

Success:
```
?smp-status=success&smp-message=Transaction%20successful.&smp-receipt-sent=false&smp-tx-code=123ABC
```

Failure:
```
?smp-status=failed&smp-failure-cause=transaction-failed&smp-message=Transaction%20failed.&smp-receipt-sent=false&smp-tx-code=123ABC
```

#III. Additional features

#####1. Include a transaction identifier

When setting up the SumUpPayment object, it is possible to pass an optional foreignTransactionID parameter. This identifier will be associated with the transaction and can be used to retrieve this transaction later. See <a href="https://sumup.com/integration#transactionReportingAPIs" target="_blank">API documentation</a> for details. Please make sure that this ID is unique within the scope of the SumUp merchant account and sub-accounts. It must not be longer than 128 characters.

#####2. Status code
A status code is provided as part of the callback

#####a) With the API Helper

 ```java 
 	int resultCode = getIntent().getExtras()getInt(SumUpAPI.EXTRA_RESULT_CODE);
 ```
 
Possible values are : 

* SumUpAPI.TRANSACTION_SUCCESSFUL = 1
* SumUpAPI.ERROR_TRANSACTION_FAILED = 2
* SumUpAPI.ERROR_GEOLOCATION_REQUIRED = 3
* SumUpAPI.ERROR_INVALID_PARAM = 4


#####b) With the URI call / Payment API - Web

* smp-status: `success/failed`
* smp-failure-cause (send it smp-status is `failed`): `transaction-failed/geolocation-required/invalid-param/invalid-token`



