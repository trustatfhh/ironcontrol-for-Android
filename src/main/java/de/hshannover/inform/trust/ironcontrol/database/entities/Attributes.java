package de.hshannover.inform.trust.ironcontrol.database.entities;


/**
 * Database table
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class Attributes extends AbstractEntity{

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

	@Override
	protected String getTable() {
		return TABLE;
	}

	@Override
	protected String getDatabaseCreate() {
		return DATABASE_CREATE;
	}
}
