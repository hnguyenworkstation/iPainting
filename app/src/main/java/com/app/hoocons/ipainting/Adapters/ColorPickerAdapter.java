package com.app.hoocons.ipainting.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.hoocons.ipainting.R;
import com.app.hoocons.ipainting.ViewHolders.ColorViewHolder;
import com.app.hoocons.ipainting.ViewHolders.OnColorClickListener;

/**
 * Created by hungnguyen on 6/7/18.
 * At: 1:20 AM
 */

/*
* Adapter for a list of colors objects
* -- This adapter will work on generate/recycle/listen to each color's circle
* */
public class ColorPickerAdapter extends RecyclerView.Adapter<ColorViewHolder> {
    private Context context;
    private OnColorClickListener listener;
    private int[] colors;

    public ColorPickerAdapter(Context context, int[] colors, OnColorClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.colors = colors;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_filled_circle,
                parent, false);
        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        holder.initCircle(colors[position], position, false, listener);
    }

    @Override
    public int getItemCount() {
        return colors.length;
    }

    @Override
    public void onViewRecycled(@NonNull ColorViewHolder holder) {
        super.onViewRecycled(holder);
    }
}
