package com.example.hp.bashi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;

public class OTP extends AppCompatActivity {
    private String transactionId = null;
    private EditText otpEditText;
    private HashMap hashMap;

    private Button mButton;
    private ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        hashMap = new HashMap();
        if(getIntent().getStringExtra("transactionId") != null){
            hashMap.put("accountName", getIntent().getStringExtra("accountName"));
            hashMap.put("accountNumber", getIntent().getStringExtra("accountNumber"));
            hashMap.put("amount", getIntent().getStringExtra("amount"));
            hashMap.put("dateOfBirth", getIntent().getStringExtra("dateOfBirth"));
            hashMap.put("transactionId", getIntent().getStringExtra("transactionId"));
            transactionId = getIntent().getStringExtra("transactionId");
            Toast.makeText(getBaseContext(), hashMap.toString(), Toast.LENGTH_SHORT).show();
        }
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_otp);
        mButton = (Button)findViewById(R.id.otp_send);
        otpEditText = (EditText)findViewById(R.id.otp_edit);
    }

    public void sendOTP(View view) {
        if(transactionId != null){
            String otpS = otpEditText.getText().toString();
            mButton.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            new BackgroundOTP(this, mButton, mProgressBar, hashMap).execute(otpS);
        }
    }
}
