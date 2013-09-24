package de.hshannover.inform.trust.ironcontrol.database.entities;


/**
 * Database table
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class ResultItems extends AbstractEntity{

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

	@Override
	protected String getTable() {
		return TABLE;
	}

	@Override
	protected String getDatabaseCreate() {
		return DATABASE_CREATE;
	}
}
