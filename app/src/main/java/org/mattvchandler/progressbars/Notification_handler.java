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

        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            reset_all_alarms(context);
        }
        // one of the alarms went off - send a notification
        else if(intent.getAction().substring(0, BASE_STARTED_ACTION_NAME.length()).equals(BASE_STARTED_ACTION_NAME) ||
                intent.getAction().substring(0, BASE_COMPLETED_ACTION_NAME.length()).equals(BASE_COMPLETED_ACTION_NAME))
        {
            long rowid = intent.getLongExtra(EXTRA_ROWID, -1);
            if(rowid < 0)
                return;
            Progress_bar_data data = new Progress_bar_data(context, rowid);

            String title, content;
            if(intent.getAction().substring(0, BASE_STARTED_ACTION_NAME.length()).equals(BASE_STARTED_ACTION_NAME))
            {
                title = context.getResources().getString(R.string.notification_start_title, data.title);
                content = data.start_text;
            }
            else // if(intent.getAction().substring(0, BASE_COMPLETED_ACTION_NAME.length()).equals(BASE_COMPLETED_ACTION_NAME))
            {
                title = context.getResources().getString(R.string.notification_end_title, data.title);
                content = data.complete_text;
            }

            NotificationCompat.Builder not_builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.progress_bar_notification)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL);

            Intent i = new Intent(context, Progress_bars.class);
            TaskStackBuilder stack = TaskStackBuilder.create(context);
            stack.addParentStack(Progress_bars.class);
            stack.addNextIntent(i);
            PendingIntent pi = stack.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            not_builder.setContentIntent(pi);

            NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify((int)data.rowid, not_builder.build());
        }
    }

    private static PendingIntent get_intent(Context context, Progress_bar_data data, String base_action)
    {
        Intent intent = new Intent(context, Notification_handler.class);
        intent.setAction(base_action + String.valueOf(data.rowid));

        Bundle extras = new Bundle();
        extras.putLong(EXTRA_ROWID, data.rowid);
        intent.putExtras(extras);

        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public static void reset_all_alarms(Context context)
    {
        SQLiteDatabase db = new Progress_bar_DB(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(Progress_bar_table.SELECT_ALL_ROWS, null);

        long now = System.currentTimeMillis() / 1000;

        boolean master_notification = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("master_notification", true);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for(int i = 0; i < cursor.getCount(); ++i)
        {
            cursor.moveToPosition(i);
            Progress_bar_data data = new Progress_bar_data(cursor);

            PendingIntent start_pi = get_intent(context, data, BASE_STARTED_ACTION_NAME);
            PendingIntent complete_pi = get_intent(context, data, BASE_COMPLETED_ACTION_NAME);

            if(master_notification && data.notify_start && now < data.start_time)
                am.setExact(AlarmManager.RTC_WAKEUP, data.start_time * 1000, start_pi);
            else
                am.cancel(start_pi);

            if(master_notification && data.notify_end && now < data.end_time)
                am.setExact(AlarmManager.RTC_WAKEUP, data.end_time * 1000, complete_pi);
            else
                am.cancel(complete_pi);
        }
        cursor.close();
        db.close();
    }

    public static void reset_notification(Context context, Progress_bar_data data)
    {
        boolean master_notification = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("master_notification", true);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long now = System.currentTimeMillis() / 1000;

        PendingIntent start_pi = get_intent(context, data, BASE_STARTED_ACTION_NAME);
        PendingIntent complete_pi = get_intent(context, data, BASE_COMPLETED_ACTION_NAME);

        if(master_notification && data.notify_start && now < data.start_time)
            am.setExact(AlarmManager.RTC_WAKEUP, data.start_time * 1000, start_pi);
        else
            am.cancel(start_pi);

        if(master_notification && data.notify_end && now < data.end_time)
            am.setExact(AlarmManager.RTC_WAKEUP, data.end_time * 1000, complete_pi);
        else
            am.cancel(complete_pi);
    }

    public static void cancel_notification(Context context, Progress_bar_data data)
    {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        am.cancel(get_intent(context, data, BASE_STARTED_ACTION_NAME));
        am.cancel(get_intent(context, data, BASE_COMPLETED_ACTION_NAME));
    }
}
