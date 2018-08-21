package ru.bullyboo.progress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.RequiresApi;

public class HorizontalProgressView extends View {

    public enum LineMode{
        CIRCLE,
        SQUARE
    }

    /**
     * Width of lines
     */
    private float backgroundLineWidth;
    private float progressLineWidth;

    /**
     * Color of lines
     */
    private int backgroundLineColor;
    private int progressLineColor;

    /**
     * Current progress
     */
    private float progress;

    /**
     * Max and min values of progress
     */
    private float min;
    private float max;

    /**
     * Mode od line
     * Values: circle, square
     */
    private LineMode lineMode;

    private boolean animateProgress;
    private int animationDuration;

    private float onePercent;

    private Paint backgroundPaint;
    private Paint progressPaint;

    public HorizontalProgressView(Context context) {
        this(context, null);
    }

    public HorizontalProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.horizontalProgressViewStyle);
    }

    public HorizontalProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, R.style.HorizontalProgressViewStyle);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HorizontalProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HorizontalProgressView,
                defStyleAttr, defStyleRes);

        backgroundLineWidth =
                array.getDimension(R.styleable.HorizontalProgressView_backgroundLineWidth, 0);
        progressLineWidth =
                array.getDimension(R.styleable.HorizontalProgressView_progressLineWidth, 0);

        backgroundLineColor =
                array.getColor(R.styleable.HorizontalProgressView_backgroundLineColor, 0);
        progressLineColor =
                array.getColor(R.styleable.HorizontalProgressView_progressLineColor, 0);

        max = array.getInteger(R.styleable.HorizontalProgressView_max, 0);
        min = array.getInteger(R.styleable.HorizontalProgressView_min, 0);
        int newProgress = array.getInteger(R.styleable.HorizontalProgressView_progress, 0);

        int mode = array.getInt(R.styleable.HorizontalProgressView_lineMode, 0);

        animateProgress =
                array.getBoolean(R.styleable.HorizontalProgressView_animateProgress, false);
        animationDuration =
                array.getInteger(R.styleable.HorizontalProgressView_animationDuration, 0);

        array.recycle();

        switch (mode){
            case 1:
                lineMode = LineMode.CIRCLE;
            case 2:
                lineMode = LineMode.SQUARE;
            default:
                lineMode = LineMode.CIRCLE;
        }

        backgroundPaint = new Paint();
        backgroundPaint.setStrokeWidth(backgroundLineWidth);
        backgroundPaint.setColor(backgroundLineColor);
        backgroundPaint.setStrokeCap(getLineMode(lineMode));
        backgroundPaint.setAntiAlias(true);

        progressPaint = new Paint();
        progressPaint.setStrokeWidth(progressLineWidth);
        progressPaint.setColor(progressLineColor);
        progressPaint.setStrokeCap(getLineMode(lineMode));
        progressPaint.setAntiAlias(true);

        if(max >= min){
            max = 100;
            min = 0;
        }

        onePercent = (max - min) / 100;

        setProgress(newProgress);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        float lineWidth = backgroundLineWidth > progressLineWidth ?
                backgroundLineWidth : progressLineWidth;

        int i = MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.AT_MOST);

        setMeasuredDimension(i,
                (int) (getPaddingTop() + lineWidth + getPaddingBottom()));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        float left = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ?
                getPaddingStart() : getPaddingLeft();

        float top = getPaddingTop();

        float right = width -
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ?
                        getPaddingEnd() : getPaddingRight());

        float bottom = getPaddingBottom();

        float centerY = (height - top - bottom) / 2 + top;

        if(lineMode == LineMode.CIRCLE){
            float lineWidth = backgroundLineWidth > progressLineWidth ?
                    backgroundLineWidth : progressLineWidth;

            left += lineWidth / 2;
            right -= lineWidth / 2;
        }

//        draw background line
        canvas.drawLine(left, centerY, right, centerY, backgroundPaint);

        float progressPercent = progress / onePercent;

        int viewWidth = (int) (right - left);

        float size = viewWidth * progressPercent / 100;

//        draw progress
        canvas.drawLine(left, height / 2,
                left + size, height / 2, progressPaint);

    }

    public float getBackgroundLineWidth() {
        return backgroundLineWidth;
    }

    public void setBackgroundLineWidth(float backgroundLineWidth) {
        this.backgroundLineWidth = backgroundLineWidth;

        backgroundPaint.setStrokeWidth(backgroundLineWidth);

        invalidate();
    }

    public float getProgressLineWidth() {
        return progressLineWidth;
    }

    public void setProgressLineWidth(float progressLineWidth) {
        this.progressLineWidth = progressLineWidth;

        progressPaint.setStrokeWidth(progressLineWidth);

        invalidate();
    }

    public int getBackgroundLineColor() {
        return backgroundLineColor;
    }

    public void setBackgroundLineColor(int backgroundLineColor) {
        this.backgroundLineColor = backgroundLineColor;

        backgroundPaint.setColor(backgroundLineColor);

        invalidate();
    }

    public int getProgressLineColor() {
        return progressLineColor;
    }

    public void setProgressLineColor(int progressLineColor) {
        this.progressLineColor = progressLineColor;

        progressPaint.setColor(progressLineColor);

        invalidate();
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        if(progress >= min &&
                progress <= max){

            if(animateProgress){
                ValueAnimator animator = ValueAnimator.ofFloat(this.progress, progress);
                animator.setDuration(animationDuration);

                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        HorizontalProgressView.this.progress = (Float) valueAnimator.getAnimatedValue();
                        invalidate();
                    }
                });

                animator.start();
            } else {
                this.progress = progress;
                invalidate();
            }
        }

    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        if(max > min){
            this.min = min;

            onePercent = (max - min) / 100;

            invalidate();
        }
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        if(max > min){
            this.max = max;

            onePercent = (max - min) / 100;

            invalidate();
        }
    }

    public LineMode getLineMode() {
        return lineMode;
    }

    public void setLineMode(LineMode lineMode) {
        this.lineMode = lineMode;

        backgroundPaint.setStrokeCap(getLineMode(lineMode));
        progressPaint.setStrokeCap(getLineMode(lineMode));

        invalidate();
    }

    private Paint.Cap getLineMode(LineMode lineMode) {
        switch (lineMode){
            case CIRCLE:
                return Paint.Cap.ROUND;
            case SQUARE:
                return Paint.Cap.SQUARE;
            default:
                return Paint.Cap.ROUND;
        }
    }

}
