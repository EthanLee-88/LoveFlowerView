package com.example.loveflowerview.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;

import com.example.loveflowerview.FlowerApplication;
import com.example.loveflowerview.R;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;

/**
 * 小心形直播点赞效果
 *
 * Ethan Lee
 */
public class LoveFlowerView extends ConstraintLayout {
    private static Context mApplicationContext = FlowerApplication.getFlowerApplicationContext();
    private ConstraintLayout.LayoutParams mParams;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    private static final int[] loveImages = {R.mipmap.love_blue, R.mipmap.love_red, R.mipmap.love_yellow};
    private Random mRandom = new Random();
    private int mWidth = 1;
    private int mHeight = 1;
    private AnimatorSet togetherAnimator;
    private int bitmapWidth = 0;
    private int bitmapHeight = 0;
    // 是否已往window添加layout
    private boolean flowerLayoutIsAdd = false;

    public LoveFlowerView(@NonNull @NotNull Context context) {
        this(context, null);
    }

    public LoveFlowerView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoveFlowerView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initRes(context, attrs, defStyleAttr);
    }

    private void initRes(Context context, AttributeSet attrs, int defStyleAttr) {
        // 初始化时添加 layout 只是为了测量宽高
        initWindowManager(context);
        mParams = new Constraints.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mParams.bottomToBottom = PARENT_ID;
        mParams.leftToLeft = PARENT_ID;
        mParams.rightToRight = PARENT_ID;
        post(() -> {
            mWidth = getWidth();
            mHeight = getHeight();
            // 宽高测量完后移除，避免点返回键五任何效果
            removeFlowerLayout();
        });
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.love_blue);
        if (bitmap != null) {
            bitmapWidth = bitmap.getWidth();
            bitmapHeight = bitmap.getHeight();
            bitmap.recycle();
        }
    }

    /**
     * 初始化 WindowManager 并将 layout 添加到 Window
     *
     * @param context
     */
    private void initWindowManager(Context context){
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.format = PixelFormat.TRANSPARENT;
        // 设置不可点点击，这里不能主动放弃焦点，否则按返回键回到桌面会导致窗体泄露
//        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        addFlowerLayout();
    }

    /**
     * 这里监听返回键，移除 Window 中的 layout，释放焦点。否则窗体占用焦点，按返回键无效
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            Log.d("tag", "getKeyCode = " + event.getKeyCode());
            removeFlowerLayout();
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 小心形移除完之后也及时移除 layout ，释放焦点，否则按返回键无效
     *
     * @param view
     */
    @Override
    public void onViewRemoved(View view) {
        super.onViewRemoved(view);
        if (getChildCount() == 0){
            removeFlowerLayout();
        }
    }

    /**
     * 往 Window添加 layout 并做标记
     */
    private void addFlowerLayout(){
        if(!flowerLayoutIsAdd) {
            mWindowManager.addView(this, mWindowParams);
            flowerLayoutIsAdd = true;
        }
    }

    /**
     * 移除 layout 释放资源
     */
    public void removeFlowerLayout(){
        if (flowerLayoutIsAdd){
            if (togetherAnimator != null ) {
                    togetherAnimator.cancel();
            }
            mWindowManager.removeView(this);
            removeAllViews();
            flowerLayoutIsAdd = false;
        }
    }

    /**
     * 往 layout 当中添加小心形，并实现动画效果
     */
    public void addFlowerView() {
        addFlowerLayout();
        ImageView loveImage = new ImageView(mApplicationContext);
        loveImage.setImageResource(loveImages[mRandom.nextInt(loveImages.length)]);
        addView(loveImage, mParams);
        togetherAnimator = getAllAnimator(loveImage);
        togetherAnimator.start();
        togetherAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束，移除小心形
                removeView(loveImage);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private AnimatorSet getAllAnimator(View view) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(getAnimatorSet(view), getBezierAnimator(view));
        return animatorSet;
    }

    /**
     * 使用自定义估值器生成贝塞尔曲线
     *
     * @param view
     * @return
     */
    private ValueAnimator getBezierAnimator(View view) {
        // 求控制点
        PointF p1 = new PointF(mRandom.nextInt(mWidth), mRandom.nextInt(mHeight / 2) + mHeight / 2);
        PointF p2 = new PointF(mRandom.nextInt(mWidth), mRandom.nextInt(mHeight / 2));
        // 求起始点和终点
        PointF P0 = new PointF(mWidth / 2 - bitmapWidth / 2,
                mHeight - getStatusBarHeight(mApplicationContext) - bitmapHeight / 2);
        PointF P3 = new PointF(mRandom.nextInt(mWidth), 0);

        ValueAnimator valueAnimator = new ValueAnimator();
        BezierEvaluator bezierEvaluator = new BezierEvaluator(p1, p2);
        valueAnimator.setEvaluator(bezierEvaluator);

        valueAnimator.setObjectValues(P0, P3);
        valueAnimator.setDuration(3000);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener((ValueAnimator animator) -> {
            // 自定义估值器BezierEvaluator的贝塞尔公式算出的 point
            PointF bezierPoint = (PointF) animator.getAnimatedValue();
            view.setX(bezierPoint.x);
            view.setY(bezierPoint.y);
            view.setAlpha((float) (1 - animator.getAnimatedFraction() + 0.2));
        });
        return valueAnimator;
    }

    private AnimatorSet getAnimatorSet(View view) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(getAlphaAnimator(view), getScaleAnimatorX(view),
                getScaleAnimatorY(view));
        return animatorSet;
    }

    private ObjectAnimator getAlphaAnimator(View loveImage) {
        return ObjectAnimator.ofFloat(loveImage, "alpha", (float) 0.1, 1).setDuration(500);
    }

    private ObjectAnimator getScaleAnimatorX(View loveImage) {
        return ObjectAnimator.ofFloat(loveImage, "scaleX", (float) 0.1, 1).setDuration(500);
    }

    private ObjectAnimator getScaleAnimatorY(View loveImage) {
        return ObjectAnimator.ofFloat(loveImage, "scaleY", (float) 0.1, 1).setDuration(500);
    }

    private ObjectAnimator getTranslationObjectX(View loveImage) {
        return ObjectAnimator.ofFloat(loveImage, "translationX", 0, 18).setDuration(1000);
    }

    private ObjectAnimator getTranslationObjectY(View loveImage) {
        return ObjectAnimator.ofFloat(loveImage, "translationY", 0, -888).setDuration(1000);
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public int getStatusBarHeight(Context context) {
        int height = 0;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            height = context.getResources().getDimensionPixelSize(resId);
        }
        Log.d("StatusBarUtil", "StatusBarHeight = " + height);
        return height;
    }

    /**
     * 创建并获取View的Bitmap
     *
     * @param view view
     * @return
     */
    public Bitmap getViewBitmap(View view) {
        view.buildDrawingCache();
        return view.getDrawingCache();
    }
}
