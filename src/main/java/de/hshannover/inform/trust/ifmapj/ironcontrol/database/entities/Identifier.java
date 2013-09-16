package de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities;


/**
 * Database table
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class Identifier extends AbstractEntity{

	// Database table
	public static final String TABLE = "identifier";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TYPE = "type";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_TYPE + " text not null "
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
