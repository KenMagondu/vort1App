package com.example.vortex1application;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    TextInputEditText txt_username, txt_password, txt_conpassword;
    MaterialButton btnRegister;
    String url_register = "http://192.168.100.7/Vortex1App/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        txt_username = findViewById(R.id.register_username);
        txt_password = findViewById(R.id.register_password);
        txt_conpassword = findViewById(R.id.register_cpassword);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoRegister();
            }
        });

    }
    public void GoRegister() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        String username = txt_username.getText().toString().trim();
        String password = txt_password.getText().toString().trim();
        String conpassword = txt_conpassword.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(Register.this, "Insert Username", Toast.LENGTH_LONG).show();
        } else if (password.isEmpty()) {
            Toast.makeText(Register.this, "Insert Password", Toast.LENGTH_LONG).show();
        } else if (conpassword.isEmpty()) {
            Toast.makeText(Register.this, "Insert Confirm Password", Toast.LENGTH_LONG).show();
        } else if (!password.equals(conpassword)) {
            Toast.makeText(Register.this, "Password Not Match", Toast.LENGTH_LONG).show();
        } else {
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url_register, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        if (success.equals("1")) {
                            progressDialog.dismiss();
                            Toast.makeText(Register.this, "Register Successful", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(Register.this, e.toString(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Register.this, error.toString(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("password", password);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }

    public void Register(View view) {
        startActivity(new Intent(Register.this, MainActivity.class));
    }








}
