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
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextInputEditText txt_username, txt_password;
    MaterialTextView tv_error;
    MaterialButton btnLogin;

    String url_login = "http://192.168.100.7/Vortex1App/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        txt_username = findViewById(R.id.login_username);
        txt_password = findViewById(R.id.login_password);
        tv_error = findViewById(R.id.error_main);
        btnLogin = findViewById(R.id.login_btnlogin);



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
//                            if (response.isEmpty()) {
//                                Toast.makeText(MainActivity.this, "Error: Empty response from server", Toast.LENGTH_LONG).show();
//                                return;
//                            }
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                JSONArray jsonArray = jsonObject.getJSONArray("login");
                                if (success.equals("1")) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        String ojbuserid = object.getString("user_id");
                                        String ojbusername = object.getString("username").trim();
                                        Toast.makeText(MainActivity.this, "User Id. " +ojbuserid +" \nYour Username: " + ojbusername, Toast.LENGTH_SHORT).show();
                                        // progressDialog.dismiss();
                                    }
                                    startActivity(new Intent( MainActivity.this, Home.class));
                                }
                               // progressDialog.dismiss();
                            } catch (JSONException e){
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "Error" + e.toString(), Toast.LENGTH_LONG).show();
                                //   progressDialog.dismiss();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.this, "Errors" + error.toString(), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);  //must be same as in the php file
                    params.put("password", password);  //must be same as in the php file
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }
    public void Login(View view) {
        startActivity(new Intent(MainActivity.this, Register.class));

    }


    }
