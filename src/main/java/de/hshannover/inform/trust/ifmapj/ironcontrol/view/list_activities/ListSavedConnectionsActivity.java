package de.hshannover.inform.trust.ifmapj.ironcontrol.view.list_activities;

import java.util.List;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.DBContentProvider;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.Connections;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.Connection;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.KeystoreManager;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Level;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LogData;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LogReceiver;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Logger;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LoggerFactory;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.appander.ListAppender;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.ConnectionFragmentActivity;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.dialogs.MultichoiceDialog;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.dialogs.MultichoiceDialogEvent;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.util.ConnectionTask;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.util.ConnectionTask.ConnectTaskEnum;

public class ListSavedConnectionsActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>, MultichoiceDialogEvent, LogReceiver{

	private static final Logger logger = LoggerFactory.getLogger(ListSavedConnectionsActivity.class);

	private static final String MASSAGE_PREFIX = "[msg] ";

	private static final int REMOVE_ID = Menu.FIRST + 1;

	private static final int EDIT_ID = Menu.FIRST + 2;

	private static final int DEFAULT_ID = Menu.FIRST + 3;

	// listAdapter
	private SimpleCursorAdapter adapter;

	// Views
	private TextView tvConnLogs;
	private ScrollView svConnLogs;
	private View selectedView;

	// Others
	private long selectedId = -1;
	private int colorSteelBlue;
	private long defaultConnectionID = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_saved_connections);

		readResources();

		initListAdapter();
		getLoaderManager().initLoader(0, null, this);
		registerForContextMenu(getListView());

		ListAppender la = (ListAppender) logger.getAppender(ListAppender.class);
		if(la != null){
			la.addLogReceiver(this);
		}

		addLogsToView();
	}

	public void connect(View v){
		if(selectedId == -1){
			Toast.makeText(getBaseContext(), R.string.noSelection, Toast.LENGTH_SHORT).show();
		}else{
			// connect
			new ConnectionTask(this, ConnectTaskEnum.CONNECT).execute(selectedId);
		}
	}

	public void disconnect(View v){
		// disconnect
		new ConnectionTask(this, ConnectTaskEnum.DISCONNECT).execute();
	}

	private void addLogsToView() {
		tvConnLogs.setText(getBaseContext().getResources().getString(R.string.waitingForInput)+ "\n");
		for(LogData lData: getLogList()){

			int index = Connection.class.getName().lastIndexOf(".");
			String className = Connection.class.getName().substring(index+1);

			if((lData.getName().equals(className) && lData.getLevel() == Level.INFO) ||
					(lData.getName().equals(className) && lData.getLevel() == Level.ERROR)){
				tvConnLogs.append(MASSAGE_PREFIX +lData.getMessage().toString()+"\n");
			}
		}
	}

	private void initListAdapter(){

		String[] from = new String[]{
				Connections.COLUMN_NAME,
				Connections.COLUMN_ADDRESS,
				Connections.COLUMN_USER,
				Connections.COLUMN_DEFAULT
		};

		int[] to = new int[]{
				R.id.tvLabel,
				R.id.tvServer_address,
				R.id.tvServerUser,
				R.id.tvDefault};

		adapter = new SimpleCursorAdapter(this,
				R.layout.connection_row, null, from, to, 0);

		adapter.setViewBinder(buildViewBinder());

		setListAdapter(adapter);

	}

	private ViewBinder buildViewBinder(){
		ViewBinder vb = new ViewBinder() {
			@Override
			public boolean setViewValue(View v, Cursor c, int i) {
				int defaultCon = c.getInt(c.getColumnIndexOrThrow(Connections.COLUMN_DEFAULT));

				// set default connection
				if(v.getId() == R.id.tvDefault && defaultCon == 1){	// only for tvDefault view and default
					v.setVisibility(View.VISIBLE);
					return true;									// no bind data on view

				}else if(v.getId() == R.id.tvDefault){				// gone all other tvDefault views
					v.setVisibility(View.GONE);
					return true;									// no bind data on view
				}

				// set active connection
				if(c.getInt(c.getColumnIndexOrThrow(Connections.COLUMN_ACTIVE)) == 1 && v.getId() == R.id.tvLabel){
					((TextView)v).setTextColor(getResources().getColor(R.color.GreenYellow));
				}else if(v.getId() == R.id.tvLabel){
					((TextView)v).setTextColor(-4276546);
				}

				return false;
			}
		};
		return vb;
	}

	private void readResources(){
		// resource
		colorSteelBlue = getResources().getColor(R.color.SteelBlue2);

		// view
		tvConnLogs = (TextView) findViewById(R.id.tvConnLogs);
		svConnLogs = (ScrollView) findViewById(R.id.svConnLogs);
	}

	private List<LogData> getLogList(){
		ListAppender la = (ListAppender) logger.getAppender(ListAppender.class);

		if(la == null){
			return null;
		}

		return la.getLogs();
	}

	private void showMultichoiceDialog(int button){

		Cursor cursor = getContentResolver().query(DBContentProvider.CONNECTIONS_URI, null, null, null, null);

		String[] ids = new String[cursor.getCount()];
		String[] labels = new String[cursor.getCount()];

		int index = 0;
		while(cursor.moveToNext()){

			ids[index] = cursor.getString(cursor.getColumnIndexOrThrow(Connections.COLUMN_ID));
			labels[index] = cursor.getString(cursor.getColumnIndexOrThrow(Connections.COLUMN_NAME));
			index++;

		}
		if(labels.length != 0){
			new MultichoiceDialog(this, ids, labels, button).create().show();
		}else {
			Toast.makeText(getBaseContext(), R.string.empty_list_view, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClickeMultichoiceDialogButton(String[] selectedRowIds, int buttonType, boolean multi) {
		switch(buttonType){
		case R.id.buttonRemove: removeListItem(selectedRowIds);
		break;
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// reset old view
		if(selectedView != null){
			selectedView.setBackgroundColor(0);
		}

		// set color
		v.setBackgroundColor(colorSteelBlue);
		// jump to select
		l.setSelection(position);

		selectedView = v;
		selectedId = id;
	}

	@Override
	public void newLogData(final LogData lData) {
		int index = Connection.class.getName().lastIndexOf(".");
		final String className = Connection.class.getName().substring(index+1);

		tvConnLogs.post(new Runnable() {
			@Override
			public void run() {
				if((lData.getName().equals(className) && lData.getLevel() == Level.INFO) ||
						(lData.getName().equals(className) && lData.getLevel() == Level.ERROR)){
					tvConnLogs.append(MASSAGE_PREFIX + lData.getMessage().toString()+"\n");
					scrollToBottom();
				}
			}
		});
	}

	private void scrollToBottom(){
		svConnLogs.post(new Runnable(){
			@Override
			public void run(){
				svConnLogs.smoothScrollTo(0, tvConnLogs.getBottom());
			}
		});
	}

	private void updateDefaultConnections(long id){
		if(defaultConnectionID == -1){
			setDefaultConnectionID();
		}

		Uri connection_uri = Uri.parse(DBContentProvider.CONNECTIONS_URI + "/"+ defaultConnectionID);
		ContentValues oldDefault = new ContentValues();
		oldDefault.put(Connections.COLUMN_DEFAULT, 0);
		getContentResolver().update(connection_uri, oldDefault, null, null);

		ContentValues newDefault = new ContentValues();
		newDefault.put(Connections.COLUMN_DEFAULT, 1);
		connection_uri = Uri.parse(DBContentProvider.CONNECTIONS_URI + "/"+ id);
		getContentResolver().update(connection_uri, newDefault, null, null);

		defaultConnectionID = id;
	}

	private void setDefaultConnectionID() {
		Cursor c = adapter.getCursor();
		c.moveToFirst();

		do{
			if(c.getInt(c.getColumnIndexOrThrow(Connections.COLUMN_DEFAULT)) == 1){
				defaultConnectionID = c.getInt(c.getColumnIndexOrThrow(Connections.COLUMN_ID));
				break;
			}
		}while(c.moveToNext());
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cursorLoader = new CursorLoader(this, DBContentProvider.CONNECTIONS_URI, null, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

	private void removeListItem(String[] selectedRowIds) {
		for (String selectedRowId : selectedRowIds) {
			removeListItem(selectedRowId);
		}
	}

	private void removeListItem(String selectedId){
		Uri uri = Uri.parse(DBContentProvider.CONNECTIONS_URI + "/"	+ selectedId);
		try{
			getContentResolver().delete(uri, null, null);
		} catch (IllegalArgumentException e){
			logger.log(Level.FATAL, e.getMessage(), e);
			Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Context Menu");
		menu.add(0, DEFAULT_ID, 0, R.string.setDefault);
		menu.add(0, EDIT_ID, 0, R.string.edit);
		menu.add(0, REMOVE_ID, 0, R.string.remove);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		String listItemId = Long.toString(info.id);
		switch (item.getItemId()) {
		case DEFAULT_ID : updateDefaultConnections(info.id);
		break;
		case REMOVE_ID : removeListItem(listItemId);
		break;
		case EDIT_ID :
			Intent intent = new Intent(this, ConnectionFragmentActivity.class);
			intent.putExtra("listItemId", listItemId);
			startActivity(intent);
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_saved_connections, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.buttonRemove:
			showMultichoiceDialog(R.id.buttonRemove);
			break;
		case R.id.buttonAdd:
			Intent intent = new Intent(this, ConnectionFragmentActivity.class);
			startActivity(intent);
			break;
		case R.id.buttonKey:
			new Thread(){
				@Override
				public void run(){
					KeystoreManager.addAllCerificateToBKS();

				}
			}.start();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
