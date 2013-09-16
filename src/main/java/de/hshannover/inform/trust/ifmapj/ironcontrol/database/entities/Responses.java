package de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities;


/**
 * Database table
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class Responses extends AbstractEntity{

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

	@Override
	protected String getTable() {
		return TABLE;
	}

	@Override
	protected String getDatabaseCreate() {
		return DATABASE_CREATE;
	}
}
