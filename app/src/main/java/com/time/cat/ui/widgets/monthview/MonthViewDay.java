package com.yumingchuan.rsqmonthcalendar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.yumingchuan.rsqmonthcalendar.R;
import com.yumingchuan.rsqmonthcalendar.app.MyApplication;
import com.yumingchuan.rsqmonthcalendar.bean.DayInfo;
import com.yumingchuan.rsqmonthcalendar.bean.DayType;
import com.yumingchuan.rsqmonthcalendar.bean.ScheduleToDo;
import com.yumingchuan.rsqmonthcalendar.utils.TimestampTool;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;


/**
 * Created by yumingchuan on 2017/6/30.
 */

public class MonthViewDay extends View {

    private Paint mPaint;
    private DisplayMetrics mDisplayMetrics;
    private int height;
    private int width;
    private int dayHeight;
    private int taskHeight;
    private DayInfo dayInfo;
    private List<ScheduleToDo> todos;
    private boolean isCurrentSelectedDate;


    public MonthViewDay(Context context) {
        super(context);
    }

    public MonthViewDay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initAttrs(attrs);

        initPaint();
    }

    private void initAttrs(AttributeSet attrs) {

    }


    public MonthViewDay(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void initPaint() {
        mDisplayMetrics = getResources().getDisplayMetrics();
        mPaint = new Paint();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        height = getHeight();
        width = getWidth();

        //画背景
        drawBackground();

        //画公里日期
        drawTitle(canvas);

        //画假休
        drawHoliday(canvas);

        //画农历
        drawLunarText(canvas);

        //画任务
        drawTask(canvas);

    }

    private void drawBackground() {
        if (dayInfo != null && dayInfo.isToday) {
            setBackgroundResource(R.drawable.shape_month_view_today);
        } else if (isSelectedDate()) {
            setBackgroundResource(R.drawable.shape_rect_border_da_bg_f5);
        } else {
            setBackgroundResource(R.drawable.shape_month_view_white);
        }
    }

    private boolean isSelectedDate() {
        return MyApplication.getInstance().getCurrentSelectDate().contains(dayInfo.date);
    }

    /**
     * @param canvas
     */
    private void drawTask(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        todos = dayInfo.todos;

        if (dayInfo != null && dayInfo.dayType == DayType.DAY_TYPE_NOW && todos != null && todos.size() > 0) {
            mPaint.setAntiAlias(true);// 消除锯齿
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG); // 消除锯齿
            mPaint.setTextSize(10 * mDisplayMetrics.scaledDensity);
            taskHeight = getStringHeight(mPaint, "");

            for (int i = 0; i < todos.size() && i < 3; i++) {
                drawTaskRect(canvas, height - (i + 1) * taskHeight, i);
            }

            for (int i = 0; i < todos.size() && i < 3; i++) {
                if (i == 0) {
                    setMonthTaskTitle(canvas, i, 4);
                } else if (i == 1) {
                    setMonthTaskTitle(canvas, i, 5);
                } else if (i == 2) {
                    setMonthTaskTitle(canvas, i, 6);
                }
            }

        }
    }

    private void setMonthTaskTitle(Canvas canvas, int i, int length) {
        ScheduleToDo scheduleToDo = (ScheduleToDo) todos.get(i);
        mPaint.setFlags(scheduleToDo.pIsDone ? Paint.STRIKE_THRU_TEXT_FLAG : 0);
        mPaint.setColor(getTaskColor(scheduleToDo));
        String title = scheduleToDo.pTitle.length() > 4 ? scheduleToDo.pTitle.substring(0, 4) + "…" : scheduleToDo.pTitle;
        canvas.drawText(title, getPixelWith(5), height - taskHeight * i - getPixelWith(length), mPaint);
    }

    private int getTaskColor(ScheduleToDo scheduleToDo) {

        if (scheduleToDo.pIsDone) {
            String pContainer = scheduleToDo.pContainer;
            return pContainer.equals("IE") ? getResources().getColor(R.color.IE_month_task_done) :
                    pContainer.equals("IU") ? getResources().getColor(R.color.IU_month_task_done) :
                            pContainer.equals("UE") ? getResources().getColor(R.color.UE_month_task_done) : getResources().getColor(R.color.UU_month_task_done);
        } else {
            String pContainer = scheduleToDo.pContainer;
            return pContainer.equals("IE") ? getResources().getColor(R.color.IE_month_task) :
                    pContainer.equals("IU") ? getResources().getColor(R.color.IU_month_task) :
                            pContainer.equals("UE") ? getResources().getColor(R.color.UE_month_task) : getResources().getColor(R.color.UU_month_task);
        }
    }


    private void drawTitle(Canvas canvas) {

        if (dayInfo == null || dayInfo.dayType != DayType.DAY_TYPE_NOW) {
            return;
        }

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(TimestampTool.sdf_yMdp.parse(dayInfo.date));
            mPaint.setColor(getResources().getColor(TimestampTool.isWeekend(calendar) ? R.color.color_month_weekend : R.color.color_month_view));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mPaint.setAntiAlias(true);// 消除锯齿
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG); // 消除锯齿
        mPaint.setTextSize(12 * mDisplayMetrics.scaledDensity);

        dayHeight = getStringHeight(mPaint, "");

        canvas.drawText(dayInfo.day + "", getPixelWith(5), getPixelWith(11), mPaint);

    }


    private void drawHoliday(Canvas canvas) {
        if (dayInfo != null && dayInfo.dayType == DayType.DAY_TYPE_NOW && dayInfo.holidays == 1) {
            mPaint.setColor(getResources().getColor(R.color.month_view_rest));
            canvas.drawRect(width - getPixelWith(15), 0 + getPixelWith(1), width - getPixelWith(1), getPixelWith(15), mPaint);
            setRestOrWork(canvas, "休");
        } else if (dayInfo != null && dayInfo.dayType == DayType.DAY_TYPE_NOW && dayInfo.holidays == 2) {
            mPaint.setColor(getResources().getColor(R.color.month_view_work));
            canvas.drawRect(width - getPixelWith(15), 0 + getPixelWith(1), width - getPixelWith(1), getPixelWith(15), mPaint);
            setRestOrWork(canvas, "班");
        } else {

        }
    }

    private void setRestOrWork(Canvas canvas, String str) {
        mPaint.setAntiAlias(true);// 消除锯齿
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG); // 消除锯齿
        mPaint.setColor(getResources().getColor(R.color.white));
        mPaint.setTextSize(10 * mDisplayMetrics.scaledDensity);

        canvas.drawText(str, width - getPixelWith(13), getPixelWith(11), mPaint);
    }


    private float getPixelWith(int width) {
        return width * mDisplayMetrics.scaledDensity;
    }

    private void drawLunarText(Canvas canvas) {

        mPaint.setAntiAlias(true);// 消除锯齿
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG); // 消除锯齿

        mPaint.setColor(getResources().getColor(R.color.color_month_view));
        mPaint.setTextSize(10 * mDisplayMetrics.scaledDensity);

        if (dayInfo != null && dayInfo.dayType == DayType.DAY_TYPE_NOW) {
            canvas.drawText(dayInfo.lunarStr + "", getPixelWith(4), dayHeight + getPixelWith(11), mPaint);
        } else {
            canvas.drawText("", getPixelWith(4), dayHeight + getPixelWith(11), mPaint);
        }
    }


    private void drawTaskRect(Canvas canvas, float position, int i) {

        ScheduleToDo scheduleToDo = (ScheduleToDo) todos.get(i);

        int left = (int) (getPixelWith(2));
        int top = (int) position;
        int right = (int) (width - getPixelWith(2));

        int bottom = (int) (top + getPixelWith(13));

        mPaint.setStyle(Paint.Style.STROKE);//设置填满

        mPaint.setColor(getTaskBgStrokeColor(scheduleToDo.pContainer));

        canvas.drawRect(left, (int) (top - getPixelWith(1) * (i + 1)), right, (int) (bottom - getPixelWith(1) * (i + 1)), mPaint);

        mPaint.setStyle(Paint.Style.FILL);//设置填满

        mPaint.setColor(getTaskBgColor(scheduleToDo.pContainer));

        canvas.drawRect(left, (int) (top - getPixelWith(1) * (i + 1)), right, (int) (bottom - getPixelWith(1) * (i + 1)), mPaint);
    }


    private int getTaskBgColor(String pContainer) {
        return pContainer.equals("IE") ? getResources().getColor(R.color.IE_solid) :
                pContainer.equals("IU") ? getResources().getColor(R.color.IU_solid) :
                        pContainer.equals("UE") ? getResources().getColor(R.color.UE_solid) : getResources().getColor(R.color.UU_solid);
    }


    private int getTaskBgStrokeColor(String pContainer) {
        return pContainer.equals("IE") ? getResources().getColor(R.color.ie_stroke) :
                pContainer.equals("IU") ? getResources().getColor(R.color.iu_stroke) :
                        pContainer.equals("UE") ? getResources().getColor(R.color.ue_stroke) : getResources().getColor(R.color.uu_stroke);
    }


    private int getStringWidth(Paint paint, String str) {
        return (int) paint.measureText(str);
    }

    private int getStringHeight(Paint paint, String str) {
        Paint.FontMetrics fr = paint.getFontMetrics();
        return (int) Math.ceil(fr.descent - fr.top) + 2;  //ceil() 函数向上舍入为最接近的整数。
    }

    /**
     * 设置数据
     *
     * @param dayInfo
     */
    public void setMonthDayData(DayInfo dayInfo) {
        this.dayInfo = dayInfo;
    }


    /**
     * 是否是选中的天
     *
     * @param isCurrentSelectedDate
     */
    public void setIsCurrentSelectedDate(boolean isCurrentSelectedDate) {
        this.isCurrentSelectedDate = isCurrentSelectedDate;
        invalidate();
    }


}
