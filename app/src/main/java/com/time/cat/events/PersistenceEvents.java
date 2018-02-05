package com.time.cat.events;

import com.time.cat.mvp.model.Routine;
import com.time.cat.mvp.model.User;
import com.time.cat.mvp.model.Schedule;

/**
 * @author dlink
 * @date 2018/2/4
 * @discription PersistenceEvents
 */
public class PersistenceEvents {

    public static ModelCreateOrUpdateEvent ROUTINE_EVENT = new ModelCreateOrUpdateEvent(Routine.class);
    public static ModelCreateOrUpdateEvent SCHEDULE_EVENT = new ModelCreateOrUpdateEvent(Schedule.class);


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
        public User user;
        public UserCreateEvent(User user) {
            this.user = user;
        }
    }

    public static class UserUpdateEvent {
        public User user;
        public UserUpdateEvent(User user) {
            this.user = user;
        }
    }

    public static class ActiveUserChangeEvent {
        public User user;
        public ActiveUserChangeEvent(User user) {
            this.user = user;
        }
    }



}
