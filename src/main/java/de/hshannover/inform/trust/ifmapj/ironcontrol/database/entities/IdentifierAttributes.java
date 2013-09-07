package de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class IdentifierAttributes {
	/**
	 * Class for Publications Database
	 * 
	 * @author Marcel Reichenbach
	 * @version %I%, %G%
	 * @since 0.1
	 */

	// Database table
	public static final String TABLE = "identifier_attributes";
	public static final String COLUMN_ID = "a_id";
	public static final String COLUMN_IDENTIFIER_ID = "a_identifier_id";
	public static final String COLUMN_NAME = "a_name";

	// Database creation SQL statement
	// TODO mal autoincrement noch bei den primary keys test
	private static final String DATABASE_CREATE = "create table "
			+ TABLE + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_IDENTIFIER_ID + " integer not null, "
			+ COLUMN_NAME + " text not null, "
			+ "FOREIGN KEY(" + COLUMN_IDENTIFIER_ID +") REFERENCES " + Identifier.TABLE + "(" +Identifier.COLUMN_ID + ") "
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
