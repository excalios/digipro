package com.example.digipro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class emergency_call extends AppCompatActivity {
    ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_call);

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

        ImageButton backBtn = findViewById(R.id.back);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SharedPreferences prefs;
        prefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", "");

        ImageButton addContactBtn = findViewById(R.id.add_more);

        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(emergency_call.this, AddContactActivity.class);
                startActivity(i);
            }
        });

        RecyclerView contactRV = findViewById(R.id.emergencyRVContacts);

        ArrayList<ContactModel> courseModelArrayList = new ArrayList<ContactModel>();

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://digitalprotect.id/api/v1/emergency-contacts",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray datas = response.getJSONArray("datas");
                            for (int i = 0; i < datas.length(); i++) {
                                JSONObject contact = datas.getJSONObject(i);
                                courseModelArrayList.add(new ContactModel(contact.get("id").toString(), contact.get("name").toString(),contact.get("phone_number").toString()));
                            }
                            contactAdapter = new ContactAdapter(emergency_call.this, courseModelArrayList);
                            contactRV.setAdapter(contactAdapter);
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

        contactAdapter = new ContactAdapter(emergency_call.this, courseModelArrayList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        contactRV.setLayoutManager(linearLayoutManager);
        contactRV.setAdapter(contactAdapter);
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
}