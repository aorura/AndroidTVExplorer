package com.aorura.familyphoto.photo;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.drive.DriveId;

import java.io.Serializable;

/**
 * Created by aorura on 15. 6. 18..
 */
public class Photo implements Serializable {
    static final long serialVersionUID = 727566175075960654L;

    DriveId driveId;
    Bitmap bitmap;
    String title;

    public Photo() {
        this.title = "none";
        this.bitmap = null;
        this.driveId = null;
    }

    public Photo(DriveId driveId, String title, Bitmap bitmap) {
        this.driveId = driveId;
        this.title = title;
        this.bitmap = bitmap;
    }

    public DriveId getDriveId() {
        return this.driveId;
    }

    public String getTitle() {
        return this.title;
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }

    @Override
    public String toString() {
        return title;
    }
}
