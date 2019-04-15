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

package org.mattvchandler.progressbars

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log
import android.widget.RemoteViews
import org.mattvchandler.progressbars.settings.Settings.Companion.get_date_format
import org.mattvchandler.progressbars.settings.Settings.Companion.get_time_format
import java.util.concurrent.TimeUnit

class Widget: AppWidgetProvider()
{
    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?)
    {
        if(context != null)
            update(context, appWidgetManager, appWidgetIds)
    }

    override fun onAppWidgetOptionsChanged(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int, newOptions: Bundle?)
    {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    }

    override fun onEnabled(context: Context?)
    {
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context?)
    {
        super.onDisabled(context)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?)
    {
        super.onDeleted(context, appWidgetIds)
    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?)
    {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
    }

    override fun onReceive(context: Context?, intent: Intent?)
    {
        Log.d("widget recv", "got: ${intent?.action}")
        when(intent?.action)
        {
            ACTION_UPDATE_TIME -> if(context != null) update(context, null, null)

            else -> super.onReceive(context, intent)
        }
    }

    private fun update(context: Context, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?)
    {
        // TODO: check PowerManager.isScreenOn
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val screen_on = if(Build.VERSION.SDK_INT >= 20) pm.isInteractive else true
        if(screen_on)
        {
            val appWidgetManager_default = appWidgetManager ?: AppWidgetManager.getInstance(context)

            for(appWidgetId in appWidgetIds?: appWidgetManager_default.getAppWidgetIds(ComponentName(context, Widget::class.java)))
                updateAppWidget(context, appWidgetManager_default, appWidgetId)
        }

        // schedule another update
        val intent = Intent(context, this::class.java)
        intent.action = ACTION_UPDATE_TIME
        val pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + TIME_INTERVAL, pi)
    }

    companion object
    {
        private const val ACTION_UPDATE_TIME = "org.mattvchandler.progressbars.ACTION_UPDATE_TIME"
        private val TIME_INTERVAL = TimeUnit.SECONDS.toMillis(10) // TODO: change interval in settings?
        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int)
        {
            // format start and end dates and times
            val date_df = get_date_format(context)
            val time_df = get_time_format()

            val now = System.currentTimeMillis()

            val widgetText = "${date_df.format(now)} ${time_df.format(now)}"
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.widget)
            views.setTextViewText(R.id.appwidget_text, widgetText)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
