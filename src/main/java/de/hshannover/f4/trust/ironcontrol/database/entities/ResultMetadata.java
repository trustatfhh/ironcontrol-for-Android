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
 * This file is part of ironcontrol for android, version 1.0.2, implemented by the Trust@HsH research group at the Hochschule Hannover.
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
