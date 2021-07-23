package com.example.fbu_final_project.models;

import org.parceler.Parcel;

@Parcel
public class DriveFile {

    private String fileId;
    private String fileName;
    private String thumbnail;

    // no-arg, empty constructor required for Parceler
    public DriveFile() {}

    public DriveFile(String id, String name, String thumbnailPath) {
        this.fileId = id;
        this.fileName = name;
        this.thumbnail = thumbnailPath;
    }

    public String getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}
