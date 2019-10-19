package com.nfc.application;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;


public class FlipAnimator {
    private static String TAG = FlipAnimator.class.getSimpleName();
    private static AnimatorSet mRightOutSet, mLeftInSet;
    private static final int distance  = 16000;

    /**
     * Performs flip animation on two views
     */
    @SuppressLint("ResourceType")
    public static void flipView(final Context context, final View front, final View back, final boolean showFront) {

        mRightOutSet = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.anim.card_flip_anim_out);
        mLeftInSet = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.anim.card_flip_anim_in);

        float scale = context.getResources().getDisplayMetrics().density * distance;
        front.setCameraDistance(scale);
        back.setCameraDistance(scale);

        final AnimatorSet showFrontAnim = new AnimatorSet();
        final AnimatorSet showBackAnim = new AnimatorSet();


        showFrontAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });

        showBackAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        if (showFront) {
            mRightOutSet.setTarget(front);
            mLeftInSet.setTarget(back);
            showFrontAnim.playTogether(mLeftInSet, mRightOutSet);
            showFrontAnim.start();
        } else {
            mRightOutSet.setTarget(back);
            mLeftInSet.setTarget(front);
            showBackAnim.playTogether(mLeftInSet, mRightOutSet);
            showBackAnim.start();
        }
    }



}
