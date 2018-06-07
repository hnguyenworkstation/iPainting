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
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
    /* View entities */
    private PaintView mPaintView;

    /* Action Buttons */
    private ImageButton mBackStepBtn;
    private ImageButton mForceStepBtn;

    /* Menu Actions */
    private LinearLayout mCustomToolbar;
    private LinearLayout mColorPicker;
    private LinearLayout mStrokeSizePicker;

    private FilledCircleView mSelectedColorCircle;
    private FilledCircleView mSizeCircle;
    private TextView mCurrentSizeTv;

    /* Color picker dialog */
    private Dialog colorPickerDialog;
    private ColorPickerAdapter colorPickerAdapter;

    private int currentColor;
    private int currentStrokeSize;
    private int[] colors;


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

        bindViews(view);
        initViewEntities();
        initColorPickerDialog();
        initClickListeners();
    }

    /* Bind view variables into related view objects by ids */
    private void bindViews(View parent) {
        mPaintView = (PaintView) parent.findViewById(R.id.paint_view);

        mBackStepBtn = (ImageButton) parent.findViewById(R.id.backstep_btn);
        mForceStepBtn = (ImageButton) parent.findViewById(R.id.forcestep_btn);

        mCustomToolbar = (LinearLayout) parent.findViewById(R.id.custom_toolbar);
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
        View customView = getLayoutInflater().inflate(R.layout.custom_color_picker_dialog_view, (ViewGroup) getView(), false);

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
        colorPickerDialog.setTitle(getString(R.string.title_color_picker_dialog));
    }

    /*
    * Attach listener to views which we want to listen to
    * */
    private void initClickListeners() {
        mBackStepBtn.setOnClickListener(this);
        mForceStepBtn.setOnClickListener(this);

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
                if (colorPickerDialog != null)
                    colorPickerDialog.show();
                break;
            case R.id.size_picker:
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
