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

public class Attributes extends AbstractEntity{

	// Database table
	public static final String TABLE = "attributes";
	public static final String COLUMN_ID = "a_id";
	public static final String COLUMN_REQUEST_ID = "a_request_id";
	public static final String COLUMN_NAME = "a_name";
	public static final String COLUMN_VALUE = "a_value";
	public static final String COLUMN_TYPE = "a_type";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_REQUEST_ID + " integer not null, "
			+ COLUMN_NAME + " text not null, "
			+ COLUMN_VALUE + " text not null, "
			+ COLUMN_TYPE + " text, "
			+ "FOREIGN KEY(" + COLUMN_REQUEST_ID +") REFERENCES " + Requests.TABLE + "(" +Requests.COLUMN_ID + ") "
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
