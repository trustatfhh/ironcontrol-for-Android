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
package de.hshannover.f4.trust.ironcontrol.asynctask;

import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;
import de.hshannover.f4.trust.ifmapj.exception.IfmapErrorResult;
import de.hshannover.f4.trust.ifmapj.exception.IfmapException;
import de.hshannover.f4.trust.ifmapj.messages.MetadataLifetime;
import de.hshannover.f4.trust.ifmapj.metadata.Cardinality;
import de.hshannover.f4.trust.ironcontrol.R;
import de.hshannover.f4.trust.ironcontrol.database.DBContentProvider;
import de.hshannover.f4.trust.ironcontrol.database.entities.Requests;
import de.hshannover.f4.trust.ironcontrol.database.entities.VendorMetadata;
import de.hshannover.f4.trust.ironcontrol.logger.Level;
import de.hshannover.f4.trust.ironcontrol.logger.Logger;
import de.hshannover.f4.trust.ironcontrol.logger.LoggerFactory;
import de.hshannover.f4.trust.ironcontrol.logic.RequestsController;
import de.hshannover.f4.trust.ironcontrol.logic.data.Operation;
import de.hshannover.f4.trust.ironcontrol.logic.data.PublishRequestData;
import de.hshannover.f4.trust.ironcontrol.view.util.Util;

/**
 * AsyncTask to publish in background and inform the user.
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class PublishTask extends AsyncTask<Void, Void, Void> {

	private static final Logger logger = LoggerFactory.getLogger(PublishTask.class);

	public static final String MASSAGEUPDATE = "Update...";
	public static final String MASSAGENOTIFY = "Notify...";
	public static final String MASSAGEDELETE = "Delete...";

	private ProgressDialog pd;
	private Context context;

	private PublishRequestData data;

	private String[] selectedRowIds;
	private Operation operation;
	private boolean multi;

	private String error;

	public PublishTask(Context context, PublishRequestData data){
		init(context);

		this.data = data;

		switch(data.getOperation()){
		case UPDATE: pd.setMessage(MASSAGEUPDATE);
		break;
		case NOTIFY: pd.setMessage(MASSAGENOTIFY);
		break;
		case DELETE: pd.setMessage(MASSAGEDELETE);
		break;
		default: logger.log(Level.WARN, context.getResources().getString(R.string.wrongButtonID));
		break;
		}
		logger.log(Level.DEBUG, "...NEW");
	}

	public PublishTask(Context context, String[] selectedRowIds, int buttonType, boolean multi){
		logger.log(Level.DEBUG, "NEW... whith "+ selectedRowIds.length +" rowIDs");

		init(context);

		this.selectedRowIds = selectedRowIds;
		this.multi = multi;
		this.operation = Operation.valueOf(buttonType);

		switch(buttonType){
		case R.id.bPublishUpdate: pd.setMessage(MASSAGEUPDATE);
		break;
		case R.id.bPublishNotify: pd.setMessage(MASSAGENOTIFY);
		break;
		case R.id.bPublishDelete: pd.setMessage(MASSAGEDELETE);
		break;
		default: logger.log(Level.WARN, context.getResources().getString(R.string.wrongButtonID));
		break;
		}
		logger.log(Level.DEBUG, "...NEW");
	}

	private void init(Context context){
		this.context = context;
		pd= new ProgressDialog(context);
	}

	@Override
	protected void onPreExecute() {
		logger.log(Level.DEBUG, "onPreExecute()...");
		super.onPreExecute();
		pd.show();
		logger.log(Level.DEBUG, "...onPreExecute()");
	}

	@Override
	protected Void doInBackground(Void... params) {
		Thread.currentThread().setName(PublishTask.class.getSimpleName());
		logger.log(Level.DEBUG, "doInBackground()...");

		try {
			if(data != null){			// For a not saved publish
				RequestsController.createPublish(data);
			} else if(multi){			// saved multiPublish

				PublishRequestData[] requestData = new PublishRequestData[selectedRowIds.length];

				for(int i=0; i < selectedRowIds.length; i++){
					requestData[i] = buildRequestData(selectedRowIds[i]);
				}

				RequestsController.createPublish(requestData);

			} else {					// saved single Publish

				for (String selectedRowId : selectedRowIds) {
					RequestsController.createPublish(buildRequestData(selectedRowId));
				}
			}
		} catch (IfmapErrorResult e) {
			error = e.getErrorCode().toString();
		} catch (IfmapException e) {
			error = e.getDescription();
		} catch (Exception e) {
			error = e.toString();
		}
		logger.log(Level.DEBUG, "...doInBackground()");
		return null;
	}

	private Cursor getCursor(Uri uri){
		return context.getContentResolver().query(uri, null, null, null, null);
	}

	private PublishRequestData buildRequestData(String id) {
		logger.log(Level.DEBUG, "buildRequestData() rowId= " +id+ "...");

		String metadata = null;
		String identifier1 = null;
		String identifier2 = null;
		String identifier1Value = null;
		String identifier2Value = null;
		String sLifeTime = null;
		MetadataLifetime lifeTime = null;

		Uri publish_uri = Uri.parse(DBContentProvider.PUBLISH_URI + "/" + id);

		Cursor cursor = getCursor(publish_uri);

		if(cursor.getCount() == 1 && cursor.moveToNext()){

			int metadata_Index = cursor.getColumnIndexOrThrow(Requests.COLUMN_METADATA);
			int identifier1_Index = cursor.getColumnIndexOrThrow(Requests.COLUMN_IDENTIFIER1);
			int identifier2_Index = cursor.getColumnIndexOrThrow(Requests.COLUMN_IDENTIFIER2);
			int identifier1Value_Index = cursor.getColumnIndexOrThrow(Requests.COLUMN_IDENTIFIER1_Value);
			int identifier2Value_Index = cursor.getColumnIndexOrThrow(Requests.COLUMN_IDENTIFIER2_Value);
			int lifeTime_Index = cursor.getColumnIndexOrThrow(Requests.COLUMN_LIFE_TIME);

			metadata = cursor.getString(metadata_Index);
			identifier1 = cursor.getString(identifier1_Index);
			identifier2 = cursor.getString(identifier2_Index);
			identifier1Value = cursor.getString(identifier1Value_Index);
			identifier2Value = cursor.getString(identifier2Value_Index);
			sLifeTime = cursor.getString(lifeTime_Index);

			lifeTime = MetadataLifetime.valueOf(sLifeTime);

		} else {
			logger.log(Level.WARN, context.getResources().getString(R.string.wrongCursorCount));
		}

		cursor.close();

		HashMap<String, String> metaAttributes = Util.getMetadataAttributes(context, id);

		PublishRequestData data = new PublishRequestData();
		data.setOperation(operation);
		data.setIdentifier1(identifier1);
		data.setIdentifier2(identifier2);
		data.setIdentifier1Value(identifier1Value);
		data.setIdentifier2Value(identifier2Value);
		data.setLifeTime(lifeTime);
		data.setMetaName(metadata);
		data.setAttributes(metaAttributes);

		// For vendorMetadata
		if(metaAttributes.containsKey(VendorMetadata.COLUMN_CARDINALITY)){

			data.setVendorMetaPrefix(metaAttributes.get(VendorMetadata.COLUMN_PREFIX));
			data.setVendorMetaUri(metaAttributes.get(VendorMetadata.COLUMN_URI));
			data.setVendorCardinality(Cardinality.valueOf(metaAttributes.get(VendorMetadata.COLUMN_CARDINALITY)));

			metaAttributes.remove(VendorMetadata.COLUMN_PREFIX);
			metaAttributes.remove(VendorMetadata.COLUMN_URI);
			metaAttributes.remove(VendorMetadata.COLUMN_CARDINALITY);
		}

		logger.log(Level.DEBUG, "...buildRequestData()");
		return data;
	}

	@Override
	protected void onPostExecute(Void result) {
		logger.log(Level.DEBUG, "onPostExecute()...");

		pd.dismiss();

		if(error == null){
			Toast.makeText(context, R.string.publishReceived, Toast.LENGTH_SHORT).show();
			logger.log(Level.INFO, context.getResources().getString(R.string.publishReceived));
		}else {
			Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
		}

		logger.log(Level.DEBUG, "...onPostExecute()");
	}
}
