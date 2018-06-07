package com.app.hoocons.ipainting.ViewFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.app.hoocons.ipainting.CustomUI.PaintView;
import com.app.hoocons.ipainting.R;

public class DrawingFragment extends Fragment implements View.OnClickListener {
    /* View entities */
    private PaintView mPaintView;


    /* Action Buttons */
    private ImageButton mBackStepBtn;
    private ImageButton mForceStepBtn;

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
        initClickListeners();
    }

    private void bindViews(View parent) {
        mPaintView = (PaintView) parent.findViewById(R.id.paint_view);

        mBackStepBtn = (ImageButton) parent.findViewById(R.id.backstep_btn);
        mForceStepBtn = (ImageButton) parent.findViewById(R.id.forcestep_btn);
    }

    private void initViewEntities() {
        DisplayMetrics metrics = new DisplayMetrics();

        if (getActivity() != null) {
            WindowManager manager = getActivity().getWindowManager();
            manager.getDefaultDisplay().getMetrics(metrics);
            mPaintView.init(metrics);
        } else {
            //Todo: Show an error message and reload app
        }
    }

    private void initClickListeners() {
        mBackStepBtn.setOnClickListener(this);
        mForceStepBtn.setOnClickListener(this);
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
            default:
                break;
        }
    }
}
