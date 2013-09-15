package de.hshannover.inform.trust.ifmapj.ironcontrol.asynctask;

import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapErrorResult;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapException;
import de.fhhannover.inform.trust.ifmapj.messages.MetadataLifetime;
import de.fhhannover.inform.trust.ifmapj.metadata.Cardinality;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.DBContentProvider;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.Requests;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.VendorMetadata;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.Operation;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.PublishRequestData;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.RequestsController;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Level;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Logger;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LoggerFactory;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.util.Util;

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
