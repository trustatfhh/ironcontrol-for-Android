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
package de.hshannover.f4.trust.ironcontrol.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.hshannover.f4.trust.ironcontrol.database.entities.Attributes;
import de.hshannover.f4.trust.ironcontrol.database.entities.Connections;
import de.hshannover.f4.trust.ironcontrol.database.entities.Identifier;
import de.hshannover.f4.trust.ironcontrol.database.entities.IdentifierAttributes;
import de.hshannover.f4.trust.ironcontrol.database.entities.MetaAttributes;
import de.hshannover.f4.trust.ironcontrol.database.entities.Requests;
import de.hshannover.f4.trust.ironcontrol.database.entities.Responses;
import de.hshannover.f4.trust.ironcontrol.database.entities.ResultItems;
import de.hshannover.f4.trust.ironcontrol.database.entities.ResultMetaAttributes;
import de.hshannover.f4.trust.ironcontrol.database.entities.ResultMetadata;
import de.hshannover.f4.trust.ironcontrol.database.entities.VendorMetadata;
import de.hshannover.f4.trust.ironcontrol.logger.Level;
import de.hshannover.f4.trust.ironcontrol.logger.Logger;
import de.hshannover.f4.trust.ironcontrol.logger.LoggerFactory;

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
		new Connections().onCreate(db);
		new VendorMetadata().onCreate(db);
		new MetaAttributes().onCreate(db);
		new Identifier().onCreate(db);
		new IdentifierAttributes().onCreate(db);
		new Requests().onCreate(db);
		new Attributes().onCreate(db);
		new Responses().onCreate(db);
		new ResultItems().onCreate(db);
		new ResultMetadata().onCreate(db);
		new ResultMetaAttributes().onCreate(db);
		logger.log(Level.DEBUG, "..... OK");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		logger.log(Level.DEBUG, "onUpgrade SQLiteDatabase .....");
		new ResultMetaAttributes().onUpgrade(db, 1, DATABASE_VERSION);
		new ResultMetadata().onUpgrade(db, 1, DATABASE_VERSION);
		new ResultItems().onUpgrade(db, 1, DATABASE_VERSION);
		new Responses().onUpgrade(db, 1, DATABASE_VERSION);
		new Attributes().onUpgrade(db, 1, DATABASE_VERSION);
		new Requests().onUpgrade(db, 1, DATABASE_VERSION);
		new IdentifierAttributes().onUpgrade(db, 1, DATABASE_VERSION);
		new Identifier().onUpgrade(db, 1, DATABASE_VERSION);
		new MetaAttributes().onUpgrade(db, 1, DATABASE_VERSION);
		new VendorMetadata().onUpgrade(db, 1, DATABASE_VERSION);
		new Connections().onUpgrade(db, 1, DATABASE_VERSION);
		logger.log(Level.DEBUG, "..... OK");
	}

}
