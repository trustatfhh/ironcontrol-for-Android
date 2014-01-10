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
 * Copyright (C) 2013 Trust@HsH
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import de.hshannover.f4.trust.ironcontrol.database.entities.Attributes;
import de.hshannover.f4.trust.ironcontrol.database.entities.Connections;
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
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class DBContentProvider extends ContentProvider {

	private static final Logger logger = LoggerFactory.getLogger(DBContentProvider.class);

	// database
	private DatabaseHelper database;

	// Used for the UriMacher
	private static final int MATCH_PUBLISHS = 10;
	private static final int MATCH_PUBLISH_ID = 20;
	private static final int MATCH_PUBLISH_ID_METADATA_ATTRIBUTES = 30;
	private static final int MATCH_SEARCHES = 40;
	private static final int MATCH_SEARCH_ID = 50;
	private static final int MATCH_SEARCH_ID_RESPONSES = 60;
	private static final int MATCH_SEARCH_ID_RESPONSES_ID = 70;
	private static final int MATCH_SUBSCRIPTION = 80;
	private static final int MATCH_SUBSCRIPTION_ID = 90;
	private static final int MATCH_SUBSCRIPTION_ID_RESPONSES = 100;
	private static final int MATCH_SUBSCRIPTION_ID_RESPONSE_ID = 110;
	private static final int MATCH_RESPONSES_ID = 130;
	private static final int MATCH_RESPONSES_ID_RESULT_ITEMS = 140;
	private static final int MATCH_RESULT_ITEMS_ID_RESULT_METADATA = 150;
	private static final int MATCH_RESULT_METADATA_ID_RESULT_META_ATTRIBUTES = 160;
	private static final int MATCH_CONNECTIONS = 170;
	private static final int MATCH_CONNECTIONS_ID = 180;
	private static final int MATCH_VENDOR_METADATA = 190;
	private static final int MATCH_VENDOR_METADATA_ID = 200;
	private static final int MATCH_VENDOR_METADATA_ID_META_ATTRIBUTES = 210;
	private static final int MATCH_VENDOR_METADATA_ID_META_ATTRIBUTES_ID = 220;

	private static final String AUTHORITY = "de.hshannover.f4.trust.ironcontrol.database";

	private static final String PUBLISH = "publish";
	public static final String METADATA_ATTRIBUTES = "metadata_attributes";
	private static final String SEARCH = "search";
	private static final String SUBSCRIPTION = "subscription";
	public static final String RESPONSES = "responses";
	public static final String RESULT_ITEMS = "resultitems";
	public static final String RESULT_METADATA = "result_metadata";
	public static final String RESULT_META_ATTRIBUTES = "result_meta_attributes";
	public static final String CONNECTIONS = "connections";
	private static final String VENDOR_METADATA = "vendor_metadata";
	public static final String VENDOR_META_ATTRIBUTES = "vendor_meta_attributes";

	public static final Uri PUBLISH_URI = Uri.parse("content://" + AUTHORITY + "/" + PUBLISH);
	public static final Uri SEARCH_URI = Uri.parse("content://" + AUTHORITY + "/" + SEARCH);
	public static final Uri SUBSCRIPTION_URI = Uri.parse("content://" + AUTHORITY + "/" + SUBSCRIPTION);
	public static final Uri ATTRIBUTE_URI = Uri.parse("content://" + AUTHORITY + "/" + METADATA_ATTRIBUTES);
	public static final Uri RESPONSES_URI = Uri.parse("content://" + AUTHORITY + "/" + RESPONSES);
	public static final Uri RESULT_ITEMS_URI = Uri.parse("content://" + AUTHORITY + "/" + RESULT_ITEMS);
	public static final Uri RESULT_METADATA_URI = Uri.parse("content://" + AUTHORITY + "/" + RESULT_METADATA);
	public static final Uri RESULT_META_ATTRIBUTES_URI = Uri.parse("content://" + AUTHORITY + "/" + RESULT_META_ATTRIBUTES);
	public static final Uri CONNECTIONS_URI = Uri.parse("content://" + AUTHORITY + "/" + CONNECTIONS);
	public static final Uri VENDOR_METADATA_URI = Uri.parse("content://" + AUTHORITY + "/" + VENDOR_METADATA);

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, PUBLISH, MATCH_PUBLISHS);
		sURIMatcher.addURI(AUTHORITY, PUBLISH + "/#", MATCH_PUBLISH_ID);
		sURIMatcher.addURI(AUTHORITY, PUBLISH + "/#/" + METADATA_ATTRIBUTES, MATCH_PUBLISH_ID_METADATA_ATTRIBUTES);
		sURIMatcher.addURI(AUTHORITY, SEARCH, MATCH_SEARCHES);
		sURIMatcher.addURI(AUTHORITY, SEARCH + "/#", MATCH_SEARCH_ID);
		sURIMatcher.addURI(AUTHORITY, SEARCH + "/#/" + RESPONSES , MATCH_SEARCH_ID_RESPONSES);
		sURIMatcher.addURI(AUTHORITY, SEARCH + "/#/" + RESPONSES + "/#", MATCH_SEARCH_ID_RESPONSES_ID);
		sURIMatcher.addURI(AUTHORITY, SUBSCRIPTION, MATCH_SUBSCRIPTION);
		sURIMatcher.addURI(AUTHORITY, SUBSCRIPTION + "/#", MATCH_SUBSCRIPTION_ID);
		sURIMatcher.addURI(AUTHORITY, SUBSCRIPTION + "/#/" + RESPONSES , MATCH_SUBSCRIPTION_ID_RESPONSES);
		sURIMatcher.addURI(AUTHORITY, SUBSCRIPTION + "/#/" + RESPONSES + "/#" , MATCH_SUBSCRIPTION_ID_RESPONSE_ID);
		sURIMatcher.addURI(AUTHORITY, RESPONSES + "/#", MATCH_RESPONSES_ID);
		sURIMatcher.addURI(AUTHORITY, RESPONSES + "/#/" + RESULT_ITEMS, MATCH_RESPONSES_ID_RESULT_ITEMS);
		sURIMatcher.addURI(AUTHORITY, RESULT_ITEMS + "/#/" + RESULT_METADATA, MATCH_RESULT_ITEMS_ID_RESULT_METADATA);
		sURIMatcher.addURI(AUTHORITY, RESULT_METADATA + "/#/" + RESULT_META_ATTRIBUTES, MATCH_RESULT_METADATA_ID_RESULT_META_ATTRIBUTES);
		sURIMatcher.addURI(AUTHORITY, CONNECTIONS, MATCH_CONNECTIONS);
		sURIMatcher.addURI(AUTHORITY, CONNECTIONS + "/#", MATCH_CONNECTIONS_ID);
		sURIMatcher.addURI(AUTHORITY, VENDOR_METADATA, MATCH_VENDOR_METADATA);
		sURIMatcher.addURI(AUTHORITY, VENDOR_METADATA + "/#", MATCH_VENDOR_METADATA_ID);
		sURIMatcher.addURI(AUTHORITY, VENDOR_METADATA + "/#/" + VENDOR_META_ATTRIBUTES , MATCH_VENDOR_METADATA_ID_META_ATTRIBUTES);
		sURIMatcher.addURI(AUTHORITY, VENDOR_METADATA + "/#/" + VENDOR_META_ATTRIBUTES + "/#" , MATCH_VENDOR_METADATA_ID_META_ATTRIBUTES_ID);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		logger.log(Level.DEBUG, "delete()... Uri = " + uri.toString());
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsDeleted = 0;
		String lastPathSegmentId = uri.getLastPathSegment();
		String pathSegmentId = null;
		if (uri.getPathSegments().size() > 1) {
			pathSegmentId = uri.getPathSegments().get(1);
		}
		switch (uriType) {
		case MATCH_PUBLISH_ID:
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = rowsDeleted + sqlDB.delete(Requests.TABLE, Requests.COLUMN_ID + "=" + lastPathSegmentId, null);
			} else {
				rowsDeleted = rowsDeleted + sqlDB.delete(Requests.TABLE, Requests.COLUMN_ID + "=" + lastPathSegmentId + " and " + selection, selectionArgs);
			}
			break;
		case MATCH_SEARCH_ID:
			if (TextUtils.isEmpty(selection)) {
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "[DBContentProvider] DELL SEARCH ID: " +lastPathSegmentId + " ...");
				delete(Uri.parse(SEARCH_URI + "/" + lastPathSegmentId + "/" + RESPONSES), selection, selectionArgs);
				rowsDeleted = rowsDeleted + sqlDB.delete(Requests.TABLE, Requests.COLUMN_ID + "=" + lastPathSegmentId, null);
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "... DELL SEARCH OK: " +lastPathSegmentId );
			} else {
				rowsDeleted = rowsDeleted + sqlDB.delete(Requests.TABLE, Requests.COLUMN_ID + "=" + lastPathSegmentId + " and " + selection, selectionArgs);
			}
			break;
		case MATCH_SEARCH_ID_RESPONSES:
			if (TextUtils.isEmpty(selection)) {
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "[DBContentProvider] DELL RESPONSES whith Search ID: " +pathSegmentId + " ...");
				Uri dell_uri = Uri.parse(SEARCH_URI + "/" + pathSegmentId + "/" + RESPONSES);
				String[] projection = new String[]{Responses.COLUMN_ID, Responses.COLUMN_REQUEST_ID, Responses.COLUMN_DATE, Responses.COLUMN_TIME};
				Cursor dell_cursor = query(dell_uri, projection, null, null, null);
				while(dell_cursor.moveToNext()){
					int responseID = dell_cursor.getInt(dell_cursor.getColumnIndexOrThrow(Responses.COLUMN_ID));
					delete(Uri.parse(RESPONSES_URI + "/" + responseID + "/" + RESULT_ITEMS), selection, selectionArgs);
				}
				rowsDeleted = rowsDeleted + sqlDB.delete(Responses.TABLE, Responses.COLUMN_REQUEST_ID + "=" + pathSegmentId, null);
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "... DELL RESPONSES whith Search ID OK");
			} else {
				rowsDeleted = rowsDeleted + sqlDB.delete(Responses.TABLE, Responses.COLUMN_REQUEST_ID + "=" + pathSegmentId + " and " + selection, selectionArgs);
			}
			break;
		case MATCH_SEARCH_ID_RESPONSES_ID:
			if (TextUtils.isEmpty(selection)) {
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "[DBContentProvider] DELL SEARCH-RESPONSES ID: " + lastPathSegmentId + "...");
				delete(Uri.parse(RESPONSES_URI + "/" + lastPathSegmentId + "/" + RESULT_ITEMS), selection, selectionArgs);
				rowsDeleted = rowsDeleted + sqlDB.delete(Responses.TABLE, Responses.COLUMN_ID + "=" + lastPathSegmentId, null);
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "... DELL SEARCH-RESPONSES ID OK");
			}else {
				rowsDeleted = rowsDeleted + sqlDB.delete(Responses.TABLE, Responses.COLUMN_ID + "=" + lastPathSegmentId + " and " + selection, selectionArgs);
			}
			break;
		case MATCH_SUBSCRIPTION_ID:
			if (TextUtils.isEmpty(selection)) {
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "[DBContentProvider] DELL SUBSCRIPTION ID: " +lastPathSegmentId + " ...");
				delete(Uri.parse(SUBSCRIPTION_URI + "/" + lastPathSegmentId + "/" + RESPONSES), selection, selectionArgs);
				rowsDeleted = rowsDeleted + sqlDB.delete(Requests.TABLE, Requests.COLUMN_ID + "=" + lastPathSegmentId, null);
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "... DELL SUBSCRIPTION OK: " + lastPathSegmentId );
			} else {
				rowsDeleted = rowsDeleted + sqlDB.delete(Requests.TABLE, Requests.COLUMN_ID + "=" + lastPathSegmentId + " and " + selection, selectionArgs);
			}
			break;
		case MATCH_SUBSCRIPTION_ID_RESPONSES:
			if (TextUtils.isEmpty(selection)) {
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "[DBContentProvider] DELL RESPONSES whith SUBSCRIPTION ID: " +pathSegmentId + " ...");
				Uri dell_uri = Uri.parse(SUBSCRIPTION_URI + "/" + pathSegmentId + "/" + RESPONSES);
				String[] projection = new String[]{Responses.COLUMN_ID, Responses.COLUMN_REQUEST_ID, Responses.COLUMN_DATE, Responses.COLUMN_TIME};
				Cursor dell_cursor = query(dell_uri, projection, null, null, null);
				while(dell_cursor.moveToNext()){
					int responseID = dell_cursor.getInt(dell_cursor.getColumnIndexOrThrow(Responses.COLUMN_ID));
					delete(Uri.parse(RESULT_ITEMS_URI + "/" + responseID), selection, selectionArgs);
				}
				rowsDeleted = rowsDeleted + sqlDB.delete(Responses.TABLE, Responses.COLUMN_REQUEST_ID + "=" + pathSegmentId, null);
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "... DELL responses whith Search ID OK");
			} else {
				rowsDeleted = rowsDeleted + sqlDB.delete(Responses.TABLE, Responses.COLUMN_REQUEST_ID + "=" + pathSegmentId + " and " + selection, selectionArgs);
			}
			break;
		case MATCH_SUBSCRIPTION_ID_RESPONSE_ID:
			if (TextUtils.isEmpty(selection)) {
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "[DBContentProvider] DELL SUBSCRIPTION-RESPONSES ID: " +lastPathSegmentId + " ...");
				delete(Uri.parse(RESPONSES_URI + "/" + lastPathSegmentId + "/" + RESULT_ITEMS), selection, selectionArgs);
				rowsDeleted = rowsDeleted + sqlDB.delete(Responses.TABLE, Responses.COLUMN_ID + "=" + lastPathSegmentId, null);
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "... DELL SUBSCRIPTION-RESPONSES ID OK");
			}else {
				rowsDeleted = rowsDeleted + sqlDB.delete(Responses.TABLE, Responses.COLUMN_ID + "=" + lastPathSegmentId + " and " + selection, selectionArgs);
			}
			break;
		case MATCH_RESPONSES_ID_RESULT_ITEMS:
			if (TextUtils.isEmpty(selection)) {
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "[DBContentProvider] DELL RESULT ITEMS whith RESPONSES ID: " +pathSegmentId + " ....");
				Uri dell_uri = Uri.parse(RESPONSES_URI + "/" + pathSegmentId + "/" + RESULT_ITEMS);
				String[] projection = new String[]{ResultItems.COLUMN_ID, ResultItems.COLUMN_RESPONSE_ID, ResultItems.COLUMN_IDENTIFIER1, ResultItems.COLUMN_IDENTIFIER2};
				Cursor dell_cursor = query(dell_uri, projection, null, null, null);
				while(dell_cursor.moveToNext()){
					int resultItemID = dell_cursor.getInt(dell_cursor.getColumnIndexOrThrow(ResultItems.COLUMN_ID));
					delete(Uri.parse(RESULT_ITEMS_URI + "/" + resultItemID +"/" + RESULT_METADATA), selection, selectionArgs);
				}
				rowsDeleted = rowsDeleted + sqlDB.delete(ResultItems.TABLE, ResultItems.COLUMN_RESPONSE_ID + "=" + pathSegmentId, null);
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "... DELL RESULT ITEMS responseID OK: " +pathSegmentId);
			} else {
				rowsDeleted = rowsDeleted + sqlDB.delete(ResultItems.TABLE, ResultItems.COLUMN_RESPONSE_ID + "=" + pathSegmentId + " and " + selection, selectionArgs);
			}
			break;
		case MATCH_RESULT_ITEMS_ID_RESULT_METADATA:
			if (TextUtils.isEmpty(selection)) {
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "[DBContentProvider] DELL RESULT_METADATA whith RESULT ITEM ID: " +pathSegmentId + " ....");
				Uri dell_uri = Uri.parse(RESULT_ITEMS_URI + "/" + pathSegmentId + "/" + RESULT_METADATA);
				String[] projection = new String[]{ResultMetadata.COLUMN_ID, ResultMetadata.COLUMN_RESULT_ITEM_ID, ResultMetadata.COLUMN_LOCAL_NAME, ResultMetadata.COLUMN_NAMESPACEURI, ResultMetadata.COLUMN_PREFIX, ResultMetadata.COLUMN_CARDINALITY, ResultMetadata.COLUMN_PUBLISHERID, ResultMetadata.COLUMN_TIMESTAMP};
				Cursor dell_cursor = query(dell_uri, projection, null, null, null);
				while(dell_cursor.moveToNext()){
					int metadataID = dell_cursor.getInt(dell_cursor.getColumnIndexOrThrow(ResultMetadata.COLUMN_ID));
					delete(Uri.parse(RESULT_METADATA_URI + "/" + metadataID + "/" + RESULT_META_ATTRIBUTES), selection, selectionArgs);
				}
				rowsDeleted = rowsDeleted + sqlDB.delete(ResultMetadata.TABLE, ResultMetadata.COLUMN_RESULT_ITEM_ID + "=" + pathSegmentId, null);
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "... DELL RESULT METADATA whith RESULT ITEM ID OK");
			} else {
				rowsDeleted = rowsDeleted + sqlDB.delete(ResultMetadata.TABLE, ResultMetadata.COLUMN_RESULT_ITEM_ID + "=" + pathSegmentId + " and " + selection, selectionArgs);
			}
			break;
		case MATCH_RESULT_METADATA_ID_RESULT_META_ATTRIBUTES:
			if (TextUtils.isEmpty(selection)) {
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "[DBContentProvider] DELL RESULT META ATTRIBUTES whith RESULT METADATA ID: " +pathSegmentId + " ....");
				rowsDeleted = rowsDeleted + sqlDB.delete(ResultMetaAttributes.TABLE, ResultMetaAttributes.COLUMN_RESULT_METADATA_ID + "=" + pathSegmentId, null);
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "..... DELL RESULT META ATTRIBUTES whith RESULT METADATA ID OK:");
			} else {
				rowsDeleted = rowsDeleted + sqlDB.delete(ResultMetaAttributes.TABLE, ResultMetaAttributes.COLUMN_ID + "=" + pathSegmentId + " and " + selection, selectionArgs);
			}
			break;
		case MATCH_CONNECTIONS_ID:
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = rowsDeleted + sqlDB.delete(Connections.TABLE, Connections.COLUMN_ID + "=" + lastPathSegmentId, null);
			} else {
				rowsDeleted = rowsDeleted + sqlDB.delete(Connections.TABLE, Connections.COLUMN_ID + "=" + lastPathSegmentId + " and " + selection, selectionArgs);
			}
			break;
		case MATCH_VENDOR_METADATA_ID:
			if (TextUtils.isEmpty(selection)) {
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "[DBContentProvider] DELL VENDOR META whith ID: " +pathSegmentId + " ....");
				delete(Uri.parse(VENDOR_METADATA_URI + "/" + lastPathSegmentId + "/" + VENDOR_META_ATTRIBUTES), selection, selectionArgs);
				rowsDeleted = rowsDeleted + sqlDB.delete(VendorMetadata.TABLE, VendorMetadata.COLUMN_ID + "=" + lastPathSegmentId, null);
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "..... DELL VENDOR META whith ID OK:");
			} else {
				rowsDeleted = rowsDeleted + sqlDB.delete(MetaAttributes.TABLE, MetaAttributes.COLUMN_ID + "=" + lastPathSegmentId + " and " + selection, selectionArgs);
			}
			break;
		case MATCH_VENDOR_METADATA_ID_META_ATTRIBUTES:
			if (TextUtils.isEmpty(selection)) {
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "[DBContentProvider] DELL VENDOR META ATTRIBUTES whith VENDOR METADATA ID: " +pathSegmentId + " ....");
				rowsDeleted = rowsDeleted + sqlDB.delete(MetaAttributes.TABLE, MetaAttributes.COLUMN_METADATA_ID + "=" + pathSegmentId, null);
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "..... DELL VENDOR META ATTRIBUTES whith VENDOR METADATA ID OK:");
			} else {
				rowsDeleted = rowsDeleted + sqlDB.delete(MetaAttributes.TABLE, MetaAttributes.COLUMN_ID + "=" + lastPathSegmentId + " and " + selection, selectionArgs);
			}
			break;
		case MATCH_VENDOR_METADATA_ID_META_ATTRIBUTES_ID:
			if (TextUtils.isEmpty(selection)) {
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "[DBContentProvider] DELL META ATTRIBUTES whith ID: " +lastPathSegmentId + " ....");
				rowsDeleted = rowsDeleted + sqlDB.delete(MetaAttributes.TABLE, MetaAttributes.COLUMN_ID + "=" + lastPathSegmentId, null);
				Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "..... DELL META ATTRIBUTES whith ID OK:");
			} else {
				rowsDeleted = rowsDeleted + sqlDB.delete(MetaAttributes.TABLE, MetaAttributes.COLUMN_ID + "=" + lastPathSegmentId + " and " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		logger.log(Level.DEBUG, "...delete() " + rowsDeleted +" rows deleted");
		return rowsDeleted;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		logger.log(Level.DEBUG, "insert()... Uri = " + uri.toString() + " " + values.valueSet());
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		String pathSegmentId = null;
		if (uri.getPathSegments().size() > 1) {
			pathSegmentId = uri.getPathSegments().get(1);
		}
		long id = 0;
		switch (uriType) {
		case MATCH_PUBLISHS :
			values.put(Requests.COLUMN_TYPE, "PUBLISH");
			id = sqlDB.insert(Requests.TABLE, null, values);
			break;
		case MATCH_PUBLISH_ID_METADATA_ATTRIBUTES :
			values.put(Attributes.COLUMN_REQUEST_ID, pathSegmentId);
			id = sqlDB.insert(Attributes.TABLE, null, values);
			break;
		case MATCH_SEARCHES :
			values.put(Requests.COLUMN_TYPE, "SEARCH");
			id = sqlDB.insert(Requests.TABLE, null, values);
			break;
		case MATCH_SUBSCRIPTION :
			values.put(Requests.COLUMN_TYPE, "SUBSCRIPTION");
			id = sqlDB.insert(Requests.TABLE, null, values);
			break;
		case MATCH_SEARCH_ID_RESPONSES :
			values.put(Responses.COLUMN_REQUEST_ID, pathSegmentId);
			id = sqlDB.insert(Responses.TABLE, null, values);
			break;
		case MATCH_SUBSCRIPTION_ID_RESPONSES :
			values.put(Responses.COLUMN_REQUEST_ID, pathSegmentId);
			id = sqlDB.insert(Responses.TABLE, null, values);
			break;
		case MATCH_RESPONSES_ID_RESULT_ITEMS :
			values.put(ResultItems.COLUMN_RESPONSE_ID, pathSegmentId);
			id = sqlDB.insert(ResultItems.TABLE, null, values);
			break;
		case MATCH_RESULT_ITEMS_ID_RESULT_METADATA :
			values.put(ResultMetadata.COLUMN_RESULT_ITEM_ID, pathSegmentId);
			id = sqlDB.insert(ResultMetadata.TABLE, null, values);
			break;
		case MATCH_RESULT_METADATA_ID_RESULT_META_ATTRIBUTES :
			values.put(ResultMetaAttributes.COLUMN_RESULT_METADATA_ID, pathSegmentId);
			id = sqlDB.insert(ResultMetaAttributes.TABLE, null, values);
			break;
		case MATCH_CONNECTIONS :
			id = sqlDB.insert(Connections.TABLE, null, values);
			break;
		case MATCH_VENDOR_METADATA:
			id = sqlDB.insert(VendorMetadata.TABLE, null, values);
			break;
		case MATCH_VENDOR_METADATA_ID_META_ATTRIBUTES:
			values.put(MetaAttributes.COLUMN_METADATA_ID, pathSegmentId);
			id = sqlDB.insert(MetaAttributes.TABLE, null, values);
			break;
		default :
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		logger.log(Level.DEBUG, "...insert() id= " + id);
		return Uri.parse("/" + id);
	}

	@Override
	public boolean onCreate() {
		database = new DatabaseHelper(getContext());
		return false;
	}

	private String getProcessNameFromPid(int givenPid) {
		ActivityManager am = (ActivityManager)getContext().getSystemService(Activity.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> lstAppInfo = am.getRunningAppProcesses();

		for(ActivityManager.RunningAppProcessInfo ai : lstAppInfo) {
			if (ai.pid == givenPid) {
				return ai.processName;
			}
		}
		return "" + givenPid;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		logger.log(Level.DEBUG, "query()... Uri = " + uri.toString());

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		//	    checkColumns(projection);
		int uriType = sURIMatcher.match(uri);

		String lastPathSegmentId = uri.getLastPathSegment();
		String pathSegmentId = null;
		if (uri.getPathSegments().size() > 1) {
			pathSegmentId = uri.getPathSegments().get(1);
		}

		switch (uriType) {
		case MATCH_PUBLISHS:
			queryBuilder.setTables(Requests.TABLE);
			queryBuilder.appendWhere(Requests.COLUMN_TYPE + "='PUBLISH'");
			break;
		case MATCH_PUBLISH_ID:
			queryBuilder.setTables(Requests.TABLE);
			queryBuilder.appendWhere(Requests.COLUMN_ID + "=" + lastPathSegmentId);
			queryBuilder.appendWhere(" and " + Requests.COLUMN_TYPE + "='PUBLISH'");
			break;
		case MATCH_PUBLISH_ID_METADATA_ATTRIBUTES:
			queryBuilder.setTables(Attributes.TABLE);
			queryBuilder.appendWhere(Attributes.COLUMN_REQUEST_ID + "=" + pathSegmentId);
			break;
		case MATCH_SEARCHES:
			queryBuilder.setTables(Requests.TABLE);
			queryBuilder.appendWhere(Requests.COLUMN_TYPE + "='SEARCH'");
			break;
		case MATCH_SEARCH_ID:
			queryBuilder.setTables(Requests.TABLE);
			queryBuilder.appendWhere(Requests.COLUMN_ID + "=" + lastPathSegmentId);
			queryBuilder.appendWhere(" and " + Requests.COLUMN_TYPE + "='SEARCH'");
			break;
		case MATCH_SEARCH_ID_RESPONSES:
			queryBuilder.setTables(Responses.TABLE);
			queryBuilder.appendWhere(Responses.COLUMN_REQUEST_ID + "=" + pathSegmentId);
			break;
		case MATCH_SUBSCRIPTION:
			queryBuilder.setTables(Requests.TABLE);
			queryBuilder.appendWhere(Requests.COLUMN_TYPE + "='SUBSCRIPTION'");
			break;
		case MATCH_SUBSCRIPTION_ID:
			queryBuilder.setTables(Requests.TABLE);
			queryBuilder.appendWhere(Requests.COLUMN_ID + "=" + lastPathSegmentId);
			queryBuilder.appendWhere(" and " + Requests.COLUMN_TYPE + "='SUBSCRIPTION'");
			break;
		case MATCH_SUBSCRIPTION_ID_RESPONSES:
			queryBuilder.setTables(Responses.TABLE);
			queryBuilder.appendWhere(Responses.COLUMN_REQUEST_ID + "=" + pathSegmentId);
			break;
		case MATCH_RESPONSES_ID_RESULT_ITEMS:
			queryBuilder.setTables(ResultItems.TABLE);
			queryBuilder.appendWhere(ResultItems.COLUMN_RESPONSE_ID + "=" + pathSegmentId);
			break;
		case MATCH_RESULT_ITEMS_ID_RESULT_METADATA:
			queryBuilder.setTables(ResultMetadata.TABLE);
			queryBuilder.appendWhere(ResultMetadata.COLUMN_RESULT_ITEM_ID + "=" + pathSegmentId);
			break;
		case MATCH_RESULT_METADATA_ID_RESULT_META_ATTRIBUTES:
			queryBuilder.setTables(ResultMetaAttributes.TABLE);
			queryBuilder.appendWhere(ResultMetaAttributes.COLUMN_RESULT_METADATA_ID + "=" + pathSegmentId);
			break;
		case MATCH_CONNECTIONS:
			queryBuilder.setTables(Connections.TABLE);
			break;
		case MATCH_CONNECTIONS_ID:
			queryBuilder.setTables(Connections.TABLE);
			queryBuilder.appendWhere(Connections.COLUMN_ID + "=" + lastPathSegmentId);
			break;
		case MATCH_VENDOR_METADATA:
			queryBuilder.setTables(VendorMetadata.TABLE);
			break;
		case MATCH_VENDOR_METADATA_ID:
			queryBuilder.setTables(VendorMetadata.TABLE);
			queryBuilder.appendWhere(VendorMetadata.COLUMN_ID + "=" + lastPathSegmentId);
			break;
		case MATCH_VENDOR_METADATA_ID_META_ATTRIBUTES:
			queryBuilder.setTables(MetaAttributes.TABLE);
			queryBuilder.appendWhere(MetaAttributes.COLUMN_METADATA_ID + "=" + pathSegmentId);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		logger.log(Level.DEBUG, "...query OK");
		if(cursor.getCount() <= 0){
			logger.log(Level.WARN, "Cursor count <= 0");
		}
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		logger.log(Level.DEBUG, "update()... Uri = " + uri.toString() + " " + values.valueSet());
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsUpdated = 0;

		String lastPathSegmentId = uri.getLastPathSegment();
		//		String pathSegmentId = null;
		//		if (uri.getPathSegments().size() > 1) {
		//			pathSegmentId = uri.getPathSegments().get(1);
		//		}

		switch (uriType) {
		case MATCH_PUBLISHS:
			rowsUpdated = sqlDB.update(Requests.TABLE, values, selection,selectionArgs);
			break;
		case MATCH_PUBLISH_ID:
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(Requests.TABLE, values, Requests.COLUMN_ID + "=" + lastPathSegmentId, null);
			} else {
				rowsUpdated = sqlDB.update(Requests.TABLE, values, Requests.COLUMN_ID + "=" + lastPathSegmentId + " and " + selection, selectionArgs);
			}
			break;
		case MATCH_CONNECTIONS :
			rowsUpdated = sqlDB.update(Connections.TABLE, values, selection, selectionArgs);
			break;
		case MATCH_CONNECTIONS_ID:
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(Connections.TABLE, values, Connections.COLUMN_ID + "=" + lastPathSegmentId, null);
			} else {
				rowsUpdated = sqlDB.update(Connections.TABLE, values, Connections.COLUMN_ID + "=" + lastPathSegmentId + " and " + selection, selectionArgs);
			}
			break;
		case MATCH_RESPONSES_ID:
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(Responses.TABLE, values, Responses.COLUMN_ID + "=" + lastPathSegmentId, null);
			} else {
				rowsUpdated = sqlDB.update(Responses.TABLE, values, Responses.COLUMN_ID + "=" + lastPathSegmentId + " and " + selection, selectionArgs);
			}
			break;
		case MATCH_SUBSCRIPTION:
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(Requests.TABLE, values, Requests.COLUMN_TYPE + "='SUBSCRIPTION'", null);
			} else {
				rowsUpdated = sqlDB.update(Requests.TABLE, values, Requests.COLUMN_TYPE + "='SUBSCRIPTION'" + " and " + selection, selectionArgs);
			}
			break;
		case MATCH_SUBSCRIPTION_ID:
			String select = Requests.COLUMN_ID + "=" + lastPathSegmentId +" and " + Requests.COLUMN_TYPE + "='SUBSCRIPTION'";
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(Requests.TABLE, values, select, null);
			} else {
				rowsUpdated = sqlDB.update(Requests.TABLE, values, select + " and " + selection, selectionArgs);
			}
			break;
		case MATCH_VENDOR_METADATA_ID:
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(VendorMetadata.TABLE, values, VendorMetadata.COLUMN_ID + "=" + lastPathSegmentId, null);
			} else {
				rowsUpdated = sqlDB.update(VendorMetadata.TABLE, values, VendorMetadata.COLUMN_ID + "=" + lastPathSegmentId + " and " + selection, selectionArgs);
			}
			break;
		case MATCH_VENDOR_METADATA_ID_META_ATTRIBUTES_ID:
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(MetaAttributes.TABLE, values, MetaAttributes.COLUMN_ID + "=" + lastPathSegmentId, null);
			} else {
				rowsUpdated = sqlDB.update(MetaAttributes.TABLE, values, MetaAttributes.COLUMN_ID + "=" + lastPathSegmentId + " and " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		logger.log(Level.DEBUG, "...update(), " + rowsUpdated +" rows Updated");
		return rowsUpdated;
	}

	public void checkColumns(String[] projection) {
		String[] available = { Requests.COLUMN_NAME, Requests.COLUMN_LIFE_TIME, Requests.COLUMN_ID };
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}

}
