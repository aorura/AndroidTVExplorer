package com.malmstein.androidtvexplorer.photo;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by aorura on 15. 6. 18..
 */
public class PhotoItemLoader extends AsyncTaskLoader<List<Photo>> {
    public PhotoItemLoader(Context context) {
        super(context);
    }

    @Override
    public List<Photo> loadInBackground() {
        return null;
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
