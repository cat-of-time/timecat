package com.time.cat.data.model;

import com.time.cat.data.model.APImodel.Note;
import com.time.cat.data.model.APImodel.Plan;
import com.time.cat.data.model.APImodel.Pomodoro;
import com.time.cat.data.model.APImodel.Routine;
import com.time.cat.data.model.APImodel.SubPlan;
import com.time.cat.data.model.APImodel.Task;
import com.time.cat.data.model.APImodel.User;
import com.time.cat.data.model.DBmodel.DBNote;
import com.time.cat.data.model.DBmodel.DBPlan;
import com.time.cat.data.model.DBmodel.DBPomodoro;
import com.time.cat.data.model.DBmodel.DBRoutine;
import com.time.cat.data.model.DBmodel.DBSubPlan;
import com.time.cat.data.model.DBmodel.DBTask;
import com.time.cat.data.model.DBmodel.DBUser;
import com.time.cat.data.network.ConstantURL;
import com.time.cat.ui.widgets.theme.ThemeManager;
import com.time.cat.util.source.AvatarManager;

/**
 * @author dlink
 * @date 2018/2/6
 * @discription 实现本地数据库model和网络请求model的转化
 */
public class Converter {
    public static DBUser toDBUser(User u) {
        DBUser dbUser = new DBUser();
        dbUser.setEmail(u.getEmail());
        dbUser.setName(u.getUsername());
        dbUser.setAvatar(AvatarManager.AVATAR_4);
        dbUser.setColor(ThemeManager.CARD_HOPE);
        dbUser.setDefault(true);
        dbUser.setPlans(u.getPlans());
        dbUser.setTags(u.getTags());
        dbUser.setTasks(u.getTasks());
//        dbUser.setPassword(u.getPassword());
        return dbUser;
    }

    public static DBUser toActiveDBUser(DBUser activeDBUser, User u) {
        activeDBUser.setEmail(u.getEmail());
        activeDBUser.setName(u.getUsername());
        activeDBUser.setPlans(u.getPlans());
        activeDBUser.setTags(u.getTags());
        activeDBUser.setTasks(u.getTasks());
//        dbUser.setPassword(u.getPassword());
        return activeDBUser;
    }

    public static User toAPIUser(DBUser dbUser) {
        User user = new User();
        user.setEmail(dbUser.getEmail());
//        user.setId(dbUser.id());
        user.setUsername(dbUser.name());
        user.setPassword(dbUser.getPassword());
        return user;
    }

    public static DBTask toDBTask(Task task) {
        DBTask dbTask = new DBTask();
        dbTask.setContent(task.getContent());
        dbTask.setCreated_datetime(task.getCreated_datetime());
        dbTask.setFinished_datetime(task.getFinished_datetime());
        dbTask.setBegin_datetime(task.getBegin_datetime());
        dbTask.setEnd_datetime(task.getEnd_datetime());
        dbTask.setIs_all_day(task.getIs_all_day());
        dbTask.setIsFinish(task.getIsFinish());
        dbTask.setUrl(task.getUrl());
        dbTask.setTitle(task.getTitle());
        dbTask.setOwner(task.getOwner());
        dbTask.setLabel(task.getLabel());
        return dbTask;
    }

    public static Task toTask(DBTask dbTask) {
        Task task = new Task();
        task.setContent(dbTask.getContent());
        task.setCreated_datetime(dbTask.getCreated_datetime());
        task.setFinished_datetime(dbTask.getFinished_datetime());
        task.setBegin_datetime(dbTask.getBegin_datetime());
        task.setEnd_datetime(dbTask.getEnd_datetime());
        task.setIs_all_day(dbTask.getIs_all_day());
        task.setIsFinish(dbTask.getIsFinish());
        task.setUrl(dbTask.getUrl());
        task.setTitle(dbTask.getTitle());
        task.setOwner(dbTask.getOwner());
        task.setLabel(dbTask.getLabel());
        return task;
    }

    public static DBNote toDBNote(Note note) {
        DBNote dbNote = new DBNote();
        dbNote.setUrl(note.getUrl());
        dbNote.setOwner(note.getOwner());
        dbNote.setTitle(note.getTitle());
        dbNote.setContent(note.getContent());
        dbNote.setCreated_datetime(note.getCreated_datetime());
        dbNote.setUpdate_datetime(note.getUpdate_datetime());
        return dbNote;
    }

    public static Note toNote(DBNote dbNote) {
        Note note = new Note();
        note.setUrl(dbNote.getUrl());
        note.setOwner(dbNote.getOwner());
        note.setTitle(dbNote.getTitle());
        note.setContent(dbNote.getContent());
        note.setCreated_datetime(dbNote.getCreated_datetime());
        note.setUpdate_datetime(dbNote.getUpdate_datetime());
        return note;
    }

    public static DBRoutine toDBRoutine(Routine routine) {
        DBRoutine dbRoutine = new DBRoutine();
        dbRoutine.setContent(routine.getContent());
        dbRoutine.setCreated_datetime(routine.getCreated_datetime());
        dbRoutine.setFinished_datetime(routine.getFinished_datetime());
        dbRoutine.setBegin_datetime(routine.getBegin_datetime());
        dbRoutine.setEnd_datetime(routine.getEnd_datetime());
        dbRoutine.setIs_all_day(routine.getIs_all_day());
        dbRoutine.setIsFinish(routine.getIsFinish());
        dbRoutine.setUrl(routine.getUrl());
        dbRoutine.setTitle(routine.getTitle());
        dbRoutine.setOwner(routine.getOwner());
        dbRoutine.setLabel(routine.getLabel());
        return dbRoutine;
    }

    public static Routine toRoutine(DBRoutine dbRoutine) {
        Routine routine = new Routine();
        routine.setContent(dbRoutine.getContent());
        routine.setCreated_datetime(dbRoutine.getCreated_datetime());
        routine.setFinished_datetime(dbRoutine.getFinished_datetime());
        routine.setBegin_datetime(dbRoutine.getBegin_datetime());
        routine.setEnd_datetime(dbRoutine.getEnd_datetime());
        routine.setIs_all_day(dbRoutine.getIs_all_day());
        routine.setIsFinish(dbRoutine.getIsFinish());
        routine.setUrl(dbRoutine.getUrl());
        routine.setTitle(dbRoutine.getTitle());
        routine.setOwner(dbRoutine.getOwner());
        routine.setLabel(dbRoutine.getLabel());
        return routine;
    }

    public static DBPlan toDBPlan(Plan plan) {
        DBPlan dbPlan = new DBPlan();
        dbPlan.setUrl(plan.getUrl());
        dbPlan.setOwner(plan.getOwner());
        dbPlan.setTitle(plan.getTitle());
        dbPlan.setContent(plan.getContent());
        dbPlan.setCreated_datetime(plan.getCreated_datetime());
        dbPlan.setUpdate_datetime(plan.getUpdate_datetime());
        dbPlan.setColor(plan.getColor());
        dbPlan.setCoverImageUrl(plan.getCoverImageUrl());
        dbPlan.setIs_star(plan.getIs_star());
        return dbPlan;
    }

    public static Plan toPlan(DBPlan dbPlan) {
        Plan plan = new Plan();
        plan.setUrl(dbPlan.getUrl());
        plan.setOwner(dbPlan.getOwner());
        plan.setTitle(dbPlan.getTitle());
        plan.setContent(dbPlan.getContent());
        plan.setCreated_datetime(dbPlan.getCreated_datetime());
        plan.setUpdate_datetime(dbPlan.getUpdate_datetime());
        plan.setColor(dbPlan.getColor());
        plan.setCoverImageUrl(dbPlan.getCoverImageUrl());
        plan.setIs_star(dbPlan.getIs_star());
        return plan;
    }

    public static DBSubPlan toDBSubPlan(SubPlan subPlan) {
        DBSubPlan dbSubPlan = new DBSubPlan();
        dbSubPlan.setUrl(subPlan.getUrl());
        dbSubPlan.setOwner(subPlan.getOwner());
        dbSubPlan.setTitle(subPlan.getTitle());
        dbSubPlan.setContent(subPlan.getContent());
        dbSubPlan.setCreated_datetime(subPlan.getCreated_datetime());
        dbSubPlan.setUpdate_datetime(subPlan.getUpdate_datetime());
        dbSubPlan.setColor(subPlan.getColor());
        return dbSubPlan;
    }

    public static SubPlan toSubPlan(DBSubPlan dbSubPlan) {
        SubPlan subPlan = new SubPlan();
        subPlan.setUrl(dbSubPlan.getUrl());
        subPlan.setOwner(dbSubPlan.getOwner());
        subPlan.setTitle(dbSubPlan.getTitle());
        subPlan.setContent(dbSubPlan.getContent());
        subPlan.setCreated_datetime(dbSubPlan.getCreated_datetime());
        subPlan.setUpdate_datetime(dbSubPlan.getUpdate_datetime());
        subPlan.setColor(dbSubPlan.getColor());
        subPlan.setId(dbSubPlan.getId());
        return subPlan;
    }

    public static DBPomodoro toDBPomodoro(Pomodoro pomodoro) {
        DBPomodoro dbPomodoro = new DBPomodoro(0L);
        dbPomodoro.setUrl(pomodoro.getUrl());
        dbPomodoro.setOwner(pomodoro.getOwner());
        dbPomodoro.setCreated_datetime(pomodoro.getCreated_datetime());
        dbPomodoro.setBegin_datetime(pomodoro.getBegin_datetime());
        dbPomodoro.setEnd_datetime(pomodoro.getEnd_datetime());
        dbPomodoro.setDate_add(pomodoro.getDate_add());
        dbPomodoro.setUser(pomodoro.getUser());
        dbPomodoro.setDuration(pomodoro.getDuration());
        return dbPomodoro;
    }

    public static Pomodoro toPomodoro(DBPomodoro dbPomodoro) {
        Pomodoro pomodoro = new Pomodoro();
        pomodoro.setId(dbPomodoro.getId());
        pomodoro.setUrl(dbPomodoro.getUrl());
        pomodoro.setOwner(dbPomodoro.getOwner());
        pomodoro.setCreated_datetime(dbPomodoro.getCreated_datetime());
        pomodoro.setBegin_datetime(dbPomodoro.getBegin_datetime());
        pomodoro.setEnd_datetime(dbPomodoro.getEnd_datetime());
        pomodoro.setDate_add(dbPomodoro.getDate_add());
        pomodoro.setUser(dbPomodoro.getUser());
        pomodoro.setDuration(dbPomodoro.getDuration());
        return pomodoro;
    }


    public static String getOwnerUrl(User u) {
        return ConstantURL.BASE_URL_USERS + u.getEmail() + "/";
    }

    public static String getOwnerUrl(DBUser u) {
        return ConstantURL.BASE_URL_USERS + u.getEmail() + "/";
    }
}
