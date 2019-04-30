package com.example.hp.bashi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

public class Refund extends AppCompatActivity {
    private EditText accountNameEditText;
    private EditText accountNumberEditText;
    private EditText amountEditText;
    private Spinner spinner;
    private DatePicker datePicker;

    private Button mButton;
    private ProgressBar mProgressBar;

    private String bankString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund);

        accountNameEditText = (EditText)findViewById(R.id.account_name_refund);

        accountNumberEditText = (EditText)findViewById(R.id.account_number_refund);

        amountEditText = (EditText)findViewById(R.id.amount_refund);
        datePicker = (DatePicker)findViewById(R.id.date_of_birth);
        if(getIntent().getStringExtra("debt") != null){
            String debt = getIntent().getFloatExtra("debt",0f)+"";
            amountEditText.setText(debt);
        }
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_refund);
        spinner = (Spinner)findViewById(R.id.bank_refund);
        mButton = (Button)findViewById(R.id.refund_buttton);

        bankString = "FCMB_NG";

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String accountName = accountNameEditText.getText().toString();
                String accountNumber = accountNumberEditText.getText().toString();
                String amount = amountEditText.getText().toString();
                String date = datePicker.getYear()+ "-"+(datePicker.getMonth() + 1) +"-"+datePicker.getDayOfMonth();
                mButton.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                new BackgroundRefund(getBaseContext(), mButton, mProgressBar, getParent()).execute(accountName,accountNumber, amount, bankString, date);
            }
        });

        ArrayAdapter<CharSequence> charSequenceArrayAdapter = ArrayAdapter.createFromResource(this, R.array.blist, R.layout.spinner_layout);
        charSequenceArrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(charSequenceArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bankString = adapterView.getItemAtPosition(i).toString();
                if(bankString.equals("ZENITH_NG")){
                    datePicker.setVisibility(View.VISIBLE);
                }else{
                    if(datePicker.getVisibility() == View.VISIBLE){
                        datePicker.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}

