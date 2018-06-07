package com.app.hoocons.ipainting;

import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.app.hoocons.ipainting.ViewFragments.DrawingFragment;

public class MainActivity extends AppCompatActivity {
    private static final String BACK_STACK_ROOT_TAG = "main_root";

    private FragmentManager mFragManager;
    private DrawingFragment mDrawingFragment;

    private boolean startedExiting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mDrawingFragment = DrawingFragment.newInstance();

        showDrawingFragment();
    }

    private void showDrawingFragment() {
        mFragManager = getSupportFragmentManager();
        mFragManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mFragManager.beginTransaction()
                .add(R.id.fragment_container, mDrawingFragment, mDrawingFragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            if (startedExiting) {
                super.onBackPressed();
                return;
            }

            this.startedExiting = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startedExiting = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
        }
    }
}
