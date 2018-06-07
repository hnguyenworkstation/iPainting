package com.app.hoocons.ipainting.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.app.hoocons.ipainting.CustomUI.FilledCircleView;
import com.app.hoocons.ipainting.R;

/**
 * Created by hungnguyen on 6/7/18.
 * At: 1:20 AM
 */

/*
* View holder for color picker's item
* */
public class ColorViewHolder extends RecyclerView.ViewHolder {
    private FilledCircleView circleView;

    public ColorViewHolder(View itemView) {
        super(itemView);
    }


    /**
     * Init circle.
     * Draw a filled circle by applying new properties
     *
     * @param color      the color to fill this circle
     * @param position   the position of the color in the collection of colors
     * @param isSelected to check if this color is currently being used
     * @param listener   the listener to tell if this view is currently being clicked
     */
    public void initCircle(final int color, final int position, boolean isSelected,
                           final OnColorClickListener listener) {
        // Bind View
        circleView = (FilledCircleView) itemView.findViewById(R.id.circle_view);

        circleView.setColor(color);
        circleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onColorClicked(position);
            }
        });
    }
}
