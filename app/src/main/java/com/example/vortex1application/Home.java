package com.example.vortex1application;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    ListView listView;
    HomeAdapter homeAdapter;
    public static ArrayList<HomeListview> homeArraylist = new ArrayList<>();
    HomeListview homeListview;

    String url_listview = "http://192.168.100.7/Vortex1App/showhome.php";
    String url_delete = "http://192.168.100.7/Vortex1App/delete.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

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
                                // Update Data
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });

        ShowHome();
    }






//functions ShowHome , Dele
    private  void  ShowHome(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, url_listview,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        homeArraylist.clear();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String success_ = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            if (success_.equals("1")) {
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
                            Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Home.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
    private void Delete(String position) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Processing...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_delete, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response", response); // Log the response to see what the server returns
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    Log.e("Success", success);
                    if (success.equals("1")) {
                        Toast.makeText(Home.this, "Delete Success", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Home.this, e.toString(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Home.this, error.toString(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userid", position);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}