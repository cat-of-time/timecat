package com.time.cat.ui.widgets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.time.cat.R;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/24
 * @discription null
 * @usage null
 */
public class RoundProgressBar extends View {

    //<editor-fold desc="Field">

    private static final float OUTER_LAYER_LARGE_SCALE = 58 / 62.F;
    private static final float MIDDLE_LAYER_LARGE_SCALE = 51 / 62.F;
    private static final float SHADOW_LAYER_LARGE_SCALE = 40 / 62.F;
    private static final float SMALL_CIRCLE_DEGREE_OFFSET = 0.7F;
    private static final int DEF_VIEW_SIZE = 300;

    private int outLayerSlideColor;
    private int outLayerStrokeColor;
    private int outLayerStrokeWidth;
    private int middleLayerProgressColor;
    private int middleLayerProgressWidth;
    private int middleLayerBgColor;
    private int smallCircleSlideColor;
    private int smallCircleStrokeColor;
    private int smallCircleSize;
    private int smallCircleStrokeWidth;
    private int shadowLayerColor;
    private int shadowLayerInnerColor;
    private int innerTextSize;
    private int innerTextColor;

    private Point circleCenterPoint;

    private int viewSize;
    private Paint outLayerStrokePaint;
    private Paint outLayerSlidePaint;
    private Paint progressBgPaint;
    private Paint progressPaint;
    private Paint smallCirclePaint;
    private Paint smallCircleInnerPaint;
    private Paint mShadowLayerInnerPaint;
    private Paint mTextDescPaint;

    private float mOuterLayerLargeSize;
    private float mOuterLayerSlideSize;
    private float mMiddleLayerSize;
    private float mShadowLayerSize;
    private String progressDesc;
    private String innerTextStart;
    private String innerTextEnd;
    private String innerTextTimeStyle;
    private float degreePercent;
    private int countdownTime;
    private int[] doughnutColors;

    private boolean isCounting;

    //</editor-fold desc="Field">

    //<editor-fold desc="draw view">

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundProgressBar, defStyleAttr, R.style.def_progress_style);
        int indexCount = typedArray.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.RoundProgressBar_outer_layer_slide_color:
                    outLayerSlideColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.RoundProgressBar_outer_layer_stroke_color:
                    outLayerStrokeColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.RoundProgressBar_outer_layer_stroke_width:
                    outLayerStrokeWidth = typedArray.getDimensionPixelOffset(attr, 0);
                    break;
                case R.styleable.RoundProgressBar_middle_layer_progress_color:
                    middleLayerProgressColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.RoundProgressBar_middle_layer_progress_width:
                    middleLayerProgressWidth = typedArray.getDimensionPixelOffset(attr, 0);
                    break;
                case R.styleable.RoundProgressBar_middle_layer_bg_color:
                    middleLayerBgColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.RoundProgressBar_middle_layer_small_circle_slide_color:
                    smallCircleSlideColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.RoundProgressBar_middle_layer_small_circle_stroke_color:
                    smallCircleStrokeColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.RoundProgressBar_middle_layer_small_circle_size:
                    smallCircleSize = typedArray.getDimensionPixelSize(attr, 0);
                    break;
                case R.styleable.RoundProgressBar_middle_layer_small_circle_stroke_width:
                    smallCircleStrokeWidth = typedArray.getDimensionPixelOffset(attr, 0);
                    break;
                case R.styleable.RoundProgressBar_shadow_layer_color:
                    shadowLayerColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.RoundProgressBar_shadow_layer_inner_color:
                    shadowLayerInnerColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.RoundProgressBar_inner_text_size:
                    innerTextSize = typedArray.getDimensionPixelSize(attr, 0);
                    break;
                case R.styleable.RoundProgressBar_inner_text_color:
                    innerTextColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.RoundProgressBar_inner_text_start:
                    innerTextStart = typedArray.getString(attr);
                    if (innerTextStart == null) innerTextStart = "开始";
                    progressDesc = innerTextStart;
                    break;
                case R.styleable.RoundProgressBar_inner_text_end:
                    innerTextEnd = typedArray.getString(attr);
                    if (innerTextEnd == null) innerTextEnd = "时间到";
                    break;
                case R.styleable.RoundProgressBar_inner_text_time_style:
                    innerTextTimeStyle = typedArray.getString(attr);
                    if (innerTextTimeStyle == null) innerTextTimeStyle = "mm:ss";
                    break;
            }
        }
        typedArray.recycle();
        initData();
    }

    private void initData() {
        outLayerStrokePaint = createPaint(outLayerStrokeColor, 0, Paint.Style.FILL, 0);
        outLayerSlidePaint = createPaint(outLayerSlideColor, 0, Paint.Style.FILL, 0);
        progressBgPaint = createPaint(middleLayerBgColor, 0, Paint.Style.STROKE, middleLayerProgressWidth);
        progressPaint = createPaint(middleLayerProgressColor, 0, Paint.Style.STROKE, middleLayerProgressWidth);
        smallCirclePaint = createPaint(smallCircleStrokeColor, 0, Paint.Style.FILL, 0);
        smallCircleInnerPaint = createPaint(smallCircleSlideColor, 0, Paint.Style.FILL, 0);
        mShadowLayerInnerPaint = createPaint(shadowLayerInnerColor, 0, Paint.Style.FILL, 0);
        mTextDescPaint = createPaint(innerTextColor, innerTextSize, Paint.Style.FILL, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize;
        int heightSize;

        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
            widthSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEF_VIEW_SIZE, getResources().getDisplayMetrics());
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        }

        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEF_VIEW_SIZE, getResources().getDisplayMetrics());
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewSize = w - h >= 0 ? h : w;
        circleCenterPoint = new Point(viewSize / 2, viewSize / 2);
        mOuterLayerLargeSize = viewSize * OUTER_LAYER_LARGE_SCALE;
        mOuterLayerSlideSize = mOuterLayerLargeSize - 2 * outLayerStrokeWidth;

        mMiddleLayerSize = viewSize * MIDDLE_LAYER_LARGE_SCALE;
        mShadowLayerSize = viewSize * SHADOW_LAYER_LARGE_SCALE;

        doughnutColors = new int[]{middleLayerProgressColor,
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.textHighLightColor),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_purple)};

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawOuterLayerCircles(canvas);
        drawMiddleProgressLayer(canvas);
        drawShadowCircle(canvas);
        drawProgressText(progressDesc, canvas);
    }

    /**
     * 绘制倒计时的描述显示
     *
     * @param progressDesc 描述
     * @param canvas       画布
     */
    private void drawProgressText(String progressDesc, Canvas canvas) {
        Point textPointInView = getTextPointInView(progressDesc);
        if (null == textPointInView) return;
        canvas.drawText(progressDesc, textPointInView.x, textPointInView.y, mTextDescPaint);
    }

    private Point getTextPointInView(String textDesc) {
        if (null == textDesc) return null;
        Point point = new Point();
        int textW = (viewSize - (int) mTextDescPaint.measureText(textDesc)) / 2;
        Paint.FontMetrics fm = mTextDescPaint.getFontMetrics();
        int textH = (int) Math.ceil(fm.descent - fm.top);
        point.set(textW, viewSize / 2 + textH / 2 - 50);
        return point;
    }

    /**
     * 绘制阴影圆圈
     *
     * @param canvas 画布
     */
    private void drawShadowCircle(Canvas canvas) {
        mShadowLayerInnerPaint.setShadowLayer(10, 2, 2, shadowLayerColor);
        //设置阴影图层
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mShadowLayerInnerPaint.setColor(shadowLayerInnerColor);
        canvas.drawCircle(circleCenterPoint.x, circleCenterPoint.y, mShadowLayerSize / 2, mShadowLayerInnerPaint);
    }

    /**
     * 绘制中间进度的背景和进度
     *
     * @param canvas 画布
     */
    private void drawMiddleProgressLayer(Canvas canvas) {

        float ddx = (viewSize - mMiddleLayerSize) / 2;
        //外切圆的坐标计算,绘制的扇形在矩形内的外切圆,注意画笔的宽度
        RectF oval = new RectF(ddx + middleLayerProgressWidth, ddx + middleLayerProgressWidth, viewSize - ddx - middleLayerProgressWidth, viewSize - ddx - middleLayerProgressWidth);

        canvas.drawCircle(circleCenterPoint.x, circleCenterPoint.y, mMiddleLayerSize / 2 - middleLayerProgressWidth, progressBgPaint);
        // 注意扫过的扇形的范围(进度)要和绘制的小圆点保持一致,所以我们需要从-90度开始
        progressPaint.setShader(new SweepGradient(0, 0, doughnutColors, null));
        canvas.drawArc(oval, -90, 360 * degreePercent, false, progressPaint);

        // 由于前面绘制了一个小圆,所以我们弧度的角度不能用于计算圆的坐标,我们需要大概的加上那么一两度来计算,
        // 由于android坐标系的问题以及角度在不同象限内的问题,所以我们需要计算几种情况
        // 0-90,90-180 ,180-270,270-360
        float animDegree = 360 * degreePercent + SMALL_CIRCLE_DEGREE_OFFSET;
        float xiaoyuanDegree;
        float xiaoYuanX = 0, xiaoYuanY = 0;
        int tempD = (int) animDegree;
        if (tempD >= 0 && tempD < 90) {
            // 第一象限内,sin和cons正常
            xiaoyuanDegree = animDegree;
            float hudu = (float) Math.abs(Math.PI * xiaoyuanDegree / 180);
            float sinAX = (float) Math.abs(Math.sin(hudu) * (mMiddleLayerSize / 2 + middleLayerProgressWidth / 4));
            float cosAY = (float) Math.abs(Math.cos(hudu) * (mMiddleLayerSize / 2 + middleLayerProgressWidth / 4));

            xiaoYuanX = (viewSize - 2 * middleLayerProgressWidth - 2 * sinAX) / 2 + 2 * sinAX;
            xiaoYuanY = viewSize / 2 + middleLayerProgressWidth - cosAY;
        } else if (tempD >= 90 && tempD < 180) {
            // 第二象限内,sin和cos互换
            xiaoyuanDegree = animDegree - 90;
            float hudu = (float) Math.abs(Math.PI * xiaoyuanDegree / 180);
            float sinAX = (float) Math.abs(Math.cos(hudu) * (mMiddleLayerSize / 2 + middleLayerProgressWidth / 4));
            float cosAY = (float) Math.abs(Math.sin(hudu) * (mMiddleLayerSize / 2 + middleLayerProgressWidth / 4));

            xiaoYuanX = (viewSize - 2 * middleLayerProgressWidth - 2 * sinAX) / 2 + 2 * sinAX;
            xiaoYuanY = viewSize / 2 + cosAY - middleLayerProgressWidth;

        } else if (tempD >= 180 && tempD < 270) {
            // 第三象限,sin和cos正常,但是x和y的坐标计算方法发生改变
            xiaoyuanDegree = animDegree - 180;
            float hudu = (float) Math.abs(Math.PI * xiaoyuanDegree / 180);
            float sinAX = (float) Math.abs(Math.sin(hudu) * (mMiddleLayerSize / 2 - middleLayerProgressWidth));
            float cosAY = (float) Math.abs(Math.cos(hudu) * (mMiddleLayerSize / 2));

            xiaoYuanX = viewSize / 2 - sinAX;
            xiaoYuanY = viewSize / 2 + cosAY - middleLayerProgressWidth;

        } else if (tempD >= 270 && tempD < 360) {
            // 第四象限内,sin和cos互换,但是x和y的坐标也发生了改变
            xiaoyuanDegree = animDegree - 270;
            float hudu = (float) Math.abs(Math.PI * xiaoyuanDegree / 180);
            float sinAX = (float) Math.abs(Math.cos(hudu) * (mMiddleLayerSize / 2 + middleLayerProgressWidth / 4));
            float cosAY = (float) Math.abs(Math.sin(hudu) * (mMiddleLayerSize / 2 - middleLayerProgressWidth));

            xiaoYuanX = viewSize / 2 - sinAX + middleLayerProgressWidth;
            xiaoYuanY = viewSize / 2 - cosAY;
        }

        canvas.drawCircle(xiaoYuanX, xiaoYuanY, smallCircleSize / 2, smallCirclePaint);
        canvas.drawCircle(xiaoYuanX, xiaoYuanY, (smallCircleSize - smallCircleStrokeWidth) / 2, smallCircleInnerPaint);
    }

    /**
     * 绘制外层的大圆圈和描边
     *
     * @param canvas 画布
     */
    private void drawOuterLayerCircles(Canvas canvas) {
        // 采用阴影绘制
        outLayerSlidePaint.setShadowLayer(10, 2, 2, shadowLayerColor);
        //设置阴影图层
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        canvas.drawCircle(circleCenterPoint.x, circleCenterPoint.y, mOuterLayerSlideSize / 2, outLayerSlidePaint);
    }

    /**
     * 初始化画笔
     *
     * @param paintColor 画笔颜色
     * @param textSize   文字大小
     * @param style      画笔风格
     * @param lineWidth  画笔宽度
     * @return 画笔
     */
    private Paint createPaint(int paintColor, int textSize, Paint.Style style, int lineWidth) {
        Paint paint = new Paint();
        paint.setColor(paintColor);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(lineWidth);
        paint.setDither(true);
        paint.setTextSize(textSize);
        paint.setStyle(style);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        return paint;
    }

    //</editor-fold desc="draw view">

    //<editor-fold desc="业务">
    ValueAnimator valA;

    boolean forceStop = false;

    /**
     * 开始倒计时任务
     */
    public void startCountdown() {
        if (isCounting) {
            if (null != onCountListener) {
                onCountListener.onCountingClick();
            }
            return;
        }
        isCounting = true;
        if (valA == null) {
            valA = getValA(countdownTime * 1000);
            valA.addUpdateListener(animation -> {
                degreePercent = Float.valueOf(valA.getAnimatedValue().toString());
                invalidate();
            });
        }
        valA.start();
        startCountDownTaskByRxAndroid();
    }

    private ValueAnimator getValA(long countdownTime) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.F);
        valueAnimator.setDuration(countdownTime);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(0);
        return valueAnimator;
    }

    private void startCountDownTaskByRxAndroid() {
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        progressDesc = innerTextEnd;
                        isCounting = false;
                        setForceStop(false);
                        countdownTime = -1;
                        if (null != valA) valA.cancel();
                        valA = null;
                        if (null != onCountListener) onCountListener.countDownFinished();
                        smallCirclePaint.setColor(getResources().getColor(android.R.color.transparent));
                        smallCircleInnerPaint.setColor(getResources().getColor(android.R.color.transparent));
                        postDelayed(()->{
                            smallCirclePaint.setColor(getResources().getColor(smallCircleStrokeColor));
                            smallCircleInnerPaint.setColor(getResources().getColor(smallCircleSlideColor));
                            this.unsubscribe();
                        }, 100);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (countdownTime < -1) {
                            this.unsubscribe();
                        }
                        --countdownTime;
                        if (countdownTime < 0) {
                            onCompleted();
                            return;
                        } else {
                            mTextDescPaint.setTextSize(innerTextSize);
                            int hours = (countdownTime / (60 * 60)) & 24;
                            int minutes = (countdownTime / 60) % 60;
                            int seconds = (countdownTime % 60);
                            String minutesStr = (minutes <= 9 && minutes > 0) ? "0" + minutes : (minutes == 0 ? "00" : minutes + "");
                            String hoursStr = (hours <= 9 && hours > 0) ? "0" + hours : (hours == 0 ? "00" : hours + "");
                            String secondsStr = (seconds <= 9 && seconds > 0) ? "0" + seconds : (seconds == 0 ? "00" : seconds + "");
                            if (hours != 0 || "HH:mm:ss".equals(innerTextTimeStyle)) {
                                progressDesc = hoursStr + ":" + minutesStr + ":" + secondsStr;
                            } else if (minutes != 0 || "mm:ss".equals(innerTextTimeStyle)) {
                                progressDesc = minutesStr + ":" + secondsStr;
                            } else if (seconds != 0 || "ss".equals(innerTextTimeStyle)) {
                                progressDesc = secondsStr;
                            }
                            if (null != onCountListener) {
                                onCountListener.counting(countdownTime);
                                if (isForceStop()) {
                                    forceStop();
                                } else {
                                    isCounting = true;
                                }
                            }
                        }
                        invalidate();
                    }

                    private void forceStop() {
                        isCounting = false;
                        setForceStop(false);
                        countdownTime = -1;
                        if (null != valA) valA.cancel();
                        valA = null;
                        mTextDescPaint.setTextSize(innerTextSize);
                        progressDesc = innerTextStart;
                        postDelayed(this::unsubscribe, 100);
                    }
                });
    }
    //</editor-fold desc="业务">

    //<editor-fold desc="getter and setter">
    /**
     * 设置计时秒数
     * @param countdownTime 秒
     */
    public void setCountdownTime(int countdownTime) {
        this.countdownTime = countdownTime;
    }

    public boolean isForceStop() {
        return forceStop;
    }

    public void setForceStop(boolean forceStop) {
        this.forceStop = forceStop;
    }

    public int getOutLayerSlideColor() {
        return outLayerSlideColor;
    }

    public void setOutLayerSlideColor(int outLayerSlideColor) {
        this.outLayerSlideColor = outLayerSlideColor;
    }

    public int getOutLayerStrokeColor() {
        return outLayerStrokeColor;
    }

    public void setOutLayerStrokeColor(int outLayerStrokeColor) {
        this.outLayerStrokeColor = outLayerStrokeColor;
    }

    public int getOutLayerStrokeWidth() {
        return outLayerStrokeWidth;
    }

    public void setOutLayerStrokeWidth(int outLayerStrokeWidth) {
        this.outLayerStrokeWidth = outLayerStrokeWidth;
    }

    public int getMiddleLayerProgressColor() {
        return middleLayerProgressColor;
    }

    public void setMiddleLayerProgressColor(int middleLayerProgressColor) {
        this.middleLayerProgressColor = middleLayerProgressColor;
    }

    public int getMiddleLayerProgressWidth() {
        return middleLayerProgressWidth;
    }

    public void setMiddleLayerProgressWidth(int middleLayerProgressWidth) {
        this.middleLayerProgressWidth = middleLayerProgressWidth;
    }

    public int getMiddleLayerBgColor() {
        return middleLayerBgColor;
    }

    public void setMiddleLayerBgColor(int middleLayerBgColor) {
        this.middleLayerBgColor = middleLayerBgColor;
    }

    public int getSmallCircleSlideColor() {
        return smallCircleSlideColor;
    }

    public void setSmallCircleSlideColor(int smallCircleSlideColor) {
        this.smallCircleSlideColor = smallCircleSlideColor;
    }

    public int getSmallCircleStrokeColor() {
        return smallCircleStrokeColor;
    }

    public void setSmallCircleStrokeColor(int smallCircleStrokeColor) {
        this.smallCircleStrokeColor = smallCircleStrokeColor;
    }

    public int getSmallCircleSize() {
        return smallCircleSize;
    }

    public void setSmallCircleSize(int smallCircleSize) {
        this.smallCircleSize = smallCircleSize;
    }

    public int getSmallCircleStrokeWidth() {
        return smallCircleStrokeWidth;
    }

    public void setSmallCircleStrokeWidth(int smallCircleStrokeWidth) {
        this.smallCircleStrokeWidth = smallCircleStrokeWidth;
    }

    public int getShadowLayerColor() {
        return shadowLayerColor;
    }

    public void setShadowLayerColor(int shadowLayerColor) {
        this.shadowLayerColor = shadowLayerColor;
    }

    public int getShadowLayerInnerColor() {
        return shadowLayerInnerColor;
    }

    public void setShadowLayerInnerColor(int shadowLayerInnerColor) {
        this.shadowLayerInnerColor = shadowLayerInnerColor;
    }

    public int getInnerTextSize() {
        return innerTextSize;
    }

    public void setInnerTextSize(int innerTextSize) {
        this.innerTextSize = innerTextSize;
    }

    public int getInnerTextColor() {
        return innerTextColor;
    }

    public void setInnerTextColor(int innerTextColor) {
        this.innerTextColor = innerTextColor;
    }

    public float getmOuterLayerLargeSize() {
        return mOuterLayerLargeSize;
    }

    public void setmOuterLayerLargeSize(float mOuterLayerLargeSize) {
        this.mOuterLayerLargeSize = mOuterLayerLargeSize;
    }

    public float getmOuterLayerSlideSize() {
        return mOuterLayerSlideSize;
    }

    public void setmOuterLayerSlideSize(float mOuterLayerSlideSize) {
        this.mOuterLayerSlideSize = mOuterLayerSlideSize;
    }

    public float getmMiddleLayerSize() {
        return mMiddleLayerSize;
    }

    public void setmMiddleLayerSize(float mMiddleLayerSize) {
        this.mMiddleLayerSize = mMiddleLayerSize;
    }

    public float getmShadowLayerSize() {
        return mShadowLayerSize;
    }

    public void setmShadowLayerSize(float mShadowLayerSize) {
        this.mShadowLayerSize = mShadowLayerSize;
    }

    public String getInnerTextStart() {
        return innerTextStart;
    }

    public void setInnerTextStart(String innerTextStart) {
        this.innerTextStart = innerTextStart;
    }

    public String getInnerTextEnd() {
        return innerTextEnd;
    }

    public void setInnerTextEnd(String innerTextEnd) {
        this.innerTextEnd = innerTextEnd;
    }

    public String getInnerTextTimeStyle() {
        return innerTextTimeStyle;
    }

    public void setInnerTextTimeStyle(String innerTextTimeStyle) {
        this.innerTextTimeStyle = innerTextTimeStyle;
    }

    public boolean isCounting() {
        return isCounting;
    }

    public void setCounting(boolean counting) {
        isCounting = counting;
    }
    //</editor-fold desc="getter and setter">

    //<editor-fold desc="接口">
    OnCountListener onCountListener;

    public void setOnCountListener(OnCountListener onCountListener) {
        this.onCountListener = onCountListener;
    }

    public interface OnCountListener {
        void countDownFinished();
        void counting(int remainTime);
        void onCountingClick();
    }
    //</editor-fold desc="接口">

}