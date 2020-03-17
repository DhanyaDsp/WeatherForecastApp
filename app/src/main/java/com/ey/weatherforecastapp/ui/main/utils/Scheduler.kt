package com.ey.weatherforecastapp.ui.main.utils

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import com.ey.weatherforecastapp.ui.main.receiver.TestJobService


class Scheduler {
    companion object {
        fun scheduleJob(context: Context) {
            val serviceComponent = ComponentName(context, TestJobService::class.java)
            val builder = JobInfo.Builder(0, serviceComponent)
//        builder.setMinimumLatency((1 * 1000).toLong()) // wait at least
            builder.setOverrideDeadline((7200000).toLong()) // maximum delay
            val jobScheduler = context.getSystemService(JobScheduler::class.java)
            jobScheduler.schedule(builder.build())
        }
    }
}