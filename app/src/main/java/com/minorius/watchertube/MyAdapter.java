package com.minorius.watchertube;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minorius.watchertube.dbtube.MyIMG;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;

class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<ViewElement> listForView;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private TextView descriptionTextView;
        private ImageView imageView;
        private TextView durationTextView;

        ViewHolder(LinearLayout v) {
            super(v);
            titleTextView = (TextView) v.findViewById(R.id.id_text_title);
            descriptionTextView = (TextView) v.findViewById(R.id.id_text_description);
            durationTextView = (TextView) v.findViewById(R.id.id_text_duration);
            imageView = (ImageView) v.findViewById(R.id.id_image_view);
        }
    }

    MyAdapter(ArrayList<ViewElement> listForView, Context context) {
        this.listForView = listForView;
        this.context = context;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_text_view, parent, false);
        return new ViewHolder((LinearLayout) v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final String title = listForView.get(position).getTitle();
        final String description = listForView.get(position).getDescription();
        final String duration = listForView.get(position).getDuration();
        final String imageName = listForView.get(position).getImageUrl();

        holder.titleTextView.setText(title);
        holder.descriptionTextView.setText(description);
        holder.durationTextView.setText(duration);


        String convertName = MyIMG.getParseNameFromUrl(imageName);
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File imageForRecyclerFromFile = new File(directory, convertName);
        if (imageForRecyclerFromFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imageForRecyclerFromFile.getAbsolutePath());
            holder.imageView.setImageBitmap(myBitmap);
        }else {
            RequestCreator requestCreator = Picasso.with(context).load(listForView.get(position).getImageUrl());

            requestCreator.placeholder(R.drawable.com_facebook_button_icon)
                    .error(R.drawable.com_facebook_button_icon)
                    .into(holder.imageView);
            requestCreator.into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    MyIMG myIMG = new MyIMG();
                    String imageName = MyIMG.getParseNameFromUrl(listForView.get(position).getImageUrl());
                    if(!myIMG.isImageLoaded(context, imageName)){
                        myIMG.saveToInternalStorage(bitmap, context, imageName);
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, VideoActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("VIDEO_URL", listForView.get(position).getVideoUrl());
                System.out.println("MyAdapter "+listForView.get(position).getVideoUrl());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listForView.size();
    }
}