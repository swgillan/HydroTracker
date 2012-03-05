package com.spts.hydrotracker.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DBAdapter
{
	private static final String TAG = "DBAdapter";
	private static final String DATABASE_NAME = "hydrotracker_db";
	private static final String READING_TABLE = "readings";
	private static final int DATABASE_VERSION = 1;
	private static final String READING_TABLE_DEFINITION = "CREATE TABLE IF NOT EXISTS "
			+ READING_TABLE
			+ " (reading_id INTEGER PRIMARY KEY AUTOINCREMENT, reading INTEGER NOT NULL DEFAULT '0', "
			+ " reading_date INTEGER NOT NULL DEFAULT '0');";
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	private SQLiteStatement insertReadingStatement = null;

	public DBAdapter(Context ctx)
	{
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(READING_TABLE_DEFINITION);
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion,
				int newVersion)
		{
			Log.w(TAG, "Upgrading database from version " + oldVersion
					+ " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + READING_TABLE);
			onCreate(db);
		}
	}

	//---opens the database---
	public DBAdapter open() throws SQLException
	{
		db = DBHelper.getWritableDatabase();
		return this;
	}
	//---closes the database---
	public void close()
	{
		insertReadingStatement.close();
		insertReadingStatement = null;
		DBHelper.close();
	}
	//---insert a title into the database---
	public void insertReading(int reading, long readingDate)
	{
		if (insertReadingStatement == null) {
			insertReadingStatement = db.compileStatement("INSERT  OR REPLACE INTO " + READING_TABLE
					+ " (reading, reading_date) VALUES (?,?)");
		}
		insertReadingStatement.bindLong(1, reading);
		insertReadingStatement.bindLong(2, readingDate);
		insertReadingStatement.execute();
	}
	//---deletes a particular title---
	public boolean deleteReading(long rowId)
	{
		return db.delete(READING_TABLE, "reading_id = " + rowId, null) > 0;
	}
}
