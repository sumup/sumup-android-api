package com.sumup.apisampleapp;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

public class URLResponseActivity extends Activity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_url_response);

    final Uri uri = getIntent().getData();
    TextView resultView = (TextView) findViewById(R.id.result);
    if (uri == null) {
      resultView.setText(R.string.callback_result_missing);
      return;
    }

    resultView.setText(getString(R.string.callback_result_format, uri.toString()));
  }
}
