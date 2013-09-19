package de.hshannover.inform.trust.ifmapj.ironcontrol.view.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Level;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Logger;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LoggerFactory;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.irondetect.IrondetectFragmentActivity;

public class SubscriptionPopUp extends PopUp{

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionPopUp.class);

	private static final CharSequence START_IDENTIFIER_PROMPT = "Start Identifier";


	private Context context;

	private View view;

	private EditText etName, etIdentifierValue;

	private Spinner spIdentifier;

	private CheckBox cbSubscribeAtStartup;

	private ArrayAdapter<CharSequence> spinnerAdapter;

	private SharedPreferences data;

	private boolean checkBox;

	public SubscriptionPopUp(Activity context, int titleId, int messageId) {
		super(context, titleId, messageId);
		logger.log(Level.DEBUG, "New...");
		this.context = context;

		// this
		readResources();
		setSpinnerAdapter();
		setText();
		setCheckBox();

		// super
		setView(view);

		logger.log(Level.DEBUG, "...New");
	}

	private void readResources(){
		// Preference
		data =  PreferenceManager.getDefaultSharedPreferences(context);

		// View
		LayoutInflater inflator = LayoutInflater.from(context);
		view = inflator.inflate(R.layout.irondetect_subscription_popup, null);

		etName = (EditText) view.findViewById(R.id.etSubscribeName);
		spIdentifier = (Spinner) view.findViewById(R.id.spStartIdentifier);
		etIdentifierValue = (EditText) view.findViewById(R.id.etIdentifierValue);
		cbSubscribeAtStartup = (CheckBox) view.findViewById(R.id.cbSubscribeAtStartup);
	}

	private void setSpinnerAdapter(){
		spinnerAdapter = ArrayAdapter.createFromResource(context, R.array.identifier1_list, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spIdentifier.setAdapter(spinnerAdapter);

		// set spinner index
		int spinnerIndex = data.getInt(
				IrondetectFragmentActivity.PREFERENCE_KEY_START_IDENT,
				IrondetectFragmentActivity.PREFERENCE_DEF_START_IDENT);

		spIdentifier.setSelection(spinnerIndex);
	}

	private void setText() {

		etName.setText(data.getString(
				IrondetectFragmentActivity.PREFERENCE_KEY_NAME,
				IrondetectFragmentActivity.PREFERENCE_DEF_NAME));

		etIdentifierValue.setText(data.getString(
				IrondetectFragmentActivity.PREFERENCE_KEY_IDENT_VALUE,
				IrondetectFragmentActivity.PREFERENCE_DEF_IDENT_VALUE));

	}

	private void setCheckBox() {
		checkBox = data.getBoolean(context.getResources().getString(R.string.subscribeAtStartup), false);
		cbSubscribeAtStartup.setChecked(checkBox);
	}

	@Override
	protected void callBack() {
		logger.log(Level.DEBUG, "callBack()...");
		String name = etName.getText().toString();
		String startIdentifier = spIdentifier.getSelectedItem().toString();
		String identifierValue = etIdentifierValue.getText().toString();

		// save preferences
		Editor edit = data.edit();
		edit.putString(IrondetectFragmentActivity.PREFERENCE_KEY_NAME, name);
		edit.putString(IrondetectFragmentActivity.PREFERENCE_KEY_IDENT_VALUE, identifierValue);
		edit.putInt(IrondetectFragmentActivity.PREFERENCE_KEY_START_IDENT, spIdentifier.getSelectedItemPosition());
		edit.putBoolean(context.getResources().getString(R.string.subscribeAtStartup), cbSubscribeAtStartup.isChecked());
		edit.commit();

		// callBack
		((PopUpEvent)context).onClickeSubscriptionPopUp(name, startIdentifier, identifierValue);
		logger.log(Level.DEBUG, "...callBack()");
	}
}