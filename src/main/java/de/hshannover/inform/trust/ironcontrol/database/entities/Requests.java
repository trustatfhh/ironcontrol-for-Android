package de.hshannover.inform.trust.ironcontrol.database.entities;


/**
 * Database table
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class Requests extends AbstractEntity{

	// Database table
	public static final String TABLE = "requests";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_METADATA = "metadata";
	public static final String COLUMN_IDENTIFIER1 = "identifier1";
	public static final String COLUMN_IDENTIFIER1_Value = "identifier1value";
	public static final String COLUMN_IDENTIFIER2 = "identifier2";
	public static final String COLUMN_IDENTIFIER2_Value = "identifier2value";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_MATCH_LINKS = "match_links";
	public static final String COLUMN_MAX_DEPTH = "max_depth";
	public static final String COLUMN_MAX_SITZ = "max_size";
	public static final String COLUMN_RESULT_FILTER = "result_filter";
	public static final String COLUMN_ACTIVE = "active";
	public static final String COLUMN_LIFE_TIME = "life_time";
	public static final String COLUMN_TERMINAL_IDENTIFIER_TYPES = "terminalidentifier";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_METADATA + " text, "
			+ COLUMN_IDENTIFIER1 + " text not null, "
			+ COLUMN_IDENTIFIER1_Value + " text not null, "
			+ COLUMN_IDENTIFIER2 + " text, "
			+ COLUMN_IDENTIFIER2_Value + " text, "
			+ COLUMN_NAME + " text not null, "
			+ COLUMN_TYPE + " text not null, "
			+ COLUMN_MATCH_LINKS + " text, "
			+ COLUMN_MAX_DEPTH + " integer, "
			+ COLUMN_MAX_SITZ + " integer, "
			+ COLUMN_RESULT_FILTER + " text, "
			+ COLUMN_ACTIVE + " integer, "
			+ COLUMN_LIFE_TIME + " integer, "
			+ COLUMN_TERMINAL_IDENTIFIER_TYPES + " text "
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
