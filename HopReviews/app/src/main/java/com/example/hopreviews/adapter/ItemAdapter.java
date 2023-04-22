package com.example.hopreviews.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hopreviews.R;
import com.example.hopreviews.data.model.Image;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

// a lot of this code is derived from its Kotlin version on an open source
// android tutorial https://developer.android.com/codelabs/basic-android-kotlin-training-display-list-cards#3

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final Context context;
    private final List<Image> dataset;

    public ItemAdapter(Context context, List<Image> dataset) {
        this.context = context;
        this.dataset = dataset;
    }
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ItemViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.item_image);
        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View adapterLayout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ItemViewHolder(adapterLayout);
    }
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Image item = dataset.get(position);
        Uri uri = item.getImageResourceId();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(uri.toString());
                    Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    holder.imageView.setImageBitmap(bitmap);
                } catch (Exception e) {

                }
            }
        });
        thread.start();
    }
    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
