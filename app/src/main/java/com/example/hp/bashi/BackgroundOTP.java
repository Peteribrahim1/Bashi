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

public class BackgroundOTP extends AsyncTask<String, Void, String> {
    private HttpURLConnection httpURLConnection;
    private Context mContext;
    private Button mButton;
    private ProgressBar mProgressBar;

    private FirebaseAuth fbAuth;
    private DatabaseReference mDatabaseReference;
    private String userID;
    private SharedPreferences mSharedPreferences;

    private URL url;
    private HashMap hashMap;

    public BackgroundOTP(Context c, Button b, ProgressBar p, HashMap s) {
        this.mContext = c;
        this.mButton = b;
        this.mProgressBar = p;
        this.hashMap = s;

    }

    @Override
    protected String doInBackground(String... strings) {
        fbAuth = FirebaseAuth.getInstance();
        mSharedPreferences = mContext.getSharedPreferences("BASHI_SHARED_PREFERENCE", MODE_PRIVATE);
        userID = mSharedPreferences.getString("userID", null);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("user_details").child(userID);
        hashMap.put("otp", strings[0].trim());

        String localstr = "";
        try {
            url = new URL("https://bashiv1.herokuapp.com/server-otp-responder.php?otp="+
                    strings[0].trim()+"&tid="+hashMap.get("transactionId").toString());
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
        Log.d(TAG, "onPostExecute: "+s);
        try{
            JSONObject jsonObject = new JSONObject(s);
            if(jsonObject.getString("status").equals("success")){
                hashMap.put("date", System.currentTimeMillis());
                mDatabaseReference.child("refund").child(hashMap.get("transactionId").toString()).setValue(hashMap);
                Toast.makeText(mContext, "Transaction Completed", Toast.LENGTH_LONG).show();
                Intent mIntent = new Intent(mContext, Debt.class);
                mContext.getApplicationContext().startActivity(mIntent);
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
