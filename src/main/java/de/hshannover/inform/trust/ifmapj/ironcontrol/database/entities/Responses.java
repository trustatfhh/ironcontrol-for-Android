package de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Database table
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class Responses {

	// Database table
	public static final String TABLE = "responses";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_REQUEST_ID = "r_request_id";
	public static final String COLUMN_DATE = "r_date";
	public static final String COLUMN_TIME = "r_time";
	public static final String COLUMN_NEW = "r_new";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_REQUEST_ID + " integer not null, "
			+ COLUMN_DATE + " text not null, "
			+ COLUMN_TIME + " text not null, "
			+ COLUMN_NEW + " integer, "
			+ "FOREIGN KEY(" + COLUMN_REQUEST_ID +") REFERENCES " + Requests.TABLE + "(" +Requests.COLUMN_ID + ") "
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.i("de.hshannover.inform.trust.ifmapj.ironcontrol", "Upgrading database(Tabel:"+TABLE+") from version "
				+ oldVersion + " to " + newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE);
		onCreate(database);
	}
}
