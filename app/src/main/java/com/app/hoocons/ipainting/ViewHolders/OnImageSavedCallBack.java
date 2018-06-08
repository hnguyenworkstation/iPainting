package com.app.hoocons.ipainting.ViewHolders;

/**
 * Created by hungnguyen on 6/7/18.
 * At: 9:30 PM
 */


public interface OnImageSavedCallBack {
    /**
     * On image saved.
     *
     * This callback will be listened from the DrawingFragment to know what is going on with the
     * saving image to disk async task.
     *
     * @param savedFile the saved file path
     *                  This file's path will be used to tell the media collection to look for such name
     *                  then collect it if exists
     *
     * @param isSuccess the status of saving process
     */
    void onImageSaved(final String savedFile, boolean isSuccess);
}
