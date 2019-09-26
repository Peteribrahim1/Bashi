package com.example.hp.bashi;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Loan extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private String accountName;
    private EditText accountNameEditText;
    private String accountNumber;
    private EditText accountNumberEditText;
    private String bvn;
    private EditText bvnEditText;
    private String amount;
    private EditText amountEditText;
    private Spinner spinner;
    private float previousDebt = -99999999999999999f;

    private TextView textView;

    private Button mButton;
    private ProgressBar mProgressBar;
    private Integer validBVN = new Integer(0);

    private String bankString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan);
        accountNameEditText = (EditText)findViewById(R.id.account_name);

        accountNumberEditText = (EditText)findViewById(R.id.account_number);

        bvnEditText = (EditText)findViewById(R.id.bvn);
        bvn = bvnEditText.getText().toString();

        SharedPreferences mSharedPreferences = getSharedPreferences("BASHI_SHARED_PREFERENCE", MODE_PRIVATE);
        float maxAmout = mSharedPreferences.getFloat("income", 0f);

        amountEditText = (EditText)findViewById(R.id.amount);
        amountEditText.setFilters(new InputFilter[]{new InputFilterMax((int)maxAmout)});
        Toast.makeText(getBaseContext(), "Your credit limit is"+maxAmout, Toast.LENGTH_LONG).show();

        spinner = (Spinner)findViewById(R.id.spinner);
        mButton = (Button)findViewById(R.id.apply);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_apply);
        bankString = "FCMB_NG";
        ArrayAdapter<CharSequence> charSequenceArrayAdapter = ArrayAdapter.createFromResource(this, R.array.blist, R.layout.spinner_layout);
        charSequenceArrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(charSequenceArrayAdapter);
        spinner.setOnItemSelectedListener(this);

        textView = (TextView)findViewById(R.id.bvn_update);
        checkDebt();
        bvnEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    textView.setHint("false");
//                    new BVNVerify(textView, validBVN).execute(bvnEditText.getText().toString());
                }
            }
        });
    }

    public void applyForLoan(View view) {
        if(previousDebt == -99999999999999999f || previousDebt<0f){
            Toast.makeText(this, "You still owe "+ previousDebt, Toast.LENGTH_LONG).show();
            return;
        }
//        if(bvnEditText.getText().length()==0){
//            bvnEditText.setError("BVN Cannot be Empty");
//            bvnEditText.requestFocus();
//            return;
//        }
        if(true){
            accountName = accountNameEditText.getText().toString();
            accountNumber = accountNumberEditText.getText().toString();
            if(accountNumber.length()==0){
                accountNumberEditText.setError("Account Number Cannot be Empty");
                accountNumberEditText.requestFocus();
                return;
            }

            amount = amountEditText.getText().toString();
            if(amount.length()==0){
                amountEditText.setError("Amount Cannot be Empty");
                amountEditText.requestFocus();
                return;
            }
            mButton.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            new BackgroundNetworks(this, mButton, mProgressBar).execute("aaa",accountNumber, amount, bankString);
        }else {
            Toast.makeText(this, "Invalid BVN", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        bankString = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    public void checkDebt(){
        String userID;
        SharedPreferences mSharedPreferences;
        final float[] debt = {0f};
        final float refund = 0f;
        mSharedPreferences = getSharedPreferences("BASHI_SHARED_PREFERENCE", MODE_PRIVATE);
        userID = mSharedPreferences.getString("userID", null);
        final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("user_details").child(userID).child("loan");
        final DatabaseReference mDatabaseRefund = FirebaseDatabase.getInstance().getReference().child("user_details").child(userID).child("refund");

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                debt[0] = 0f;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    float cc = Float.parseFloat(snapshot.child("amount").getValue().toString());
                    debt[0] +=  (cc *0.1f)+cc;
                }
                float total = refund- debt[0];
                previousDebt = total;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public class InputFilterMax implements android.text.InputFilter{
        private  int max;
        public InputFilterMax(int max){
            this.max=max;
        }

        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
            try {
                int input = Integer.parseInt(spanned.toString()+charSequence.toString());
                if(isInRange(0, max, input)){
                    return null;
                }
            }catch ( NumberFormatException e){

            }
            return "";
        }

        private boolean isInRange(int a, int b, int c){
            return b>a?c>=a&&c<=b:c>=b &&c<=a;
        }
    }
}
