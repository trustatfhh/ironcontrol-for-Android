package de.hshannover.inform.trust.ifmapj.ironcontrol.view;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TabHost;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.DBContentProvider;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.Connections;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.Requests;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Logger;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LoggerFactory;

public class SubscribeFragmentActivity extends FragmentActivity {

	private static final Logger logger = LoggerFactory.getLogger(SubscribeFragmentActivity.class);

	private Spinner sStartIdentifier;
	private SeekBar sbMaxDepth;
	private EditText etStartIdentifier, etName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_activity_subscribe);

		Cursor connection_cursor = getContentResolver().query(DBContentProvider.CONNECTIONS_URI, new String[]{Connections.COLUMN_NAME}, null, null, null);
		if(connection_cursor.getCount() == 0){
			createConnectionSettingsDialog().show();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		readResources();

		// Start Activity with data
		Bundle bundle = getIntent().getExtras();
		if(bundle != null && bundle.getString("listItemId") != null){
			fillActivityViews(bundle.getString("listItemId"));
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_subscribe, menu);
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

	public void subscribe(View view){
		FragmentManager fm = getSupportFragmentManager();
		Fragment tabsFragment = fm.findFragmentById(R.id.tabs_fragment);
		TabHost tabHost = (TabHost)tabsFragment.getView().findViewById(android.R.id.tabhost);

		if(tabHost.getCurrentView().getId() == R.id.tab1){
			//Simple Subscribe
			SimpleRequestFragment fSimple = (SimpleRequestFragment) fm.findFragmentByTag(TabFragment.TAB_SIMPLE);
			fSimple.subscription(view);

		}else if(tabHost.getCurrentView().getId() == R.id.tab2){
			//Advanced Subscribe
			AdvancedRequestFragment fAdvanced = (AdvancedRequestFragment) fm.findFragmentByTag(TabFragment.TAB_ADVANCED);
			fAdvanced.subscription(view);
		}
	}

	public void saveSubscribtion(View view){
		FragmentManager fm = getSupportFragmentManager();
		Fragment tabsFragment = fm.findFragmentById(R.id.tabs_fragment);
		TabHost tabHost = (TabHost)tabsFragment.getView().findViewById(android.R.id.tabhost);

		if(tabHost.getCurrentView().getId() == R.id.tab1){
			//Simple Subscribe
			SimpleRequestFragment fSimple = (SimpleRequestFragment) fm.findFragmentByTag(TabFragment.TAB_SIMPLE);
			fSimple.createSubscribeSaveDialog().show();

		}else if(tabHost.getCurrentView().getId() == R.id.tab2){
			//Advanced Subscribe
			AdvancedRequestFragment fAdvanced = (AdvancedRequestFragment) fm.findFragmentByTag(TabFragment.TAB_ADVANCED);
			fAdvanced.createSubscribeSaveDialog().show();
		}
	}

	private void readResources(){
		sStartIdentifier = (Spinner)findViewById(R.id.sIdentifier1);
		etStartIdentifier = (EditText)findViewById(R.id.etIdentifier1);
		sbMaxDepth = (SeekBar)findViewById(R.id.seekBarMaxDepth);
		etName = (EditText)findViewById(R.id.etName);
	}

	private void fillActivityViews(String itemId) {
		Uri publish_uri = Uri.parse(DBContentProvider.SUBSCRIPTION_URI + "/"+ itemId);
		String[] subscription_projection = new String[]{Requests.COLUMN_ID, Requests.COLUMN_IDENTIFIER1, Requests.COLUMN_IDENTIFIER1_Value, Requests.COLUMN_NAME, Requests.COLUMN_MATCH_LINKS, Requests.COLUMN_MAX_DEPTH, Requests.COLUMN_MAX_SITZ, Requests.COLUMN_RESULT_FILTER, Requests.COLUMN_TERMINAL_IDENTIFIER_TYPES, Requests.COLUMN_ACTIVE};

		Cursor publish_cursor = getContentResolver().query(publish_uri, subscription_projection, null, null, null);

		publish_cursor.moveToNext();
		System.out.println(publish_cursor.getCount());
		String name = publish_cursor.getString(publish_cursor.getColumnIndexOrThrow(Requests.COLUMN_NAME));
		String[] identifiers = {publish_cursor.getString(publish_cursor.getColumnIndexOrThrow(Requests.COLUMN_IDENTIFIER1))};
		String[] values = {publish_cursor.getString(publish_cursor.getColumnIndexOrThrow(Requests.COLUMN_IDENTIFIER1_Value))};
		int maxDepth = publish_cursor.getInt(publish_cursor.getColumnIndexOrThrow(Requests.COLUMN_MAX_DEPTH));

		publish_cursor.close();

		etName.setText(name);
		sStartIdentifier.setSelection(getSpinnerIndex(sStartIdentifier, identifiers[0]));
		etStartIdentifier.setText(values[0]);
		sbMaxDepth.setProgress(maxDepth);
	}

	private int getSpinnerIndex(Spinner spinner, String s){
		int index = 0;
		for (int i=0;i<spinner.getCount();i++){
			if (spinner.getItemAtPosition(i).equals(s)){
				index = i;
			}
		}
		return index;
	}

	private Dialog createConnectionSettingsDialog(){
		AlertDialog.Builder connectionSettingsDialog = createDialog(R.string.no_connection_settings, R.string.no_connection_settings_message);

		connectionSettingsDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				Intent intent = new Intent(getBaseContext(), ConnectionFragmentActivity.class);
				startActivity(intent);
			}
		});

		connectionSettingsDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled
			}
		});

		return connectionSettingsDialog.create();
	}
	private AlertDialog.Builder createDialog(int titleId, int messageId){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(titleId);
		alert.setMessage(messageId);
		return alert;
	}
}
