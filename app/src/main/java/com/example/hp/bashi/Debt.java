package com.example.hp.bashi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

public class Debt extends AppCompatActivity {
    private String userID;
    private SharedPreferences mSharedPreferences;
    private float debt = 0f;
    private float refund = 0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt);
        mSharedPreferences = getSharedPreferences("BASHI_SHARED_PREFERENCE", MODE_PRIVATE);
        userID = mSharedPreferences.getString("userID", null);
        final TextView textView = (TextView)findViewById(R.id.debttext);
        final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("user_details").child(userID).child("loan");
        final DatabaseReference mDatabaseRefund = FirebaseDatabase.getInstance().getReference().child("user_details").child(userID).child("refund");

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            debt = 0f;
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                float cc = Float.parseFloat(snapshot.child("amount").getValue().toString());
                debt +=  (cc *0.1f)+cc;
                }
                float total = refund-debt;
                textView.setText("Your current debt + 10% interest to be paid back in a month is "+total);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabaseRefund.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                refund = 0f;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    float cc = Float.parseFloat(snapshot.child("amount").getValue().toString());
                    refund +=  cc;
                }
                float total = refund-debt;
                textView.setText("Your current debt + 10% interest is "+total);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void payLoan(View view) {
        Intent intent = new Intent(this, Refund.class);
        intent.putExtra("debt", debt);
        startActivity(intent);
    }
}
