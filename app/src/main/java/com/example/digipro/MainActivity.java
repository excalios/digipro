package com.example.digipro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs;
        SharedPreferences.Editor edit;
        prefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", "");
        edit = prefs.edit();

        if(!token.isEmpty()){
            Intent i = new Intent(this, ActivityHome.class);
            startActivity(i);
        }

        RequestQueue queue = VolleySingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        EditText usernameText = findViewById(R.id.username);
        EditText emailText = findViewById(R.id.sign_email);
        EditText phoneNumberText = findViewById(R.id.Phone_number);
        EditText passText = findViewById(R.id.Pass);
        EditText confPassText = findViewById(R.id.ConfPass);
        Button btn = findViewById(R.id.btn_login);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!passText.getText().toString().equals(confPassText.getText().toString())){
                    btn.setText("Password Doesn't Match");
                    btn.setClickable(false);
                    btn.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            btn.setText("Sign Up");
                            btn.setClickable(true);
                        }
                    }, 1000);
                } else {
                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("username", usernameText.getText().toString());
                        jsonBody.put("email", emailText.getText().toString());
                        jsonBody.put("phone_number", phoneNumberText.getText().toString());
                        jsonBody.put("password", passText.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest jsonRequest = new JsonObjectRequest(
                            Request.Method.POST,
                            "https://digitalprotect.id/api/v1/auth/register",
                            jsonBody,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // Display the first 500 characters of the response string.
                                    try {
                                        String saveToken=response.getString("token");
                                        edit.putString("username", response.getJSONObject("payload").getString("username"));
                                        edit.putString("email", response.getJSONObject("payload").getString("email"));
                                        edit.putString("phone_number", response.getJSONObject("payload").getString("phone_number"));
                                        edit.putString("token",saveToken);
                                        Log.i("Login",saveToken);
                                        edit.commit();
                                    }
                                    catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    }

                                    Intent i = new Intent(MainActivity.this, ActivityHome.class);
                                    startActivity(i);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String json = null;

                            NetworkResponse response = error.networkResponse;
                            if(response != null && response.data != null){
                                json = new String(response.data);
                                json = trimMessage(json, "message");
                                if(json != null) displayMessage(json);
                                //Additional cases
                            }
                            btn.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    btn.setText("Sign Up");
                                }
                            }, 2000);
                        }
                    });

                    queue.add(jsonRequest);
                }

            }
        });
    }

    public String trimMessage(String json, String key){
        String trimmedString = null;

        try{
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }

    //Somewhere that has access to a context
    public void displayMessage(String toastString){
        Toast.makeText(this, toastString, Toast.LENGTH_LONG).show();
    }

    public void goToLoginPage(View view){
        Intent i = new Intent(this, MainActivity2.class);
        startActivity(i);
    }
}