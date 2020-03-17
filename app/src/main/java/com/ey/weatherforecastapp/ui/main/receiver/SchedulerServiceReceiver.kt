package com.ey.weatherforecastapp.ui.main.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ey.weatherforecastapp.ui.main.utils.Scheduler

class SchedulerServiceReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, p1: Intent?) {
        Scheduler.scheduleJob(context!!)
    }


}