package de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities;


/**
 * Database table
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class VendorMetadata extends AbstractEntity{

	// Database table
	public static final String TABLE = "vendor_metadata";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "v_name";
	public static final String COLUMN_CARDINALITY = "v_cardinality";
	public static final String COLUMN_URI = "v_uri";
	public static final String COLUMN_PREFIX = "v_prefix";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_NAME + " text not null, "
			+ COLUMN_CARDINALITY + " text not null, "
			+ COLUMN_URI + " text not null, "
			+ COLUMN_PREFIX + " text not null "
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
