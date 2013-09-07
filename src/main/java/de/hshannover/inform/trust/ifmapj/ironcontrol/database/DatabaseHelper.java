package de.hshannover.inform.trust.ifmapj.ironcontrol.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
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

public class DatabaseHelper extends SQLiteOpenHelper {
	/**
	 * Class for connection management
	 * @author Marcel Reichenbach
	 * @version %I%, %G%
	 * @since 0.1
	 */

	private static final String DATABASE_NAME = "ironControl.db";
	private static final int DATABASE_VERSION = 1;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "onCreate SQLiteDatabase .....");
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
		Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "..... OK");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "onUpgrade SQLiteDatabase .....");
		ResultMetaAttributes.onCreate(db);
		ResultMetadata.onCreate(db);
		ResultItems.onCreate(db);
		Responses.onCreate(db);
		Attributes.onCreate(db);
		Requests.onCreate(db);
		IdentifierAttributes.onCreate(db);
		Identifier.onCreate(db);
		MetaAttributes.onCreate(db);
		VendorMetadata.onCreate(db);
		Connections.onCreate(db);
		Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "..... OK");
	}

}
