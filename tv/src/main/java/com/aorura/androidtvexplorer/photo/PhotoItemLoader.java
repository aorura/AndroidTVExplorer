package com.aorura.androidtvexplorer.photo;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aorura on 15. 6. 18..
 */
public class PhotoItemLoader extends AsyncTaskLoader<List<Photo>> {
    private static final String TAG = "Loader";
    private Context context;
    private MetadataBuffer metadatas;
    GoogleApiClient googleApiClient;

    public PhotoItemLoader(Context context) {
        super(context);
        this.context = context;
    }

    public PhotoItemLoader(Context context, MetadataBuffer metadatas, GoogleApiClient googleApiClient) {
        super(context);
        this.context = context;
        this.metadatas = metadatas;
        this.googleApiClient = googleApiClient;
    }

    @Override
    public List<Photo> loadInBackground() {
        List<Photo> photos = new ArrayList<Photo>();
        Bitmap bitmap = null;

        for (Metadata m : metadatas) {
            DriveFile file = Drive.DriveApi.getFile(googleApiClient, m.getDriveId());
            DriveApi.DriveContentsResult driveContentsResult = file.open(googleApiClient, DriveFile.MODE_READ_ONLY, null).await();
            if (!driveContentsResult.getStatus().isSuccess()) {
                return null;
            }
            DriveContents driveContents = driveContentsResult.getDriveContents();
            bitmap = BitmapFactory.decodeStream(driveContents.getInputStream());
            Photo photo = new Photo(m.getDriveId(), m.getTitle(), bitmap);
            photos.add(photo);
            driveContents.discard(googleApiClient);
        }

        Log.d(TAG, "size: " + photos.size());

        return photos;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        cancelLoad();
    }
}
