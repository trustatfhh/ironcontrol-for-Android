/*
 * #%L
 * =====================================================
 *   _____                _     ____  _   _       _   _
 *  |_   _|_ __ _   _ ___| |_  / __ \| | | | ___ | | | |
 *    | | | '__| | | / __| __|/ / _` | |_| |/ __|| |_| |
 *    | | | |  | |_| \__ \ |_| | (_| |  _  |\__ \|  _  |
 *    |_| |_|   \__,_|___/\__|\ \__,_|_| |_||___/|_| |_|
 *                             \____/
 * 
 * =====================================================
 * 
 * Hochschule Hannover
 * (University of Applied Sciences and Arts, Hannover)
 * Faculty IV, Dept. of Computer Science
 * Ricklinger Stadtweg 118, 30459 Hannover, Germany
 * 
 * Email: trust@f4-i.fh-hannover.de
 * Website: http://trust.f4.hs-hannover.de/
 * 
 * This file is part of ironcontrol for android, version 1.0.1, implemented by the Trust@HsH research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2013 - 2015 Trust@HsH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package de.hshannover.f4.trust.ironcontrol.database.entities;

import android.database.sqlite.SQLiteDatabase;
import de.hshannover.f4.trust.ironcontrol.logger.Level;
import de.hshannover.f4.trust.ironcontrol.logger.Logger;
import de.hshannover.f4.trust.ironcontrol.logger.LoggerFactory;

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
