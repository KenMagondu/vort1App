package com.example.vortex1application;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

public class Home extends AppCompatActivity {

    ListView listView;
    HomeAdapter homeAdapter;
    public static ArrayList<HomeListview> homeArraylist = new ArrayList<>();
    HomeListview homeListview;

    String url_listview = "http://192.168.100.7/Vortex1App/showhome.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        listView = findViewById(R.id.historylistview);
        homeAdapter = new HomeAdapter(this, homeArraylist);
        listView.setAdapter(homeAdapter);

        ShowHome();
    }
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
}