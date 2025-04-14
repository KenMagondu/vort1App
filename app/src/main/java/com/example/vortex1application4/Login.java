package com.example.vortex1application4;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    // variables
    TextInputEditText txt_username, txt_password;
    MaterialTextView tv_error;
    MaterialButton btnLogin;
    MaterialTextView callSignUp;

 //   String url_login = "http://192.168.100.7/Vortex1App/login.php";

  String url_login = "http://192.168.0.22/Vortex1App/login.php";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        // hooking the variables
        callSignUp = findViewById(R.id.register_TV);
        txt_username = findViewById(R.id.login_username);
        txt_password = findViewById(R.id.login_password);
        //tv_error = findViewById(R.id.tv_error);
        btnLogin = findViewById(R.id.login_btnlogin);

        // Register text
        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        // Login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoLogin();
            }
        });
    }

    private void GoLogin() {
        String username = txt_username.getText().toString().trim();
        String password = txt_password.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            tv_error.setText("Enter username and password");
        } else {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Logging in...");
            progressDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url_login,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                JSONArray jsonArray = jsonObject.getJSONArray("login");
                                if (success.equals("1")) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        String ojbuserid = object.getString("user_id");
                                        String ojbusername = object.getString("username").trim();
                                        int isAdmin = object.getInt("isAdmin"); // Retrieve isAdmin
                                        Toast.makeText(Login.this, "User Id: " + ojbuserid + "\nYour Username: " + ojbusername, Toast.LENGTH_SHORT).show();

                                        // Pass the username &isAdmin to Home activity
                                        Intent intent = new Intent(Login.this, Home.class);
                                        intent.putExtra("username", ojbusername); //Pass username
                                        intent.putExtra("isAdmin", isAdmin); // Pass isAdmin
                                        startActivity(intent);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(Login.this, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(Login.this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
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
}