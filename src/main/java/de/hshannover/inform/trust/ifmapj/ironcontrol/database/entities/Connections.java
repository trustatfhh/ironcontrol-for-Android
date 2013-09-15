package de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Database table
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class Connections {

	// Database table
	public static final String TABLE = "connections";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_URL = "user_url";
	public static final String COLUMN_ADDRESS = "address";
	public static final String COLUMN_PORT = "port";
	public static final String COLUMN_USER = "user";
	public static final String COLUMN_PASS = "pass";
	public static final String COLUMN_DEFAULT = "columndefault";
	public static final String COLUMN_ACTIVE = "active";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_NAME + " text not null, "
			+ COLUMN_URL + " text not null, "
			+ COLUMN_ADDRESS + " text not null, "
			+ COLUMN_PORT + " integer not null, "
			+ COLUMN_USER + " text not null, "
			+ COLUMN_PASS + " text not null, "
			+ COLUMN_DEFAULT + " integer, "
			+ COLUMN_ACTIVE + " integer "
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
