package com.example.hp.bashi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccountActivity extends AppCompatActivity {
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private EditText mEditTextPasswordRepeat;
    private EditText lastname;
    private EditText othernames;
    private EditText mState;
    private EditText occupation;
    private String emptyFieldMessage = "";
    private CheckBox termCheckBox;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_activity);
        mEditTextEmail = (EditText)findViewById(R.id.create_email);
        mEditTextPassword = (EditText)findViewById(R.id.create_password);
        mEditTextPasswordRepeat = (EditText)findViewById(R.id.create_password_repeat);
        termCheckBox = (CheckBox) findViewById(R.id.term_check);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar3);
        lastname = (EditText)findViewById(R.id.create_last_name);
        othernames = (EditText)findViewById(R.id.create_other_name);
        mState = (EditText)findViewById(R.id.create_State);
        occupation = (EditText)findViewById(R.id.create_occupation);

        if(getIntent().getStringExtra("pre_email") != null){
            mEditTextEmail.setText(getIntent().getStringExtra("pre_email"));
        }

    }

    public void createAccount(View view) {
        if(!isWellFormed()){
            Toast.makeText(this, "Incorrect Details \n"+emptyFieldMessage, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        final Button mButton = (Button)mProgressBar.getRootView().findViewById(R.id.create_submit_button);
        mButton.setVisibility(View.GONE);
        final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("user_details");

        mFirebaseAuth.createUserWithEmailAndPassword(mEditTextEmail.getText().toString(), mEditTextPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("T>>>>>>>>>", "onComplete: Not Cool");
                Log.d("T>>>>>>>>>", "onComplete: Not Cool " +task);
                if(task.isSuccessful()){
                    Log.d("T>>>>>>>>>", "onComplete: Coollll");
                    Log.d("T>>>>>>>>>", "onComplete: "+mFirebaseAuth.getCurrentUser());
                    Log.d("T>>>>>>>>>", "onComplete: "+mFirebaseAuth.getCurrentUser().getUid());
                    DatabaseReference currentDatabaseReference = mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid());
                    currentDatabaseReference.child("lname").setValue(lastname.getText().toString());
                    currentDatabaseReference.child("fname").setValue(othernames.getText().toString());
                    currentDatabaseReference.child("occupation").setValue(occupation.getText().toString());
                    currentDatabaseReference.child("income").setValue(mState.getText().toString());
                    currentDatabaseReference.child("email").setValue(mEditTextEmail.getText().toString());
                    Toast.makeText(getApplicationContext(), "Account creation successful",Toast.LENGTH_SHORT).show();
                    Intent mIntent  = new Intent(getApplicationContext(), MainActivity.class);
                    mIntent.putExtra("new_email", mEditTextEmail.getText().toString());
                    mIntent.putExtra("new_password", mEditTextPassword.getText().toString());
                    startActivity(mIntent);
                    finish();
                }
                try {
                    throw task.getException();
                } catch(FirebaseAuthWeakPasswordException e) {
                    Toast.makeText(getApplicationContext(), "Incorrect password \n", Toast.LENGTH_SHORT).show();
                } catch(FirebaseAuthInvalidCredentialsException e) {
                    Toast.makeText(getApplicationContext(), "Existing Email \n", Toast.LENGTH_SHORT).show();
                } catch(FirebaseAuthUserCollisionException e) {
                    Toast.makeText(getApplicationContext(), "An error occured \n", Toast.LENGTH_SHORT).show();
                } catch(Exception e) {
                    Log.e("><>>", e.getMessage());
                }
                mButton.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
//        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener(){
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//
//                if(user != null) {
//                    mEditTextEmail.setText(user.getEmail());
//                    mEditTextPassword.setText("Signed In");
//
//                }else {
//                    mEditTextEmail.setText("");
//                    mEditTextPassword.setText("Signed Out");
//                }
//            }
//        };
    }

    private boolean isWellFormed() {
        if(lastname.getText().length()<3){
            emptyFieldMessage = "Lastname must be more than 2 characters";
            return false;
        }
        if(othernames.getText().length()<3){
            emptyFieldMessage = "firstname must be more than 2 characters";
            return false;
        }
        if(occupation.getText().length()<3){
            emptyFieldMessage = "occupation must be more than 2 characters";
            return false;
        }
        if(mState.getText().length()<3){
            emptyFieldMessage = "state must be more than 2 characters";
            return false;
        }
        if(mEditTextEmail.getText().length()<3){
            emptyFieldMessage = "email must be more than 2 characters";
            return false;
        }
        if(mEditTextPassword.getText().length()<3){
            emptyFieldMessage = "PassWord must be more than 2 characters";
            return false;
        }
        if(!mEditTextPassword.getText().toString().equals( mEditTextPasswordRepeat.getText().toString())){
            emptyFieldMessage = "PassWord Must tally";
            return false;
        }
        if(!termCheckBox.isChecked()){
            Toast.makeText(this, "You have to accept the terms", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void readTerms(View view) {
        startActivity(new Intent(this, Terms.class));
    }
}
