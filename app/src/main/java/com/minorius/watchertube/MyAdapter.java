package com.minorius.watchertube;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<ViewElement> listForView;
    private Context context;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private TextView descriptionTextView;
        private TextView duration;
        private ImageView imageView;

        public ViewHolder(LinearLayout v) {
            super(v);
            titleTextView = (TextView) v.findViewById(R.id.id_text_title);
            descriptionTextView = (TextView) v.findViewById(R.id.id_text_description);
            duration = (TextView) v.findViewById(R.id.id_text_duration);
            imageView = (ImageView) v.findViewById(R.id.id_image_view);

        }
    }

    public MyAdapter(ArrayList<ViewElement> listForView, Context context) {
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

        holder.titleTextView.setText(listForView.get(position).getTitle());
        holder.descriptionTextView.setText(listForView.get(position).getDescription());
        holder.duration.setText(listForView.get(position).getDuration());
        Picasso.with(context)
                .load(listForView.get(position).getImageUrl())
                .placeholder(R.drawable.com_facebook_button_icon)
                .error(R.drawable.com_facebook_button_icon)
                .into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, VideoActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("VIDEO_URL", listForView.get(position).getVideoUrl());
                System.out.println(listForView.get(position).getVideoUrl());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listForView.size();
    }
}