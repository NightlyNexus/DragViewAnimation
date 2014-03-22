package com.brianco.dragviewanimation;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;


public class MainActivity extends Activity{

    private static final float SMALL_RATIO = 0.5f;
    private static final int ANIMATION_TIME = 1000;
    private ViewGroup mRootView;
    private View mBigView;
    private View mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRootView = (ViewGroup) findViewById(R.id.root);
        mBigView = findViewById(R.id.big_image);
        mTextView = findViewById(R.id.wikipedia_text);
        mBigView.setOnTouchListener(bigTouch);
    }

    private View.OnTouchListener bigTouch = new View.OnTouchListener() {
        private int dy = 0;
        public boolean onTouch(View view, MotionEvent event) {
            RelativeLayout.LayoutParams layoutParams
                    = (RelativeLayout.LayoutParams) view.getLayoutParams();
            final int Y = (int) event.getRawY();
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    dy = Y - layoutParams.topMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    if ((Y - dy / 2f) / mRootView.getHeight() > 0.5) {
                        mAnimationBottom.setDuration(ANIMATION_TIME);
                        view.startAnimation(mAnimationBottom);
                    } else {
                        mAnimationTop.setDuration(ANIMATION_TIME);
                        view.startAnimation(mAnimationTop);
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    int topMargin = Y - dy;
                    if (topMargin > mRootView.getHeight() - view.getHeight()) {
                        topMargin = mRootView.getHeight() - view.getHeight();
                    } else if (topMargin < 0) {
                        topMargin = 0;
                    }
                    float ratio = (mRootView.getHeight() - (float) topMargin)
                            /  (float) mRootView.getHeight();
                    float opacityRatio = (mRootView.getHeight() - (float) topMargin - view.getHeight())
                            /  ((float) mRootView.getHeight() - view.getHeight());
                    if (ratio < SMALL_RATIO) {
                        ratio = SMALL_RATIO;
                    }
                    layoutParams.width = (int) (mRootView.getWidth() * ratio);
                    layoutParams.topMargin = topMargin;
                    view.setLayoutParams(layoutParams);
                    mTextView.setAlpha(opacityRatio);
                    break;
            }
            mRootView.invalidate();
            return true;
        }
    };

    private Animation mAnimationTop = new Animation() {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            RelativeLayout.LayoutParams layoutParams
                    = (RelativeLayout.LayoutParams) mBigView.getLayoutParams();
            float ratio = (mRootView.getHeight() - (float) layoutParams.topMargin)
                    /  (float) mRootView.getHeight();
            float opacityRatio = (mRootView.getHeight() - (float) layoutParams.topMargin - mBigView.getHeight())
                    /  ((float) mRootView.getHeight() - mBigView.getHeight());
            layoutParams.width = (int) (mRootView.getWidth() * ratio);
            layoutParams.topMargin
                    = (int) (layoutParams.topMargin - layoutParams.topMargin * interpolatedTime);
            mBigView.setLayoutParams(layoutParams);
            mTextView.setAlpha(opacityRatio);
        }
    };

    private Animation mAnimationBottom = new Animation() {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            RelativeLayout.LayoutParams layoutParams
                    = (RelativeLayout.LayoutParams) mBigView.getLayoutParams();
            float ratio = (mRootView.getHeight() - (float) layoutParams.topMargin)
                    /  (float) mRootView.getHeight();
            if (ratio < SMALL_RATIO) {
                ratio = SMALL_RATIO;
            }
            float opacityRatio = (mRootView.getHeight() - (float) layoutParams.topMargin - mBigView.getHeight())
                    /  ((float) mRootView.getHeight() - mBigView.getHeight());
            layoutParams.width = (int) (mRootView.getWidth() * ratio);
            layoutParams.topMargin = (int) (layoutParams.topMargin
                    + (mRootView.getHeight() - mBigView.getHeight() - layoutParams.topMargin) * interpolatedTime);
            mBigView.setLayoutParams(layoutParams);
            mTextView.setAlpha(opacityRatio);
        }
    };

    //This was meant for going from the top left corner to the bottom right corner,
    //but I decided to go with the above instead.
    /*private View.OnTouchListener starting = new View.OnTouchListener() {
        private int dx = 0;
        private int dy = 0;
        public boolean onTouch(View view, MotionEvent event) {
            RelativeLayout.LayoutParams layoutParams
                    = (RelativeLayout.LayoutParams) view.getLayoutParams();
            //final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();
            final int X = (int) ((float) (mRootView.getWidth() - layoutParams.width)
                    / (float) (mRootView.getHeight() - layoutParams.height) * Y);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    dx = X - layoutParams.leftMargin;
                    dy = Y - layoutParams.topMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    int leftMargin = X - dx;
                    int topMargin = Y - dy;
                    //prevent overshooting bottom right
                    if (leftMargin > mRootView.getWidth() - layoutParams.width) {
                        leftMargin = mRootView.getWidth() - layoutParams.width;
                    }
                    if (topMargin > mRootView.getHeight() - layoutParams.height) {
                        topMargin = mRootView.getHeight() - layoutParams.height;
                    }
                    //prevent overshooting top left
                    if (leftMargin < 0) {
                        leftMargin = 0;
                    }
                    if (topMargin < 0) {
                        topMargin = 0;
                    }
                    layoutParams.leftMargin = leftMargin;
                    layoutParams.topMargin = topMargin;
                    view.setLayoutParams(layoutParams);
                    break;
            }
            mRootView.invalidate();
            return true;
        }
    };*/

}
