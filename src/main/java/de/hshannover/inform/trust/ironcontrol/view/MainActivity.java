package de.hshannover.inform.trust.ironcontrol.view;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import de.hshannover.inform.trust.ironcontrol.R;
import de.hshannover.inform.trust.ironcontrol.asynctask.ConnectionTask;
import de.hshannover.inform.trust.ironcontrol.asynctask.ConnectionTask.ConnectTaskEnum;
import de.hshannover.inform.trust.ironcontrol.asynctask.PurgePublisherTask;
import de.hshannover.inform.trust.ironcontrol.database.DBContentProvider;
import de.hshannover.inform.trust.ironcontrol.database.entities.Connections;
import de.hshannover.inform.trust.ironcontrol.database.entities.Requests;
import de.hshannover.inform.trust.ironcontrol.exceptions.IronControlUncaughtExceptionHandler;
import de.hshannover.inform.trust.ironcontrol.logger.Level;
import de.hshannover.inform.trust.ironcontrol.logger.Logger;
import de.hshannover.inform.trust.ironcontrol.logger.LoggerFactory;
import de.hshannover.inform.trust.ironcontrol.logger.appander.LogCatAppender;
import de.hshannover.inform.trust.ironcontrol.logger.appander.LogFileAppender;
import de.hshannover.inform.trust.ironcontrol.logger.appander.LogListAppender;
import de.hshannover.inform.trust.ironcontrol.logger.appander.LogToastAppender;
import de.hshannover.inform.trust.ironcontrol.logic.Connection;
import de.hshannover.inform.trust.ironcontrol.logic.KeystoreManager;
import de.hshannover.inform.trust.ironcontrol.view.list_activities.ListOverviewActivity;
import de.hshannover.inform.trust.ironcontrol.view.list_activities.ListSavedConnectionsActivity;
import de.hshannover.inform.trust.ironcontrol.view.list_activities.ListSavedPublishsActivity;
import de.hshannover.inform.trust.ironcontrol.view.list_activities.ListVendorMetadataActivity;
import de.hshannover.inform.trust.ironcontrol.view.logger.LoggerListActivity;
import de.hshannover.inform.trust.ironcontrol.view.util.PopUp;
import de.hshannover.inform.trust.ironcontrol.view.util.PopUpEvent;

/**
 * MainActivity
 * 
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class MainActivity extends Activity implements PopUpEvent{

	private static MainActivity cont;
	private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);

	private EditText popUpInput;

	private static boolean firstRun = true;

	private void firstRun(){
		if(firstRun){

			UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
			Thread.setDefaultUncaughtExceptionHandler(new IronControlUncaughtExceptionHandler(getBaseContext(), handler));

			SharedPreferences prefData = PreferenceManager.getDefaultSharedPreferences(this);

			boolean bAutoConnect = prefData.getBoolean(getString(R.string.PREF_KEY_B_AUTO_CONNECT), false);
			boolean bLogList = prefData.getBoolean(getString(R.string.PREF_KEY_B_LOG_LIST_APPANDER), true);
			boolean bLogCat= prefData.getBoolean(getString(R.string.PREF_KEY_B_LOG_CAT_APPANDER), false);
			boolean bLogFile = prefData.getBoolean(getString(R.string.PREF_KEY_B_LOG_FILE_APPANDER), true);
			boolean bLogToast = prefData.getBoolean(getString(R.string.PREF_KEY_B_LOG_TOAST_APPANDER), true);

			if(bAutoConnect){
				new ConnectionTask(this, ConnectTaskEnum.CONNECT).execute();
			}

			if(bLogFile){
				try {
					Logger.addAppender(new LogFileAppender());
				} catch (IOException e) {
					logger.log(Level.ERROR, "Failed to add the FileAppender!");
				}
			}

			if(bLogCat){
				Logger.addAppender(new LogCatAppender());
			}

			if(bLogList){
				Logger.addAppender(new LogListAppender());
			}

			if(bLogToast){
				Logger.addAppender(new LogToastAppender());
			}

			// reset connections
			ContentValues value = new ContentValues();
			value.put(Connections.COLUMN_ACTIVE, 0);
			getContentResolver().update(DBContentProvider.CONNECTIONS_URI, value, null, null);

			// reset subscriptions
			ContentValues value2 = new ContentValues();
			value2.put(Requests.COLUMN_ACTIVE, 0);
			getContentResolver().update(DBContentProvider.SUBSCRIPTION_URI, value, null, null);

			firstRun = false;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		firstRun();

		cont= this;

		logger.log(Level.DEBUG, "onCreate()...");

		setContentView(R.layout.activity_main);

		setTouchListenerOnLinearLayouts();
		logger.log(Level.DEBUG, "...onCreate()");
	}

	@Override
	public void onResume() {
		super.onResume();
		logger.log(Level.DEBUG, "onResume()");
	}

	@Override
	public void onStart() {
		super.onStart();
		logger.log(Level.DEBUG, "onStart()...");

		//authentication
		try{
			KeystoreManager.checkANDcreateSDCardFolder();
		}catch(Exception e){
			logger.log(Level.ERROR, e.getMessage(), e);
		}
		if(KeystoreManager.isSDMounted()){
			try{
				KeystoreManager.addAllCerificateToBKS();
			}catch (Exception e)
			{
				logger.log(Level.ERROR, e.getMessage(), e);
			}
		}

		logger.log(Level.DEBUG, "...onStart()");
	}

	@Override
	public void onPause() {
		super.onPause();
		logger.log(Level.DEBUG, "onPause()");
	}

	@Override
	public void onStop() {
		super.onStop();
		logger.log(Level.DEBUG, "onStop()");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		logger.log(Level.DEBUG, "onDestroy()");
	}

	private void setTouchListenerOnLinearLayouts() {
		OnTouchListener touchListener = getOnTouchListener();
		LinearLayout ll = (LinearLayout)findViewById(R.id.rootLinearLayout);
		for(int i=0; i<ll.getChildCount(); i++){
			if (ll.getChildAt(i) instanceof LinearLayout) {
				((LinearLayout) ll.getChildAt(i)).setOnTouchListener(touchListener);
			}
		}
	}

	private OnTouchListener getOnTouchListener(){
		return new OnTouchListener() {
			int colorSteelBlue = getResources().getColor(R.color.SteelBlue2);
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN : v.setBackgroundColor(colorSteelBlue);
				break;
				case MotionEvent.ACTION_UP : v.setBackgroundColor(0);
				break;
				case MotionEvent.ACTION_CANCEL : v.setBackgroundColor(0);
				break;
				}
				return false;
			}
		};
	}

	//	@Override
	//	public void onBackPressed(){
	//		finish();
	//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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

	public void startPurgePublisherPopUp(View v) {
		popUpInput = new EditText(getContext());

		String publisherId = Connection.getPublisherId();

		if(publisherId != null){
			popUpInput.setText(publisherId);
		}else {
			popUpInput.setHint("Publisher ID");
		}

		PopUp pp = new PopUp(this, R.string.purgePublisher, R.string.purgePublisherMessage);
		pp.setView(popUpInput);
		pp.create().show();
	}

	public void startSettingsActivity(View v) {
		Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
		startActivity(intent);
	}

	public void startSearchActivity(View v) {
		Intent intent = new Intent(getBaseContext(), SearchFragmentActivity.class);
		startActivity(intent);
	}

	public void startSavedSearchActivity(View v) {
		Intent intent = new Intent(getBaseContext(), ListOverviewActivity.class);
		intent.setAction(getResources().getString(R.string.ACTION_SAVED_SEARCHS));
		startActivity(intent);
	}

	public void startSubscribeActivity(View v) {
		Intent intent = new Intent(getBaseContext(), SubscribeFragmentActivity.class);
		startActivity(intent);
	}

	public void startSavedSubscribeActivity(View v) {
		Intent intent = new Intent(getBaseContext(), ListOverviewActivity.class);
		intent.setAction(getResources().getString(R.string.ACTION_SAVED_SUBSCRIPTIONS));
		startActivity(intent);
	}

	public void startPublishActivity(View v) {
		Intent intent = new Intent(getBaseContext(), PublishActivity.class);
		startActivity(intent);
	}

	public void startSavedPublishActivity(View v) {
		Intent intent = new Intent(getBaseContext(), ListSavedPublishsActivity.class);
		startActivity(intent);
	}

	public void startVendorSpecificMetadataBuilderActivity(View v) {
		Intent intent = new Intent(getBaseContext(), ListVendorMetadataActivity.class);
		startActivity(intent);
	}

	public static Context getContext(){
		return cont;
	}

	public void startLoggerActivity(View v) {
		Intent intent = new Intent(getBaseContext(), LoggerListActivity.class);
		startActivity(intent);
	}

	public void startSettingsApplication(View v) {
		Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
		intent.setAction(getResources().getString(R.string.ACTION_APPLICATION_SETTINGS));
		startActivity(intent);
	}

	public void startSettingsConnections(View v) {
		Intent intent = new Intent(getBaseContext(), ListSavedConnectionsActivity.class);
		startActivity(intent);
	}

	@Override
	public void onClickePopUp() {
		new PurgePublisherTask(this, popUpInput.getText().toString()).execute();
	}

	@Override
	public boolean onClickeSavePopUp(String savedName) {return false;}

}
