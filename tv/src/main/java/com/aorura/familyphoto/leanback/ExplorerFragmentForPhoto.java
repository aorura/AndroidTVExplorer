package com.aorura.familyphoto.leanback;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.Loader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.aorura.familyphoto.R;
import com.aorura.familyphoto.photo.Photo;
import com.aorura.familyphoto.photo.PhotoItemLoader;
import com.aorura.familyphoto.presenters.GridItemPresenter;
import com.aorura.familyphoto.presenters.PicassoBackgroundManagerTarget;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.URI;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chalse.park on 2015-06-19.
 */
public class ExplorerFragmentForPhoto extends BrowseFragment implements LoaderManager.LoaderCallbacks<List<Photo>>, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final int REQUEST_CODE_RESOLUTION = 1;
    private static final String TAG = "Photo";
    private GoogleApiClient mGoogleApiClient;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private Drawable mDefaultBackground;
    private Target mBackgroundTarget;
    private final Handler mHandler = new Handler();
    private URI mBackgroundURI;
    MetadataBuffer metaDatas;
    private Context mContext;
    private ArrayObjectAdapter mRowsAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);

        prepareBackgroundManager();
        setupUIElements();
        setupEventListeners();
    }

    private void loadPhotoData() {
        // todo
        getLoaderManager().initLoader(0, null, this);
    }

    private void prepareBackgroundManager() {
        BackgroundManager backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());
        mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);

        mDefaultBackground = getResources().getDrawable(R.drawable.default_background);

        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupUIElements() {
        setTitle(getString(R.string.browse_title));
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
    }

    private void setupEventListeners() {
        setOnItemViewSelectedListener(getDefaultItemSelectedListener());
        setOnItemViewClickedListener(getDefaultItemClickedListener());
        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (metaDatas != null) {
                    queryFromDrive();
                    Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Drive is not connected...", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    protected OnItemViewSelectedListener getDefaultItemSelectedListener() {
        return new OnItemViewSelectedListener() {
            @Override
            public void onItemSelected(Presenter.ViewHolder viewHolder, Object item, RowPresenter.ViewHolder viewHolder2, Row row) {
                if (item instanceof Photo) {
//                    mBackgroundURI = ((Video) item).getBackgroundImageURI();
//                    startBackgroundTimer()
                }
            }
        };
    }

    protected OnItemViewClickedListener getDefaultItemClickedListener() {
        return new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder viewHolder, Object item, RowPresenter.ViewHolder viewHolder2, Row row) {
//                Toast.makeText(getActivity(), "OnItemViewClickedListener", Toast.LENGTH_LONG).show();
//                if (item instanceof Photo) {
//                    Photo photo = (Photo) item;
//                    Intent videoIntent = new Intent(getActivity(), VideoDetailsActivity.class);
//                    videoIntent.putExtra(getResources().getString(R.string.video), photo);
//                    startActivity(videoIntent);
//                } else if (item instanceof String) {
//                    showMessage((String) item);
//                }
            }
        };
    }

    protected void updateBackground(URI uri) {
        Picasso.with(getActivity())
                .load(uri.toString())
                .resize(mMetrics.widthPixels, mMetrics.heightPixels)
                .centerCrop()
                .error(mDefaultBackground)
                .into(mBackgroundTarget);
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), 300);
    }

    private class UpdateBackgroundTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mBackgroundURI != null) {
                        updateBackground(mBackgroundURI);
                    }
                }
            });

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER) // required for App Folder sample
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == Activity.RESULT_OK) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    /**
     * Shows a toast message.
     */
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    /**
     * Getter for the {@code GoogleApiClient}.
     */
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    // Loader

    @Override
    public Loader<List<Photo>> onCreateLoader(int id, Bundle args) {
        return new PhotoItemLoader(getActivity(), metaDatas, mGoogleApiClient);
    }

    @Override
    public void onLoadFinished(Loader<List<Photo>> loader, List<Photo> data) {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        GridItemPresenter gridPresenter = new GridItemPresenter();
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(gridPresenter);
        int i = 0;


        for (Photo photo : data) {
            listRowAdapter.add(photo);
        }
        HeaderItem header = new HeaderItem(i++, getResources().getString(R.string.photo), null);
        mRowsAdapter.add(new ListRow(header, listRowAdapter));
        setAdapter(mRowsAdapter);
    }

    @Override
    public void onLoaderReset(Loader<List<Photo>> loader) {
        mRowsAdapter.clear();
    }

    // google api

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected");
//        Query query = new Query.Builder()
//                .addFilter(Filters.or(
//                        Filters.eq(SearchableField.MIME_TYPE, "image/png"),
//                        Filters.eq(SearchableField.MIME_TYPE, "image/jpeg"),
//                        Filters.eq(SearchableField.MIME_TYPE, "image/jpg")))
//                .build();
//
//        Drive.DriveApi.query(getGoogleApiClient(), query)
//                .setResultCallback(metadataCallback);
        queryFromDrive();
    }

    protected void queryFromDrive() {
        Query query = new Query.Builder()
                .addFilter(Filters.or(
                        Filters.eq(SearchableField.MIME_TYPE, "image/png"),
                        Filters.eq(SearchableField.MIME_TYPE, "image/jpeg"),
                        Filters.eq(SearchableField.MIME_TYPE, "image/jpg")))
                .build();

        Drive.DriveApi.query(getGoogleApiClient(), query)
                .setResultCallback(metadataCallback);
    }

    final private ResultCallback<DriveApi.MetadataBufferResult> metadataCallback =
            new ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Problem while retrieving results");
                        return;
                    }

                    metaDatas = result.getMetadataBuffer();

                    loadPhotoData();
                }
            };

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), getActivity(), 0).show();
            return;
        }
        try {
            result.startResolutionForResult(getActivity(), REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }
}
