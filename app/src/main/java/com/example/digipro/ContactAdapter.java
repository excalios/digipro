package com.example.digipro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private final Context context;
    private ArrayList<ContactModel> contactModelArrayList;

    public ContactAdapter(Context context, ArrayList<ContactModel> contactModelArrayList){
        this.context = context;
        this.contactModelArrayList = contactModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_card_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContactModel model = contactModelArrayList.get(position);
        holder.name.setText(model.getName());
        holder.phoneNumber.setText(model.getPhoneNumber());
        int index = position;

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs;
                SharedPreferences.Editor edit;
                prefs = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                String token = prefs.getString("token", "");

                JsonObjectRequest jsonRequest = new JsonObjectRequest(
                        Request.Method.DELETE,
                        "https://digitalprotect.id/api/v1/emergency-contacts/" + model.getId(),
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                contactModelArrayList.remove(index);
                                notifyItemRemoved(index);
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

                VolleySingleton.getInstance(context).addToRequestQueue(jsonRequest);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView phoneNumber;
        private final ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.emergency_name);
            phoneNumber = itemView.findViewById(R.id.emergency_phone);
            deleteButton  = itemView.findViewById(R.id.deleteCardContact);
        }
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
        Toast.makeText(context, toastString, Toast.LENGTH_LONG).show();
    }
}
