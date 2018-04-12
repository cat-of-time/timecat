/*
 *    Calendula - An assistant for personal medication management.
 *    Copyright (C) 2016 CITIUS - USC
 *
 *    Calendula is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.time.cat.data.database;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.data.model.APImodel.Note;
import com.time.cat.data.model.Converter;
import com.time.cat.data.model.DBmodel.DBNote;
import com.time.cat.data.model.DBmodel.DBUser;
import com.time.cat.data.model.events.PersistenceEvents;
import com.time.cat.util.string.TimeUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class NoteDao extends GenericDao<DBNote, Long> {
    public static final String TAG = "NoteDao";

    public NoteDao(DatabaseHelper db) {
        super(db);
    }

    @Override
    public Dao<DBNote, Long> getConcreteDao() {
        try {
            return dbHelper.getNoteDao();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating users dao", e);
        }
    }

    public List<DBNote> findAllForActiveUser() {
        return findAll(DB.users().getActive());
    }

    public List<DBNote> findAll(DBUser p) {
        return findAll(p.id());
    }

    public List<DBNote> findAll(Long userId) {
        try {
            return dao.queryBuilder()
                    .orderBy(DBNote.COLUMN_CREATED_DATETIME, true)
                    .where().eq(DBNote.COLUMN_USER, userId).query();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding models", e);
        }
    }

    public List<DBNote> findBetween(Date start_date, Date end_date) {
        List<DBNote> dbNoteList = findAllForActiveUser();
        List<DBNote> result = new ArrayList<>();
        for (DBNote dbNote : dbNoteList) {
            Date Created_datetime = TimeUtil.formatGMTDateStr(dbNote.getCreated_datetime());
            if (TimeUtil.isDateEarlier(start_date, Created_datetime) && TimeUtil.isDateEarlier(Created_datetime, end_date)) {
                result.add(dbNote);
            }
        }
        return result;
    }

    @Override
    public void saveAndFireEvent(DBNote u) {

        Object event = u.getId() <= 0 ? new PersistenceEvents.NoteCreateEvent(u) : new PersistenceEvents.NoteUpdateEvent(u);
        save(u);
        TimeCatApp.eventBus().post(event);

    }

    public void createOrUpdateAndFireEvent(DBNote u) throws SQLException {

        Object event = u.getId() <= 0 ? new PersistenceEvents.NoteCreateEvent(u) : new PersistenceEvents.NoteUpdateEvent(u);
        createOrUpdate(u);
        TimeCatApp.eventBus().post(event);

    }

    public void updateAndFireEvent(DBNote u) {
        Object event = new PersistenceEvents.NoteUpdateEvent(u);
        try {
            update(u);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        TimeCatApp.eventBus().post(event);
    }

    public void safeSaveNoteAndFireEvent(Note note) {
        Log.i(TAG, "返回的任务信息 --> " + note.toString());
        //保存用户信息到本地
        DBNote dbNote = Converter.toDBNote(note);
        List<DBNote> existing = null;
        try {
            existing = DB.notes().queryForEq(DBNote.COLUMN_URL, dbNote.getUrl());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbNote.setId(id);
            int color = existing.get(0).getColor();
            dbNote.setColor(color);
            DB.notes().updateAndFireEvent(dbNote);
            Log.i(TAG, "更新笔记信息 --> updateAndFireEvent -- > " + dbNote.toString());
        } else {
            List<Integer> CardStackViewDataList = new ArrayList<>();
            int[] CardStackViewData = TimeCatApp.getInstance().getResources().getIntArray(R.array.card_stack_view_data);
            for (int aCardStackViewData : CardStackViewData) {
                CardStackViewDataList.add(aCardStackViewData);
            }
            Random random = new Random();
            int randomColor = random.nextInt(CardStackViewDataList.size());
            dbNote.setColor(CardStackViewDataList.get(randomColor));
            DB.notes().saveAndFireEvent(dbNote);
            Log.i(TAG, "保存笔记信息 --> saveAndFireEvent -- > " + dbNote.toString());
        }
    }

    public void deleteAndFireEvent(DBNote dbNote) {
        try {
            delete(dbNote);
            TimeCatApp.eventBus().post(new PersistenceEvents.NoteDeleteEvent());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void safeSaveNote(Note note) {
        Log.i(TAG, "返回的任务信息 --> " + note.toString());
        //保存用户信息到本地
        DBNote dbNote = Converter.toDBNote(note);
        List<DBNote> existing = null;
        try {
            existing = DB.notes().queryForEq(DBNote.COLUMN_URL, dbNote.getUrl());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbNote.setId(id);
            int color = existing.get(0).getColor();
            dbNote.setColor(color);
            try {
                DB.notes().update(dbNote);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "更新笔记信息 --> update -- > " + dbNote.toString());
        } else {
            List<Integer> CardStackViewDataList = new ArrayList<>();
            int[] CardStackViewData = TimeCatApp.getInstance().getResources().getIntArray(R.array.card_stack_view_data);
            for (int aCardStackViewData : CardStackViewData) {
                CardStackViewDataList.add(aCardStackViewData);
            }
            Random random = new Random();
            int randomColor = random.nextInt(CardStackViewDataList.size());
            dbNote.setColor(CardStackViewDataList.get(randomColor));
            DB.notes().save(dbNote);
            Log.i(TAG, "保存笔记信息 --> save -- > " + dbNote.toString());
        }
    }

    public void safeSaveDBNote(DBNote dbNote) {
        List<DBNote> existing = null;
        try {
            existing = DB.notes().queryForEq(DBNote.COLUMN_CREATED_DATETIME, dbNote.getCreated_datetime());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbNote.setId(id);
            int color = existing.get(0).getColor();
            dbNote.setColor(color);
            DB.notes().updateAndFireEvent(dbNote);
            Log.i(TAG, "更新笔记信息 --> updateAndFireEvent -- > " + dbNote.toString());
        } else {
            DB.notes().saveAndFireEvent(dbNote);
            Log.i(TAG, "保存笔记信息 --> saveAndFireEvent -- > " + dbNote.toString());
        }
    }

    public void safeSaveDBNoteAndFireEvent(DBNote dbNote) {
        List<DBNote> existing = null;
        try {
            existing = DB.notes().queryForEq(DBNote.COLUMN_CREATED_DATETIME, dbNote.getCreated_datetime());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbNote.setId(id);
            int color = existing.get(0).getColor();
            dbNote.setColor(color);
            DB.notes().updateAndFireEvent(dbNote);
            Log.i(TAG, "更新笔记信息 --> updateAndFireEvent -- > " + dbNote.toString());
        } else {
            DB.notes().saveAndFireEvent(dbNote);
            Log.i(TAG, "保存笔记信息 --> saveAndFireEvent -- > " + dbNote.toString());
        }
    }


    public void updateActiveUserAndFireEvent(DBNote activeDBNote, Note user) {
//        Object event = new PersistenceEvents.UserUpdateEvent(activeDBNote);
//        try {
//            update(Converter.toActiveDBNote(activeDBNote, user));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        TimeCatApp.eventBus().post(event);
    }

    /// Mange active user through preferences

    public void removeCascade(DBNote u) {
        // remove all data
//        removeAllStuff(u);
        // remove dbPlan
        DB.notes().remove(u);

    }

}
