package com.aorura.androidtvexplorer.presenters;

import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aorura.androidtvexplorer.R;
import com.aorura.androidtvexplorer.photo.Photo;

public class GridItemPresenter extends Presenter {

    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.grid_item, null);
        ImageView faceImage = (ImageView) relativeLayout.findViewById(R.id.face_image);
        TextView faceTitle = (TextView) relativeLayout.findViewById(R.id.face_title);

//        TextView view = new TextView(parent.getContext());
        relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(400, 400));
        relativeLayout.setFocusable(true);
        relativeLayout.setFocusableInTouchMode(true);
        faceTitle.setBackgroundColor(parent.getResources().getColor(R.color.default_background));

        return new ViewHolder(relativeLayout);
    }

    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        RelativeLayout relativeLayout = (RelativeLayout) viewHolder.view;
        ImageView faceImage = (ImageView) relativeLayout.findViewById(R.id.face_image);
        TextView faceTitle = (TextView) relativeLayout.findViewById(R.id.face_title);
        Photo photo = (Photo) item;

        faceTitle.setText(photo.getTitle());
        faceImage.setImageBitmap(photo.getBitmap());
    }

    public void onUnbindViewHolder(ViewHolder viewHolder) {
    }
}
