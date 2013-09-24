package de.hshannover.inform.trust.ironcontrol.view.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import de.hshannover.inform.trust.ironcontrol.R;
import de.hshannover.inform.trust.ironcontrol.database.DBContentProvider;
import de.hshannover.inform.trust.ironcontrol.database.entities.MetaAttributes;
import de.hshannover.inform.trust.ironcontrol.database.entities.VendorMetadata;

public class MetadataValueFieldsBuilder {

	private class AttributeData {
		String attribute;
		boolean required;

		public AttributeData(String s, boolean b){
			attribute = s;
			required = b;
		}
	}

	private Activity context;
	private LinearLayout metadataValueFields;

	private List<String> metaList;
	private HashMap<String,String> metadataMap;

	public MetadataValueFieldsBuilder(Activity context, int linearLayoutResId){

		this.context = context;
		this.metaList = Util.getMetaList(context, R.array.metadaten_list);
		this.metadataValueFields = (LinearLayout)context.findViewById(linearLayoutResId);

	}

	private void addValueFields(String selectedItem) {
		List<MetaDataLinearLayout> aList = buildAttributesList(selectedItem);

		if(aList != null){

			for(MetaDataLinearLayout l: aList){

				metadataValueFields.addView(l);

			}
		}
	}

	private void setAttributesValue(HashMap<String,String> metadataMap){

		for(int i=0; i<metadataValueFields.getChildCount(); i++){

			LinearLayout ll = (LinearLayout)metadataValueFields.getChildAt(i);

			for(int y=0; y<ll.getChildCount(); y++){

				if(ll.getChildAt(y) instanceof  EditText){
					EditText eT = (EditText)ll.getChildAt(y);
					eT.setText(metadataMap.get(eT.getHint().toString()));
				}
			}
		}
	}

	public void setValueFieldsFor(String selectedItem) {
		metadataValueFields.removeAllViews();
		addValueFields(selectedItem);

		if(metadataMap != null){						// ugly, it set the value for attributes
			setAttributesValue(metadataMap);			// when call onActivityResult in PublishActivity
			metadataMap = null;
		}
	}

	private List<MetaDataLinearLayout> buildAttributesList(String selectedItem) {
		List<AttributeData> aList = null;


		if (selectedItem.equals(metaList.get(5))) {		// capability
			aList = buildCapabilityAttributesList();

		} else if (selectedItem.equals(metaList.get(6))) {		// device-attribute
			aList = buildDeviceAttributeAttributesList();

		} else if (selectedItem.equals(metaList.get(7))) {		// device-characteristic
			aList = buildDeviceCharacteristicAttributesList();

		} else if (selectedItem.equals(metaList.get(10))) {		// enforcement-report
			aList = buildEnforcementReportAttributesList();
			return buildLinearLayoutListFor_Enforcement_Report(aList);

		} else if (selectedItem.equals(metaList.get(11))) {		// event
			aList = buildEventAttributesList();
			return buildLinearLayoutListFor_Event(aList);

		} else if (selectedItem.equals(metaList.get(12))) {		// ip-mac
			aList = buildIPMACAttributesList();

		} else if (selectedItem.equals(metaList.get(13))) {		// layer2-information
			aList = buildLayer2InformationAttributesList();

		} else if (selectedItem.equals(metaList.get(14))) {		// location
			aList = buildLocationAttributesList();
			return buildLinearLayoutListFor_Location(aList);

		} else if (selectedItem.equals(metaList.get(16))) {		// role
			aList = buildRoleAttributesList();

		} else if (selectedItem.equals(metaList.get(17))) {		// unexpected-behavior
			aList = buildUnexpectedBehaviorAttributesList();
			return buildLinearLayoutListFor_Unexpected_Behavior(aList);

		} else if (selectedItem.equals(metaList.get(18))) {		// WlanInformation
			aList = buildWlanInformationAttributesList();
			return buildLinearLayoutListFor_WlanInformation(aList);

		} else if (metaList.contains(selectedItem)) {			// For all other Metadata with empty attributes
			return null;

		}else if(selectedItem.equals("")) {		// For the SpinnerPrompt
			return null;

		} else {	// For VendorMetadata attributes
			aList = buildVendorSpecificMetaAttributesList(selectedItem);
		}

		return buildLinearLayoutList(aList);
	}

	private List<MetaDataLinearLayout> buildLinearLayoutListFor_Unexpected_Behavior (List<AttributeData> data){

		MetaDataLinearLayout lLayout1 = buildLinearLayout(data.get(0), data.get(1));
		MetaDataLinearLayout lLayout2 = buildLinearLayout(data.get(2), data.get(3));
		MetaDataLinearLayout lLayout3 = buildLinearLayoutWith_EditText_Spinner(data.get(4), data.get(5), R.array.significance_enum);
		MetaDataLinearLayout lLayout4 = buildLinearLayout(data.get(6), null);

		List<MetaDataLinearLayout> llList = new ArrayList<MetaDataLinearLayout>();
		llList.add(lLayout1);
		llList.add(lLayout2);
		llList.add(lLayout3);
		llList.add(lLayout4);

		return llList;
	}

	private List<MetaDataLinearLayout> buildLinearLayoutListFor_Event (List<AttributeData> data){

		MetaDataLinearLayout lLayout1 = buildLinearLayout(data.get(0), data.get(1));
		MetaDataLinearLayout lLayout2 = buildLinearLayout(data.get(2), data.get(3));
		MetaDataLinearLayout lLayout3 = buildLinearLayoutWith_EditText_Spinner(data.get(4), data.get(5), R.array.significance_enum);
		MetaDataLinearLayout lLayout4 = buildLinearLayoutWith_EditText_Spinner(data.get(6), data.get(7), R.array.event_type_enum);
		MetaDataLinearLayout lLayout5 = buildLinearLayout(data.get(8), data.get(9));

		List<MetaDataLinearLayout> llList = new ArrayList<MetaDataLinearLayout>();
		llList.add(lLayout1);
		llList.add(lLayout2);
		llList.add(lLayout3);
		llList.add(lLayout4);
		llList.add(lLayout5);

		return llList;
	}

	private List<MetaDataLinearLayout> buildLinearLayoutListFor_Enforcement_Report (List<AttributeData> data){

		MetaDataLinearLayout lLayout1 = buildLinearLayoutWith_EditText_Spinner(data.get(0), data.get(1), R.array.enforcement_action_enum);

		MetaDataLinearLayout lLayout2 = buildLinearLayoutWith_EditText_Spinner(data.get(2), null, 0);	// no spinner

		List<MetaDataLinearLayout> llList = new ArrayList<MetaDataLinearLayout>();
		llList.add(lLayout1);
		llList.add(lLayout2);

		return llList;
	}

	private List<MetaDataLinearLayout> buildLinearLayoutListFor_Location (List<AttributeData> data){
		List<MetaDataLinearLayout> lLayout = buildLinearLayoutList(data);

		MetaDataLinearLayout lLocationInformation = new MetaDataLinearLayout(context, LinearLayout.HORIZONTAL);
		lLocationInformation.setGravity(Gravity.CENTER);

		TextView tvLocationInformation = new TextView(context);
		tvLocationInformation.setText("Location Information");
		tvLocationInformation.setTextSize(15);

		lLocationInformation.addView(tvLocationInformation);

		lLayout.add(0, lLocationInformation);

		return lLayout;
	}

	private List<MetaDataLinearLayout> buildLinearLayoutListFor_WlanInformation (List<AttributeData> data){

		MetaDataLinearLayout lLayout1 = buildLinearLayoutWith_EditText_Spinner(data.get(0), data.get(1), R.array.wlan_security_enum);

		MetaDataLinearLayout lLayout2 = buildLinearLayoutWith_EditText_Spinner(data.get(2), data.get(3), R.array.wlan_security_enum);

		List<MetaDataLinearLayout> llList = new ArrayList<MetaDataLinearLayout>();
		llList.add(lLayout1);
		llList.add(lLayout2);

		return llList;
	}

	private MetaDataLinearLayout buildLinearLayoutWith_EditText_Spinner(AttributeData aData1, AttributeData aData2, int resourcesArrayId) {
		MetaDataLinearLayout lLayout1 = new MetaDataLinearLayout(context, LinearLayout.HORIZONTAL);

		MetaDataEditText etMeta = new MetaDataEditText(context, aData1.attribute, aData1.required);
		lLayout1.addView(etMeta);

		if(aData2 != null){

			RequiredSpinnerAdapter sAdapter = new RequiredSpinnerAdapter(context, aData2.attribute, resourcesArrayId, aData2.required);
			Spinner s = new Spinner(context);
			s.setTag(aData2.attribute);
			s.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f));
			s.setAdapter(sAdapter);
			lLayout1.addView(s);

		}
		return lLayout1;
	}

	private List<MetaDataLinearLayout> buildLinearLayoutList (List<AttributeData> data){

		if(data == null){
			return null;
		}

		List<MetaDataLinearLayout> llList = new ArrayList<MetaDataLinearLayout>();

		for(int i=0; i < data.size(); i= i+2){	// add max. 2  on a LinearLayout

			MetaDataLinearLayout lLayout;

			if(i+1 < data.size()){

				lLayout = buildLinearLayout(data.get(i), data.get(i+1));

			}else{

				lLayout = buildLinearLayout(data.get(i), null);

			}

			llList.add(lLayout);
		}


		return llList;
	}

	private MetaDataLinearLayout buildLinearLayout (AttributeData aData1, AttributeData aData2){
		MetaDataLinearLayout newLayout = new MetaDataLinearLayout(context, LinearLayout.HORIZONTAL);

		MetaDataEditText tView1 = new MetaDataEditText(context, aData1.attribute, aData1.required);
		newLayout.addView(tView1);

		if(aData2 != null){
			MetaDataEditText tView2 = new MetaDataEditText(context, aData2.attribute, aData2.required);
			newLayout.addView(tView2);
		}

		return newLayout;
	}

	private List<AttributeData> buildVendorSpecificMetaAttributesList(String selectedItem) {
		List<AttributeData> list = new ArrayList<AttributeData>();
		String metaID = getMetadataId(selectedItem);

		if(metaID == null){				// No VendorMetadata for selectedItem was saved
			return null;
		}

		Cursor metaAttributes = getMetadataAttributes(metaID);

		if(metaAttributes == null){		// VendorMetadata has no attributes
			return null;
		}

		while(metaAttributes.moveToNext()){
			list.add(new AttributeData(metaAttributes.getString(metaAttributes.getColumnIndex(MetaAttributes.COLUMN_NAME)), false));
		}

		metaAttributes.close();
		return list;
	}

	private Cursor getMetadataAttributes(String metaID) {

		Cursor metaAttributes = context.getContentResolver().query(
				Uri.parse(DBContentProvider.VENDOR_METADATA_URI + "/" + metaID + "/" + DBContentProvider.VENDOR_META_ATTRIBUTES),
				null, null, null, null);

		if(metaAttributes.getCount() == 0){
			return null;
		}

		return metaAttributes;
	}

	private String getMetadataId(String selectedItem) {

		Cursor metaId = context.getContentResolver().query(
				DBContentProvider.VENDOR_METADATA_URI,			// uri
				new String[]{VendorMetadata.COLUMN_ID},			// projection
				VendorMetadata.COLUMN_NAME + "=?",				// select
				new String[]{selectedItem},						// select value
				null);

		if(metaId.getCount() !=1){		// too much was found
			metaId.close();
			return null;
		}

		metaId.moveToFirst();
		String metaIdString = metaId.getString(metaId.getColumnIndex(VendorMetadata.COLUMN_ID));
		metaId.close();

		return metaIdString;
	}

	private List<AttributeData> buildCapabilityAttributesList() {
		List<AttributeData> list = new ArrayList<AttributeData>();
		list.add(new AttributeData(context.getResources().getString(R.string.meta_name), true));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_administrative_domain), false));
		return list;
	}

	private List<AttributeData> buildDeviceAttributeAttributesList() {
		List<AttributeData> list = new ArrayList<AttributeData>();
		list.add(new AttributeData(context.getResources().getString(R.string.meta_name), true));
		return list;
	}

	private List<AttributeData> buildDeviceCharacteristicAttributesList() {
		List<AttributeData> list = new ArrayList<AttributeData>();
		list.add(new AttributeData(context.getResources().getString(R.string.meta_manufacturer), false));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_model), false));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_os), false));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_os_version), false));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_device_type), false));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_discovered_time), true));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_discoverer_id), true));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_discovery_method), true));
		return list;
	}

	private List<AttributeData> buildEnforcementReportAttributesList() {
		List<AttributeData> list = new ArrayList<AttributeData>();
		list.add(new AttributeData(context.getResources().getString(R.string.meta_other_type_definition), false));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_enforcement_action), true));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_enforcement_reason), false));
		return list;
	}

	private List<AttributeData> buildEventAttributesList() {
		List<AttributeData> list = new ArrayList<AttributeData>();
		list.add(new AttributeData(context.getResources().getString(R.string.meta_name), true));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_discovered_time), true));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_discoverer_id), true));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_magnitude), true));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_confidence), true));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_significance), true));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_other_type_definition), false));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_type), false));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_information), false));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_vulnerability_uri), false));
		return list;
	}

	private List<AttributeData> buildIPMACAttributesList() {
		List<AttributeData> list = new ArrayList<AttributeData>();
		list.add(new AttributeData(context.getResources().getString(R.string.meta_start_time), false));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_end_time), false));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_dhcp_server), false));
		return list;
	}

	private List<AttributeData> buildLayer2InformationAttributesList() {
		List<AttributeData> list = new ArrayList<AttributeData>();
		list.add(new AttributeData(context.getResources().getString(R.string.meta_vlan), false));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_vlan_name), false));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_port), false));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_administrative_domain), false));
		return list;
	}

	private List<AttributeData> buildLocationAttributesList() {
		List<AttributeData> list = new ArrayList<AttributeData>();
		list.add(new AttributeData(context.getResources().getString(R.string.meta_type), true));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_value), true));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_discovered_time), true));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_discoverer_id), true));
		return list;
	}

	private List<AttributeData> buildRoleAttributesList() {
		List<AttributeData> list = new ArrayList<AttributeData>();
		list.add(new AttributeData(context.getResources().getString(R.string.meta_administrative_domain), false));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_name), true));
		return list;
	}

	private List<AttributeData> buildUnexpectedBehaviorAttributesList() {
		List<AttributeData> list = new ArrayList<AttributeData>();
		list.add(new AttributeData(context.getResources().getString(R.string.meta_discovered_time), true));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_discoverer_id), true));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_information), false));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_magnitude), false));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_confidence), false));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_significance), true));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_type), false));
		return list;
	}

	private List<AttributeData> buildWlanInformationAttributesList() {
		List<AttributeData> list = new ArrayList<AttributeData>();
		list.add(new AttributeData(context.getResources().getString(R.string.meta_ssid), false));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_ssid_unicast_security), true));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_ssid_group_security), true));
		list.add(new AttributeData(context.getResources().getString(R.string.meta_ssid_management_security), true));
		return list;
	}

	public void setMetadataMap(HashMap<String, String> metadataMap) {
		this.metadataMap = metadataMap;
	}

}
