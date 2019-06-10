package com.example.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Plusbtn extends AppCompatActivity {
    FirebaseAnalytics mFirebaseAnalytics;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plusbtn);
        Intent intent = getIntent();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        final EditText editText1 = (EditText) findViewById(R.id.edit_text1);
        final EditText editText2 = (EditText) findViewById(R.id.edit_text2);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("kora");

        Button plusbutton =(Button) findViewById(R.id.button);
        Button closebutton=(Button) findViewById(R.id.button1);

        plusbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name =editText1.getText().toString();
                final int number = Integer.parseInt(editText2.getText().toString());
                long now= System.currentTimeMillis();
                Date date_date = new Date(now);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                final String date= dateFormat.format(date_date);

                myRef.child(name).child("name").setValue(name);
                myRef.child(name).child("date").setValue(date);
                myRef.child(name).child("number").setValue(number);

                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        closebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
