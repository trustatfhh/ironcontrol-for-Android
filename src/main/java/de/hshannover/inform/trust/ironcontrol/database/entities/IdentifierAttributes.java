package de.hshannover.inform.trust.ironcontrol.database.entities;


/**
 * Database table
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class IdentifierAttributes extends AbstractEntity{

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

	@Override
	protected String getTable() {
		return TABLE;
	}

	@Override
	protected String getDatabaseCreate() {
		return DATABASE_CREATE;
	}
}
