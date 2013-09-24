package de.hshannover.inform.trust.ironcontrol.database.entities;


/**
 * Database table
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class ResultMetadata extends AbstractEntity{

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

	@Override
	protected String getTable() {
		return TABLE;
	}

	@Override
	protected String getDatabaseCreate() {
		return DATABASE_CREATE;
	}
}
