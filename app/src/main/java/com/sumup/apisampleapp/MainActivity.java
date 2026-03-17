package com.sumup.apisampleapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.sumup.merchant.api.SumUpAPI;
import com.sumup.merchant.api.SumUpPayment;
import java.math.BigDecimal;
import java.util.UUID;

public class MainActivity extends Activity {

  private static final String AFFILIATE_KEY = "REPLACE_WITH_YOUR_AFFILIATE_KEY";
  private static final String APP_ID = "com.sumup.apisampleapp";
  private static final String CALLBACK_URI = "sumupsampleresult://result";
  private static final int REQUEST_CODE_PAYMENT = 2;

  private TextView mResultCode;
  private TextView mResultMessage;
  private TextView mTxCode;
  private TextView mReceiptSent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViews();

    Button btnCheckoutIntent = (Button) findViewById(R.id.button_charge_intent);
    btnCheckoutIntent.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            SumUpPayment payment =
                SumUpPayment.builder()
                    // mandatory parameters
                    // Please go to https://me.sumup.com/developers to get your Affiliate Key by
                    // entering the application ID of your app. (e.g. com.sumup.sdksampleapp)
                    .affiliateKey(AFFILIATE_KEY)
                    .total(new BigDecimal("1.23"))
                    .currency(SumUpPayment.Currency.EUR) // optional: add details
                    .title("Taxi Ride")
                    .receiptEmail("customer@mail.com")
                    .receiptSMS("+3531234567890")
                    // optional: Add metadata
                    .addAdditionalInfo("AccountId", "taxi0334")
                    .addAdditionalInfo("From", "Paris")
                    .addAdditionalInfo("To", "Berlin")
                    // optional: foreign transaction ID, must be unique!
                    .foreignTransactionId(UUID.randomUUID().toString()) // can not exceed 128 chars
                    // optional: skip the success screen
                    .skipSuccessScreen()
                    .build();

            SumUpAPI.checkout(MainActivity.this, payment, REQUEST_CODE_PAYMENT);
          }
        });

    Button btnCheckoutUri = (Button) findViewById(R.id.button_charge_uri);
    btnCheckoutUri.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Uri payUri =
                new Uri.Builder()
                    .scheme("sumupmerchant")
                    .authority("pay")
                    .appendPath("1.0")
                    .appendQueryParameter("affiliate-key", AFFILIATE_KEY)
                    .appendQueryParameter("app-id", APP_ID)
                    // field available from App version 1.88.0 and above. Otherwise keep
                    // deprecated field "amount"
                    .appendQueryParameter("total", "1.23")
                    .appendQueryParameter("currency", "EUR")
                    .appendQueryParameter("title", "Taxi Ride")
                    .appendQueryParameter("receipt-mobilephone", "+3531234567890")
                    .appendQueryParameter("receipt-email", "customer@mail.com")
                    .appendQueryParameter("foreign-tx-id", UUID.randomUUID().toString())
                    // optional: skip the success screen
                    .appendQueryParameter("skip-screen-success", "true")
                    .appendQueryParameter("callback", CALLBACK_URI)
                    .build();

            Intent payIntent = new Intent(Intent.ACTION_VIEW, payUri);
            startActivity(payIntent);
          }
        });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    resetViews();

    switch (requestCode) {
      case REQUEST_CODE_PAYMENT:
        if (data != null) {
          Bundle extra = data.getExtras();

          mResultCode.setText("Result code: " + extra.getInt(SumUpAPI.Response.RESULT_CODE));
          mResultMessage.setText("Message: " + extra.getString(SumUpAPI.Response.MESSAGE));

          String txCode = extra.getString(SumUpAPI.Response.TX_CODE);
          mTxCode.setText(txCode == null ? "" : "Transaction Code: " + txCode);

          boolean receiptSent = extra.getBoolean(SumUpAPI.Response.RECEIPT_SENT);
          mReceiptSent.setText("Receipt sent: " + receiptSent);
        }
        break;
      default:
        break;
    }
  }

  private void resetViews() {
    mResultCode.setText("");
    mResultMessage.setText("");
    mTxCode.setText("");
    mReceiptSent.setText("");
  }

  private void findViews() {
    mResultCode = (TextView) findViewById(R.id.result);
    mResultMessage = (TextView) findViewById(R.id.result_msg);
    mTxCode = (TextView) findViewById(R.id.tx_code);
    mReceiptSent = (TextView) findViewById(R.id.receipt_sent);
  }
}
