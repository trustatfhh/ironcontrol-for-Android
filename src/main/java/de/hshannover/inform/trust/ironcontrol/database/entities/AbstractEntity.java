package de.hshannover.inform.trust.ironcontrol.database.entities;

import android.database.sqlite.SQLiteDatabase;
import de.hshannover.inform.trust.ironcontrol.logger.Level;
import de.hshannover.inform.trust.ironcontrol.logger.Logger;
import de.hshannover.inform.trust.ironcontrol.logger.LoggerFactory;

/**
 * @author Marcel Reichenbach
 * @version 1.0
 */

public abstract class AbstractEntity {

	private static final Logger logger = LoggerFactory.getLogger(AbstractEntity.class);

	public void onCreate(SQLiteDatabase database) {
		String tabel = getTable();
		String createString = getDatabaseCreate();

		logger.log(Level.DEBUG, "Create database(Tabel:"+tabel+") ...");
		database.execSQL(createString);
		logger.log(Level.DEBUG, "... Create(Tabel:"+tabel+")");
	}

	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		String tabel = getTable();
		logger.log(Level.DEBUG, "Upgrading database(Tabel:"+tabel+") from version "+ oldVersion + " to " + newVersion + ", destroy all old data ...");
		database.execSQL("DROP TABLE IF EXISTS " + tabel);
		onCreate(database);
		logger.log(Level.DEBUG, "... Upgrading(Tabel:"+tabel+") OK");
	}

	protected abstract String getTable();
	protected abstract String getDatabaseCreate();

}
