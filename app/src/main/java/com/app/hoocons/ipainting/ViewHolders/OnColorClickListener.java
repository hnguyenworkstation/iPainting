package com.app.hoocons.ipainting.ViewHolders;

/**
 * Created by hungnguyen on 6/7/18.
 * At: 1:31 AM
 */
public interface OnColorClickListener {
    /**
     * On color clicked int.
     *
     * Drawing fragment will listen to the new position clicked on the color views
     * so it will update the equivalent color based on the new position.
     *
     * @param position the position of the item clicked on the adapter
     */
    void onColorClicked(final int position);
}
