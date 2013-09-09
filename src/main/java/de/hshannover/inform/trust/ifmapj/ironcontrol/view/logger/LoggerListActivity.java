package de.hshannover.inform.trust.ifmapj.ironcontrol.view.logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Level;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LogData;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Logger;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LoggerFactory;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.appander.ListAppender;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.dialogs.MultichoiceDialog;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.dialogs.MultichoiceDialogEvent;

public class LoggerListActivity extends ListActivity implements MultichoiceDialogEvent{

	private static final Logger logger = LoggerFactory.getLogger(LoggerListActivity.class);

	private Set<String> filterNames;

	private LoggerListArrayAdapter mAdapter;

	boolean info, warn, error, fatal, debug;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view);
		filterNames = new HashSet<String>();
		mAdapter = new LoggerListArrayAdapter(getApplicationContext(), buildLogList());
		setListAdapter(mAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// index for a inverted list
		int index = l.getAdapter().getCount() -1 -position;
		LogData data = (LogData) l.getAdapter().getItem(index);
		LoggerPopUp popUp = new LoggerPopUp(this, data);
		popUp.create().show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_logger_list, menu);

		menu.findItem(R.id.info).setChecked(info);
		menu.findItem(R.id.warn).setChecked(warn);
		menu.findItem(R.id.error).setChecked(error);
		menu.findItem(R.id.fatal_error).setChecked(fatal);
		menu.findItem(R.id.debug).setChecked(debug);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.clear: clearLog();
		break;

		case R.id.filter:
			String[] labels = buildFilterLabels();
			new MultichoiceDialog(this, labels, labels, item.getItemId()).create().show();
			break;

		default:
			Editor edit = getSharedPreferences("LogLevel", MODE_PRIVATE).edit();
			if(item.isChecked()){
				item.setChecked(false);
			}else {
				item.setChecked(true);
			}
			edit.putBoolean(item.getTitle().toString(), item.isChecked());
			edit.commit();
			onChangeList();
			break;
		}
		return false;
	}

	private String[] buildFilterLabels(){
		List<LogData> logData = buildLogList();
		Set<String> labels = new HashSet<String>();

		for(LogData ld: logData){	// add to set, no duplicate
			labels.add(ld.getName());
		}

		return labels.toArray(new String[labels.size()]);
	}

	private List<LogData> buildLogList(){
		readSharedPreferences();
		List<LogData> logList = new ArrayList<LogData>();
		List<LogData> appenderLogList = getLogList();
		if(appenderLogList != null){
			for (LogData ld : appenderLogList) {
				if(filterNames.isEmpty() || filterNames.contains(ld.getName())){
					if (ld.getLevel() == Level.INFO && info) {
						logList.add(ld);
					} else if (ld.getLevel() == Level.WARN && warn) {
						logList.add(ld);
					} else if (ld.getLevel() == Level.ERROR && error) {
						logList.add(ld);
					} else if (ld.getLevel() == Level.FATAL && fatal) {
						logList.add(ld);
					} else if (ld.getLevel() == Level.DEBUG && debug) {
						logList.add(ld);
					}
				}

			}
		}
		return logList;
	}

	private void readSharedPreferences(){
		SharedPreferences data =  getSharedPreferences("LogLevel", MODE_PRIVATE);
		info = data.getBoolean(getResources().getString(R.string.info), true);
		warn = data.getBoolean(getResources().getString(R.string.warn), false);
		error = data.getBoolean(getResources().getString(R.string.error), true);
		fatal = data.getBoolean(getResources().getString(R.string.fatal_error), true);
		debug = data.getBoolean(getResources().getString(R.string.debug), false);
	}

	private List<LogData> getLogList(){
		ListAppender la = (ListAppender) logger.getAppender(ListAppender.class);

		if(la == null){
			return null;
		}

		return la.getLogs();
	}

	private void onChangeList() {
		//TODO [MR] Noch version abfragen um ab api 11 addAll zu machen
		//		mAdapter.clear();
		//		mAdapter.addAll(list);
		mAdapter = new LoggerListArrayAdapter(getApplicationContext(), buildLogList());
		setListAdapter(mAdapter);
	}

	private void clearLog(){
		logger.getAppender(ListAppender.class).clear();
		getLogList().clear();
		mAdapter.clear();
		Toast.makeText(getApplicationContext(), "Log was clear", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClickeMultichoiceDialogButton(String[] selectedRowIds, int buttonType, boolean multi) {
		Set<String> newFilterNames = new HashSet<String>();
		newFilterNames.addAll(Arrays.asList(selectedRowIds));
		this.filterNames = newFilterNames;
		onChangeList();
	}
}