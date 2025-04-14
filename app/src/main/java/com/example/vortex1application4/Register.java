package com.example.vortex1application4;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Register extends AppCompatActivity {
    // variables
    TextInputEditText txt_username, txt_password, txt_conpassword;
    MaterialButton btnRegister;
    android.widget.TextView callLogin;

   // String url_register = "http://192.168.100.7/Vortex1App/register.php";

    String url_register = "http://192.168.0.22/Vortex1App/register.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // EdgeToEdge.enable(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        //hooking the variables
        callLogin = findViewById(R.id.login_TV);
        txt_username = findViewById(R.id.register_username);
        txt_password = findViewById(R.id.register_password);
        txt_conpassword = findViewById(R.id.register_cpassword);
        btnRegister = findViewById(R.id.btn_register);


        //Register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoRegister();
            }
        });

        // Login text
        callLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);

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
                        } else if (success.equals("2")) {
                            progressDialog.dismiss();
                            Toast.makeText(Register.this, "Username Already Exist", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(Register.this, e.toString(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
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
// Set custom retry policy
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    5000, // Timeout in milliseconds
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));



            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }
}