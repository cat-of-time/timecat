package com.time.cat.data.model.events;

import com.time.cat.data.model.DBmodel.DBNote;
import com.time.cat.data.model.DBmodel.DBPlan;
import com.time.cat.data.model.DBmodel.DBPomodoro;
import com.time.cat.data.model.DBmodel.DBRoutine;
import com.time.cat.data.model.DBmodel.DBSubPlan;
import com.time.cat.data.model.DBmodel.DBTask;
import com.time.cat.data.model.DBmodel.DBUser;

/**
 * @author dlink
 * @date 2018/2/4
 * @discription PersistenceEvents
 */
public class PersistenceEvents {

    public static ModelCreateOrUpdateEvent ROUTINE_EVENT = new ModelCreateOrUpdateEvent(DBRoutine.class);
    public static ModelCreateOrUpdateEvent SCHEDULE_EVENT = new ModelCreateOrUpdateEvent(DBTask.class);
    public static ModelCreateOrUpdateEvent NOTE_EVENT = new ModelCreateOrUpdateEvent(DBNote.class);


    public static class ModelCreateOrUpdateEvent {
        public Class<?> clazz;

        public ModelCreateOrUpdateEvent(Class<?> clazz) {
            this.clazz = clazz;
        }
    }

    public static class MedicineAddedEvent {

        public Long id;

        public MedicineAddedEvent(Long id) {
            this.id = id;
        }
    }

    public static class UserCreateEvent {
        public DBUser user;

        public UserCreateEvent(DBUser user) {
            this.user = user;
        }
    }

    public static class UserUpdateEvent {
        public DBUser user;

        public UserUpdateEvent(DBUser user) {
            this.user = user;
        }
    }

    public static class ActiveUserChangeEvent {
        public DBUser user;

        public ActiveUserChangeEvent(DBUser user) {
            this.user = user;
        }
    }

    public static class NoteCreateEvent {
        public DBNote note;

        public NoteCreateEvent(DBNote note) {
            this.note = note;
        }
    }

    public static class NoteUpdateEvent {
        public DBNote note;

        public NoteUpdateEvent(DBNote note) {
            this.note = note;
        }
    }

    public static class NoteDeleteEvent {
        public NoteDeleteEvent() {}
    }

    public static class TaskCreateEvent {
        public DBTask task;

        public TaskCreateEvent(DBTask task) {
            this.task = task;
        }
    }

    public static class TaskUpdateEvent {
        public DBTask task;

        public TaskUpdateEvent(DBTask task) {
            this.task = task;
        }
    }

    public static class TaskDeleteEvent {
        public TaskDeleteEvent(DBTask task) {}
    }

    public static class RoutineCreateEvent {
        public DBRoutine routine;

        public RoutineCreateEvent(DBRoutine routine) {
            this.routine = routine;
        }
    }

    public static class RoutineUpdateEvent {
        public DBRoutine routine;

        public RoutineUpdateEvent(DBRoutine routine) {
            this.routine = routine;
        }
    }

    public static class RoutineDeleteEvent {
        public RoutineDeleteEvent(DBRoutine routine) {}
    }

    public static class PlanCreateEvent {
        public DBPlan dbPlan;

        public PlanCreateEvent(DBPlan dbPlan) {
            this.dbPlan = dbPlan;
        }
    }

    public static class PlanUpdateEvent {
        public DBPlan dbPlan;

        public PlanUpdateEvent(DBPlan dbPlan) {
            this.dbPlan = dbPlan;
        }
    }

    public static class PlanDeleteEvent {
        public PlanDeleteEvent() {}
    }

    public static class SubPlanCreateEvent {
        public DBSubPlan dbSubPlan;

        public SubPlanCreateEvent(DBSubPlan dbSubPlan) {
            this.dbSubPlan = dbSubPlan;
        }
    }

    public static class SubPlanUpdateEvent {
        public DBSubPlan dbSubPlan;

        public SubPlanUpdateEvent(DBSubPlan dbSubPlan) {
            this.dbSubPlan = dbSubPlan;
        }
    }

    public static class SubPlanDeleteEvent {
        public SubPlanDeleteEvent() {}
    }

    public static class PomodoroCreateEvent {
        public DBPomodoro dbPomodoro;

        public PomodoroCreateEvent(DBPomodoro dbPomodoro) {
            this.dbPomodoro = dbPomodoro;
        }
    }

    public static class PomodoroUpdateEvent {
        public DBPomodoro dbPomodoro;

        public PomodoroUpdateEvent(DBPomodoro dbPomodoro) {
            this.dbPomodoro = dbPomodoro;
        }
    }

    public static class PomodoroDeleteEvent {
        public PomodoroDeleteEvent() {}
    }
}
