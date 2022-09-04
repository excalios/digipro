package com.example.digipro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences prefs;
        prefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        TextView username = findViewById(R.id.username);
        TextView phoneNumber = findViewById(R.id.Phone_number);
        TextView email = findViewById(R.id.profile_email);

        username.setText(prefs.getString("username", ""));
        phoneNumber.setText(prefs.getString("phone_number", ""));
        email.setText(prefs.getString("email", ""));

        BottomNavigationView btm = findViewById(R.id.bottomNavigation);
        btm.setSelectedItemId(R.id.profile2);
        btm.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.activity_history4:
                        startActivity(new Intent(getApplicationContext(), activity_history4.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.activityHome:
                        startActivity(new Intent(getApplicationContext(), ActivityHome.class));
                        overridePendingTransition(0, 0);
                        return true;
                }

                return false;
            }
        });

        LinearLayout emergencyCallBtn = findViewById(R.id.prfEmergencyCall);

        emergencyCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, emergency_call.class);
                startActivity(i);
            }
        });

        LinearLayout logOutBtn = findViewById(R.id.prfLogOut);

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs;
                SharedPreferences.Editor edit;
                prefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                edit = prefs.edit();
                edit.clear();
                edit.commit();

                Intent i = new Intent(ProfileActivity.this, MainActivity2.class);
                startActivity(i);
            }
        });
    }
}