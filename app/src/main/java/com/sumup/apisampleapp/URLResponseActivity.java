package com.sumup.apisampleapp;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

public class URLResponseActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_response);


        final Uri uri = getIntent().getData();
        ((TextView) findViewById(R.id.result)).setText("Result: " + uri.toString());
    }
}
