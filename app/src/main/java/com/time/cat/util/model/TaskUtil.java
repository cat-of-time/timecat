package com.time.cat.util.model;

import com.ldf.calendar.model.CalendarDate;
import com.time.cat.data.database.DB;
import com.time.cat.data.model.DBmodel.DBTask;
import com.time.cat.data.model.DBmodel.DBUser;
import com.time.cat.util.string.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/7
 * @discription null
 * @usage null
 */
public class TaskUtil {

    public static ArrayList<DBTask> DBTaskFilter(List<DBTask> taskArrayList, CalendarDate currentDate) {
        ArrayList<DBTask> tasks = new ArrayList<>();
        if (taskArrayList == null || taskArrayList.size() <= 0) {
            return tasks;
        }
        // 需要显示的
        // 首先是active user的
        // 今天刚刚finished的
        // 顺延的
        // begin_datetime < today <= end_datetime
        DBUser dbUser = DB.users().getActive();

        Date today = new Date();
        if (currentDate != null) {
            today = TimeUtil.transferCalendarDate(currentDate);
        }
        for (DBTask task : taskArrayList) {
            if (!task.getOwner().equals((ModelUtil.getOwnerUrl(dbUser)))) {
                continue;
            }
            boolean hasAddedTask = false;
            // 把今天刚刚完成的任务(getIsFinish()==true)添加到显示List并标记
            if (task.getIsFinish()) {
                Date finished_datetime = TimeUtil.formatGMTDateStr(task.getFinished_datetime());
                if (finished_datetime != null) {
                    if (finished_datetime.getDay() == today.getDay()
                            && finished_datetime.getMonth() == today.getMonth()
                            && finished_datetime.getYear() == today.getYear()) {
                        tasks.add(task);
//                        LogUtil.i("add task, because task is finished today");
                    }
                }
                hasAddedTask = true; // 只要是完成了的，之后都不再判断
            }
//            LogUtil.e(task.getIs_all_day() + task.toString());
            if (!task.getIs_all_day() && !hasAddedTask
                    && task.getBegin_datetime() != null
                    && task.getEnd_datetime() != null) {
                Date begin_datetime = TimeUtil.formatGMTDateStr(task.getBegin_datetime());
                Date end_datetime = TimeUtil.formatGMTDateStr(task.getEnd_datetime());
                if (begin_datetime != null && end_datetime != null) {
                    if (TimeUtil.isDateEarlier(begin_datetime, today) && TimeUtil.isDateEarlier(today, end_datetime)) {
                        tasks.add(task);
                        hasAddedTask = true;
//                        LogUtil.i("add task, because begin <= today <= end");
                    }
                }
            }
            // 把顺延的添加到显示List并标记
            if (task.getCreated_datetime() != null || task.getCreated_datetime() != "null") {
                Date created_datetime = TimeUtil.formatGMTDateStr(task.getCreated_datetime());
                if (!hasAddedTask && created_datetime != null) {
                    long during = today.getTime() - created_datetime.getTime();
                    if (TimeUtil.isDateEarlier(created_datetime, today)) {
                        if (task.getIs_all_day()) {
                            tasks.add(task);
//                            LogUtil.i("add task, because of delay");
                        }
                    }
                }
            }
        }

        return tasks;
    }

    public static ArrayList<DBTask> sort(ArrayList<DBTask> taskArrayList) {
        ArrayList<DBTask> sortedDBTaskList = new ArrayList<>();
        if (taskArrayList == null || taskArrayList.size() <= 0) {
            return sortedDBTaskList;
        }
        ArrayList<DBTask> label_0_DBTaskList = new ArrayList<>();
        ArrayList<DBTask> label_1_DBTaskList = new ArrayList<>();
        ArrayList<DBTask> label_2_DBTaskList = new ArrayList<>();
        ArrayList<DBTask> label_3_DBTaskList = new ArrayList<>();
        ArrayList<DBTask> finished_DBTaskList = new ArrayList<>();

        for (DBTask dbTask : taskArrayList) {
            if (dbTask.getIsFinish()) {
                finished_DBTaskList.add(dbTask);
                continue;
            }
            switch (dbTask.getLabel()) {
                case DBTask.LABEL_IMPORTANT_URGENT:
                    label_0_DBTaskList.add(dbTask);
                    break;
                case DBTask.LABEL_IMPORTANT_NOT_URGENT:
                    label_1_DBTaskList.add(dbTask);
                    break;
                case DBTask.LABEL_NOT_IMPORTANT_URGENT:
                    label_2_DBTaskList.add(dbTask);
                    break;
                case DBTask.LABEL_NOT_IMPORTANT_NOT_URGENT:
                    label_3_DBTaskList.add(dbTask);
                    break;
            }
        }
        mergeSort2List(label_0_DBTaskList, sortedDBTaskList);
        mergeSort2List(label_1_DBTaskList, sortedDBTaskList);
        mergeSort2List(label_2_DBTaskList, sortedDBTaskList);
        mergeSort2List(label_3_DBTaskList, sortedDBTaskList);
        mergeSort2List(finished_DBTaskList, sortedDBTaskList);

        return sortedDBTaskList;
    }

    private static void reverse(ArrayList<DBTask> arr, int i, int j) {
        while(i < j) {
            DBTask temp = arr.get(i);
            arr.set(i++, arr.get(j));
            arr.set(j--, temp);
        }
    }

    // swap [bias, bias+headSize) and [bias+headSize, bias+headSize+endSize)
    private static void swapAdjacentBlocks(ArrayList<DBTask> arr, int bias, int oneSize, int anotherSize) {
        reverse(arr, bias, bias + oneSize - 1);
        reverse(arr, bias + oneSize, bias + oneSize + anotherSize - 1);
        reverse(arr, bias, bias + oneSize + anotherSize - 1);
    }

    private static void inplaceMerge(ArrayList<DBTask> arr, int l, int mid, int r) {
        int i = l;     // 指示左侧有序串
        int j = mid + 1; // 指示右侧有序串
        while(i < j && j <= r) { //原地归并结束的条件。
            while(i < j && isValid(arr, i, j)) {
                i++;
            }
            int index = j;
            while(j <= r && isValid(arr, j, i)) {
                j++;
            }
            swapAdjacentBlocks(arr, i, index-i, j-index);
            i += (j-index);
        }
    }

    private static boolean isValid(ArrayList<DBTask> arr, int i, int j) {
        Date date_i = TimeUtil.formatGMTDateStr(arr.get(i).getCreated_datetime());
        Date date_j = TimeUtil.formatGMTDateStr(arr.get(j).getCreated_datetime());
        return (date_i != null ? date_i.getTime() : 0) <= (date_j != null ? date_j.getTime() : 0);
    }

    private static void mergeSort(ArrayList<DBTask> arr, int l, int r) {
        if(l < r) {
            int mid = (l + r) / 2;
            mergeSort(arr, l, mid);
            mergeSort(arr, mid + 1, r);
            inplaceMerge(arr, l, mid, r);
        }
    }

    private static void mergeSort2List(ArrayList<DBTask> taskArrayList, ArrayList<DBTask> result) {
        if (taskArrayList == null || taskArrayList.size() <= 0) {
            return;
        }
        mergeSort(taskArrayList, 0, taskArrayList.size()-1);
        result.addAll(taskArrayList);
    }

}
