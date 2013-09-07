package de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Attributes {
	/**
	 * Class for Publications Database
	 * 
	 * @author Marcel Reichenbach
	 * @version %I%, %G%
	 * @since 0.1
	 */

	// Database table
	public static final String TABLE = "attributes";
	public static final String COLUMN_ID = "a_id";
	public static final String COLUMN_REQUEST_ID = "a_request_id";
	public static final String COLUMN_NAME = "a_name";
	public static final String COLUMN_VALUE = "a_value";
	public static final String COLUMN_TYPE = "a_type";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_REQUEST_ID + " integer not null, "
			+ COLUMN_NAME + " text not null, "
			+ COLUMN_VALUE + " text not null, "
			+ COLUMN_TYPE + " text, "
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
