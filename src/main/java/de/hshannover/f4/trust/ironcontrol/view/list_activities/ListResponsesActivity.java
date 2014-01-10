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
package de.hshannover.f4.trust.ironcontrol.view.list_activities;

import java.util.List;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.hshannover.f4.trust.ironcontrol.R;
import de.hshannover.f4.trust.ironcontrol.database.DBContentProvider;
import de.hshannover.f4.trust.ironcontrol.database.entities.Responses;
import de.hshannover.f4.trust.ironcontrol.logger.Level;
import de.hshannover.f4.trust.ironcontrol.logger.Logger;
import de.hshannover.f4.trust.ironcontrol.logger.LoggerFactory;
import de.hshannover.f4.trust.ironcontrol.view.dialogs.MultichoiceListEvent;
import de.hshannover.f4.trust.ironcontrol.view.dialogs.MultichoiceRemoveDialog;

public class ListResponsesActivity extends ListHierarchyActivity implements MultichoiceListEvent{

	private static final Logger logger = LoggerFactory.getLogger(ListResponsesActivity.class);

	private static final int REMOVE_ID = Menu.FIRST + 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerForContextMenu(getListView());
	}

	@Override
	protected SimpleCursorAdapter setListAdapter(ListHierarchyType type) {
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.responses_list_row, null, FROM_RESPONSES, TO_LIST2_ROW, 0);
		adapter.setViewBinder(buildViewBinder());
		return adapter;
	}

	private ViewBinder buildViewBinder(){
		ViewBinder vb = new ViewBinder() {
			@Override
			public boolean setViewValue(View v, Cursor c, int i) {
				int newResponses = c.getInt(c.getColumnIndexOrThrow(Responses.COLUMN_NEW));

				// set tvNew VISIBLE
				if(v.getId() == R.id.tvNew && newResponses == 0){	// only for label tvNew and new Responses
					((TextView)v).setVisibility(TextView.VISIBLE);
					return true;	// no bind data on view
				}else if(v.getId() == R.id.tvNew){
					((TextView)v).setVisibility(TextView.GONE);
					return true;	// no bind data on view
				}

				return false;
			}
		};
		return vb;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if(v.findViewById(R.id.tvNew).getVisibility() == View.VISIBLE){
			ContentValues value = new ContentValues();
			value.put(Responses.COLUMN_NEW, 1);
			getContentResolver().update(Uri.parse(DBContentProvider.RESPONSES_URI + "/" + id), value, null, null);
		}

		Intent intent = new Intent(this, ListResultItemsActivity.class);
		intent.setAction(mAction);
		intent.putExtra(EXTRA_ID_KEY, String.valueOf(id));
		startActivity(intent);
	}

	@Override
	protected Loader<Cursor> onCreateLoader(int id, Bundle args, ListHierarchyType type) {
		Uri uri = null;
		switch(type){
		case SEARCH: uri = Uri.parse(DBContentProvider.SEARCH_URI + "/" + id + "/" + DBContentProvider.RESPONSES);
		break;
		case SUBSCRIPTION: uri = Uri.parse(DBContentProvider.SUBSCRIPTION_URI + "/" + id + "/" + DBContentProvider.RESPONSES);
		break;
		}
		CursorLoader cursorLoader = new CursorLoader(this, uri, null, null, null, Responses.COLUMN_DATE + " DESC, " + Responses.COLUMN_TIME + " DESC");
		return cursorLoader;
	}

	@Override
	protected boolean onCreateOptionsMenu(Menu menu, ListHierarchyType type) {
		switch(type){
		case SEARCH:
			getMenuInflater().inflate(R.menu.activity_saved_searches, menu);
			break;
		case SUBSCRIPTION:
			getMenuInflater().inflate(R.menu.activity_saved_subscription, menu);
			break;
		}
		return true;
	}

	@Override
	protected void onOptionsItemSelected(MenuItem item, ListHierarchyType type) {
		switch(item.getItemId()){
		case R.id.bRemove:
			Uri uri = null;
			switch(type){
			case SEARCH: uri = Uri.parse(DBContentProvider.SEARCH_URI + "/" + lastID + "/" + DBContentProvider.RESPONSES);
			break;
			case SUBSCRIPTION: uri = Uri.parse(DBContentProvider.SUBSCRIPTION_URI + "/" + lastID + "/" + DBContentProvider.RESPONSES);
			break;
			}

			new MultichoiceRemoveDialog(
					this,
					uri,
					R.string.remove, item.getItemId()).create().show();
			break;

		case R.id.bSearch: search(lastID);
		break;
		case R.id.bSubscribeUpdate: subscribeUpdate(lastID);
		break;
		case R.id.bSubscribeDelete: subscribeDelete(lastID);
		break;
		}
	}

	@Override
	protected void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo, ListHierarchyType type) {
		menu.add(0, REMOVE_ID, 0, R.string.remove);
	}

	@Override
	protected void onContextItemSelected(MenuItem item, ListHierarchyType type) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		String listItemId = Long.toString(info.id);
		switch (item.getItemId()) {
		case REMOVE_ID : remove(listItemId, type);
		break;
		}
	}

	@Override
	protected void remove(String selectedId, ListHierarchyType type) {
		Uri uri = null;
		switch(type){
		case SEARCH: uri = Uri.parse(DBContentProvider.SEARCH_URI + "/" + lastID + "/" + DBContentProvider.RESPONSES + "/" + selectedId);
		break;
		case SUBSCRIPTION: uri = Uri.parse(DBContentProvider.SUBSCRIPTION_URI + "/" + lastID + "/" + DBContentProvider.RESPONSES + "/" + selectedId);
		break;
		}
		try{
			getContentResolver().delete(uri, null, null);
		} catch (IllegalArgumentException e){
			logger.log(Level.FATAL, e.getMessage(), e);
			Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClickeMultichoiceDialogButton(List<String> selectedRowIds, int resIdButton, int clicked) {
		switch(resIdButton){
		case R.id.bRemove: remove(selectedRowIds);
		break;
		}
	}
}
