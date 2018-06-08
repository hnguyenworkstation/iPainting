package com.app.hoocons.ipainting.ViewFragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.hoocons.ipainting.Adapters.ColorPickerAdapter;
import com.app.hoocons.ipainting.CustomUI.FilledCircleView;
import com.app.hoocons.ipainting.CustomUI.PaintView;
import com.app.hoocons.ipainting.Helpers.AppUtils;
import com.app.hoocons.ipainting.Helpers.Constants;
import com.app.hoocons.ipainting.Helpers.SaveImageToDiskAsync;
import com.app.hoocons.ipainting.R;
import com.app.hoocons.ipainting.ViewHolders.OnColorClickListener;
import com.app.hoocons.ipainting.ViewHolders.OnImageSavedCallBack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * The Drawing fragment.
 */
public class DrawingFragment extends Fragment implements View.OnClickListener, OnColorClickListener, OnImageSavedCallBack {
    private final String TAG = this.getClass().getSimpleName();

    private final int MIN_STROKE_SIZE = 5;
    private final int IMAGE_PICKING_CODE = 1;
    private final int READ_WRITE_STORAGE_PERMISSION_CODE = 2;

    /* View entities */
    private PaintView mPaintView;

    /* Action Buttons */
    private ImageButton mBackStepBtn;
    private ImageButton mForceStepBtn;

    /* Menu Actions */
    private LinearLayout mColorPicker;
    private LinearLayout mStrokeSizePicker;
    private LinearLayout mImagePicker;

    private FilledCircleView mSelectedColorCircle;
    private FilledCircleView mSizeCircle;
    private TextView mCurrentSizeTv;

    private ImageButton mPencilActionBtn;
    private ImageButton mEraserActionBtn;
    private ImageButton mMoreActionBtn;

    /* picker dialog */
    private Dialog sizePickerDialog;
    private Dialog colorPickerDialog;
    private ColorPickerAdapter colorPickerAdapter;

    private int currentColor;
    private int currentStrokeSize;
    private int[] colors;

    // Current action determine that if the user is currently wanted to draw or erase
    private Constants.Action mCurrentAction = Constants.Action.PAINTING;

    // Popup menu will be used for more actions;
    private PopupMenu mMoreMenu;

    // this a flag to tell if user has already sent one request to save an image and it is currently
    // running in the background or not.
    private boolean isSavingImage = false;

    public DrawingFragment() {
        // Required empty public constructor
    }


    public static DrawingFragment newInstance() {
        DrawingFragment fragment = new DrawingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        colors = getResources().getIntArray(R.array.colors);

        currentColor = colors[0];
        currentStrokeSize = Constants.DEFAULT_BRUSH_STROKE_SIZE;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drawing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCurrentAction = Constants.Action.PAINTING;

        bindViews(view);
        initViewEntities();
        initColorPickerDialog();
        initSizePickerDialog();
        initClickListeners();
    }

    /* Bind view variables into related view objects by ids */
    private void bindViews(View parent) {
        mPaintView = (PaintView) parent.findViewById(R.id.paint_view);

        /* Action Buttons */
        mBackStepBtn = (ImageButton) parent.findViewById(R.id.backstep_btn);
        mForceStepBtn = (ImageButton) parent.findViewById(R.id.forcestep_btn);

        mPencilActionBtn = (ImageButton) parent.findViewById(R.id.pen_action_btn);
        mEraserActionBtn = (ImageButton) parent.findViewById(R.id.eraser_action_btn);
        mMoreActionBtn = (ImageButton) parent.findViewById(R.id.more_action_btn);

        mColorPicker = (LinearLayout) parent.findViewById(R.id.color_picker);
        mStrokeSizePicker = (LinearLayout) parent.findViewById(R.id.size_picker);
        mImagePicker = (LinearLayout) parent.findViewById(R.id.image_picker);

        mSelectedColorCircle = (FilledCircleView) parent.findViewById(R.id.selected_color);
        mSizeCircle = (FilledCircleView) parent.findViewById(R.id.size_circle);
        mCurrentSizeTv = (TextView) parent.findViewById(R.id.stroke_size);
    }

    /*
    * initViewEntities
    *
    * initialize the paintview and also display its initial properties to the screen
    * */
    private void initViewEntities() {
        DisplayMetrics metrics = new DisplayMetrics();

        if (getActivity() != null) {
            WindowManager manager = getActivity().getWindowManager();
            manager.getDefaultDisplay().getMetrics(metrics);

            mPaintView.init(currentColor, currentStrokeSize);
            mCurrentSizeTv.setText(String.valueOf(currentStrokeSize));

            if (mSelectedColorCircle != null)
                mSelectedColorCircle.setColor(currentColor);

            if (mSizeCircle != null)
                mSizeCircle.setColor(currentColor);

            // Set initial action is Painting
            updateAction(Constants.Action.PAINTING);
        } else {
            //Todo: Show an error message and reload app
        }
    }


    /*
    * startGalleryIntent
    *
    * Internal intent to request opening the gallery app in your android phone
    * */
    private void startGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_PICKING_CODE);
    }


    /*
    * initColorPickerDialog
    * This function will inflate the custom view for the dialog and make it as a color picker
    * The custom will hold the recycler view which contains a list of circles representing for
    * available colors that the user can pick from.
    * */
    private void initColorPickerDialog() {
        View customView = getLayoutInflater().inflate(R.layout.custom_color_picker_dialog_view,
                (ViewGroup) getView(), false);

        // Init custom view (list of colors) for the color picker dialog
        RecyclerView recyclerView = (RecyclerView) customView.findViewById(R.id.color_recycler);
        final RecyclerView.LayoutManager mLayoutManager =
                new GridLayoutManager(getContext(), 4);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);

        colorPickerAdapter = new ColorPickerAdapter(getContext(), colors, this);
        recyclerView.setAdapter(colorPickerAdapter);

        // Init color picker dialog with custom view
        colorPickerDialog = new Dialog(getContext());
        colorPickerDialog.setContentView(customView);
    }

    // Update the selected action so user will be aware of it
    // Also update the action to the paint in DrawView
    private void updateAction(Constants.Action action) {
        mCurrentAction = action;

        if (mCurrentAction == Constants.Action.PAINTING) {
            mPencilActionBtn.setBackground(getResources().getDrawable(R.drawable.bg_icon_button));
            mEraserActionBtn.setBackground(null);
        } else if (mCurrentAction == Constants.Action.ERASING) {
            mEraserActionBtn.setBackground(getResources().getDrawable(R.drawable.bg_icon_button));
            mPencilActionBtn.setBackground(null);
        }

        mPaintView.changeAction(action);
    }


    /*
    * initSizePickerDialog
    * This function will inflate the custom view for the dialog and make it as a size picker
    * This custom view will have a seekbar as a size selector and button to confirm
    * */
    private void initSizePickerDialog() {
        final View customView = getLayoutInflater().inflate(R.layout.custom_size_picker_dialog_view,
                (ViewGroup) getView(), false);

        final SeekBar seekBar = (SeekBar) customView.findViewById(R.id.seekbar_size);
        final TextView selectedSize = (TextView) customView.findViewById(R.id.size_tv);
        Button pickBtn = (Button) customView.findViewById(R.id.pick_btn);

        // Show current size value
        selectedSize.setText(String.valueOf(currentStrokeSize));

        seekBar.setProgress(currentStrokeSize - MIN_STROKE_SIZE);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedSize.setText(String.valueOf(progress + MIN_STROKE_SIZE));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        pickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sizePickerDialog != null) {
                    // Update brush's size
                    currentStrokeSize = seekBar.getProgress() + MIN_STROKE_SIZE;
                    mPaintView.changeBrushSize(currentStrokeSize);
                    mCurrentSizeTv.setText(String.valueOf(currentStrokeSize));

                    sizePickerDialog.dismiss();
                }
            }
        });

        // Init color picker dialog with custom view
        sizePickerDialog = new Dialog(getContext());
        sizePickerDialog.setContentView(customView);
    }

    /*
    * Attach listener to views which we want to listen to
    * */
    private void initClickListeners() {
        mBackStepBtn.setOnClickListener(this);
        mForceStepBtn.setOnClickListener(this);

        mEraserActionBtn.setOnClickListener(this);
        mPencilActionBtn.setOnClickListener(this);
        mMoreActionBtn.setOnClickListener(this);

        mColorPicker.setOnClickListener(this);
        mStrokeSizePicker.setOnClickListener(this);
        mImagePicker.setOnClickListener(this);
    }


    /*
    * Show extra menu by small popup for more actions
    *
    * Actions:
    *   save_action: this menu will attempt saving the current painting to disk
    *   clear_action: this menu will clear the whole painting (even the painting image background)
    * */
    private void showPopUpMenu() {
        if (mMoreMenu == null) {
            mMoreMenu = new PopupMenu(getContext(), mMoreActionBtn);
            mMoreMenu.inflate(R.menu.draw_menu);
            mMoreMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.save_action:
                            attemptSavingImageToDisk();
                            return true;
                        case R.id.clear_action:
                            updateAction(Constants.Action.PAINTING);
                            mPaintView.clear();
                            return true;
                        default:
                            return false;
                    }
                }
            });
        }
        mMoreMenu.show();
    }

    /*
    * attemptSavingImageToDisk
    * -- This method will do mostly checking the permission of the app to the android system
    * If the app have Read-Write external storage - we proceed to next step which is actually saving image service
    * Else, we need to request the permission to the android system
    * */
    private void attemptSavingImageToDisk() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // request the permission with the request code WRITE_STORAGE_PERMISSION_CODE
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_WRITE_STORAGE_PERMISSION_CODE);
        } else {
            // Permission is already granted! Allow user to sent a request if there is none alive same-request
            startSavingFileToDisk();
        }
    }


    /*
    * startSavingFileToDisk
    *
    * The actual process to requesting to start saving file
    * This basically will cache the bitmap of the paintview and create a background service to save
    * that bitmap into file
    *
    * We need to attach the callback listener because we want to know what happened back in the background
    * and decide what to proceed for next step in main thread.
    * */
    private void startSavingFileToDisk() {
        if (!isSavingImage ) {
            isSavingImage = true;

            mPaintView.setDrawingCacheEnabled(true);
            Bitmap mImageBitmap = mPaintView.getDrawingCache();

            new SaveImageToDiskAsync(mImageBitmap, this)
                    .execute();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_WRITE_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If the permission is granted from this code -- which meant user requested to save file
                // Now the permission is granted -- start saving file to disk
                startSavingFileToDisk();
            } else {
                // If the permission is denied -- show a toast to show why we asked
                Toast.makeText(getContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /* Draw the image background if user requested it
        * => Since the background is now an image so we need to update the background of paint view (canvas) to be transparent
        * */
        if (requestCode == IMAGE_PICKING_CODE && resultCode == Activity.RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                final Bitmap backGroundPic = BitmapFactory.decodeStream(imageStream);
                Log.e(TAG, "onActivityResult: " + backGroundPic.getByteCount());

                mPaintView.addBackgroundImage(backGroundPic);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backstep_btn:
                if (mPaintView != null)
                    mPaintView.goBackOneStep();
                break;
            case R.id.forcestep_btn:
                if (mPaintView != null)
                    mPaintView.goForwardOneStep();
                break;
            case R.id.color_picker:
                if (colorPickerDialog != null) {
                    colorPickerDialog.show();
                    Window window = colorPickerDialog.getWindow();

                    // Update the dialog layout to match parent window
                    if (window != null)
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                break;
            case R.id.size_picker:
                if (sizePickerDialog != null) {
                    sizePickerDialog.show();
                    Window window = sizePickerDialog.getWindow();

                    // Update the dialog layout to match parent window
                    if (window != null)
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                break;
            case R.id.image_picker:
                startGalleryIntent();
                break;
            case R.id.pen_action_btn:
                updateAction(Constants.Action.PAINTING);
                break;
            case R.id.eraser_action_btn:
                updateAction(Constants.Action.ERASING);
                break;
            case R.id.more_action_btn:
                showPopUpMenu();
                break;
            default:
                break;
        }
    }


    /*
    * When the new color is selected from the color picker
    * Update the paint view and menu to interact with user
    * */
    @Override
    public void onColorClicked(int position) {
        currentColor = colors[position];

        if (mPaintView != null)
            mPaintView.changeBrushColor(currentColor);

        if (mSelectedColorCircle != null)
            mSelectedColorCircle.setColor(currentColor);

        if (mSizeCircle != null)
            mSizeCircle.setColor(currentColor);

        if (colorPickerDialog != null)
            colorPickerDialog.dismiss();
    }

    @Override
    public void onImageSaved(String savedFile, boolean isSuccess) {
        Toast.makeText(getContext(), isSuccess? R.string.image_saved_message: R.string.error_occurs,
                Toast.LENGTH_SHORT).show();

        // Run a scan service to make sure new saved file will be available to the gallery
        MediaScannerConnection.scanFile(getContext(),
                new String[] { savedFile }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":-> uri=" + uri);
                    }
                });

        // Update the current flag to make sure the last requested saving request is now done
        // Now user can send another same-request
        isSavingImage = false;
    }
}
