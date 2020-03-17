package com.ey.weatherforecastapp.ui.main.receiver

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import com.ey.weatherforecastapp.ui.main.utils.Scheduler
import android.content.Intent
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log
import com.ey.weatherforecastapp.MainActivity
import com.ey.weatherforecastapp.ui.main.utils.MESSENGER_INTENT_KEY
import com.ey.weatherforecastapp.ui.main.utils.MSG_COLOR_START


class TestJobService : JobService() {
    private var activityMessenger: Messenger? = null

    override fun onStopJob(p0: JobParameters?): Boolean {
        val service = Intent(applicationContext, MainActivity::class.java)
        applicationContext.startService(service)
        Scheduler.scheduleJob(applicationContext)
        return true;
    }
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        activityMessenger = intent.getParcelableExtra(MESSENGER_INTENT_KEY)
        return Service.START_NOT_STICKY
    }
    override fun onStartJob(p0: JobParameters?): Boolean {

        sendMessage(MSG_COLOR_START, p0?.jobId)
        return true;
    }

    private fun sendMessage(messageID: Int, params: Any?) {
        // If this service is launched by the JobScheduler, there's no callback Messenger. It
        // only exists when the MainActivity calls startService() with the callback in the Intent.
        if (activityMessenger == null) {
            Log.d(TAG, "Service is bound, not started. There's no callback to send a message to.")
            return
        }
        val message = Message.obtain()
        message.run {
            what = messageID
            obj = params
        }
        try {
            activityMessenger?.send(message)
        } catch (e: RemoteException) {
            Log.e(TAG, "Error passing service object back to activity.")
        }
    }

    companion object {
        private val TAG = "MyJobService"
    }
}