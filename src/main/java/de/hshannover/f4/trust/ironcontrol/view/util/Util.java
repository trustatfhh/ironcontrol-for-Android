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
package de.hshannover.inform.trust.ironcontrol.view.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import de.hshannover.inform.trust.ironcontrol.database.DBContentProvider;
import de.hshannover.inform.trust.ironcontrol.database.entities.Attributes;
import de.hshannover.inform.trust.ironcontrol.view.MainActivity;

public class Util {

	public static List<String> getMetaList(Context context, int textArrayResId){
		String[] metaResouceList = context.getResources().getStringArray(textArrayResId);
		return Arrays.asList(metaResouceList);
	}

	public static HashMap<String, String> getMetadataAttributes(Context context, String publishId){

		Uri uri = Uri.parse(DBContentProvider.PUBLISH_URI + "/"+ publishId + "/" + DBContentProvider.METADATA_ATTRIBUTES);

		Cursor cursor = getCursor(context, uri);
		HashMap<String,String> metadataMap = new HashMap<String, String>();

		if(cursor.getCount() > 0){
			while(cursor.moveToNext()){
				metadataMap.put(
						cursor.getString(cursor.getColumnIndexOrThrow(Attributes.COLUMN_NAME)),
						cursor.getString(cursor.getColumnIndexOrThrow(Attributes.COLUMN_VALUE)));
			}
		}

		cursor.close();

		return metadataMap;
	}

	private static Cursor getCursor(Context context, Uri uri){
		return context.getContentResolver().query(uri, null, null, null, null);
	}

	public static String getString(int resId){
		return MainActivity.getContext().getResources().getString(resId);
	}
}
