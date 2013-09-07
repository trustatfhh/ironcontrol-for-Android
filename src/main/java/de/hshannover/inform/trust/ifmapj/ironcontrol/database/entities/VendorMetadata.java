package de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class VendorMetadata {
	/**
	 * Class for Publications Database
	 * 
	 * @author Marcel Reichenbach
	 * @version %I%, %G%
	 * @since 0.1
	 */

	// Database table
	public static final String TABLE = "vendor_metadata";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "v_name";
	public static final String COLUMN_CARDINALITY = "v_cardinality";
	public static final String COLUMN_URI = "v_uri";
	public static final String COLUMN_PREFIX = "v_prefix";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_NAME + " text not null, "
			+ COLUMN_CARDINALITY + " text not null, "
			+ COLUMN_URI + " text not null, "
			+ COLUMN_PREFIX + " text not null "
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
