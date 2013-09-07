package de.hshannover.inform.trust.ifmapj.ironcontrol.view;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	@SuppressWarnings("deprecation")
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
		
    }
    
    @SuppressWarnings("deprecation")
	@Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @SuppressWarnings("deprecation")
	@Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

}
