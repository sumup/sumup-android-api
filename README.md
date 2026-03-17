<div align="center">

# SumUp Android Payment API

[![Documentation](https://img.shields.io/badge/docs-developer.sumup.com-0A2540)](https://developer.sumup.com)
[![CI Status](https://github.com/sumup/sumup-android-api/actions/workflows/ci.yaml/badge.svg)](https://github.com/sumup/sumup-android-api/actions/workflows/ci.yaml)
[![License](https://img.shields.io/github/license/sumup/sumup-android-api)](./LICENSE)
[![Platform](https://img.shields.io/badge/platform-Android-3DDC84)](https://developer.android.com)

</div>

This repository contains a sample Android app for the SumUp Payment API and demonstrates integration via the `com.sumup:merchant-api` artifact published in the SumUp Maven repository.

Use it when you want to:

- start a SumUp checkout from a native Android app
- start a SumUp checkout via the `sumupmerchant://pay/1.0` URI contract
- receive the checkout result back in your app or website

The sample app in this repository can be used as a reference implementation for the integration contract. Full SumUp API documentation lives at [developer.sumup.com](https://developer.sumup.com).

## Getting Started

1. Create a SumUp account.
2. Generate an affiliate key in [me.sumup.com/developers](https://me.sumup.com/developers).
3. Choose the integration path you need:
   - `API Helper`: native Android app using `com.sumup:merchant-api`
   - `URI call`: Android app or mobile website using the `sumupmerchant://` URI

## API Helper

### Recommended Gradle setup

Add the SumUp Maven repository to `settings.gradle`:

```groovy
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.sumup.com/releases") }
    }
}
```

Add the dependency to your Android module:

```groovy
dependencies {
    implementation "com.sumup:merchant-api:1.4.0"
}
```

### Legacy Gradle setup

If you still use older Gradle builds, the repository can be added like this:

```groovy
allprojects {
    repositories {
        maven { url "https://maven.sumup.com/releases" }
    }
}
```

### Make a payment

```java
SumUpPayment payment = SumUpPayment.builder()
        .affiliateKey("YOUR_AFFILIATE_KEY")
        .total(new BigDecimal("1.23"))
        .currency(SumUpPayment.Currency.EUR)
        .title("Taxi Ride")
        .receiptEmail("customer@mail.com")
        .receiptSMS("+3531234567890")
        .addAdditionalInfo("AccountId", "taxi0334")
        .addAdditionalInfo("From", "Paris")
        .addAdditionalInfo("To", "Berlin")
        .foreignTransactionId(UUID.randomUUID().toString())
        .skipSuccessScreen()
        .build();

SumUpAPI.checkout(this, payment, REQUEST_CODE_PAYMENT);
```

### Handle the payment result

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode != REQUEST_CODE_PAYMENT || data == null) {
        return;
    }

    Bundle extras = data.getExtras();
    if (extras == null) {
        return;
    }

    int sumUpResultCode = extras.getInt(SumUpAPI.Response.RESULT_CODE);
    String message = extras.getString(SumUpAPI.Response.MESSAGE);
    String txCode = extras.getString(SumUpAPI.Response.TX_CODE);
    boolean receiptSent = extras.getBoolean(SumUpAPI.Response.RECEIPT_SENT);
}
```

## URI Call

### Provide a callback activity

```xml
<activity
    android:name="com.example.URLResponseActivity"
    android:exported="true"
    android:label="Payment Result">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <action android:name="com.example.URLResponseActivity" />

        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />

        <data
            android:host="result"
            android:scheme="mycallbackscheme" />
    </intent-filter>
</activity>
```

### Make a payment

```java
Uri checkoutUri = new Uri.Builder()
        .scheme("sumupmerchant")
        .authority("pay")
        .appendPath("1.0")
        .appendQueryParameter("affiliate-key", "YOUR_AFFILIATE_KEY")
        .appendQueryParameter("app-id", "com.example.myapp")
        .appendQueryParameter("total", "1.23")
        .appendQueryParameter("currency", "EUR")
        .appendQueryParameter("title", "Taxi Ride")
        .appendQueryParameter("receipt-mobilephone", "+3531234567890")
        .appendQueryParameter("receipt-email", "customer@mail.com")
        .appendQueryParameter("foreign-tx-id", UUID.randomUUID().toString())
        .appendQueryParameter("skip-screen-success", "true")
        .appendQueryParameter("callback", "mycallbackscheme://result")
        .build();

Intent payIntent = new Intent(Intent.ACTION_VIEW, checkoutUri);
startActivity(payIntent);
```

The result is received as a URI in the callback activity:

```java
Uri result = getIntent().getData();
```

Success:

```text
mycallbackscheme://result?smp-status=success&smp-message=Transaction%20successful.&smp-receipt-sent=false&smp-tx-code=123ABC&foreign-tx-id=0558637a-b73c-43ad-b358-f93cb909251x
```

Failure:

```text
mycallbackscheme://result?smp-status=failed&smp-failure-cause=transaction-failed&smp-message=Transaction%20failed.&smp-receipt-sent=false&smp-tx-code=123ABC&foreign-tx-id=05c14c86-a7a0-49c5-a1ec-acb168f5198x
```

## Payment API for Mobile Web

Put a link like this on your website:

```html
<a href="sumupmerchant://pay/1.0?affiliate-key=YOUR_AFFILIATE_KEY&app-id=com.example.myapp&total=1.23&currency=EUR&title=Taxi%20Ride&receipt-mobilephone=%2B3531234567890&receipt-email=customer%40mail.com&callback=https%3A%2F%2Fexample.com%2Fmyapp%2Fmycallback">
  Start SumUp Payment
</a>
```

`total` is available from SumUp app version `1.88.0` and above. If you still need to support older SumUp app versions, keep sending the deprecated `amount` field as a fallback.

Make sure the callback URL is controlled by you.

Success:

```text
?smp-status=success&smp-message=Transaction%20successful.&smp-receipt-sent=false&smp-tx-code=123ABC
```

Failure:

```text
?smp-status=failed&smp-failure-cause=transaction-failed&smp-message=Transaction%20failed.&smp-receipt-sent=false&smp-tx-code=123ABC
```

## Response Fields

### API Helper

The callback `Bundle` can contain:

- `SumUpAPI.Response.RESULT_CODE`
- `SumUpAPI.Response.MESSAGE`
- `SumUpAPI.Response.TX_CODE`
- `SumUpAPI.Response.RECEIPT_SENT`

Known `RESULT_CODE` values:

- `SumUpAPI.Response.ResultCode.TRANSACTION_SUCCESSFUL = 1`
- `SumUpAPI.Response.ResultCode.ERROR_TRANSACTION_FAILED = 2`
- `SumUpAPI.Response.ResultCode.ERROR_GEOLOCATION_REQUIRED = 3`
- `SumUpAPI.Response.ResultCode.ERROR_INVALID_PARAM = 4`
- `SumUpAPI.Response.ResultCode.ERROR_NO_CONNECTIVITY = 6`
- `SumUpAPI.Response.ResultCode.ERROR_DUPLICATE_FOREIGN_TX_ID = 9`
- `SumUpAPI.Response.ResultCode.ERROR_INVALID_AFFILIATE_KEY = 10`

### URI Call and Mobile Web

- `smp-status`: `success` or `failed`
- `smp-failure-cause`: present when `smp-status=failed`
- Known `smp-failure-cause` values:
  `transaction-failed`, `geolocation-required`, `invalid-param`,
  `invalid-token`

## Additional Checkout Parameters

### Transaction Identifier

`foreignTransactionId` is associated with the transaction and can be used later to retrieve transaction details. It must be unique within the SumUp merchant account scope and must not exceed 128 characters.

It is available in the callback activity as:

```java
Bundle extras = data.getExtras();
String foreignTransactionId = extras.getString(SumUpAPI.Param.FOREIGN_TRANSACTION_ID);
```

### Skip Success Screen

When `skipSuccessScreen` is enabled, your application becomes responsible for showing the final transaction state to the customer. Success screens can still be shown when using SumUp Air Lite readers.

## Community

- Questions: contact
  [integration@sumup.com](mailto:integration@sumup.com)
- Bugs: [open an issue](https://github.com/sumup/sumup-android-api/issues/new)
