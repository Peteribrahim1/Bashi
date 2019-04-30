package com.example.hp.bashi;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;

public class Records extends AppCompatActivity {
    private String userID;
    private SharedPreferences mSharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        ListView listView = (ListView)findViewById(R.id.history_list);
        final ArrayList<String> arrayList = new ArrayList<>();
        final ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.spinner_layout,arrayList);
        listView.setAdapter(adapter);
        mSharedPreferences = getSharedPreferences("BASHI_SHARED_PREFERENCE", MODE_PRIVATE);
        userID = mSharedPreferences.getString("userID", null);
        final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("user_details").child(userID).child("loan");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayList.clear();
                float debt = 0f;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    arrayList.add(String.format("%s : %s >>> N%s","Account",snapshot.child("accountNumber").getValue().toString(), snapshot.child("amount").getValue().toString()));
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        final DatabaseReference mDatabaseRefund = FirebaseDatabase.getInstance().getReference().child("user_details").child(userID).child("refund");
        mDatabaseRefund.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    arrayList.add(String.format("%s : %s <<< N%s",snapshot.child("accountName").getValue().toString(),snapshot.child("accountNumber").getValue().toString(), snapshot.child("amount").getValue().toString()));
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
