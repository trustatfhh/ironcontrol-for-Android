package de.hshannover.inform.trust.ifmapj.ironcontrol.view.irondetect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.asynctask.SubscriptionTask;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.Operation;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Level;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Logger;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LoggerFactory;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.SettingsActivity;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.util.PopUpEvent;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.util.SubscriptionPopUp;

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