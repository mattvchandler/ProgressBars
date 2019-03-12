/*
Copyright (C) 2019 Matthew Chandler

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package org.mattvchandler.progressbars.util

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.TypedValue
import org.mattvchandler.progressbars.Progress_bars
import org.mattvchandler.progressbars.R
import org.mattvchandler.progressbars.db.DB
import org.mattvchandler.progressbars.db.Data
import org.mattvchandler.progressbars.db.Progress_bars_table
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.math.min

// all notification / alarm handling done here
class Notification_handler: BroadcastReceiver()
{
    // TODO: single-events not respecting send notification flag
    override fun onReceive(context: Context, intent: Intent)
    {
        if(intent.action == null)
            return

        val action = intent.action!!
        val start_action = action.substring(0, min(BASE_STARTED_ACTION_NAME.length, action.length)) == BASE_STARTED_ACTION_NAME
        val completion_action = action.substring(0, min(BASE_COMPLETED_ACTION_NAME.length, action.length)) == BASE_COMPLETED_ACTION_NAME

        // we're set up to receive the system's bootup broadcast, so use it to reset the alarms
        if(action == "android.intent.action.BOOT_COMPLETED")
        {
            // get new start/end times first
            Data.apply_all_repeats(context)
            reset_all_alarms(context)
        }
        // one of the alarms went off - send a notification
        else if(start_action || completion_action)
        {
            // get the data for the alarm that went off
            val data_as_bytes = intent.getByteArrayExtra(EXTRA_DATA) ?: return
            val instream = ByteArrayInputStream(data_as_bytes)
            val istream = ObjectInputStream(instream)
            val data = istream.readObject() as Data

            // send notifications iff master notification setting is on
            if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("master_notification", true))
            {
                // set up start or completion text
                var content = ""
                var notification_when: Long = 0
                var do_notify = false

                if(data.separate_time && ((data.notify_start && start_action) ||
                        // special case - when start and end times are the same, only the completed alarm is fired. if only the start notification is enabled, we still need to send it!
                        (data.notify_start && !data.notify_end && data.start_time == data.end_time && completion_action)))
                {
                    do_notify = true
                    content = data.start_text
                    notification_when = data.start_time
                }
                else if(data.notify_end && completion_action)
                {
                    do_notify = true

                    if(data.separate_time)
                    {
                        content = data.complete_text
                        notification_when = data.end_time
                    }
                    else
                    {
                        content = data.single_complete_text
                        notification_when = data.start_time
                    }
                }

                if(do_notify)
                {
                    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    setup_notification_channel(context)

                    // get the primary color from the theme
                    context.setTheme(R.style.Theme_progress_bars)
                    val color_tv = TypedValue()
                    context.theme.resolveAttribute(R.attr.colorPrimary, color_tv, true)

                    // build the notification
                    val not_builder = NotificationCompat.Builder(context, CHANNEL_ID)
                            .setContentTitle(data.title)
                            .setContentText(content)
                            .setSmallIcon(R.drawable.ic_notification)
                            .setColor(color_tv.data)
                            .setGroup(GROUP)
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_EVENT)
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setWhen(notification_when * 1000)

                    // create an intent for clicking the notification to take us to the main activity
                    val i = Intent(context, Progress_bars::class.java)
                    i.putExtra(Progress_bars.EXTRA_ROWID, data.rowid)

                    // create an artificial back-stack
                    val stack = TaskStackBuilder.create(context)
                    stack.addParentStack(Progress_bars::class.java)
                    stack.addNextIntent(i)

                    // package intent into a pending intent for the notification
                    val pi = stack.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                    not_builder.setContentIntent(pi)

                    // send the notification. rowid will be used as the notification's ID
                    nm.notify(data.rowid.toInt(), not_builder.build())

                    // build a group summary notification
                    if(Build.VERSION.SDK_INT >= 24)
                    {
                        val summary = NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_notification)
                            .setColor(color_tv.data)
                            .setCategory(NotificationCompat.CATEGORY_EVENT)
                            .setGroup(GROUP)
                            .setGroupSummary(true)
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_EVENT)
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                        nm.notify(GROUP_SUMMARY_ID, summary.build())
                    }
                }
            }

            // update the displayed list of timers
            if(data.repeats && (if(data.separate_time) data.end_time else data.start_time) <= System.currentTimeMillis() / 1000)
            {
                val update_intent = Intent(Progress_bars.CHANGE_LIST_EVENT)
                update_intent.putExtra(Progress_bars.EXTRA_ROWID, data.rowid)
                LocalBroadcastManager.getInstance(context).sendBroadcast(update_intent)
            }
        }
    }

    companion object
    {
        const val BASE_STARTED_ACTION_NAME = "org.mattvchandler.progressbars.STARTED_ROWID_"
        const val BASE_COMPLETED_ACTION_NAME = "org.mattvchandler.progressbars.COMPLETED_ROWID_"
        const val EXTRA_DATA = "EXTRA_DATA"

        const val CHANNEL_ID = "org.mattvchandler.progressbars.notification_channel"
        const val GROUP_SUMMARY_ID = 0
        const val GROUP = "org.mattvchandler.progressbars.notification_group"

        fun setup_notification_channel(context: Context)
        {
            // Set up notification channel (API 26+)
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if(nm.getNotificationChannel(CHANNEL_ID) == null)
                {
                    val channel = NotificationChannel(CHANNEL_ID,
                            context.resources.getString(R.string.notification_channel_name),
                            NotificationManager.IMPORTANCE_HIGH)
                    channel.description = context.resources.getString(R.string.notification_channel_desc)
                    channel.enableVibration(true)
                    channel.enableLights(true)
                    channel.setShowBadge(true)

                    nm.createNotificationChannel(channel)
                }
            }
        }

        // build start or completion intent for a start/end alarms
        private fun get_intents(context: Context, data: Data, pack_data: Boolean): Pair<PendingIntent, PendingIntent>
        {
            // set intents to bring us to the notification handler
            val start_intent = Intent(context, Notification_handler::class.java)
            start_intent.action = BASE_STARTED_ACTION_NAME + data.rowid.toString()

            val complete_intent = Intent(context, Notification_handler::class.java)
            complete_intent.action = BASE_COMPLETED_ACTION_NAME + data.rowid.toString()

            if(pack_data)
            {
                // put the rowid in the intent extras (convert to byte array to work around issue w/ passing serializable to Broadcast receiver
                val out = ByteArrayOutputStream()
                val os = ObjectOutputStream(out)
                os.writeObject(data)
                val data_as_bytes = out.toByteArray()

                start_intent.putExtra(EXTRA_DATA, data_as_bytes)
                complete_intent.putExtra(EXTRA_DATA, data_as_bytes)
            }

            return Pair(PendingIntent.getBroadcast(context, 0, start_intent, PendingIntent.FLAG_CANCEL_CURRENT), PendingIntent.getBroadcast(context, 0, complete_intent, PendingIntent.FLAG_CANCEL_CURRENT))
        }

        private fun reset_alarm_details(context: Context, data: Data, am: AlarmManager, now: Long)
        {
            // build start and completion intents
            val (start_pi, complete_pi) = get_intents(context, data, true)

            // cancel any existing alarms
            am.cancel(start_pi)
            am.cancel(complete_pi)

            // if start time is in the future, set an alarm
            // if start and end times are the same or using a single time, only set the completion alarm
            // (will overwrite any existing alarm with the same action and target)
            if(now < data.start_time && data.start_time != data.end_time && data.separate_time)
            {
                if(Build.VERSION.SDK_INT >= 23)
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, data.start_time * 1000, start_pi)
                else
                    am.setExact(AlarmManager.RTC_WAKEUP, data.start_time * 1000, start_pi)
            }

            // same as above for completion alarms
            val end_time = if(data.separate_time) data.end_time else data.start_time
            if(now < end_time)
            {
                if(Build.VERSION.SDK_INT >= 23)
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, end_time * 1000, complete_pi)
                else
                    am.setExact(AlarmManager.RTC_WAKEUP, end_time * 1000, complete_pi)
            }
        }

        fun reset_all_alarms(context: Context)
        {
            val db = DB(context).readableDatabase
            val cursor = db.rawQuery(Progress_bars_table.SELECT_ALL_ROWS, null)

            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val now = System.currentTimeMillis() / 1000

            // for every timer
            for(i in 0 until cursor.count)
            {
                cursor.moveToPosition(i)
                val data = Data(cursor)

                reset_alarm_details(context, data, am, now)
            }
            cursor.close()
            db.close()
        }

        // reset an individual timer's start/end alarms
        fun reset_alarm(context: Context, data: Data)
        {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val now = System.currentTimeMillis() / 1000

            reset_alarm_details(context, data, am, now)
        }

        // cancel an alarm
        fun cancel_alarm(context: Context, data: Data)
        {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // cancel both start and completion alarms
            val (start_pi, complete_pi) = get_intents(context, data, false)
            am.cancel(start_pi)
            am.cancel(complete_pi)
        }
    }
}
