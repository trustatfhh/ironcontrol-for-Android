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
package de.hshannover.f4.trust.ironcontrol.view.util;

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
import de.hshannover.f4.trust.ironcontrol.R;
import de.hshannover.f4.trust.ironcontrol.logger.Level;
import de.hshannover.f4.trust.ironcontrol.logger.Logger;
import de.hshannover.f4.trust.ironcontrol.logger.LoggerFactory;
import de.hshannover.f4.trust.ironcontrol.view.irondetect.IrondetectFragmentActivity;

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