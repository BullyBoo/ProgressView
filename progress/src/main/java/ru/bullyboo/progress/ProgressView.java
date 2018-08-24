/*
 * Copyright (C) 2018 BullyBoo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.bullyboo.progress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

public class ProgressView extends View {

    public enum LineMode{
        CIRCLE,
        SQUARE
    }

    public enum Mode{
        HORIZONTAL,
        VERTICAL
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

    private Mode mode;

    private boolean reverse;

    private boolean animateProgress;
    private int animationDuration;

    private float onePercent;

    private Paint backgroundPaint;
    private Paint progressPaint;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.progressViewStyle);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, R.style.ProgressViewStyle);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ProgressView,
                defStyleAttr, defStyleRes);

        backgroundLineWidth =
                array.getDimension(R.styleable.ProgressView_backgroundLineWidth, 0);
        progressLineWidth =
                array.getDimension(R.styleable.ProgressView_progressLineWidth, 0);

        backgroundLineColor =
                array.getColor(R.styleable.ProgressView_backgroundLineColor, 0);
        progressLineColor =
                array.getColor(R.styleable.ProgressView_progressLineColor, 0);

        max = array.getInteger(R.styleable.ProgressView_max, 0);
        min = array.getInteger(R.styleable.ProgressView_min, 0);
        int newProgress = array.getInteger(R.styleable.ProgressView_progress, 0);

        int lineModeFlag = array.getInt(R.styleable.ProgressView_lineMode, 0);

        animateProgress =
                array.getBoolean(R.styleable.ProgressView_animateProgress, false);
        animationDuration =
                array.getInteger(R.styleable.ProgressView_animationDuration, 0);

        int modeFlag = array.getInt(R.styleable.ProgressView_mode, 0);

        reverse = array.getBoolean(R.styleable.ProgressView_reverse, false);

        array.recycle();

        switch (lineModeFlag){
            case 1:
                lineMode = LineMode.CIRCLE;
                break;
            case 2:
                lineMode = LineMode.SQUARE;
                break;
            default:
                lineMode = LineMode.CIRCLE;
                break;
        }

        switch (modeFlag){
            case 1:
                mode = Mode.HORIZONTAL;
                break;
            case 2:
                mode = Mode.VERTICAL;
                break;
            default:
                mode = Mode.HORIZONTAL;
                break;
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

        if(mode == Mode.VERTICAL){
            float left, right;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                left = getPaddingStart();
                right = getPaddingEnd();

            } else {
                left = getPaddingLeft();
                right = getPaddingRight();
            }

            int i = MeasureSpec.makeMeasureSpec(heightMeasureSpec, MeasureSpec.AT_MOST);

            setMeasuredDimension((int) (left + lineWidth + right), i);

        } else {
            int i = MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.AT_MOST);

            setMeasuredDimension(i,
                    (int) (getPaddingTop() + lineWidth + getPaddingBottom()));
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        float left = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ?
                getPaddingStart() : getPaddingLeft();

        float top = getPaddingTop();

        float right = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ?
                        getPaddingEnd() : getPaddingRight();

        float bottom = getPaddingBottom();

        if(mode == Mode.VERTICAL){
            float centerX = (width - left - right) / 2 + left;

            if(lineMode == LineMode.CIRCLE){
                float lineWidth = backgroundLineWidth > progressLineWidth ?
                        backgroundLineWidth : progressLineWidth;

                top += lineWidth / 2;
                bottom -= lineWidth / 2;
            }

//        draw background line
            canvas.drawLine(centerX, top, centerX, height - Math.abs(bottom), backgroundPaint);

            float progressPercent = progress / onePercent;

            int viewHeight = (int) (height - bottom - top);

            float size = viewHeight * progressPercent / 100;

//        draw progress
            if(reverse){
                canvas.drawLine(centerX, height - Math.abs(bottom),
                        centerX, height - Math.abs(size), progressPaint);
            } else {
                canvas.drawLine(centerX, top,
                        centerX, top + size, progressPaint);
            }

        } else {
            float centerY = (height - top - bottom) / 2 + top;

            if(lineMode == LineMode.CIRCLE){
                float lineWidth = backgroundLineWidth > progressLineWidth ?
                        backgroundLineWidth : progressLineWidth;

                left += lineWidth / 2;
                right -= lineWidth / 2;
            }

//        draw background line
            canvas.drawLine(left, centerY, width - Math.abs(right), centerY, backgroundPaint);

            float progressPercent = progress / onePercent;

            int viewWidth = (int) (width - right - left);

            float size = viewWidth * progressPercent / 100;

//        draw progress
            if(reverse){
                canvas.drawLine(width - Math.abs(right), height / 2,
                        width - Math.abs(size), height / 2, progressPaint);

            } else {
                canvas.drawLine(left, height / 2,
                        left + size, height / 2, progressPaint);
            }
        }
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
                        ProgressView.this.progress = (Float) valueAnimator.getAnimatedValue();
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
