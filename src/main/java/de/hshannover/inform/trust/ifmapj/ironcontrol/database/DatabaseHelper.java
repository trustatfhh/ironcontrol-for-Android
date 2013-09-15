package de.hshannover.inform.trust.ifmapj.ironcontrol.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.Attributes;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.Connections;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.Identifier;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.IdentifierAttributes;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.MetaAttributes;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.Requests;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.Responses;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.ResultItems;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.ResultMetaAttributes;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.ResultMetadata;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.VendorMetadata;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Level;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Logger;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LoggerFactory;

/**
 * Class for DB-Management
 * Create DB-Tables
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseHelper.class);

	private static final String DATABASE_NAME = "ironControl.db";
	private static final int DATABASE_VERSION = 1;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		logger.log(Level.DEBUG, "onCreate SQLiteDatabase .....");
		Connections.onCreate(db);
		VendorMetadata.onCreate(db);
		MetaAttributes.onCreate(db);
		Identifier.onCreate(db);
		IdentifierAttributes.onCreate(db);
		Requests.onCreate(db);
		Attributes.onCreate(db);
		Responses.onCreate(db);
		ResultItems.onCreate(db);
		ResultMetadata.onCreate(db);
		ResultMetaAttributes.onCreate(db);
		logger.log(Level.DEBUG, "..... OK");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		logger.log(Level.DEBUG, "onUpgrade SQLiteDatabase .....");
		ResultMetaAttributes.onUpgrade(db, 1, DATABASE_VERSION);
		ResultMetadata.onUpgrade(db, 1, DATABASE_VERSION);
		ResultItems.onUpgrade(db, 1, DATABASE_VERSION);
		Responses.onUpgrade(db, 1, DATABASE_VERSION);
		Attributes.onUpgrade(db, 1, DATABASE_VERSION);
		Requests.onUpgrade(db, 1, DATABASE_VERSION);
		IdentifierAttributes.onUpgrade(db, 1, DATABASE_VERSION);
		Identifier.onUpgrade(db, 1, DATABASE_VERSION);
		MetaAttributes.onUpgrade(db, 1, DATABASE_VERSION);
		VendorMetadata.onUpgrade(db, 1, DATABASE_VERSION);
		Connections.onUpgrade(db, 1, DATABASE_VERSION);
		logger.log(Level.DEBUG, "..... OK");
	}

}
