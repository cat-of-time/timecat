package com.yumingchuan.rsqmonthcalendar.bean;

import java.util.List;

/**
 * Created by love on 2016/2/23.
 */
public class ScheduleListBean {
    private String type;
    private String date;
    private List<ScheduleToDo> data;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ScheduleToDo> getData() {
        return data;
    }

    public void setData(List<ScheduleToDo> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object obj) {
        return this.date.equals(((ScheduleListBean) obj).date);
    }

    public String getDonePercent() {
        if (data != null) {
            int doneNum = 0;
            for (ScheduleToDo scheduleTodo : data) {
                if (scheduleTodo.pIsDone) {
                    doneNum++;
                }
            }
            return doneNum + "/" + data.size();
        } else {
            return "0/0";
        }
    }

    public int getIntType() {
        //default,pContainer,dateCreatedAsc,dateCreatedDesc
        if ("default".equals(type)) {
            return 0;
        } else if ("pContainer".equals(type)) {
            return 1;
        } else if ("dateCreatedAsc".equals(type)) {
            return 2;
        } else if ("dateCreatedDesc".equals(type)) {
            return 3;
        } else {
            return 0;
        }
    }

}
