package com.example.hp.bashi;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Customer_care extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_care);
    }

    public void startCall(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:08160550464"));
        startActivity(intent);
    }
}
