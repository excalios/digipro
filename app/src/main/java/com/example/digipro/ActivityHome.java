package com.example.digipro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivityHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView btm = findViewById(R.id.bottomNavigation);
        btm.setSelectedItemId(R.id.activityHome);
        btm.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.activity_history4:
                        startActivity(new Intent(getApplicationContext(), activity_history4.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile2:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }

                return false;
            }
        });

        SharedPreferences prefs;
        prefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", "");

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://digitalprotect.id/api/v1/emergency-contacts",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(ActivityHome.this, response.get("datas").toString(), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String json = null;

                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        json = new String(response.data);
                        json = trimMessage(json, "message");
                        if (json != null) displayMessage(json);
                    }
                }
            }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer: " + token);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(jsonRequest);
    }

    public String trimMessage(String json, String key) {
        String trimmedString = null;

        try {
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }

    //Somewhere that has access to a context
    public void displayMessage(String toastString) {
        Toast.makeText(this, toastString, Toast.LENGTH_LONG).show();
    }

    public boolean goToProfile(MenuItem menuItem){
        Intent i = new Intent(ActivityHome.this, ProfileActivity.class);
        startActivity(i);

        return true;
    }
}