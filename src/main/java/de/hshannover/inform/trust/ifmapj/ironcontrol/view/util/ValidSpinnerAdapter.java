package de.hshannover.inform.trust.ifmapj.ironcontrol.view.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Level;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Logger;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LoggerFactory;

/**
 * Use this adapter to Define your 'Prompt' layout as the first
 * row in the returned choices. If you do this, you probably don't want a
 * prompt on your spinner or it'll have two 'Select' rows.
 * Set a Spinner-Type for a valid identifier and metadata combination.
 * 
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class ValidSpinnerAdapter extends PromptSpinnerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ValidSpinnerAdapter.class);

	private HashMap<String, Set<String>> validNodeMap1, validNodeMap2;

	private Spinner sIdentifier1, sIdentifier2, sMetaDaten;

	private Node spinnerType;

	private static List<String> identifier1List;
	private static List<String> identifier2List;
	private static List<String> metadataList;

	private ValidSpinnerAdapter(Activity context, CharSequence prompt, int textArrayResId, List<CharSequence> data, Node spinnerType) {
		super(context, prompt, textArrayResId, data);
		logger.log(Level.DEBUG, "NEW " + spinnerType + " ...");

		this.spinnerType = spinnerType;

		if(identifier1List == null || identifier2List == null || metadataList == null){
			identifier1List = Util.getMetaList(context, R.array.identifier1_list);
			identifier2List = Util.getMetaList(context, R.array.identifier2_list);
			metadataList = Util.getMetaList(context, R.array.metadaten_list);
		}

		buildValidNodeMaps(spinnerType);
		readResources(context);

		logger.log(Level.DEBUG, "...NEW");
	}

	/**
	 * Use this constructor to Define your 'Prompt' layout as the first
	 * row in the returned choices. If you do this, you probably don't want a
	 * prompt on your spinner or it'll have two 'Select' rows.
	 * Set a Spinner-Type for a valid identifier and metadata combination.
	 * 
	 * @param context			The Activity Context
	 * @param prompt			The first TextView in your Spinner
	 * @param data			 	The the array to use as the data source.
	 * @param spinnerType		The type of this Spinner (METADATA / IDENTIFIER1 / IDENTIFIER2)
	 */

	public ValidSpinnerAdapter(Activity context, CharSequence prompt, List<CharSequence> data, Node spinnerType) {
		this(context, prompt, -1, data, spinnerType);
	}

	/**
	 * Use this constructor to Define your 'Prompt' layout as the first
	 * row in the returned choices. If you do this, you probably don't want a
	 * prompt on your spinner or it'll have two 'Select' rows.
	 * Set a Spinner-Type for a valid identifier and metadata combination.
	 * 
	 * @param context			The Activity Context
	 * @param prompt			The first TextView in your Spinner
	 * @param textArrayResId 	The the array to use as the data source.
	 * @param spinnerType		The type of this Spinner (METADATA / IDENTIFIER1 / IDENTIFIER2)
	 */

	public ValidSpinnerAdapter(Activity context, CharSequence prompt, int textArrayResId, Node spinnerType) {
		this(context, prompt, textArrayResId, null, spinnerType);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {		// set the invalid View not enabled
		View v = super.getDropDownView(position, convertView, parent);

		if (v instanceof TextView) {
			TextView tvDropDown = (TextView) v;

			if(!isValid(tvDropDown.getText().toString())){
				v.setEnabled(false);
			}

		} else {		// no TextView
			return v;
		}
		return v;
	}

	public boolean isValid(String sView){

		String key1, key2;
		Set<String> validSet1 = null, validSet2 = null;

		// get the valid sets
		switch(spinnerType){
		case METADATA:
			key1 = (String) sIdentifier1.getSelectedItem();
			key2 = (String) sIdentifier2.getSelectedItem();
			validSet1 = validNodeMap1.get(key1);
			validSet2 = validNodeMap2.get(key2);
			break;
		case IDENTIEFIER1:
			key1 = (String) sMetaDaten.getSelectedItem();
			key2 = (String) sIdentifier2.getSelectedItem();
			validSet1 = validNodeMap1.get(key1);
			validSet2 = validNodeMap1.get(key2);
			break;
		case IDENTIEFIER2:
			key1 = (String) sIdentifier1.getSelectedItem();
			key2 = (String) sMetaDaten.getSelectedItem();
			validSet1 = validNodeMap1.get(key1);
			validSet2 = validNodeMap1.get(key2);
			break;
		}

		if(validSet1 != null){					// get Set of Metadaten for valid identifier1

			if(!validSet1.contains(sView)){		// is the DropDownView Text in the Set?
				return false;
			}

			if(validSet2 != null){

				if(!(validSet1.contains(sView) && validSet2.contains(sView))){		// is the DropDownView Text in the Set?
					return false;
				}

			}

		} else if(validSet2 != null){

			if(!validSet2.contains(sView)){		// is the DropDownView Text in the Set?
				return false;
			}
		}
		return true;
	}

	private void buildValidNodeMaps(Node spinnerType){
		switch(spinnerType){
		case METADATA:
			validNodeMap1 = buildMetadataWithIdentifier1Map();
			validNodeMap2 = buildMetadataWithIdentifier2Map();;
			break;
		case IDENTIEFIER1: validNodeMap1 = buildIdentifier1Map();
		break;
		case IDENTIEFIER2: validNodeMap1 = buildIdentifier2Map();
		break;
		}
	}

	private HashMap<String, Set<String>> buildIdentifier1Map(){
		HashMap<String, Set<String>> metaMap = new HashMap<String, Set<String>>();

		/**
		 * Metadata
		 */

		// IdentifierSet für Metadata: access-request-device
		Set<String> identifierSet1 = new HashSet<String>();
		identifierSet1.add(identifier1List.get(0));

		// IdentifierSet für Metadata: access-request-ip
		Set<String> identifierSet2 = new HashSet<String>();
		identifierSet2.add(identifier1List.get(0));

		// IdentifierSet für Metadata: access-request-mac
		Set<String> identifierSet3 = new HashSet<String>();
		identifierSet3.add(identifier1List.get(0));

		// IdentifierSet für Metadata: authenticated-as
		Set<String> identifierSet4 = new HashSet<String>();
		identifierSet4.add(identifier1List.get(0));

		// IdentifierSet für Metadata: authenticated-by
		Set<String> identifierSet5 = new HashSet<String>();
		identifierSet5.add(identifier1List.get(0));

		// IdentifierSet für Metadata: capability
		Set<String> identifierSet6 = new HashSet<String>();
		identifierSet6.add(identifier1List.get(0));

		// IdentifierSet für Metadata: device-attribute
		Set<String> identifierSet7 = new HashSet<String>();
		identifierSet7.add(identifier1List.get(0));

		// IdentifierSet für Metadata: device-characteristic
		Set<String> identifierSet8 = new HashSet<String>();
		identifierSet8.add(identifier1List.get(0));
		identifierSet8.add(identifier1List.get(1));
		identifierSet8.add(identifier1List.get(2));

		// IdentifierSet für Metadata: device-ip
		Set<String> identifierSet9 = new HashSet<String>();
		identifierSet9.add(identifier1List.get(3));

		// IdentifierSet für Metadata: discovered-by
		Set<String> identifierSet10 = new HashSet<String>();
		identifierSet10.add(identifier1List.get(1));
		identifierSet10.add(identifier1List.get(2));

		// IdentifierSet für Metadata: enforcement-report
		Set<String> identifierSet11 = new HashSet<String>();
		identifierSet11.add(identifier1List.get(1));
		identifierSet11.add(identifier1List.get(2));

		// IdentifierSet für Metadata: event
		Set<String> identifierSet12 = new HashSet<String>();
		identifierSet12.add(identifier1List.get(0));
		identifierSet12.add(identifier1List.get(1));
		identifierSet12.add(identifier1List.get(2));
		identifierSet12.add(identifier1List.get(4));

		// IdentifierSet für Metadata: ip-mac
		Set<String> identifierSet13 = new HashSet<String>();
		identifierSet13.add(identifier1List.get(1));

		// IdentifierSet für Metadata: layer2-information
		Set<String> identifierSet14 = new HashSet<String>();
		identifierSet14.add(identifier1List.get(0));

		// IdentifierSet für Metadata: location
		Set<String> identifierSet15 = new HashSet<String>();
		identifierSet15.add(identifier1List.get(1));
		identifierSet15.add(identifier1List.get(2));
		identifierSet15.add(identifier1List.get(4));

		// IdentifierSet für Metadata: request-for-investigation
		Set<String> identifierSet16 = new HashSet<String>();
		identifierSet16.add(identifier1List.get(1));
		identifierSet16.add(identifier1List.get(2));

		// IdentifierSet für Metadata: role
		Set<String> identifierSet17 = new HashSet<String>();
		identifierSet17.add(identifier1List.get(0));

		// IdentifierSet für Metadata: unexpected-behavior
		Set<String> identifierSet18 = new HashSet<String>();
		identifierSet18.add(identifier1List.get(0));
		identifierSet18.add(identifier1List.get(1));
		identifierSet18.add(identifier1List.get(2));
		identifierSet18.add(identifier1List.get(4));

		// IdentifierSet für Metadata: wlan-information
		Set<String> identifierSet19 = new HashSet<String>();
		identifierSet19.add(identifier1List.get(0));

		// access-request-device
		metaMap.put(metadataList.get(0), identifierSet1);
		// access-request-ip
		metaMap.put(metadataList.get(1), identifierSet2);
		// access-request-mac
		metaMap.put(metadataList.get(2), identifierSet3);
		// authenticated-as
		metaMap.put(metadataList.get(3), identifierSet4);
		// authenticated-by
		metaMap.put(metadataList.get(4), identifierSet5);
		// capability
		metaMap.put(metadataList.get(5), identifierSet6);
		// device-attribute
		metaMap.put(metadataList.get(6), identifierSet7);
		// device-characteristic
		metaMap.put(metadataList.get(7), identifierSet8);
		// device-ip
		metaMap.put(metadataList.get(8), identifierSet9);
		// discovered-by
		metaMap.put(metadataList.get(9), identifierSet10);
		// enforcement-report
		metaMap.put(metadataList.get(10), identifierSet11);
		// event
		metaMap.put(metadataList.get(11), identifierSet12);
		// ip-mac
		metaMap.put(metadataList.get(12), identifierSet13);
		// layer2-information
		metaMap.put(metadataList.get(13), identifierSet14);
		// location
		metaMap.put(metadataList.get(14), identifierSet15);
		// request-for-investigation
		metaMap.put(metadataList.get(15), identifierSet16);
		// role
		metaMap.put(metadataList.get(16), identifierSet17);
		// unexpected-behavior
		metaMap.put(metadataList.get(17), identifierSet18);
		// wlan-information
		metaMap.put(metadataList.get(18), identifierSet19);

		/**
		 * Identifier 2
		 */

		// IdentifierSet für Identifier2: ip-address
		Set<String> identifierSet20 = new HashSet<String>();
		identifierSet20.add(identifier1List.get(0));
		identifierSet20.add(identifier1List.get(3));

		// IdentifierSet für Identifier2: mac-address
		Set<String> identifierSet21 = new HashSet<String>();
		identifierSet21.add(identifier1List.get(0));
		identifierSet21.add(identifier1List.get(1));

		// IdentifierSet für Identifier2: device
		Set<String> identifierSet22 = new HashSet<String>();
		identifierSet22.add(identifier1List.get(0));
		identifierSet22.add(identifier1List.get(1));
		identifierSet22.add(identifier1List.get(2));

		// IdentifierSet für Identifier2: identity
		Set<String> identifierSet23 = new HashSet<String>();
		identifierSet23.add(identifier1List.get(0));

		// IdentifierSet für Identifier2: none
		Set<String> identifierSet24 = new HashSet<String>();
		identifierSet24.add(identifier1List.get(0));
		identifierSet24.add(identifier1List.get(1));
		identifierSet24.add(identifier1List.get(2));
		identifierSet24.add(identifier1List.get(4));

		// ip-address
		metaMap.put(identifier2List.get(0), identifierSet20);
		// mac-address
		metaMap.put(identifier2List.get(1), identifierSet21);
		// device
		metaMap.put(identifier2List.get(2), identifierSet22);
		// identity
		metaMap.put(identifier2List.get(3), identifierSet23);
		// none
		metaMap.put(identifier2List.get(4), identifierSet24);

		return metaMap;
	}

	private HashMap<String, Set<String>> buildIdentifier2Map(){
		HashMap<String, Set<String>> metaMap = new HashMap<String, Set<String>>();

		/**
		 * Metadata
		 */

		// IdentifierSet für Metadata: access-request-device
		Set<String> identifierSet1 = new HashSet<String>();
		identifierSet1.add(identifier2List.get(2));

		// IdentifierSet für Metadata: access-request-ip
		Set<String> identifierSet2 = new HashSet<String>();
		identifierSet2.add(identifier2List.get(0));

		// IdentifierSet für Metadata: access-request-mac
		Set<String> identifierSet3 = new HashSet<String>();
		identifierSet3.add(identifier2List.get(1));

		// IdentifierSet für Metadata: authenticated-as
		Set<String> identifierSet4 = new HashSet<String>();
		identifierSet4.add(identifier2List.get(3));

		// IdentifierSet für Metadata: authenticated-by
		Set<String> identifierSet5 = new HashSet<String>();
		identifierSet5.add(identifier2List.get(2));

		// IdentifierSet für Metadata: capability
		Set<String> identifierSet6 = new HashSet<String>();
		identifierSet6.add(identifier2List.get(4));

		// IdentifierSet für Metadata: device-attribute
		Set<String> identifierSet7 = new HashSet<String>();
		identifierSet7.add(identifier2List.get(2));

		// IdentifierSet für Metadata: device-characteristic
		Set<String> identifierSet8 = new HashSet<String>();
		identifierSet8.add(identifier2List.get(2));

		// IdentifierSet für Metadata: device-ip
		Set<String> identifierSet9 = new HashSet<String>();
		identifierSet9.add(identifier2List.get(0));

		// IdentifierSet für Metadata: discovered-by
		Set<String> identifierSet10 = new HashSet<String>();
		identifierSet10.add(identifier2List.get(2));

		// IdentifierSet für Metadata: enforcement-report
		Set<String> identifierSet11 = new HashSet<String>();
		identifierSet11.add(identifier2List.get(2));

		// IdentifierSet für Metadata: event
		Set<String> identifierSet12 = new HashSet<String>();
		identifierSet12.add(identifier2List.get(4));

		// IdentifierSet für Metadata: ip-mac
		Set<String> identifierSet13 = new HashSet<String>();
		identifierSet13.add(identifier2List.get(1));

		// IdentifierSet für Metadata: layer2-information
		Set<String> identifierSet14 = new HashSet<String>();
		identifierSet14.add(identifier2List.get(2));

		// IdentifierSet für Metadata: location
		Set<String> identifierSet15 = new HashSet<String>();
		identifierSet15.add(identifier2List.get(4));

		// IdentifierSet für Metadata: request-for-investigation
		Set<String> identifierSet16 = new HashSet<String>();
		identifierSet16.add(identifier2List.get(2));

		// IdentifierSet für Metadata: role
		Set<String> identifierSet17 = new HashSet<String>();
		identifierSet17.add(identifier2List.get(3));

		// IdentifierSet für Metadata: unexpected-behavior
		Set<String> identifierSet18 = new HashSet<String>();
		identifierSet18.add(identifier2List.get(4));

		// IdentifierSet für Metadata: wlan-information
		Set<String> identifierSet19 = new HashSet<String>();
		identifierSet19.add(identifier2List.get(2));

		// access-request-device
		metaMap.put(metadataList.get(0), identifierSet1);
		// access-request-ip
		metaMap.put(metadataList.get(1), identifierSet2);
		// access-request-mac
		metaMap.put(metadataList.get(2), identifierSet3);
		// authenticated-as
		metaMap.put(metadataList.get(3), identifierSet4);
		// authenticated-by
		metaMap.put(metadataList.get(4), identifierSet5);
		// capability
		metaMap.put(metadataList.get(5), identifierSet6);
		// device-attribute
		metaMap.put(metadataList.get(6), identifierSet7);
		// device-characteristic
		metaMap.put(metadataList.get(7), identifierSet8);
		// device-ip
		metaMap.put(metadataList.get(8), identifierSet9);
		// discovered-by
		metaMap.put(metadataList.get(9), identifierSet10);
		// enforcement-report
		metaMap.put(metadataList.get(10), identifierSet11);
		// event
		metaMap.put(metadataList.get(11), identifierSet12);
		// ip-mac
		metaMap.put(metadataList.get(12), identifierSet13);
		// layer2-information
		metaMap.put(metadataList.get(13), identifierSet14);
		// location
		metaMap.put(metadataList.get(14), identifierSet15);
		// request-for-investigation
		metaMap.put(metadataList.get(15), identifierSet16);
		// role
		metaMap.put(metadataList.get(16), identifierSet17);
		// unexpected-behavior
		metaMap.put(metadataList.get(17), identifierSet18);
		// wlan-information
		metaMap.put(metadataList.get(18), identifierSet19);

		/**
		 * Identifier 1
		 */

		// IdentifierSet für Identifier1: access-request
		Set<String> identifierSet20 = new HashSet<String>();
		identifierSet20.add(identifier2List.get(0));
		identifierSet20.add(identifier2List.get(1));
		identifierSet20.add(identifier2List.get(2));
		identifierSet20.add(identifier2List.get(3));
		identifierSet20.add(identifier2List.get(4));

		// IdentifierSet für Identifier1: ip-address
		Set<String> identifierSet21 = new HashSet<String>();
		identifierSet21.add(identifier2List.get(1));
		identifierSet21.add(identifier2List.get(2));
		identifierSet21.add(identifier2List.get(4));

		// IdentifierSet für Identifier1: mac-address
		Set<String> identifierSet22 = new HashSet<String>();
		identifierSet22.add(identifier2List.get(2));
		identifierSet22.add(identifier2List.get(4));

		// IdentifierSet für Identifier1: device
		Set<String> identifierSet23 = new HashSet<String>();
		identifierSet23.add(identifier2List.get(0));

		// IdentifierSet für Identifier1: identity
		Set<String> identifierSet24 = new HashSet<String>();
		identifierSet24.add(identifier2List.get(4));

		// access-request
		metaMap.put(identifier1List.get(0), identifierSet20);
		// ip-address
		metaMap.put(identifier1List.get(1), identifierSet21);
		// mac-address
		metaMap.put(identifier1List.get(2), identifierSet22);
		// device
		metaMap.put(identifier1List.get(3), identifierSet23);
		// identity
		metaMap.put(identifier1List.get(4), identifierSet24);

		return metaMap;
	}

	private HashMap<String, Set<String>> buildMetadataWithIdentifier1Map(){
		HashMap<String, Set<String>> identifierMap = new HashMap<String, Set<String>>();

		// MetaSet für Identifier1: access-request
		Set<String> metaSet1 = new HashSet<String>();
		metaSet1.add(metadataList.get(0));
		metaSet1.add(metadataList.get(1));
		metaSet1.add(metadataList.get(2));
		metaSet1.add(metadataList.get(3));
		metaSet1.add(metadataList.get(4));
		metaSet1.add(metadataList.get(5));
		metaSet1.add(metadataList.get(6));
		metaSet1.add(metadataList.get(7));
		metaSet1.add(metadataList.get(11));
		metaSet1.add(metadataList.get(13));
		metaSet1.add(metadataList.get(16));
		metaSet1.add(metadataList.get(17));
		metaSet1.add(metadataList.get(18));

		// MetaSet für Identifier1: ip-address
		Set<String> metaSet2 = new HashSet<String>();
		metaSet2.add(metadataList.get(7));
		metaSet2.add(metadataList.get(9));
		metaSet2.add(metadataList.get(10));
		metaSet2.add(metadataList.get(11));
		metaSet2.add(metadataList.get(12));
		metaSet2.add(metadataList.get(14));
		metaSet2.add(metadataList.get(15));
		metaSet2.add(metadataList.get(17));

		// MetaSet für Identifier1: mac-address
		Set<String> metaSet3 = new HashSet<String>();
		metaSet3.add(metadataList.get(7));
		metaSet3.add(metadataList.get(9));
		metaSet3.add(metadataList.get(10));
		metaSet3.add(metadataList.get(11));
		metaSet3.add(metadataList.get(14));
		metaSet3.add(metadataList.get(15));
		metaSet3.add(metadataList.get(17));

		// MetaSet für Identifier1: device
		Set<String> metaSet4 = new HashSet<String>();
		metaSet4.add(metadataList.get(8));

		// MetaSet für Identifier1: identity
		Set<String> metaSet5 = new HashSet<String>();
		metaSet5.add(metadataList.get(11));
		metaSet5.add(metadataList.get(14));
		metaSet5.add(metadataList.get(17));

		// access-request
		identifierMap.put(identifier1List.get(0), metaSet1);
		// ip-address
		identifierMap.put(identifier1List.get(1), metaSet2);
		// mac-address
		identifierMap.put(identifier1List.get(2), metaSet3);
		// device
		identifierMap.put(identifier1List.get(3), metaSet4);
		// identity
		identifierMap.put(identifier1List.get(4), metaSet5);

		return identifierMap;
	}

	private HashMap<String, Set<String>> buildMetadataWithIdentifier2Map(){
		HashMap<String, Set<String>> identifierMap = new HashMap<String, Set<String>>();

		// MetaSet für Identifier2: ip-address
		Set<String> metaSet1 = new HashSet<String>();
		metaSet1.add(metadataList.get(1));
		metaSet1.add(metadataList.get(8));

		// MetaSet für Identifier2: mac-address
		Set<String> metaSet2 = new HashSet<String>();
		metaSet2.add(metadataList.get(2));
		metaSet2.add(metadataList.get(12));

		// MetaSet für Identifier2: device
		Set<String> metaSet3 = new HashSet<String>();
		metaSet3.add(metadataList.get(0));
		metaSet3.add(metadataList.get(4));
		metaSet3.add(metadataList.get(6));
		metaSet3.add(metadataList.get(7));
		metaSet3.add(metadataList.get(9));
		metaSet3.add(metadataList.get(10));
		metaSet3.add(metadataList.get(13));
		metaSet3.add(metadataList.get(15));
		metaSet3.add(metadataList.get(18));

		// MetaSet für Identifier2: identity
		Set<String> metaSet4 = new HashSet<String>();
		metaSet4.add(metadataList.get(3));
		metaSet4.add(metadataList.get(16));

		// MetaSet für Identifier2: none
		Set<String> metaSet5 = new HashSet<String>();
		metaSet5.add(metadataList.get(5));
		metaSet5.add(metadataList.get(11));
		metaSet5.add(metadataList.get(14));
		metaSet5.add(metadataList.get(17));

		// ip-address
		identifierMap.put(identifier2List.get(0), metaSet1);
		// mac-address
		identifierMap.put(identifier2List.get(1), metaSet2);
		// device
		identifierMap.put(identifier2List.get(2), metaSet3);
		// identity
		identifierMap.put(identifier2List.get(3), metaSet4);
		// none
		identifierMap.put(identifier2List.get(4), metaSet5);

		return identifierMap;
	}

	private void readResources(Activity context){
		sMetaDaten = (Spinner)context.findViewById(R.id.metaDataSpinner);
		sIdentifier1 = (Spinner)context.findViewById(R.id.sIdentifier1);
		sIdentifier2 = (Spinner)context.findViewById(R.id.sIdentifier2);
	}

}
