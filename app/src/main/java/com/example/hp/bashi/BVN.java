package com.example.hp.bashi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class BVN extends AppCompatActivity {
    private EditText bvnEditText;
    private TextView textView;
    private Button mButton;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bvn);
        textView = (TextView) findViewById(R.id.bvn_user_name);
        bvnEditText = (EditText)findViewById(R.id.bvn_edit_text);
        mButton = (Button)findViewById(R.id.bvn_button);
        mProgressBar = (ProgressBar) findViewById(R.id.bvn_progress);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "checking Account information", Toast.LENGTH_LONG).show();
                Thread myThread = new Thread(){
                    @Override
                    public void run() {
                        try {
                            sleep(4000);
                            Intent intent = new Intent(getApplicationContext(),Loan.class);
                            startActivity(intent);
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                myThread.start();
            }
        });
        bvnEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){


                }
            }
        });
    }

    public void checkBVN(View view) {
        if(bvnEditText.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Empty BVN \n", Toast.LENGTH_SHORT).show();
            return;
        }
        new BVNVerify(textView).execute(bvnEditText.getText().toString());
    }
}
