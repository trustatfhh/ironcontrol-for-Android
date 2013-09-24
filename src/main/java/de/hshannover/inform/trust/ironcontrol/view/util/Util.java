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
