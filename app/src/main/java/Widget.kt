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
import android.app.WallpaperManager
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import org.mattvchandler.progressbars.db.DB
import org.mattvchandler.progressbars.db.Data
import org.mattvchandler.progressbars.db.Progress_bars_table
import org.mattvchandler.progressbars.db.get_nullable_int
import org.mattvchandler.progressbars.list.View_data
import org.mattvchandler.progressbars.settings.Settings
import kotlin.math.sqrt

// TODO: cleanup debug stmts
// TODO: not always being updated (possibly not being initialized on startup)
// TODO: often can't click
// TODO: doesn't work in  power save mode
// TODO: repeats not always honored (especially on first repeat)

class Widget: AppWidgetProvider()
{
    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?)
    {
        if(context != null)
            update(context, appWidgetManager, appWidgetIds)
    }

    override fun onAppWidgetOptionsChanged(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int, newOptions: Bundle?)
    {
        if(context != null)
            update(context, appWidgetManager, intArrayOf(appWidgetId))
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
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
                Log.d("Widget::onDeleted", "deleting: $widget_id")
                val arg_array = arrayOf(widget_id.toString())

                val cursor = db.rawQuery(Progress_bars_table.SELECT_WIDGET, arg_array)
                if(cursor.count > 0)
                {
                    cursor.moveToFirst()
                    val data = Data(cursor)
                    Log.d("Widget::onDeleted", "deleting: $widget_id from DB rowid: ${data.rowid}")

                    data.unregister_alarms(context)
                    db.delete(Progress_bars_table.TABLE_NAME, "${Progress_bars_table.WIDGET_ID_COL} = ?", arg_array)
                }
                cursor.close()
            }

            db.close()
        }
    }

    override fun onReceive(context: Context?, intent: Intent?)
    {
        Log.v("Widget::onReceive", "${intent?.action}")
        when(intent?.action)
        {
            Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIMEZONE_CHANGED, ACTION_UPDATE_TIME -> if(context != null) update(context, null, null)
            else -> super.onReceive(context, intent)
        }
    }

    companion object
    {
        private const val ACTION_UPDATE_TIME = "org.mattvchandler.progressbars.ACTION_UPDATE_TIME"

        fun create_or_update_data(context: Context, widget_id: Int, data: Data)
        {
            Log.d("Widget::create_or_up…", "widget_id: ${widget_id}, data.id: ${data.id}, data.title: ${data.title}, data.rowid: ${data.rowid}")
            val db = DB(context).writableDatabase
            val cursor = db.rawQuery(Progress_bars_table.SELECT_WIDGET, arrayOf(widget_id.toString()))
            if(cursor.count == 0)
            {
                Log.d("Widget::create", "$widget_id")

                data.register_alarms(context)
                data.insert(db)
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

        fun create_data_from_id(context: Context, widget_id: Int): Data
        {
            // TODO: return some indication that we've adopted an orphan (will need to move the DB update), and show a dialog saying that there is an orphan to adopt
            var data: Data? = null
            val db = DB(context).writableDatabase
            val cursor = db.rawQuery(Progress_bars_table.SELECT_WIDGET, arrayOf(widget_id.toString()))

            if(cursor.count == 0)
            {
                // check to see if there is an orphaned widget ID we can adopt
                val valid_widget_ids = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context.packageName, Widget::class.java.name))

                val orphan_cursor = db.rawQuery(Progress_bars_table.SELECT_ALL_WIDGETS, null)
                orphan_cursor.moveToFirst()
                for(i in 0 until orphan_cursor.count)
                {
                    val orphan_widget_id = orphan_cursor.get_nullable_int(Progress_bars_table.WIDGET_ID_COL)!!

                    if(orphan_widget_id !in valid_widget_ids)
                    {
                        Log.d("Widget::create_data_fr…", "Adopting orphaned id: $orphan_widget_id to $widget_id")

                        val adopted_cursor = db.rawQuery(Progress_bars_table.SELECT_WIDGET, arrayOf(widget_id.toString()))
                        adopted_cursor.moveToFirst()
                        data = Data(adopted_cursor)
                        data.widget_id = widget_id
                        adopted_cursor.close()

                        break
                    }
                    orphan_cursor.moveToNext()
                }
                orphan_cursor.close()
                // didn't find an orphan to adopt, so we'll leave data null to make a new one
            }
            else
            {
                cursor.moveToFirst()
                data = Data(cursor)
                Log.d("Widget::create_data_fr…", "widget_id: $widget_id, data.id: ${data.id}, data.rowid: ${data.rowid}")
            }

            cursor.close()
            db.close()

            if(data == null)
            {
                data = Data(context)
                data.widget_id = widget_id
                Log.d("Widget::create_data_fr…", "created new data, widget_id: $widget_id, data.id: ${data.id}, data.rowid: ${data.rowid}")
            }

            return data
        }

        private fun get_data_from_id(context: Context, widget_id: Int): Data?
        {

            var data: Data? = null
            val db = DB(context).writableDatabase
            val cursor = db.rawQuery(Progress_bars_table.SELECT_WIDGET, arrayOf(widget_id.toString()))

            if(cursor.count != 0)
            {
                cursor.moveToFirst()
                data = Data(cursor)
                Log.d("Widget::get_data_from_…", "widget_id: $widget_id, data.id: ${data.id}, data.rowid: ${data.rowid}")
            }

            cursor.close()
            db.close()

            return data
        }

        private fun update(context: Context, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?)
        {
            Log.d("Widget::update", "$appWidgetIds")
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val screen_on = if(Build.VERSION.SDK_INT >= 20) pm.isInteractive else true
            if(screen_on)
            {
                val appWidgetManager_default = appWidgetManager ?: AppWidgetManager.getInstance(context)

                for(appWidgetId in appWidgetIds?: appWidgetManager_default.getAppWidgetIds(ComponentName(context, Widget::class.java)))
                {
                    Log.d("Widget::update", "for loop: $appWidgetId")
                    val data = get_data_from_id(context, appWidgetId)
                    if(data != null)
                        build_view(context, appWidgetManager_default, appWidgetId, View_data(context, data))
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
            data.reinit(context)

            val views = RemoteViews(context.packageName, if(data.separate_time) R.layout.progress_bar_widget else R.layout.single_progress_bar_widget)

            val text_color = when(PreferenceManager.getDefaultSharedPreferences(context).getString(context.resources.getString(R.string.pref_widget_text_color_key),
                                                                                                   context.resources.getString(R.string.pref_widget_text_color_default)))
            {
                "white" -> Color.WHITE
                "black" -> Color.BLACK
                "auto" ->
                {
                    if(Build.VERSION.SDK_INT >= 27)
                    {
                        val lum = WallpaperManager.getInstance(context)?.getWallpaperColors(WallpaperManager.FLAG_SYSTEM)?.primaryColor?.luminance() ?: 0.0f
                        if(lum > sqrt(0.0525f) - 0.05f)
                            Color.BLACK
                        else
                            Color.WHITE
                    }
                    else
                        Color.WHITE
                }
                else -> Color.WHITE
            }

            val has_bg = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.resources.getString(R.string.pref_widget_bg_key),
                                                                                           context.resources.getBoolean(R.bool.pref_widget_bg_default))
            views.setInt(R.id.background, "setBackgroundColor",
                    if(has_bg)
                        ContextCompat.getColor(context, R.color.widget_bg_color)
                    else
                        Color.TRANSPARENT)


            views.setTextViewText(R.id.title, data.title)
            views.setTextColor(R.id.title, text_color)

            if(data.show_time_text)
            {
                views.setTextViewText(R.id.time_text, data.time_text_disp.get())
                views.setTextColor(R.id.time_text, text_color)
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

                    views.setTextColor(R.id.start_time_label, text_color)
                    views.setTextColor(R.id.start_time_date, text_color)
                    views.setTextColor(R.id.start_time_time, text_color)

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

                    views.setTextColor(R.id.end_time_label, text_color)
                    views.setTextColor(R.id.end_time_date, text_color)
                    views.setTextColor(R.id.end_time_time, text_color)

                    views.setViewVisibility(R.id.end_time_box, View.VISIBLE)
                }
                else
                {
                    views.setViewVisibility(R.id.end_time_box, View.GONE)
                }
                if(data.show_progress)
                {
                    views.setTextViewText(R.id.percentage, data.percentage_disp.get())
                    views.setTextColor(R.id.percentage, text_color)
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

                    views.setTextColor(R.id.date, text_color)
                    views.setTextColor(R.id.time, text_color)

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
