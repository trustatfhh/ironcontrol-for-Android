package de.hshannover.inform.trust.ifmapj.ironcontrol.view;

import java.io.IOException;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Level;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Logger;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LoggerFactory;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.appander.LogCatAppender;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.appander.LogFileAppender;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.appander.LogListAppender;

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
				logger.log(Level.INFO, "LogListAppender add");
			}else{
				Logger.removeAppender(LogListAppender.class);
				logger.log(Level.INFO, "LogListAppender remove");
			}
		}else if(key.equals(getString(R.string.PREF_KEY_B_LOG_CAT_APPANDER))){
			if(sP.getBoolean(key, false)){
				Logger.addAppender(new LogCatAppender());
				logger.log(Level.INFO, "LogCatAppender add");
			}else{
				Logger.removeAppender(LogCatAppender.class);
				logger.log(Level.INFO, "LogCatAppender remove");
			}
		}else if(key.equals(getString(R.string.PREF_KEY_B_LOG_FILE_APPANDER))){
			if(sP.getBoolean(key, true)){
				try {
					Logger.addAppender(new LogFileAppender());
				} catch (IOException e) {
					logger.log(Level.ERROR, e.toString(), e);
				}
				logger.log(Level.INFO, "LogFileAppender add");
			}else{
				Logger.removeAppender(LogFileAppender.class);
				logger.log(Level.INFO, "LogFileAppender remove");
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
