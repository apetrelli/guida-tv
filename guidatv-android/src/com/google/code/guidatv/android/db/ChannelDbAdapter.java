/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.code.guidatv.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class ChannelDbAdapter {

    public static final String KEY_CODE = "code";
    public static final String KEY_NAME = "name";
    public static final String KEY_ORDER = "ord";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "ChannelDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
            "create table channel (_id integer primary key autoincrement, "
                    + "code text not null, name text not null, ord integer not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "channel";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
            addChannel(db, "RaiUno", "Rai Uno", 1);
            addChannel(db, "RaiDue", "Rai Due", 2);
            addChannel(db, "RaiTre", "Rai Tre", 3);
            addChannel(db, "C5", "Canale 5", 4);
            addChannel(db, "I1", "Italia 1", 5);
            addChannel(db, "R4", "Rete 4", 6);
            addChannel(db, "La7", "La 7", 7);
        }

		private void addChannel(SQLiteDatabase db, String code, String name,
				int index) {
			ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_CODE, code);
            initialValues.put(KEY_NAME, name);
            initialValues.put(KEY_ORDER, index);
            db.insert(DATABASE_TABLE, null, initialValues);
		}

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS channel");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public ChannelDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public ChannelDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param code the title of the note
     * @param name the body of the note
     * @return rowId or -1 if failed
     */
    public long addChannel(String code, String name) {
    	Cursor cursor = mDb.rawQuery("select max(ord) from channel", null);
    	int max = 1;
    	if (cursor.moveToFirst()) {
    		max = cursor.getInt(1);
    	}
    	cursor.close();
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CODE, code);
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_ORDER, max + 1);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteChannel(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllChannels() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CODE,
                KEY_NAME, KEY_ORDER}, null, null, null, null, KEY_ORDER);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchChannel(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                        KEY_CODE, KEY_NAME, KEY_ORDER}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchChannelByCode(String code) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                        KEY_CODE, KEY_NAME, KEY_ORDER}, KEY_CODE + "=" + code, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateChannel(long rowId, int order) {
        ContentValues args = new ContentValues();
        args.put(KEY_ORDER, order);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
