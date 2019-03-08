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

package org.mattvchandler.progressbars.db

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// helper functions for getting data from a cursor
fun Cursor.get_nullable_string(column_name: String): String?
{
    val column_index = this.getColumnIndexOrThrow(column_name)
    return if(this.isNull(column_index)) null else this.getString(column_index)
}
fun Cursor.get_nullable_long(column_name: String): Long?
{
    val column_index = this.getColumnIndexOrThrow(column_name)
    return if(this.isNull(column_index)) null else this.getLong(column_index)
}
fun Cursor.get_nullable_int(column_name: String): Int?
{
    val column_index = this.getColumnIndexOrThrow(column_name)
    return if(this.isNull(column_index)) null else this.getInt(column_index)
}
fun Cursor.get_nullable_bool(column_name: String): Boolean?
{
    val column_index = this.getColumnIndexOrThrow(column_name)
    return if(this.isNull(column_index)) null else this.getInt(column_index) != 0
}

// DB container
class DB(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION)
{
    companion object
    {
        private const val DB_VERSION = 4
        private const val DB_NAME = "progress_bar_db"
    }
    // build the tables / whatever else when new
    override fun onCreate(sqLiteDatabase: SQLiteDatabase)
    {
        sqLiteDatabase.execSQL(Progress_bars_table.CREATE_TABLE)
        sqLiteDatabase.execSQL(Undo.CREATE_TABLE)
    }

    // if DB schema changes, put logic to migrate data here
    override fun onUpgrade(db: SQLiteDatabase, old_version: Int, new_version: Int)
    {
        if(new_version != DB_VERSION)
            throw IllegalStateException("DB version mismatch")

        Progress_bars_table.upgrade(db, old_version)
        Undo.upgrade(db, old_version)
    }
}
