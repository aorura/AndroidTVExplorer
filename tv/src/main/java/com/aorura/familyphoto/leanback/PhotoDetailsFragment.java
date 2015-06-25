package com.aorura.familyphoto.leanback;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v17.leanback.app.DetailsFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aorura.familyphoto.R;

/**
 * Created by aorura on 15. 6. 25..
 */
public class PhotoDetailsFragment extends Fragment {
    Bitmap selectedPhoto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedPhoto = (Bitmap) getActivity().getIntent().getParcelableExtra(getString(R.string.photo));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.photo_view_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ImageView imageview = (ImageView) getView().findViewById(R.id.photo_view);

        if (imageview != null && selectedPhoto != null) {
            imageview.setImageBitmap(selectedPhoto);
        }
    }

}
