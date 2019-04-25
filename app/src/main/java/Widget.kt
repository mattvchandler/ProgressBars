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
import android.view.View
import android.widget.RemoteViews
import androidx.preference.PreferenceManager
import org.mattvchandler.progressbars.db.DB
import org.mattvchandler.progressbars.db.Data
import org.mattvchandler.progressbars.db.Progress_bars_table
import org.mattvchandler.progressbars.list.View_data
import org.mattvchandler.progressbars.settings.Settings

// TODO preview image?
// TODO: repeat & notifications not working consistently

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
        val am = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(build_alarm_intent(context))
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?)
    {
        if(appWidgetIds != null)
        {
            val db = DB(context!!).writableDatabase
            for(widget_id in appWidgetIds)
            {
                val arg_array = arrayOf(widget_id.toString())

                val cursor = db.rawQuery(Progress_bars_table.SELECT_WIDGET, arg_array)
                val data = Data(cursor)
                cursor.close()

                data.unregister_alarms(context)
                db.delete(Progress_bars_table.TABLE_NAME, "${Progress_bars_table.WIDGET_ID_COL} = ?", arg_array)
            }

            db.close()
        }
    }

    // TODO: test this
    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?)
    {
        if(oldWidgetIds != null && newWidgetIds != null && oldWidgetIds.size == newWidgetIds.size)
        {
            val db = DB(context!!).writableDatabase

            data class Widget_migration(val data: Data, val new_id: Int)
            val widget_data = Array(oldWidgetIds.size)
            {
                val cursor = db.rawQuery(Progress_bars_table.SELECT_WIDGET, arrayOf(oldWidgetIds[it].toString()))
                val data = Data(cursor)
                cursor.close()
                Widget_migration(data, newWidgetIds[it])
            }

            db.beginTransaction()
            try
            {
                db.delete(Progress_bars_table.TABLE_NAME, "${Progress_bars_table.WIDGET_ID_COL} IS NOT NULL", null)

                for((data, new_id) in widget_data)
                {
                    data.insert(db, null, new_id)
                    data.register_alarms(context)
                }

                db.setTransactionSuccessful()
            }
            finally
            {
                db.endTransaction()
                db.close()
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?)
    {
//        Log.v("Widget::onReceive", intent?.action)
        when(intent?.action)
        {
            ACTION_UPDATE_TIME -> if(context != null) update(context, null, null)

            else -> super.onReceive(context, intent)
        }
    }

    companion object
    {
        private const val ACTION_UPDATE_TIME = "org.mattvchandler.progressbars.ACTION_UPDATE_TIME"

        fun create_or_update_data(context: Context, widget_id: Int, data: Data)
        {
            val db = DB(context).writableDatabase
            val cursor = db.rawQuery(Progress_bars_table.SELECT_WIDGET, arrayOf(widget_id.toString()))
            if(cursor.count == 0)
            {
                Log.d("Widget::create", "$widget_id")

                data.register_alarms(context)
                data.insert(db, null, widget_id)
            }
            else
            {
                Log.d("Widget::update", "$widget_id")
                data.update_alarms(context)
                data.update(db)
            }
            cursor.close()
            db.close()

            update(context, AppWidgetManager.getInstance(context), intArrayOf(widget_id))
        }

        fun get_data_from_id(context: Context, widget_id: Int): View_data?
        {
            val db = DB(context).readableDatabase
            val cursor = db.rawQuery(Progress_bars_table.SELECT_WIDGET, arrayOf(widget_id.toString()))
            if(cursor.count == 0)
            {
                Log.e("Widget::updateAppWidget", "No data found for widget: $widget_id")

                cursor.close()
                db.close()

                return null
            }

            cursor.moveToFirst()
            val data = View_data(context, Data(cursor))

            cursor.close()
            db.close()

            return data
        }

        private fun update(context: Context, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?)
        {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val screen_on = if(Build.VERSION.SDK_INT >= 20) pm.isInteractive else true
            if(screen_on)
            {
                val appWidgetManager_default = appWidgetManager ?: AppWidgetManager.getInstance(context)

                for(appWidgetId in appWidgetIds?: appWidgetManager_default.getAppWidgetIds(ComponentName(context, Widget::class.java)))
                {
                    val data = get_data_from_id(context, appWidgetId)
                    if(data != null)
                        build_view(context, appWidgetManager_default, appWidgetId, data)
                }
            }

            // schedule another update

            val time_interval = PreferenceManager.getDefaultSharedPreferences(context).getString(context.resources.getString(R.string.pref_widget_refresh_key), context.resources.getString(R.string.pref_widget_refresh_default))!!.toInt()
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + time_interval, build_alarm_intent(context))
        }

        private fun build_alarm_intent(context:Context):PendingIntent
        {
            val intent = Intent(context, Widget::class.java)
            intent.action = ACTION_UPDATE_TIME

            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        }

        private fun build_view(context: Context, appWidgetManager: AppWidgetManager, widget_id: Int, data: View_data)
        {
            val views = RemoteViews(context.packageName, if(data.separate_time) R.layout.progress_bar_widget else R.layout.single_progress_bar_widget)

            views.setTextViewText(R.id.title, data.title)

            if(data.show_time_text)
            {
                views.setTextViewText(R.id.time_text, data.time_text_disp.get())
                views.setViewVisibility(R.id.time_text, View.VISIBLE)
            }
            else
            {
                views.setViewVisibility(R.id.time_text, View.GONE)
            }

            if(data.separate_time)
            {
                if(data.show_start)
                {
                    views.setTextViewText(R.id.start_time_date, data.start_date_disp.get())
                    views.setTextViewText(R.id.start_time_time, data.start_time_disp.get())

                    views.setViewVisibility(R.id.start_time_box, View.VISIBLE)
                }
                else
                {
                    views.setViewVisibility(R.id.start_time_box, View.GONE)
                }
                if(data.show_end)
                {
                    views.setTextViewText(R.id.end_time_date, data.end_date_disp.get())
                    views.setTextViewText(R.id.end_time_time, data.end_time_disp.get())

                    views.setViewVisibility(R.id.end_time_box, View.VISIBLE)
                }
                else
                {
                    views.setViewVisibility(R.id.end_time_box, View.GONE)
                }
                if(data.show_progress)
                {
                    views.setTextViewText(R.id.percentage, data.percentage_disp.get())
                    views.setInt(R.id.progress_bar, "setProgress", data.progress_disp.get())

                    views.setViewVisibility(R.id.percentage_box, View.VISIBLE)
                }
                else
                {
                    views.setViewVisibility(R.id.percentage_box, View.GONE)
                }

                if(data.show_start || data.show_end || data.show_progress)
                    views.setViewVisibility(R.id.center_box, View.VISIBLE)
                else
                    views.setViewVisibility(R.id.center_box, View.GONE)
            }
            else
            {
                if(data.show_end)
                {
                    views.setTextViewText(R.id.date, data.end_date_disp.get())
                    views.setTextViewText(R.id.time, data.end_time_disp.get())

                    views.setViewVisibility(R.id.center_box, View.VISIBLE)
                }
                else
                {
                    views.setViewVisibility(R.id.center_box, View.GONE)
                }
            }

            val edit_intent = Intent(context, Settings::class.java)
            edit_intent.action = AppWidgetManager.ACTION_APPWIDGET_CONFIGURE
            edit_intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            edit_intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget_id)
            views.setOnClickPendingIntent(R.id.background, PendingIntent.getActivity(context, 0, edit_intent, PendingIntent.FLAG_CANCEL_CURRENT))

            appWidgetManager.updateAppWidget(widget_id, views)
        }
    }
}
