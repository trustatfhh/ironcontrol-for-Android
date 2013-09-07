package de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ResultMetadata {
	/**
	 * Class for Publications Database
	 * 
	 * @author Marcel Reichenbach
	 * @version %I%, %G%
	 * @since 0.1
	 */

	// Database table
	public static final String TABLE = "result_metadata";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_RESULT_ITEM_ID = "result_item_id";
	public static final String COLUMN_LOCAL_NAME = "local_name";
	public static final String COLUMN_NAMESPACEURI = "namespaceuri";
	public static final String COLUMN_PREFIX = "prefix";
	public static final String COLUMN_CARDINALITY = "cardinality";
	public static final String COLUMN_PUBLISHERID = "publisherid";
	public static final String COLUMN_TIMESTAMP = "timestamp";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_RESULT_ITEM_ID + " integer not null, "
			+ COLUMN_LOCAL_NAME + " text not null, "
			+ COLUMN_NAMESPACEURI + " text not null, "
			+ COLUMN_PREFIX + " text not null, "
			+ COLUMN_CARDINALITY + " text not null, "
			+ COLUMN_PUBLISHERID + " text not null, "
			+ COLUMN_TIMESTAMP + " text not null, "
			+ "FOREIGN KEY(" + COLUMN_RESULT_ITEM_ID +") REFERENCES " + ResultItems.TABLE + "(" +ResultItems.COLUMN_ID + ") "
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
