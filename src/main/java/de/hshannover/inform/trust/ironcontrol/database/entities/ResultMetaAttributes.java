package de.hshannover.inform.trust.ironcontrol.database.entities;


/**
 * Database table
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class ResultMetaAttributes extends AbstractEntity{

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

	@Override
	protected String getTable() {
		return TABLE;
	}

	@Override
	protected String getDatabaseCreate() {
		return DATABASE_CREATE;
	}
}
