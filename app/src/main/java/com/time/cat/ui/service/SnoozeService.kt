package com.time.cat.ui.service

import android.app.IntentService
import android.content.Intent
import com.time.cat.config
import com.time.cat.data.Constants.EVENT_ID
import com.time.cat.data.database.DB
import com.time.cat.rescheduleReminder

class SnoozeService : IntentService("Snooze") {
    override fun onHandleIntent(intent: Intent) {
        val eventId = intent.getIntExtra(EVENT_ID, 0)
        val event = DB.routines().findById(eventId.toLong())
        rescheduleReminder(event, config.snoozeDelay)
    }
}
