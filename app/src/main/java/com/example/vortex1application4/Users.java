package com.example.vortex1application4;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Users extends AppCompatActivity {

    ListView listView;
    HomeAdapter homeAdapter;
    public static ArrayList<HomeListview> homeArraylist = new ArrayList<>();
    HomeListview homeListview;

//    String url_listview = "http://192.168.100.7/Vortex1App/showhome.php";
//    String url_delete = "http://192.168.100.7/Vortex1App/delete.php";
//    String url_update = "http://192.168.100.7/Vortex1App/update.php";

    String url_listview = "http://192.168.0.22/Vortex1App/showhome.php";
   String url_delete = "http://192.168.0.22/Vortex1App/delete.php";
    String url_update = "http://192.168.0.22/Vortex1App/update.php";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        listView = findViewById(R.id.historylistview);
        homeAdapter = new HomeAdapter(this, homeArraylist);
        listView.setAdapter(homeAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                ProgressDialog progressDialog = new ProgressDialog(view.getContext());

                CharSequence[] dialogItem = {"Delete Data", "Update Data"};
                builder.setTitle(homeArraylist.get(position).getUsername());
                builder.setItems(dialogItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch (i) {
                            case 0:
                                Delete(homeArraylist.get(position).getUserid());
                                break;
                            case 1:
                                showUpdateDialog(homeArraylist.get(position).getUserid(), homeArraylist.get(position).getUsername());
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });

        ShowUsers();
    }

    private void ShowUsers() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading users...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, url_listview,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        homeArraylist.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String userid = object.getString("userid");
                                    String username = object.getString("username");

                                    homeListview = new HomeListview(userid, username);
                                    homeArraylist.add(homeListview);
                                    homeAdapter.notifyDataSetChanged();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Users.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Users.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void Delete(String userid) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Processing...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_delete,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if (success.equals("1")) {
                                Toast.makeText(Users.this, "Delete Success", Toast.LENGTH_LONG).show();
                                ShowUsers();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Users.this, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Users.this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userid", userid);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showUpdateDialog(String userid, String currentUsername) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update User");

        final EditText inputUsername = new EditText(this);
        inputUsername.setHint("New Username");
        inputUsername.setText(currentUsername); // Pre-fill with current username
        builder.setView(inputUsername);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newUsername = inputUsername.getText().toString().trim();
                if (newUsername.isEmpty()) {
                    Toast.makeText(Users.this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    Update(userid, newUsername);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void Update(String userid, String newUsername) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_update,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if (success.equals("1")) {
                                Toast.makeText(Users.this, "Update Success", Toast.LENGTH_LONG).show();
                                ShowUsers(); // Refresh the list
                            } else {
                                String message = jsonObject.optString("message", "Update Failed");
                                Toast.makeText(Users.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Users.this, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Users.this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userid", userid);
                params.put("username", newUsername);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}