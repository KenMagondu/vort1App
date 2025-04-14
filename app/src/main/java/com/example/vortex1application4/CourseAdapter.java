package com.example.vortex1application4;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private Context context;
    private List<Course> courseList;
    private static final String TAG = "CourseAdapter";

    public CourseAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.fileNameTextView.setText(course.getFileName());
        holder.uploadedByTextView.setText("Uploaded by: " + course.getUploadedBy());

        holder.itemView.setOnClickListener(v -> {
            // Download the file instead of viewing it
          //  String fileUrl = "http://10.0.2.2/Vortex1App/" + course.getFilePath(); // Use 10.0.2.2 for emulator
            String fileUrl = "http://192.168.0.22/Vortex1App/" + course.getFilePath();
            String filePath = course.getFilePath();
            String fileName = filePath.substring(filePath.lastIndexOf("_") + 1);

         //   String fileName = course.getFileName();

            try {
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(fileUrl);

                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setTitle(fileName);
                request.setDescription("Downloading " + fileName);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                request.setMimeType("application/pdf"); // Adjust MIME type if needed

                downloadManager.enqueue(request);
                Toast.makeText(context, "Downloading " + fileName, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(context, "Download failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView fileNameTextView, uploadedByTextView;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.course_file_name);
            uploadedByTextView = itemView.findViewById(R.id.course_uploaded_by);
        }
    }
}