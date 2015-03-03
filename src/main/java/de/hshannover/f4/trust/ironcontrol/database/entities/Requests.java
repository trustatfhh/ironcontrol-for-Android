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


/**
 * Database table
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class Requests extends AbstractEntity{

	// Database table
	public static final String TABLE = "requests";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_METADATA = "metadata";
	public static final String COLUMN_IDENTIFIER1 = "identifier1";
	public static final String COLUMN_IDENTIFIER1_Value = "identifier1value";
	public static final String COLUMN_IDENTIFIER2 = "identifier2";
	public static final String COLUMN_IDENTIFIER2_Value = "identifier2value";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_MATCH_LINKS = "match_links";
	public static final String COLUMN_MAX_DEPTH = "max_depth";
	public static final String COLUMN_MAX_SITZ = "max_size";
	public static final String COLUMN_RESULT_FILTER = "result_filter";
	public static final String COLUMN_ACTIVE = "active";
	public static final String COLUMN_LIFE_TIME = "life_time";
	public static final String COLUMN_TERMINAL_IDENTIFIER_TYPES = "terminalidentifier";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_METADATA + " text, "
			+ COLUMN_IDENTIFIER1 + " text not null, "
			+ COLUMN_IDENTIFIER1_Value + " text not null, "
			+ COLUMN_IDENTIFIER2 + " text, "
			+ COLUMN_IDENTIFIER2_Value + " text, "
			+ COLUMN_NAME + " text not null, "
			+ COLUMN_TYPE + " text not null, "
			+ COLUMN_MATCH_LINKS + " text, "
			+ COLUMN_MAX_DEPTH + " integer, "
			+ COLUMN_MAX_SITZ + " integer, "
			+ COLUMN_RESULT_FILTER + " text, "
			+ COLUMN_ACTIVE + " integer, "
			+ COLUMN_LIFE_TIME + " integer, "
			+ COLUMN_TERMINAL_IDENTIFIER_TYPES + " text "
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
