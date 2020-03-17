/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ey.weatherforecastapp.ui.main.receiver

import android.os.Handler
import android.os.Message
import com.ey.weatherforecastapp.MainActivity
import com.ey.weatherforecastapp.ui.main.utils.MSG_COLOR_START
import com.ey.weatherforecastapp.ui.main.utils.MSG_COLOR_STOP
import com.ey.weatherforecastapp.ui.main.utils.MSG_UNCOLOR_START
import com.ey.weatherforecastapp.ui.main.utils.MSG_UNCOLOR_STOP
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit


internal class IncomingMessageHandler(activity: MainActivity) : Handler() {

    private val mainActivity: WeakReference<MainActivity> = WeakReference(activity)

    override fun handleMessage(msg: Message) {
        val mainActivity = mainActivity.get() ?: return

        when (msg.what) {
            MSG_COLOR_START -> {
                sendMessageDelayed(Message.obtain(this, MSG_UNCOLOR_START),
                        TimeUnit.SECONDS.toMillis(3000))
            }
            MSG_COLOR_STOP -> {
                sendMessageDelayed(obtainMessage(MSG_UNCOLOR_STOP), TimeUnit.SECONDS.toMillis(1))
            }
            MSG_UNCOLOR_START -> {
            }
            MSG_UNCOLOR_STOP -> {
            }
        }
    }


}
