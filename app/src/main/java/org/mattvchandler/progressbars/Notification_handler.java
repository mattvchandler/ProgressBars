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
import android.support.v7.app.NotificationCompat;

public class Notification_handler extends BroadcastReceiver
{
    public static final String BASE_ACTION_NAME = "org.mattvchandler.progressbars.COMPLETED_ROWID_";
    public static final String EXTRA_ROWID = "EXTRA_ROWID";
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent.getAction() == null)
            return;

        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            reset_all_alarms(context);
        }
        else if(intent.getAction().substring(0, BASE_ACTION_NAME.length()).equals(BASE_ACTION_NAME))
        {
            // one of the alarms went off - send a notification
            long rowid = intent.getLongExtra(EXTRA_ROWID, -1);
            if(rowid < 0)
                return;
            Progress_bar_data data = new Progress_bar_data(context, rowid);

            NotificationCompat.Builder not_builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.progress_bar_notification)
                    .setContentTitle(context.getResources().getString(R.string.notification_title, data.title))
                    .setContentText(data.complete_text)
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

    private static PendingIntent get_intent(Context context, Progress_bar_data data)
    {
        Intent intent = new Intent(context, Notification_handler.class);
        intent.setAction(BASE_ACTION_NAME + String.valueOf(data.rowid));

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

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for(int i = 0; i < cursor.getCount(); ++i)
        {
            cursor.moveToPosition(i);
            Progress_bar_data data = new Progress_bar_data(cursor);

            PendingIntent pi = get_intent(context, data);

            if(data.notify && now < data.end_time)
            {
                am.setExact(AlarmManager.RTC_WAKEUP, data.end_time * 1000, pi);
            }
            else
            {
                am.cancel(pi);
            }
        }
        cursor.close();
        db.close();
    }

    public static void reset_notification(Context context, Progress_bar_data data)
    {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long now = System.currentTimeMillis() / 1000;

        PendingIntent pi = get_intent(context, data);

        if(data.notify && now < data.end_time)
        {
            am.setExact(AlarmManager.RTC_WAKEUP, data.end_time * 1000, pi);
        }
        else
        {
            am.cancel(pi);
        }
    }

    public static void cancel_notification(Context context, Progress_bar_data data)
    {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = get_intent(context, data);

        am.cancel(pi);
    }
}
