package com.example.vortex1application4;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private int isAdmin; // Admin status
    private String username; // Store username

    private RecyclerView coursesRecyclerView;
    private CourseAdapter courseAdapter;
    private List<Course> courseList;
  //  private String url_courses = getString(R.string.base_url) + "fetch_courses.php";
  private String url_courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize URL in onCreate
        url_courses = getString(R.string.base_url) + "fetch_courses.php";

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Retrieve username and isAdmin from Intent
        username = getIntent().getStringExtra("username");
        isAdmin = getIntent().getIntExtra("isAdmin", 0); // Default to 0 if not found

        // Set username in the TextView
        TextView usernameTextView = navigationView.getHeaderView(0).findViewById(R.id.usernameTextView);
        usernameTextView.setText(username);

        // Enable "Users" and "Courses" menu items for admins only
        Menu menu = navigationView.getMenu();
        MenuItem usersItem = menu.findItem(R.id.nav_users);
        MenuItem coursesItem = menu.findItem(R.id.nav_courses);
        usersItem.setEnabled(isAdmin == 1);
        coursesItem.setVisible(isAdmin == 1); // Show Courses only for admins

        // Initialize RecyclerView
        coursesRecyclerView = findViewById(R.id.courses_recycler_view);
        coursesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseList = new ArrayList<>();
        courseAdapter = new CourseAdapter(this, courseList);
        coursesRecyclerView.setAdapter(courseAdapter);

// Fetch courses
        fetchCourses();


        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private void fetchCourses() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_courses,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        if (success.equals("1")) {
                            courseList.clear();
                            JSONArray coursesArray = jsonObject.getJSONArray("courses");
                            for (int i = 0; i < coursesArray.length(); i++) {
                                JSONObject courseObj = coursesArray.getJSONObject(i);
                                Course course = new Course(
                                        courseObj.getString("id"),
                                        courseObj.getString("file_name"),
                                        courseObj.getString("file_path"),
                                        courseObj.getString("uploaded_by")
                                );
                                courseList.add(course);
                            }
                            courseAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "Failed to load courses: " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing courses", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_users && isAdmin == 0) {
            Toast.makeText(this, "You do not have permission to access Users.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (id == R.id.nav_users) {
            Intent intent = new Intent(Home.this, Users.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_courses && isAdmin == 1) {
            Intent intent = new Intent(Home.this, AdminCourses.class);
            intent.putExtra("username", username); // Pass username for upload tracking
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(Home.this, Login.class);
            startActivity(intent);
            finish(); // Close Home activity
            return true;
        }
        return true;
    }
}