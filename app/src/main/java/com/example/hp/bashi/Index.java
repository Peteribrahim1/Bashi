package com.example.hp.bashi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

public class Index extends AppCompatActivity {
    private String lname;
    private String fname;
    private String userID;
    private SharedPreferences mSharedPreferences;
    private FirebaseAuth fbAuth;
    private ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        String log_data = getIntent().getStringExtra("log_data");
        final TextView textView = (TextView)findViewById(R.id.welcome);
        mProgressBar = (ProgressBar)findViewById(R.id.indexProgress);
        textView.setText(log_data);
        mSharedPreferences = getSharedPreferences("BASHI_SHARED_PREFERENCE", MODE_PRIVATE);
        userID = mSharedPreferences.getString("userID", null);
        fbAuth = FirebaseAuth.getInstance();
        final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("user_details");
        final DatabaseReference currentRef = mDatabaseReference.child(userID);
        DatabaseReference lnameRef = currentRef.child("lname");
        currentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("<<<<<<<<<<<<<", "onDataChange: "+dataSnapshot.getValue());
                try{
                    JSONObject jsonObject = new JSONObject(dataSnapshot.getValue().toString());
                    lname = jsonObject.getString("lname");
                    fname = jsonObject.getString("fname");
                    mProgressBar.setVisibility(View.GONE);
                    textView.setText("Welcome "+lname+" "+fname);
                }catch (Exception e){
                    Log.d(">>>>>>>>", "onCreate: "+e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.d(">>>>>>>>>>>", "onCreate: "+log_data);
    }

    public void Applyloan(View view) {
        Intent intent = new Intent(this, Loan.class);
        startActivity(intent);
    }

    public void howitworks(View view) {
        Intent intent = new Intent(this, How_it_works.class);
        startActivity(intent);
    }
    public void customercare(View view) {
        Intent intent = new Intent(this, Customer_care.class);
        startActivity(intent);
    }

    public void signOut(View view) {
        fbAuth.signOut();
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(this, MainActivity.class));
    }
}
