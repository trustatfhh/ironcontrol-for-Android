package de.hshannover.inform.trust.ifmapj.ironcontrol.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapErrorResult;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapException;
import de.fhhannover.inform.trust.ifmapj.messages.MetadataLifetime;
import de.fhhannover.inform.trust.ifmapj.metadata.Cardinality;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.asynctask.PublishTask;
import de.hshannover.inform.trust.ifmapj.ironcontrol.asynctask.PublishTestTask;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.DBContentProvider;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.Attributes;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.Requests;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.VendorMetadata;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.data.Operation;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.data.PublishRequestData;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Level;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Logger;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LoggerFactory;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.util.MetadataValueFieldsBuilder;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.util.Node;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.util.PopUp;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.util.PopUpEvent;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.util.PromptSpinnerAdapter;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.util.SavePopUp;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.util.Util;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.util.ValidSpinnerAdapter;

public class PublishActivity extends Activity implements PopUpEvent{

	private static final Logger logger = LoggerFactory.getLogger(PublishActivity.class);

	private static final CharSequence IDENTIFIER1_SPINNER_PROMPT = "Identifier 1";
	private static final CharSequence IDENTIFIER2_SPINNER_PROMPT = "Identifier 2";
	private static final CharSequence METADATA_SPINNER_PROMPT = "Metadata";

	// Metadata
	private Spinner sMetaDaten;
	private LinearLayout metadataValueFields;
	private RadioGroup rgMetaList;
	private ValidSpinnerAdapter metadataAdapter;
	private MetadataValueFieldsBuilder valueFieldsBuilder;

	// Identifier
	private EditText etIdentifier1, etIdentifier2;
	private Spinner sIdentifier1, sIdentifier2;
	private ValidSpinnerAdapter identifier1Adapter, identifier2Adapter;

	private RadioGroup rgLifeTime;
	private Button bUpdate, bNotify;


	//TODO [MR] vor final löschen
	public void publishTest(View v) throws IfmapErrorResult, IfmapException{
		switch(v.getId()){
		case R.id.buttonTestUpdate: new PublishTestTask(this, Operation.UPDATE).execute();
		break;
		case R.id.buttonTestDelete: new PublishTestTask(this, Operation.DELETE).execute();
		break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publish);

		this.valueFieldsBuilder = new MetadataValueFieldsBuilder(this, R.id.linearLayoutForMetadataValueFields);

		findActivityViews();
		setSpinnerAdapter();
		setListener();

		// looking for saved connections
		Cursor connections = getContentResolver().query(
				DBContentProvider.CONNECTIONS_URI,
				null, null, null, null);

		if(connections.getCount() == 0){		// Show popup when no connection was saved, callBack is onClickePopUp()
			new PopUp(this, R.string.no_connection_settings,
					R.string.no_connection_settings_message).show();
		}

		connections.close();

		// Start Activity with data
		Bundle bundle = getIntent().getExtras();
		if(bundle != null && bundle.getString("listItemId") != null){
			fillActivityViews(bundle.getString("listItemId"));
		}

		int a = Build.VERSION_CODES.FROYO;
		int aa = Build.VERSION.SDK_INT;
		//		System.out.println("FROYO : "+a);
		//		System.out.println("Installed : "+aa);
	}

	public void publish(View v){
		new PublishTask(this, buildRequestData(v.getId())).execute();
	}

	@Override
	public void onClickePopUp() {
		Intent intent = new Intent(getBaseContext(), ConnectionFragmentActivity.class);
		startActivity(intent);
	}

	public void publishSave(View view){
		new SavePopUp(this, R.string.saving_publishs_message).show();		// callBack is onClickeSavePopUp()
	}

	@Override
	public boolean onClickeSavePopUp(String savedName) {
		if(!isPublishNameValid(savedName)){
			return false;
		}

		MetadataLifetime lifeTime = null;

		switch(rgLifeTime.getCheckedRadioButtonId()){
		case R.id.rbForever: lifeTime = MetadataLifetime.forever;
		break;
		case R.id.rbSession: lifeTime = MetadataLifetime.session;
		break;
		}

		ContentValues publishValues = new ContentValues();
		publishValues.put(Requests.COLUMN_NAME, savedName);
		publishValues.put(Requests.COLUMN_METADATA, sMetaDaten.getSelectedItem().toString());
		publishValues.put(Requests.COLUMN_IDENTIFIER1, sIdentifier1.getSelectedItem().toString());
		publishValues.put(Requests.COLUMN_IDENTIFIER1_Value, etIdentifier1.getText().toString());
		publishValues.put(Requests.COLUMN_IDENTIFIER2, sIdentifier2.getSelectedItem().toString());
		publishValues.put(Requests.COLUMN_IDENTIFIER2_Value, etIdentifier2.getText().toString());
		publishValues.put(Requests.COLUMN_LIFE_TIME, lifeTime.toString());

		Uri uriId = getContentResolver().insert(DBContentProvider.PUBLISH_URI, publishValues);
		String id = uriId.getLastPathSegment();

		HashMap<String, String> attributes = readMetadataAttributes();
		ContentValues attributesValues = new ContentValues();

		for(String s: attributes.keySet()){
			attributesValues.put(Attributes.COLUMN_NAME, s);
			attributesValues.put(Attributes.COLUMN_VALUE, attributes.get(s));
			getContentResolver().insert(Uri.parse(DBContentProvider.PUBLISH_URI + "/" + id + "/" + DBContentProvider.METADATA_ATTRIBUTES) , attributesValues);
		}

		// For VendorMetadata
		switch(rgMetaList.getCheckedRadioButtonId()){
		case R.id.rbVendor:
			String selection = VendorMetadata.COLUMN_NAME + "='" + sMetaDaten.getSelectedItem().toString()+"'";

			Cursor vendorMetaCursor = getContentResolver().query(
					DBContentProvider.VENDOR_METADATA_URI,
					null, selection, null, null);

			vendorMetaCursor.moveToNext();

			attributesValues.put(Attributes.COLUMN_NAME, VendorMetadata.COLUMN_PREFIX);
			attributesValues.put(Attributes.COLUMN_VALUE, vendorMetaCursor.getString(vendorMetaCursor.getColumnIndex(VendorMetadata.COLUMN_PREFIX)));
			getContentResolver().insert(Uri.parse(DBContentProvider.PUBLISH_URI + "/" + id + "/" + DBContentProvider.METADATA_ATTRIBUTES) , attributesValues);

			attributesValues.put(Attributes.COLUMN_NAME, VendorMetadata.COLUMN_URI);
			attributesValues.put(Attributes.COLUMN_VALUE, vendorMetaCursor.getString(vendorMetaCursor.getColumnIndex(VendorMetadata.COLUMN_URI)));
			getContentResolver().insert(Uri.parse(DBContentProvider.PUBLISH_URI + "/" + id + "/" + DBContentProvider.METADATA_ATTRIBUTES) , attributesValues);

			attributesValues.put(Attributes.COLUMN_NAME, VendorMetadata.COLUMN_CARDINALITY);
			attributesValues.put(Attributes.COLUMN_VALUE, vendorMetaCursor.getString(vendorMetaCursor.getColumnIndex(VendorMetadata.COLUMN_CARDINALITY)));
			getContentResolver().insert(Uri.parse(DBContentProvider.PUBLISH_URI + "/" + id + "/" + DBContentProvider.METADATA_ATTRIBUTES) , attributesValues);

			vendorMetaCursor.close();
		}
		return true;
	}

	private boolean isPublishNameValid(String savedName) {
		logger.log(Level.DEBUG, "Valid check for publish name...");
		if(!savedName.equals("")){

			String selectionArgs[] = {savedName};
			String selection = Requests.COLUMN_NAME + "=?";

			Cursor cursor = getContentResolver().query(DBContentProvider.PUBLISH_URI, null, selection, selectionArgs, null);

			if(cursor.getCount() < 1){
				cursor.close();
				logger.log(Level.DEBUG, "... ok");
				return true;
			}else {
				cursor.close();
				Toast.makeText(getBaseContext(), "Publish: " + savedName + " exists", Toast.LENGTH_SHORT).show();
			}
		}else {
			Toast.makeText(getBaseContext(), "empty name", Toast.LENGTH_SHORT).show();
		}

		logger.log(Level.DEBUG, "... fail");
		return false;
	}

	private PublishRequestData buildRequestData(int buttonId) {

		String metadata = sMetaDaten.getSelectedItem().toString();
		String identifier1 = sIdentifier1.getSelectedItem().toString();
		String identifier2 = sIdentifier2.getSelectedItem().toString();
		String identifier1Value = etIdentifier1.getText().toString();
		String identifier2Value = etIdentifier2.getText().toString();

		HashMap<String, String> metaAttributes = readMetadataAttributes();
		MetadataLifetime lifeTime = null;

		switch(rgLifeTime.getCheckedRadioButtonId()){

		case R.id.rbForever: lifeTime = MetadataLifetime.forever; break;
		case R.id.rbSession: lifeTime = MetadataLifetime.session; break;

		}

		PublishRequestData data = new PublishRequestData();

		data.setOperation(Operation.valueOf(buttonId));
		data.setIdentifier1(identifier1);
		data.setIdentifier2(identifier2);
		data.setIdentifier1Value(identifier1Value);
		data.setIdentifier2Value(identifier2Value);
		data.setLifeTime(lifeTime);
		data.setMetaName(metadata);
		data.setAttributes(metaAttributes);

		switch(rgMetaList.getCheckedRadioButtonId()){
		case R.id.rbVendor:
			String selection = VendorMetadata.COLUMN_NAME + "='" + metadata+"'";

			Cursor vendorMetaCursor = getContentResolver().query(
					DBContentProvider.VENDOR_METADATA_URI,
					null, selection, null, null);

			vendorMetaCursor.moveToNext();
			data.setVendorMetaPrefix(vendorMetaCursor.getString(vendorMetaCursor.getColumnIndex(VendorMetadata.COLUMN_PREFIX)));
			data.setVendorMetaUri(vendorMetaCursor.getString(vendorMetaCursor.getColumnIndex(VendorMetadata.COLUMN_URI)));
			data.setVendorCardinality(Cardinality.valueOf(vendorMetaCursor.getString(vendorMetaCursor.getColumnIndex(VendorMetadata.COLUMN_CARDINALITY))));

			vendorMetaCursor.close();
		}

		return data;
	}

	private HashMap<String, String> readMetadataAttributes(){

		HashMap<String,String> metadataMap = new HashMap<String, String>();

		for(int i=0; i<metadataValueFields.getChildCount(); i++){

			LinearLayout ll = (LinearLayout)metadataValueFields.getChildAt(i);

			for(int y=0; y<ll.getChildCount(); y++){

				if(ll.getChildAt(y) instanceof Spinner){

					Spinner s = (Spinner)ll.getChildAt(y);
					metadataMap.put(s.getTag().toString(), s.getSelectedItem().toString());

				}else if(ll.getChildAt(y) instanceof EditText){

					EditText eT = (EditText)ll.getChildAt(y);

					if(!eT.getText().toString().equals("")){

						metadataMap.put(eT.getHint().toString(), eT.getText().toString());

					}
				}
			}
		}

		return metadataMap;
	}

	private void findActivityViews(){
		// Metadata
		sMetaDaten = (Spinner)findViewById(R.id.metaDataSpinner);
		metadataValueFields = (LinearLayout)findViewById(R.id.linearLayoutForMetadataValueFields);
		rgMetaList = (RadioGroup)findViewById(R.id.rgMetaList);

		// Identifier
		sIdentifier1 = (Spinner)findViewById(R.id.sIdentifier1);
		sIdentifier2 = (Spinner)findViewById(R.id.sIdentifier2);
		etIdentifier1 = (EditText)findViewById(R.id.etIdentifier1);
		etIdentifier2 = (EditText)findViewById(R.id.etIdentifier2);

		// Other
		rgLifeTime = (RadioGroup)findViewById(R.id.rgLifetime);
		bUpdate = (Button)findViewById(R.id.bPublishUpdate);
		bNotify = (Button)findViewById(R.id.bPublishNotify);
	}

	private void setSpinnerAdapter(){
		identifier1Adapter = new ValidSpinnerAdapter(this, IDENTIFIER1_SPINNER_PROMPT, R.array.identifier1_list, Node.IDENTIEFIER1);
		identifier2Adapter = new ValidSpinnerAdapter(this, IDENTIFIER2_SPINNER_PROMPT, R.array.identifier2_list, Node.IDENTIEFIER2);
		metadataAdapter = new ValidSpinnerAdapter(this, METADATA_SPINNER_PROMPT, R.array.metadaten_list, Node.METADATA);

		sIdentifier1.setAdapter(identifier1Adapter);
		sIdentifier2.setAdapter(identifier2Adapter);
		sMetaDaten.setAdapter(metadataAdapter);
	}

	private void setListener(){
		// METADATA-Spinner
		sMetaDaten.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String itemString = (String) sMetaDaten.getSelectedItem();

				// Metadata Attribute fields
				valueFieldsBuilder.setValueFieldsFor(itemString);

				// Valid check when Metadata Standard
				switch(rgMetaList.getCheckedRadioButtonId()){
				case R.id.rbStandard:
					if(!metadataAdapter.isValid(itemString)){
						Toast.makeText(getApplication(), R.string.not_valid_identifier_metadata, Toast.LENGTH_SHORT).show();
					}
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});

		// Identifier1-Spinner
		sIdentifier1.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String itemString = (String) sIdentifier1.getSelectedItem();

				// set Hint for EditText
				etIdentifier1.setHint(itemString);

				// Valid check
				if(!identifier1Adapter.isValid(itemString)){
					Toast.makeText(getApplication(), R.string.not_valid_identifier_metadata, Toast.LENGTH_SHORT).show();
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});

		// Identifier2-Spinner
		sIdentifier2.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String itemString = (String) sIdentifier2.getSelectedItem();

				// set Hint for EditText
				etIdentifier2.setHint(itemString);

				// set Enabled for EditText
				if(itemString.equals("none")){
					etIdentifier2.setEnabled(false);
					etIdentifier2.setText("");
				}else{
					etIdentifier2.setEnabled(true);
				}

				// Valid check
				if(!identifier2Adapter.isValid(itemString)){
					Toast.makeText(getApplication(), R.string.not_valid_identifier_metadata, Toast.LENGTH_SHORT).show();
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		// Radio-Group for Metadata-Spinner type
		rgMetaList.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId){
				case R.id.rbNone:
					// change spinner
					sMetaDaten.setAdapter(new ArrayAdapter<CharSequence>(
							getApplication(),
							android.R.layout.simple_list_item_1,
							new ArrayList<CharSequence>()));

					// disable buttons
					bNotify.setEnabled(false);
					bUpdate.setEnabled(false);

					break;

				case R.id.rbStandard:
					// change spinner
					sMetaDaten.setAdapter(metadataAdapter);

					// enable buttons
					bNotify.setEnabled(true);
					bUpdate.setEnabled(true);

					break;

				case R.id.rbVendor:
					// change spinner
					sMetaDaten.setAdapter(new PromptSpinnerAdapter(
							getApplication(),
							METADATA_SPINNER_PROMPT,
							getVendorSpecificMetadata()));

					// enable buttons
					bNotify.setEnabled(true);
					bUpdate.setEnabled(true);

					break;
				}
			}
		});
	}

	private List<CharSequence> getVendorSpecificMetadata() {

		List<CharSequence> csList = new ArrayList<CharSequence>();

		Cursor vendorMetaCursor = getContentResolver().query(
				DBContentProvider.VENDOR_METADATA_URI,
				null, null, null, null);

		while(vendorMetaCursor.moveToNext()){
			csList.add(vendorMetaCursor.getString(vendorMetaCursor.getColumnIndex(VendorMetadata.COLUMN_NAME)));
		}

		vendorMetaCursor.close();

		return csList;
	}

	private void fillActivityViews(String itemId){
		String metadata = null;
		String identifier1 = null;
		String identifier2 = null;
		String identifier1Value = null;
		String identifier2Value = null;
		String sLifeTime = null;
		MetadataLifetime lifeTime = null;

		Uri publish_uri = Uri.parse(DBContentProvider.PUBLISH_URI + "/" + itemId);

		Cursor cursor = getContentResolver().query(
				publish_uri,
				null, null, null, null);

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

			// Attributes

			HashMap<String, String> metaAttributes = Util.getMetadataAttributes(getApplication(), itemId);

			if(metaAttributes.containsKey(VendorMetadata.COLUMN_CARDINALITY)){
				rgMetaList.check(R.id.rbVendor);
			}

			if(!metaAttributes.isEmpty()){
				valueFieldsBuilder.setMetadataMap(metaAttributes);
			}

			// Set Activity Views
			if(lifeTime.equals("forever")){
				rgLifeTime.check(R.id.rbForever);
			}else if(lifeTime.equals("session")){
				rgLifeTime.check(R.id.rbSession);
			}

			sMetaDaten.setSelection(getSpinnerIndex(sMetaDaten, metadata));
			sIdentifier1.setSelection(getSpinnerIndex(sIdentifier1, identifier1));
			sIdentifier2.setSelection(getSpinnerIndex(sIdentifier2, identifier2));
			etIdentifier1.setText(identifier1Value);
			etIdentifier2.setText(identifier2Value);

		} else {
			logger.log(Level.WARN, getResources().getString(R.string.wrongCursorCount));
		}

		cursor.close();
	}

	private int getSpinnerIndex(Spinner spinner, String s){
		int index = 0;
		for (int i=0;i<spinner.getCount();i++){
			if (spinner.getItemAtPosition(i).equals(s)){
				index = i;
				break;
			}
		}
		return index;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_publish, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(getBaseContext(), SettingsActivity.class));
			return true;
		case R.id.menu_exit:
			Intent home = new Intent(Intent.ACTION_MAIN);
			home.addCategory(Intent.CATEGORY_HOME);
			home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(home);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClickeSubscriptionPopUp(String subscribeName, String startIdentifier, String identifierValue) {
	}
}
