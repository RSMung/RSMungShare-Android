package edu.cqu.rsmungshare.myview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import edu.cqu.rsmungshare.R;

/*自定义view,加载动画*/
public class LoadingAnimation extends View {
    private String TAG = "LoadingAnimation";
    private Paint mPaint;
    private int myColor;
    private float mRadius1,mRadius2,mRadius3;
    private float mSize;
    private int height,width;
    private int x,y;//圆心位置
    public LoadingAnimation(Context context) {
        super(context);
    }

    public LoadingAnimation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // 读取xml文件属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingAnimation);
        myColor = typedArray.getColor(R.styleable.LoadingAnimation_main_color,Color.CYAN);
        height=typedArray.getLayoutDimension(R.styleable.LoadingAnimation_android_layout_height,-1);
        width = typedArray.getLayoutDimension(R.styleable.LoadingAnimation_android_layout_width,-1);
        typedArray.recycle();
        //画笔初始化
        mPaint = new Paint();
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(myColor);
        mSize = height /2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mPaint!=null){
            x = getWidth()/2;
            y = getHeight()/2;
            canvas.drawCircle(x-mSize, y, mRadius1, mPaint);
            canvas.drawCircle(x, y, mRadius2, mPaint);
            canvas.drawCircle(x+mSize, y, mRadius3, mPaint);
        }
    }

    public void start() {
        ValueAnimator animator1 = ValueAnimator.ofFloat(mSize/4, mSize/2);
        animator1.setDuration(500);
        animator1.setRepeatCount(ValueAnimator.INFINITE);
        animator1.setRepeatMode(ValueAnimator.REVERSE);
        animator1.setInterpolator(new LinearInterpolator());
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRadius1 = (Float)animation.getAnimatedValue();
                invalidate();
            }
        });
        animator1.start();

        ValueAnimator animator2 = ValueAnimator.ofFloat(mSize/4, mSize/2);
        animator2.setDuration(500);
        animator2.setStartDelay(500);
        animator2.setRepeatCount(ValueAnimator.INFINITE);
        animator2.setRepeatMode(ValueAnimator.REVERSE);
        animator2.setInterpolator(new LinearInterpolator());
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRadius2 = (Float)animation.getAnimatedValue();
                invalidate();
            }
        });
        animator2.start();

        ValueAnimator animator3 = ValueAnimator.ofFloat(mSize/4, mSize/2);
        animator3.setDuration(500);
        animator3.setStartDelay(1000);
        animator3.setRepeatCount(ValueAnimator.INFINITE);
        animator3.setRepeatMode(ValueAnimator.REVERSE);
        animator3.setInterpolator(new LinearInterpolator());
        animator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRadius3 = (Float)animation.getAnimatedValue();
                invalidate();
            }
        });
        animator3.start();
    }
}
