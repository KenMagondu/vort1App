package com.example.vortex1application4;

public class Course {
    private String id;
    private String fileName;
    private String filePath;
    private String uploadedBy;

    public Course(String id, String fileName, String filePath, String uploadedBy) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
        this.uploadedBy = uploadedBy;
    }

    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }
}