package com.app.hoocons.ipainting.ViewFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.app.hoocons.ipainting.Adapters.ColorPickerAdapter;
import com.app.hoocons.ipainting.CustomUI.FilledCircleView;
import com.app.hoocons.ipainting.CustomUI.PaintView;
import com.app.hoocons.ipainting.Helpers.Constants;
import com.app.hoocons.ipainting.R;
import com.app.hoocons.ipainting.ViewHolders.OnColorClickListener;

/**
 * The Drawing fragment.
 */
public class DrawingFragment extends Fragment implements View.OnClickListener, OnColorClickListener {
    private final int MIN_STROKE_SIZE = 5;

    /* View entities */
    private PaintView mPaintView;

    /* Action Buttons */
    private ImageButton mBackStepBtn;
    private ImageButton mForceStepBtn;

    /* Menu Actions */
    private LinearLayout mColorPicker;
    private LinearLayout mStrokeSizePicker;

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
    private Constants.Action mCurrentAction;

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

        mColorPicker = (LinearLayout) parent.findViewById(R.id.color_picker);
        mStrokeSizePicker = (LinearLayout) parent.findViewById(R.id.size_picker);
        mSelectedColorCircle = (FilledCircleView) parent.findViewById(R.id.selected_color);
        mSizeCircle = (FilledCircleView) parent.findViewById(R.id.size_circle);
        mCurrentSizeTv = (TextView) parent.findViewById(R.id.stroke_size);
    }


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

        mColorPicker.setOnClickListener(this);
        mStrokeSizePicker.setOnClickListener(this);
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
            case R.id.pen_action_btn:
                updateAction(Constants.Action.PAINTING);
                break;
            case R.id.eraser_action_btn:
                updateAction(Constants.Action.ERASING);
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
            mPaintView.changeColor(currentColor);

        if (mSelectedColorCircle != null)
            mSelectedColorCircle.setColor(currentColor);

        if (mSizeCircle != null)
            mSizeCircle.setColor(currentColor);

        if (colorPickerDialog != null)
            colorPickerDialog.dismiss();
    }
}
