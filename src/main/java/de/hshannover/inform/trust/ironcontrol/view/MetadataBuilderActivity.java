package de.hshannover.inform.trust.ironcontrol.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import de.hshannover.inform.trust.ironcontrol.R;
import de.hshannover.inform.trust.ironcontrol.database.DBContentProvider;
import de.hshannover.inform.trust.ironcontrol.database.entities.MetaAttributes;
import de.hshannover.inform.trust.ironcontrol.database.entities.VendorMetadata;
import de.hshannover.inform.trust.ironcontrol.logger.Level;
import de.hshannover.inform.trust.ironcontrol.logger.Logger;
import de.hshannover.inform.trust.ironcontrol.logger.LoggerFactory;
import de.hshannover.inform.trust.ironcontrol.view.util.MetaDataEditText;

public class MetadataBuilderActivity extends Activity {

	private static final Logger logger = LoggerFactory.getLogger(MetadataBuilderActivity.class);

	private LinearLayout linearLayoutForMetadataValueFields;
	private TextView tvRemoveValueField, tvAddValueField;
	private ArrayList<EditText> listMetadataEditText = new ArrayList<EditText>();
	private EditText metadataName;
	private RadioGroup rgType;
	private String itemId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_metadata_builder);

		readResources();

		// Start Activity with data
		Bundle bundle = getIntent().getExtras();
		if(bundle != null && bundle.getString("listItemId") != null){
			itemId = bundle.getString("listItemId");
			fillActivityViews(itemId);
		}
	}

	private void readResources() {
		// Metadata
		linearLayoutForMetadataValueFields = (LinearLayout)findViewById(R.id.linearLayoutForMetadataValueFields);
		tvAddValueField = (TextView)findViewById(R.id.addElementValueFields);
		tvRemoveValueField = (TextView)findViewById(R.id.removeElementValueFields);
		metadataName = (EditText)findViewById(R.id.editTextMetaDataName);
		rgType = (RadioGroup)findViewById(R.id.radioGroupType);

		addTextViewListenner();
	}

	private void addTextViewListenner() {
		tvAddValueField.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String hint = getResources().getString(R.string.field_name);
				listMetadataEditText.add(new MetaDataEditText(getBaseContext(), hint, InputType.TYPE_CLASS_TEXT, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				linearLayoutForMetadataValueFields.addView(listMetadataEditText.get(listMetadataEditText.size()-1));
				System.out.println("add");
			}
		});

		tvRemoveValueField.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(listMetadataEditText.size() > 0){
					linearLayoutForMetadataValueFields.removeView(listMetadataEditText.get(listMetadataEditText.size()-1));
					listMetadataEditText.remove(listMetadataEditText.get(listMetadataEditText.size()-1));
					System.out.println("remove");
				}
			}
		});
	}

	public void saveMetadata(View view){
		if(itemId == null){
			// save
			ContentValues metaValues = new ContentValues();

			metaValues.put(VendorMetadata.COLUMN_NAME, metadataName.getText().toString());
			metaValues.put(VendorMetadata.COLUMN_PREFIX, "TODOPREFIX");
			metaValues.put(VendorMetadata.COLUMN_URI, "TODOURI");

			switch (rgType.getCheckedRadioButtonId()) {
			case R.id.rbSingleValue : metaValues.put(VendorMetadata.COLUMN_CARDINALITY, "singleValue");
			break;
			case R.id.rbMultiValue : metaValues.put(VendorMetadata.COLUMN_CARDINALITY, "multiValue");
			break;
			}

			Uri metaId = getContentResolver().insert(DBContentProvider.VENDOR_METADATA_URI, metaValues);
			String id = metaId.getLastPathSegment();

			for(EditText et: listMetadataEditText){
				ContentValues metaAttributValues = new ContentValues();
				metaAttributValues.put(MetaAttributes.COLUMN_NAME, et.getText().toString());
				getContentResolver().insert(Uri.parse(DBContentProvider.VENDOR_METADATA_URI + "/" + id + "/" + DBContentProvider.VENDOR_META_ATTRIBUTES), metaAttributValues);
			}

			Toast.makeText(getBaseContext(), "Metadata: " + metadataName.getText() + " " + getResources().getString(R.string.is_saved), Toast.LENGTH_SHORT).show();
		}else {
			// update
			ContentValues metaValues = new ContentValues();

			metaValues.put(VendorMetadata.COLUMN_NAME, metadataName.getText().toString());
			metaValues.put(VendorMetadata.COLUMN_PREFIX, "TODOPREFIX");
			metaValues.put(VendorMetadata.COLUMN_URI, "TODOURI");

			switch (rgType.getCheckedRadioButtonId()) {
			case R.id.rbSingleValue : metaValues.put(VendorMetadata.COLUMN_CARDINALITY, "singleValue");
			break;
			case R.id.rbMultiValue : metaValues.put(VendorMetadata.COLUMN_CARDINALITY, "multiValue");
			break;
			}

			getContentResolver().update(Uri.parse(DBContentProvider.VENDOR_METADATA_URI + "/" + itemId), metaValues, null, null);

			for(EditText et: listMetadataEditText){
				ContentValues metaAttributValues = new ContentValues();
				metaAttributValues.put(MetaAttributes.COLUMN_NAME, et.getText().toString());
				try{
					getContentResolver().update(Uri.parse(DBContentProvider.VENDOR_METADATA_URI + "/" + itemId + "/" + DBContentProvider.VENDOR_META_ATTRIBUTES + "/" + et.getTag().toString()), metaAttributValues, null, null);
				}catch (IllegalArgumentException e) {
					// For new attributes
					logger.log(Level.DEBUG, "New attribute, try insert", e);
					getContentResolver().insert(Uri.parse(DBContentProvider.VENDOR_METADATA_URI + "/" + itemId + "/" + DBContentProvider.VENDOR_META_ATTRIBUTES), metaAttributValues);
				}
			}

			Toast.makeText(getBaseContext(), "Metadata: " + metadataName.getText() + " " + getResources().getString(R.string.was_update), Toast.LENGTH_SHORT).show();

		}

		finish();
	}

	private void fillActivityViews(String itemId){

		Uri publish_uri = Uri.parse(DBContentProvider.VENDOR_METADATA_URI + "/" + itemId);

		Cursor cursor = getContentResolver().query(
				publish_uri,
				null, null, null, null);

		cursor.moveToNext();

		metadataName.setText(cursor.getString(cursor.getColumnIndex(VendorMetadata.COLUMN_NAME)));
		cursor.close();


		publish_uri = Uri.parse(DBContentProvider.VENDOR_METADATA_URI + "/" + itemId + "/" + DBContentProvider.VENDOR_META_ATTRIBUTES);

		cursor = getContentResolver().query(
				publish_uri,
				null, null, null, null);

		while(cursor.moveToNext()){
			String hint = getResources().getString(R.string.field_name);
			String name = cursor.getString(cursor.getColumnIndex(MetaAttributes.COLUMN_NAME));
			MetaDataEditText etName = new MetaDataEditText(getBaseContext(), hint, InputType.TYPE_CLASS_TEXT, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			etName.setText(name);
			etName.setTag(cursor.getString(cursor.getColumnIndex(MetaAttributes.COLUMN_ID)));
			listMetadataEditText.add(etName);
			linearLayoutForMetadataValueFields.addView(listMetadataEditText.get(listMetadataEditText.size()-1));
		}

		cursor.close();
	}
}
