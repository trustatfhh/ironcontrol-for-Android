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
package de.hshannover.f4.trust.ironcontrol.view;

import java.io.IOException;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import de.hshannover.f4.trust.ironcontrol.R;
import de.hshannover.f4.trust.ironcontrol.logger.Level;
import de.hshannover.f4.trust.ironcontrol.logger.Logger;
import de.hshannover.f4.trust.ironcontrol.logger.LoggerFactory;
import de.hshannover.f4.trust.ironcontrol.logger.appander.LogCatAppender;
import de.hshannover.f4.trust.ironcontrol.logger.appander.LogFileAppender;
import de.hshannover.f4.trust.ironcontrol.logger.appander.LogListAppender;
import de.hshannover.f4.trust.ironcontrol.logger.appander.LogToastAppender;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	private static final Logger logger = LoggerFactory.getLogger(SettingsActivity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String action = getIntent().getAction();

		if (action != null && action.equals(getResources().getString(R.string.ACTION_APPLICATION_SETTINGS))) {
			addPreferencesFromResource(R.xml.preference_application_settings);
		}else if(true){
			// Load the legacy preferences headers
			addPreferencesFromResource(R.xml.preference_headers_legacy);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sP, String key) {
		if(key.equals(getString(R.string.PREF_KEY_B_LOG_LIST_APPANDER))){

			if(sP.getBoolean(key, true)){

				Logger.addAppender(new LogListAppender());

			}else{

				Logger.removeAppender(LogListAppender.class);

			}

		}else if(key.equals(getString(R.string.PREF_KEY_B_LOG_CAT_APPANDER))){

			if(sP.getBoolean(key, false)){

				Logger.addAppender(new LogCatAppender());

			}else{

				Logger.removeAppender(LogCatAppender.class);

			}

		}else if(key.equals(getString(R.string.PREF_KEY_B_LOG_FILE_APPANDER))){

			if(sP.getBoolean(key, true)){

				try {
					Logger.addAppender(new LogFileAppender());
				} catch (IOException e) {
					logger.log(Level.ERROR, e.toString(), e);
				}

			}else{

				Logger.removeAppender(LogFileAppender.class);

			}

		}else if(key.equals(getString(R.string.PREF_KEY_B_LOG_TOAST_APPANDER))){

			if(sP.getBoolean(key, false)){

				Logger.addAppender(new LogToastAppender());

			}else{

				Logger.removeAppender(LogToastAppender.class);

			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

}
