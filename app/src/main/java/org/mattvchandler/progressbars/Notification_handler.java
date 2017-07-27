package org.mattvchandler.progressbars;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.TypedValue;

/*
Copyright (C) 2017 Matthew Chandler

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

// all notification / alarm handling done here
public class Notification_handler extends BroadcastReceiver
{
    private static final String BASE_STARTED_ACTION_NAME = "org.mattvchandler.progressbars.STARTED_ROWID_";
    private static final String BASE_COMPLETED_ACTION_NAME = "org.mattvchandler.progressbars.COMPLETED_ROWID_";
    private static final String EXTRA_ROWID = "EXTRA_ROWID";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent.getAction() == null)
            return;

        // we're set up to receive the system's bootup broadcast, so use it to reset the alarms
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            // get new start/end times first
            Progress_bar_data.apply_all_repeats(context);
            reset_all_alarms(context);
        }
        // one of the alarms went off - send a notification
        else if(intent.getAction().substring(0, BASE_STARTED_ACTION_NAME.length()).equals(BASE_STARTED_ACTION_NAME) ||
                intent.getAction().substring(0, BASE_COMPLETED_ACTION_NAME.length()).equals(BASE_COMPLETED_ACTION_NAME))
        {
            // get the data for the alarm that went off
            long rowid = intent.getLongExtra(EXTRA_ROWID, -1);
            if(rowid < 0)
                return;
            Progress_bar_data data = new Progress_bar_data(context, rowid);

            // update row to get new repeat time, if needed
            if(data.repeats && data.end_time >= System.currentTimeMillis() / 1000)
            {
                data.update(context);
            }

            // send notifications iff master notification setting is on
            if(!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("master_notification", true))
                return;

            // set up start or completion text
            String title, content;
            long when;
            if(intent.getAction().substring(0, BASE_STARTED_ACTION_NAME.length()).equals(BASE_STARTED_ACTION_NAME))
            {
                // don't notify if notification is off
                if(!data.notify_start)
                    return;

                title = context.getResources().getString(R.string.notification_start_title, data.title);
                content = data.start_text;
                when = data.start_time;
            }
            else // if(intent.getAction().substring(0, BASE_COMPLETED_ACTION_NAME.length()).equals(BASE_COMPLETED_ACTION_NAME))
            {
                // don't notify if notification is off
                if(!data.notify_end)
                    return;

                title = context.getResources().getString(R.string.notification_end_title, data.title);
                content = data.complete_text;
                when = data.end_time;

            }

            // get the primary color from the theme
            context.setTheme(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dark_theme", false) ? R.style.Theme_progress_bars_dark : R.style.Theme_progress_bars);
            TypedValue color_tv = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.colorPrimary, color_tv, true);

            // build the notification
            NotificationCompat.Builder not_builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.progress_bar_notification_icon)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setWhen(when * 1000)
                    .setColor(color_tv.data);

            // create an intent for clicking the notification to take us to the main activity
            Intent i = new Intent(context, Progress_bars.class);
            i.putExtra(Progress_bars.EXTRA_SCROLL_TO_ROWID, data.rowid);

            // create an artificial back-stack
            TaskStackBuilder stack = TaskStackBuilder.create(context);
            stack.addParentStack(Progress_bars.class);
            stack.addNextIntent(i);

            // package intent into a pending intent for the notification
            PendingIntent pi = stack.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            not_builder.setContentIntent(pi);

            // send the notification. rowid will be used as the notification's ID
            NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify((int)data.rowid, not_builder.build());
        }
    }

    // build start or completion intent for a start/end alarms
    private static PendingIntent get_intent(Context context, Progress_bar_data data, String base_action)
    {
        // set intent to bring us to the notification handler
        Intent intent = new Intent(context, Notification_handler.class);
        intent.setAction(base_action + String.valueOf(data.rowid));

        // put the rowid in the intent extras
        Bundle extras = new Bundle();
        extras.putLong(EXTRA_ROWID, data.rowid);
        intent.putExtras(extras);

        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private static void reset_alarm_details(Context context, Progress_bar_data data, AlarmManager am, long now)
    {
        // build start and completion intents
        PendingIntent start_pi = get_intent(context, data, BASE_STARTED_ACTION_NAME);
        PendingIntent complete_pi = get_intent(context, data, BASE_COMPLETED_ACTION_NAME);

        // if notifications are enabled and the start time is in the future, set an alarm
        // (will overwrite any existing alarm with the same action and target)
        if(now < data.start_time)
            am.setExact(AlarmManager.RTC_WAKEUP, data.start_time * 1000, start_pi);
            // otherwise cancel any existing alarm
        else
            am.cancel(start_pi);

        // same as above for completion alarms
        if(now < data.end_time)
            am.setExact(AlarmManager.RTC_WAKEUP, data.end_time * 1000, complete_pi);
        else
            am.cancel(complete_pi);
    }

    public static void reset_all_alarms(Context context)
    {
        SQLiteDatabase db = new Progress_bar_DB(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(Progress_bar_table.SELECT_ALL_ROWS, null);

        long now = System.currentTimeMillis() / 1000;

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        // for every timer
        for(int i = 0; i < cursor.getCount(); ++i)
        {
            cursor.moveToPosition(i);
            Progress_bar_data data = new Progress_bar_data(cursor);

            reset_alarm_details(context, data, am, now);
        }
        cursor.close();
        db.close();
    }

    // reset an individual timer's start/end alarms
    public static void reset_alarm(Context context, Progress_bar_data data)
    {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        long now = System.currentTimeMillis() / 1000;

        reset_alarm_details(context, data, am, now);
    }

    // cancel an alarm
    public static void cancel_alarm(Context context, Progress_bar_data data)
    {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // cancel both start and completion alarms
        am.cancel(get_intent(context, data, BASE_STARTED_ACTION_NAME));
        am.cancel(get_intent(context, data, BASE_COMPLETED_ACTION_NAME));
    }
}
