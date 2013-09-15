package de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Database table
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class ResultItems {

	// Database table
	public static final String TABLE = "result_items";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_RESPONSE_ID = "response_id";
	public static final String COLUMN_IDENTIFIER1 = "identifier1";
	public static final String COLUMN_IDENTIFIER2 = "identifier2";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_RESPONSE_ID + " integer not null, "
			+ COLUMN_IDENTIFIER1 + " text not null, "
			+ COLUMN_IDENTIFIER2 + " text, "
			+ "FOREIGN KEY(" + COLUMN_RESPONSE_ID +") REFERENCES " + Responses.TABLE + "(" +Responses.COLUMN_ID + ") "
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
