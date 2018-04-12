package com.time.cat.ui.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.time.cat.data.Constants.EVENT_ID
import com.time.cat.data.database.DB
import com.time.cat.scheduleAllEvents

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Simple Calendar")
        wakelock.acquire(5000)

//        context.updateListWidget() TODO
        val id = intent.getIntExtra(EVENT_ID, -1)
        if (id == -1) {
            return
        }

        val event = DB.routines().findById(id.toLong())
        if (event == null || event.getReminders().isEmpty()) {
            return
        }

//        if (!event.ignoreEventOccurrences.contains(Formatter.getDayCodeFromTS(event.beginTs).toInt())) {
//            context.notifyEvent(event)
//        } TODO
        context.scheduleAllEvents()
    }
}
