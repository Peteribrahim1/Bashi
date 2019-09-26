package com.example.hp.bashi;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by hp on 20/04/2019.
 */

public class BVNVerify  extends AsyncTask<String, Void, String> implements NetworkHost {
    private final TextView textView;
    private HttpURLConnection httpURLConnection;
    private URL url;
    public BVNVerify(TextView textView){
        this.textView = textView;
        this.textView.setVisibility(View.VISIBLE);
        this.textView.setText("Checking BVN...");
    }
    @Override
    protected String doInBackground(String... strings) {
        String localstr = "";
        try {
            url = new URL("http://bashiv2.000webhostapp.com"+"/bvn-respond.php?bvn="+
                    strings[0].trim());

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
            textView.setVisibility(View.VISIBLE);
            if(jsonObject.getString("status").equals("true")){
                Toast.makeText(textView.getContext(), "Your request is being processed", Toast.LENGTH_LONG).show();
                String name = jsonObject.getJSONObject("data").getString("first_name")+" "+jsonObject.getJSONObject("data").getString("last_name");
                String dob = jsonObject.getJSONObject("data").getString("formatted_dob");
                textView.setText("Name : "+name+" DOB : "+dob);
                textView.setHint("true");
                Button mButton = (Button)textView.getRootView().findViewById(R.id.bvn_button);
                Button mButtontwo = (Button)textView.getRootView().findViewById(R.id.bvn_button_two);
                mButton.setVisibility(View.VISIBLE);
                mButtontwo.setVisibility(View.GONE);
            }else if(jsonObject.getString("status").equals("true")){
                textView.setText(jsonObject.getString("message"));
            }else{
                textView.setText("Invalid BVN");
            }
        } catch (Exception e) {
            textView.setVisibility(View.VISIBLE);
            textView.setText("Network error");
            Log.d(TAG, "onPostExecute: "+e);
        }
    }
}
