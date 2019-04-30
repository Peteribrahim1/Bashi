package com.example.hp.bashi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by hp on 19/11/2018.
 */

public class BackgroundRefund extends AsyncTask<String, Void, String> {
    private HttpURLConnection httpURLConnection;
    private Context mContext;
    private Activity mActivity;
    private Button mButton;
    private ProgressBar mProgressBar;

    private FirebaseAuth fbAuth;
    private DatabaseReference mDatabaseReference;
    private String userID;
    private SharedPreferences mSharedPreferences;

    private URL url;
    private HashMap hashMap;

    public BackgroundRefund(Context c, Button b, ProgressBar p, Activity a) {
        this.mContext = c;
        this.mButton = b;
        this.mProgressBar = p;
        this.mActivity = a;

    }

    @Override
    protected String doInBackground(String... strings) {
        fbAuth = FirebaseAuth.getInstance();
        mSharedPreferences = mContext.getSharedPreferences("BASHI_SHARED_PREFERENCE", MODE_PRIVATE);
        userID = mSharedPreferences.getString("userID", null);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("user_details").child(userID);
        hashMap = new HashMap();
        hashMap.put("accountName", strings[0].trim());
        hashMap.put("accountNumber", strings[1].trim());
        hashMap.put("amount", strings[2].trim());
        hashMap.put("dateOfBirth", strings[4].trim());

        Log.d(TAG, "doInBackground: >>>>>>> in the back");
        String localstr = "";
        try {
            url = new URL("https://bashiv1.herokuapp.com/server-bank-responder.php?accountName="+
                    strings[0].trim()+"&accountNumber="+strings[1].trim()+"&amount="+strings[2].trim()+"&bankCode="+strings[3].trim()+"&dob="+strings[4].trim());
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream stream = httpURLConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer stringBuffer = new StringBuffer();
            String string;
            while ( (string = reader.readLine()) != null ){
                stringBuffer.append(string);
            }
            if(stringBuffer.length()==0)return null;
            localstr = stringBuffer.toString();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();


        }

        return localstr;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
//        Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
        Log.d(TAG, "onPostExecute: "+s);
        try{
            JSONObject jsonObject = new JSONObject(s);
            if(jsonObject.getString("status").equals("success")){

                if( jsonObject.getJSONObject("data").getString("status").equals("PendingValidation")){
                    String transactionId = jsonObject.getJSONObject("data").getString("transactionId");
                    Toast.makeText(mContext, "Your request is being processed", Toast.LENGTH_LONG).show();
//                    Toast.makeText(mContext, "Your request is being processed "+transactionId, Toast.LENGTH_LONG).show();
//                hashMap.put("transId", jsonObject.getJSONObject("").);
//                    hashMap.put("date", System.currentTimeMillis());
//                    hashMap.put("transactionId", transactionId);

//                    mDatabaseReference.child("refund").child(transactionId).setValue(hashMap);
                    Intent mIntent = new Intent(mContext, OTP.class);
//                    hashMap.put("accountName", strings[0].trim());
//                    hashMap.put("accountNumber", strings[1].trim());
//                    hashMap.put("amount", strings[2].trim());
//                    hashMap.put("dateOfBirth", strings[4].trim());
                    mIntent.putExtra("accountName", hashMap.get("accountName").toString());
                    mIntent.putExtra("accountNumber", hashMap.get("accountNumber").toString());
                    mIntent.putExtra("amount", hashMap.get("amount").toString());
                    mIntent.putExtra("dateOfBirth", hashMap.get("dateOfBirth").toString());
                    mIntent.putExtra("transactionId", transactionId);
                    mContext.getApplicationContext().startActivity(mIntent);
                }
            }else{
                Toast.makeText(mContext, "An Error occurred, Try again", Toast.LENGTH_LONG).show();
                mButton.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
            mButton.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "An Error occurred, Try again", Toast.LENGTH_LONG).show();
            mButton.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
