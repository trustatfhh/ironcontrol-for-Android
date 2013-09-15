package de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Database table
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class ResultMetaAttributes {

	// Database table
	public static final String TABLE = "result_meta_attributes";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_RESULT_METADATA_ID = "result_metadata_id";
	public static final String COLUMN_NODE_NAME = "node_name";
	public static final String COLUMN_NODE_VALUE = "node_value";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_RESULT_METADATA_ID + " integer not null, "
			+ COLUMN_NODE_NAME + " text not null, "
			+ COLUMN_NODE_VALUE + " text not null, "
			+ "FOREIGN KEY(" + COLUMN_RESULT_METADATA_ID +") REFERENCES " + ResultMetadata.TABLE + "(" +ResultMetadata.COLUMN_ID + ") "
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
