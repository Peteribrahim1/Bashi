package com.example.hp.bashi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private TextView userText;
    private TextView statusText;
    private TextView emailText;
    private TextView passwordText;
    private ProgressBar progressBar;
    private Button mButton;

    private FirebaseAuth fbAuth;

    private FirebaseAuth.AuthStateListener authListener;
    private String userID ;

    private SharedPreferences mPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreferences = getSharedPreferences("BASHI_SHARED_PREFERENCE", MODE_PRIVATE);
        if(mPreferences.getString("userID", null) != null){
            Intent mIntent  = new Intent(getApplicationContext(), Index.class);
            startActivity(mIntent);
            finish();
            return;
        }
        progressBar = (ProgressBar)findViewById(R.id.progressBar2);
        mButton = (Button)findViewById(R.id.buttontwo);
        userText = (TextView) findViewById(R.id.userText);
        statusText = (TextView) findViewById(R.id.statusText);
        emailText = (EditText) findViewById(R.id.emailText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        userText.setText("");
        statusText.setText("Signed out");
//        if(getIntent().getStringExtra("new_email") != null){
//            emailText.setText(getIntent().getStringExtra("new_email"));
//            passwordText.setText(getIntent().getStringExtra("new_password"));
//        }

        fbAuth = FirebaseAuth.getInstance();
//        final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("user_details");
        authListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null) {
                    Log.d(">>>>.", "onAuthStateChanged: "+user.getUid().toString());
                    userID = user.getUid().toString();
                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.putString("userID", userID);
                    editor.apply();
//                    final DatabaseReference currentRef = mDatabaseReference.child(user.getUid().toString());
//                    DatabaseReference lnameRef = currentRef.child("lname");
//                    currentRef.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            Log.d(">>>>>>>>>>>>>>>>>>", "onDataChange: "+dataSnapshot.getValue());
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                    userText.setText(user.getEmail());
//                    Log.d(">>>>>>>>>>>>>>>>>>.", "onAuthStateChanged: "+mDatabaseReference.child(user.getUid().toString()).child("lname").toString() );
//                    statusText.setText("Signed In");

                }else {
                    userID = null;
                }
            }
        };

    }
    @Override
    public void onStart(){
        super.onStart();
        fbAuth.addAuthStateListener(authListener);
    }
    @Override
    public void onStop(){
        super.onStop();
        if(authListener != null) {
            fbAuth.removeAuthStateListener(authListener);
        }
    }
    public void createAccount(View view){
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        Intent mIntent = new Intent(this, CreateAccountActivity.class);
        mIntent.putExtra("pre_email",email);
        startActivity(mIntent);

//        if(email.length()==0){
//            emailText.setError("Enter an email address");
//            return;
//        }
//        if(password.length() < 6){
//           passwordText.setError("Password must be atleast 6 characters");
//            return;
//        }
//        fbAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if(!task.isSuccessful()){
//                    notifyUser("Account creation failed");
//                }
//                notifyUser(task.getException().getMessage());
//
//            }
//        });
////        fbAuth.createUserWithEmailAndPassword(email,password)
////                .addOnCompleteListener(this,
////                        (task) {
////                                if(!task.isSuccessful()){
////                                    notifyUser("Account creation failed");
////                                }
////
////                        });
//

    }
    public void signIn(View view) {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if(email.length()==0){
            emailText.setError("Enter an email address");
            return;
        }
        if(password.length() < 6){
            passwordText.setError("Password must be atleast 6 characters");
            return;
        }
        mButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        authListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null) {
                    userID = user.getUid().toString();
                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.putString("userID", userID);
                    editor.apply();
                }else {
                    userText.setText("");
                    userID = null;
                    return;
                }
            }
        };
        fbAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    notifyUser("Authentication failed");
                    Toast.makeText(getApplicationContext(), "Incorrect Details", Toast.LENGTH_SHORT).show();
                    mButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }else {
                    Intent mIntent  = new Intent(getApplicationContext(), Index.class);
                    startActivity(mIntent);
                }
            }
        });

//        fbAuth.signInWithEmailAndPassword(email,password)
//                .addOnCompleteListener(this,
//                        (task) {
//                                if(!task.isSuccessful()){
//                                    notifyUser("Authentication failed");
//                                }
//
//                        });

    }

    public void resetPassword(View view){
        String email = emailText.getText().toString();
        if(email.length()==0){
            emailText.setError("Enter an email address");
            return;
        }
        fbAuth.sendPasswordResetEmail(email).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    notifyUser("Reset email sent");

                }
            }
        });
//        fbAuth.sendPasswordResetEmail(email)
//                .addOnCompleteListener((task){
//                        if(task.isSuccessful()){
//                            notifyUser("Reset email sent");
//
//                        }
//                });

    }
    private void notifyUser(String message){
        Toast.makeText(MainActivity.this,message,
                Toast.LENGTH_SHORT).show();

    }

    public void phone(View view) {
        Intent intent = new Intent(this, Phone.class);
        startActivity(intent);
    }

}

