package de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities;


/**
 * Database table
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class MetaAttributes extends AbstractEntity{

	// Database table
	public static final String TABLE = "meta_attributes";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_METADATA_ID = "a_metadata_id";
	public static final String COLUMN_NAME = "a_name";

	// Database creation SQL statement
	// TODO mal autoincrement noch bei den primary keys test
	private static final String DATABASE_CREATE = "create table "
			+ TABLE + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_METADATA_ID + " integer not null, "
			+ COLUMN_NAME + " text not null, "
			+ "FOREIGN KEY(" + COLUMN_METADATA_ID +") REFERENCES " + VendorMetadata.TABLE + "(" +VendorMetadata.COLUMN_ID + ") "
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
