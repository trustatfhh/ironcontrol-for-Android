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
package de.hshannover.f4.trust.ironcontrol.view.irondetect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import de.hshannover.f4.trust.ironcontrol.R;
import de.hshannover.f4.trust.ironcontrol.asynctask.SubscriptionTask;
import de.hshannover.f4.trust.ironcontrol.logger.Level;
import de.hshannover.f4.trust.ironcontrol.logger.Logger;
import de.hshannover.f4.trust.ironcontrol.logger.LoggerFactory;
import de.hshannover.f4.trust.ironcontrol.logic.data.Operation;
import de.hshannover.f4.trust.ironcontrol.view.SettingsActivity;
import de.hshannover.f4.trust.ironcontrol.view.util.PopUpEvent;
import de.hshannover.f4.trust.ironcontrol.view.util.SubscriptionPopUp;

public class IrondetectFragmentActivity  extends FragmentActivity implements PopUpEvent{

	private static final Logger logger = LoggerFactory.getLogger(IrondetectFragmentActivity.class);

	private PageAdapter mPageAdapter;

	private SharedPreferences preferenceData;

	public static final String PREFERENCE_KEY_NAME = "SubscribeName";

	public static final String PREFERENCE_DEF_NAME = "irondetect";

	public static final String PREFERENCE_KEY_IDENT_VALUE = "startIdentifierValue";

	public static final String PREFERENCE_DEF_IDENT_VALUE = "irondetect-status";

	public static final String PREFERENCE_KEY_START_IDENT = "startIdentifier";

	public static final int PREFERENCE_DEF_START_IDENT = 3;	// default Device

	private static boolean first = true;

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		logger.log(Level.DEBUG, "New...");
		setContentView(R.layout.fragment_activity_irondetect);

		// set PageAdapter
		mPageAdapter = new PageAdapter(getBaseContext(), getSupportFragmentManager());
		ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(mPageAdapter);

		// read Preferences
		preferenceData =  PreferenceManager.getDefaultSharedPreferences(getBaseContext());

		int spinnerIndex = preferenceData.getInt(
				PREFERENCE_KEY_START_IDENT,
				PREFERENCE_DEF_START_IDENT);

		String name = preferenceData.getString(
				PREFERENCE_KEY_NAME,
				PREFERENCE_DEF_NAME);

		String startIdentifier = getResources().getStringArray(
				R.array.identifier1_list)[spinnerIndex];

		String identifierValue = preferenceData.getString(
				PREFERENCE_KEY_IDENT_VALUE,
				PREFERENCE_DEF_IDENT_VALUE);

		boolean subscribAtStartup = preferenceData.getBoolean(
				getResources().getString(R.string.subscribeAtStartup),
				false);

		// start subscrib or show pupup
		if(subscribAtStartup){

			new SubscriptionTask(this, name, startIdentifier, identifierValue, 0, null, Operation.UPDATE).execute();

		}else if(first){

			new SubscriptionPopUp(this, R.string.irondetectSubscribe, R.string.irondetectPDPSubscrption).show();
			first = false;

		}

		logger.log(Level.DEBUG, "...New");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_irondetect, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_subscribe:
			new SubscriptionPopUp(
					this,
					R.string.irondetectSubscribe,
					R.string.irondetectPDPSubscrption).show();

			return true;

		case R.id.menu_subscribe_delete:
			String name = preferenceData.getString(
					PREFERENCE_KEY_NAME,
					PREFERENCE_DEF_NAME);

			new SubscriptionTask(this, name, null, null, 0, null, Operation.DELETE).execute();

			return true;

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
	public void onClickePopUp() {}

	@Override
	public boolean onClickeSavePopUp(String savedName) {return false;}

	@Override
	public void onClickeSubscriptionPopUp(String subscribeName, String startIdentifier, String identifierValue) {
		new SubscriptionTask(this, subscribeName, startIdentifier, identifierValue, 0, null, Operation.UPDATE).execute();
	}

}