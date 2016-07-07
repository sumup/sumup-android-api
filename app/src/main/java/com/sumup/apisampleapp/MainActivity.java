package com.sumup.apisampleapp;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.sumup.merchant.api.SumUpAPI;
import com.sumup.merchant.api.SumUpPayment;

import java.util.UUID;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCheckoutIntent = (Button) findViewById(R.id.button_charge_intent);
        btnCheckoutIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SumUpPayment payment = SumUpPayment.builder()
                        // mandatory parameters
                        // Please go to https://me.sumup.com/developers to get your Affiliate Key by entering the application ID of your app. (e.g. com.sumup.sdksampleapp)
                        .affiliateKey("7ca84f17-84a5-4140-8df6-6ebeed8540fc")
                        .productAmount(1.23)
                        .currency(SumUpPayment.Currency.EUR)// optional: add details
                        .productTitle("Taxi Ride")
                        .receiptEmail("customer@mail.com")
                        .receiptSMS("+3531234567890")
                        // optional: Add metadata
                        .addAdditionalInfo("AccountId", "taxi0334")
                        .addAdditionalInfo("From", "Paris").addAdditionalInfo("To", "Berlin")
                        // optional: foreign transaction ID, must be unique!
                        .foreignTransactionId(UUID.randomUUID().toString()) // can not exceed 128 chars
                        .build();

                SumUpAPI.openPaymentActivity(MainActivity.this, ResponseActivity.class, payment);
            }
        });

        Button btnCheckoutUri = (Button) findViewById(R.id.button_charge_uri);
        btnCheckoutUri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent payIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                        "sumupmerchant://pay/1.0"
                                + "?affiliate-key=7ca84f17-84a5-4140-8df6-6ebeed8540fc"
                                + "&app-id=com.sumup.apisampleapp"
                                + "&amount=1.23"
                                + "&currency=EUR"
                                + "&title=Taxi Ride"
                                + "&receipt-mobilephone=+3531234567890"
                                + "&receipt-email=customer@mail.com"
                                + "&foreign-tx-id=" + UUID.randomUUID().toString()
                                + "&callback=sumupsampleresult://result"));

                startActivityForResult(payIntent, 0);


            }
        });
    }
}
