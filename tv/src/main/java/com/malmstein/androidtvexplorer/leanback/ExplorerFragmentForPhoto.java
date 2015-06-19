package com.malmstein.androidtvexplorer.leanback;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;

import com.malmstein.androidtvexplorer.photo.Photo;

import java.util.List;

/**
 * Created by chalse.park on 2015-06-19.
 */
public class ExplorerFragmentForPhoto extends BrowseFragment implements LoaderManager.LoaderCallbacks<List<Photo>> {


    @Override
    public Loader<List<Photo>> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Photo>> loader, List<Photo> data) {

    }

    @Override
    public void onLoaderReset(Loader<List<Photo>> loader) {

    }
}
