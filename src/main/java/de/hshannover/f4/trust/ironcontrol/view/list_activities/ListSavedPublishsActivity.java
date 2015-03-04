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
 * This file is part of ironcontrol for android, version 1.0.2, implemented by the Trust@HsH research group at the Hochschule Hannover.
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
package de.hshannover.f4.trust.ironcontrol.view.list_activities;

import java.util.List;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;
import de.hshannover.f4.trust.ironcontrol.R;
import de.hshannover.f4.trust.ironcontrol.asynctask.PublishTask;
import de.hshannover.f4.trust.ironcontrol.database.DBContentProvider;
import de.hshannover.f4.trust.ironcontrol.database.entities.Requests;
import de.hshannover.f4.trust.ironcontrol.logger.Level;
import de.hshannover.f4.trust.ironcontrol.logger.Logger;
import de.hshannover.f4.trust.ironcontrol.logger.LoggerFactory;
import de.hshannover.f4.trust.ironcontrol.view.PublishActivity;
import de.hshannover.f4.trust.ironcontrol.view.dialogs.MultichoiceListDialog;
import de.hshannover.f4.trust.ironcontrol.view.dialogs.MultichoiceListEvent;
import de.hshannover.f4.trust.ironcontrol.view.dialogs.MultichoicePublishDialog;
import de.hshannover.f4.trust.ironcontrol.view.dialogs.MultichoiceRemoveDialog;

public class ListSavedPublishsActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>, MultichoiceListEvent {

	private static final Logger logger = LoggerFactory.getLogger(ListVendorMetadataActivity.class);

	private static final int REMOVE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = Menu.FIRST + 2;
	private static final int UPDATE_ID = Menu.FIRST + 3;
	private static final int NOTIFY_ID = Menu.FIRST + 4;
	private static final int DELETE_ID = Menu.FIRST + 5;

	private SimpleCursorAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view);
		fillData();
		registerForContextMenu(getListView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_saved_publishs, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		MultichoiceListDialog dialog = null;
		switch(item.getItemId()){
		case R.id.bPublishUpdate : dialog = new MultichoicePublishDialog(this, DBContentProvider.PUBLISH_URI, R.string.publish_update, item.getItemId());
		break;
		case R.id.bPublishNotify : dialog = new MultichoicePublishDialog(this, DBContentProvider.PUBLISH_URI, R.string.publish_notify, item.getItemId());
		break;
		case R.id.bPublishDelete : dialog = new MultichoicePublishDialog(this, DBContentProvider.PUBLISH_URI, R.string.publish_delete, item.getItemId());
		break;
		case R.id.bRemove: dialog = new MultichoiceRemoveDialog(this, DBContentProvider.PUBLISH_URI, R.string.remove, item.getItemId());
		}

		if(!dialog.isEmpty()){
			dialog.create().show();
		}else {
			Toast.makeText(getBaseContext(), R.string.empty_list_view, Toast.LENGTH_SHORT).show();
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Context Menu");
		menu.add(0, UPDATE_ID, 0, R.string.publish_update);
		menu.add(0, NOTIFY_ID, 0, R.string.publish_notify);
		menu.add(0, DELETE_ID, 0, R.string.publish_delete);
		menu.add(0, EDIT_ID, 0, R.string.edit);
		menu.add(0, REMOVE_ID, 0, R.string.remove);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		String listItemId = Long.toString(info.id);
		switch (item.getItemId()) {
		case REMOVE_ID : removePublish(listItemId);
		break;
		case EDIT_ID : startEditActivity(listItemId);
		break;
		case UPDATE_ID : createPublish(listItemId, R.id.bPublishUpdate);
		break;
		case NOTIFY_ID : createPublish(listItemId, R.id.bPublishNotify);
		break;
		case DELETE_ID : createPublish(listItemId, R.id.bPublishDelete);
		break;
		}
		return super.onContextItemSelected(item);
	}

	private void startEditActivity(String listItemId) {
		Intent intent = new Intent(getBaseContext(), PublishActivity.class);
		intent.putExtra("listItemId", listItemId);
		startActivity(intent);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Toast.makeText(this, "TODO: Detail Activity is coming soon!", Toast.LENGTH_LONG).show();

	}

	private void fillData() {
		String[] from = new String[]{Requests.COLUMN_NAME, Requests.COLUMN_IDENTIFIER1, Requests.COLUMN_METADATA};
		int[] to = new int[]{R.id.label, R.id.label_info1, R.id.label_info2};
		getLoaderManager().initLoader(0, null, this);
		adapter = new SimpleCursorAdapter(this, R.layout.publish_list_row, null, from, to, 0);
		setListAdapter(adapter);
	}

	// Creates a new loader after the initLoader() was call
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = {Requests.COLUMN_ID, Requests.COLUMN_METADATA, Requests.COLUMN_IDENTIFIER1, Requests.COLUMN_IDENTIFIER1_Value, Requests.COLUMN_IDENTIFIER2, Requests.COLUMN_IDENTIFIER2_Value, Requests.COLUMN_NAME, Requests.COLUMN_LIFE_TIME};
		CursorLoader cursorLoader = new CursorLoader(this, DBContentProvider.PUBLISH_URI, projection, null, null, null);
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

	private void removePublish(List<String> selectedRowIds) {
		for (String selectedRowId : selectedRowIds) {
			removePublish(selectedRowId);
		}
	}

	private void removePublish(String selectedId){
		Uri uri = Uri.parse(DBContentProvider.PUBLISH_URI + "/"	+ selectedId);
		try{
			getContentResolver().delete(uri, null, null);
		} catch (IllegalArgumentException e){
			logger.log(Level.FATAL, e.getMessage(), e);
			Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	private void createPublish(String selectedRowId, int buttonId){
		String[] oneString= {selectedRowId};
		createPublish(oneString, buttonId, false);
	}

	private void createPublish(String[] selectedRowIds, int buttonType, boolean multi){
		new PublishTask(this, selectedRowIds, buttonType, multi).execute();
	}

	@Override
	public void onClickeMultichoiceDialogButton(List<String> selectedRowIds, int resIdButton, int clicked) {
		boolean multi = false;
		switch( clicked ){
		case DialogInterface.BUTTON_NEUTRAL: multi = true;
		}

		switch(resIdButton){
		case R.id.bRemove: removePublish(selectedRowIds);
		break;
		case R.id.bPublishUpdate: createPublish(selectedRowIds.toArray(new String[selectedRowIds.size()]), resIdButton, multi);
		break;
		case R.id.bPublishNotify: createPublish(selectedRowIds.toArray(new String[selectedRowIds.size()]), resIdButton, multi);
		break;
		case R.id.bPublishDelete: createPublish(selectedRowIds.toArray(new String[selectedRowIds.size()]), resIdButton, multi);
		break;
		}
	}
}
