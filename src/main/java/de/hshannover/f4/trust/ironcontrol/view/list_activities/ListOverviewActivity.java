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
import de.hshannover.f4.trust.ironcontrol.database.entities.Requests;
import de.hshannover.f4.trust.ironcontrol.logger.Level;
import de.hshannover.f4.trust.ironcontrol.logger.Logger;
import de.hshannover.f4.trust.ironcontrol.logger.LoggerFactory;
import de.hshannover.f4.trust.ironcontrol.view.dialogs.MultichoiceListDialog;
import de.hshannover.f4.trust.ironcontrol.view.dialogs.MultichoiceListEvent;
import de.hshannover.f4.trust.ironcontrol.view.dialogs.MultichoiceRemoveDialog;
import de.hshannover.f4.trust.ironcontrol.view.dialogs.MultichoiceSearchDialog;
import de.hshannover.f4.trust.ironcontrol.view.dialogs.MultichoiceSubscribeDialog;

public class ListOverviewActivity extends ListHierarchyActivity implements MultichoiceListEvent{

	private static final Logger logger = LoggerFactory.getLogger(ListResponsesActivity.class);

	private static final int REMOVE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = Menu.FIRST + 2;
	private static final int SEARCH_ID = Menu.FIRST + 3;
	private static final int SUBSCRIPTION_UPDATE_ID = Menu.FIRST + 4;
	private static final int SUBSCRIPTION_DELETE_ID = Menu.FIRST + 5;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerForContextMenu(getListView());

		getLoaderManager().initLoader(-1, null, this);
	}

	@Override
	protected SimpleCursorAdapter setListAdapter(ListHierarchyType type) {
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.search_list_row, null, FROM_REQUESTS, TO_LIST1_ROW, 0);
		switch (type) {
		case SEARCH:
			break;
		case SUBSCRIPTION: adapter.setViewBinder(buildViewBinder());
		break;
		}
		return adapter;
	}

	private ViewBinder buildViewBinder(){
		ViewBinder vb = new ViewBinder() {
			@Override
			public boolean setViewValue(View v, Cursor c, int i) {
				int activeSub = c.getInt(c.getColumnIndexOrThrow(Requests.COLUMN_ACTIVE));

				// set active subscription
				if(v.getId() == R.id.label && activeSub == 1){	// only for label view and active subscription
					((TextView)v).setTextColor(getResources().getColor(R.color.GreenYellow));
				}else if(v.getId() == R.id.label){				// reset color all other label views
					((TextView)v).setTextColor(-4276546);
				}

				return false;
			}
		};
		return vb;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this, ListResponsesActivity.class);
		intent.setAction(mAction);
		intent.putExtra(EXTRA_ID_KEY, String.valueOf(id));
		startActivity(intent);
	}

	@Override
	protected Loader<Cursor> onCreateLoader(int id, Bundle args, ListHierarchyType type) {
		Uri uri = null;
		switch(type){
		case SEARCH: uri = DBContentProvider.SEARCH_URI;
		break;
		case SUBSCRIPTION: uri = DBContentProvider.SUBSCRIPTION_URI;
		break;
		}
		CursorLoader cursorLoader = new CursorLoader(this, uri, null, null, null, null);
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
		MultichoiceListDialog dialog = null;
		switch(item.getItemId()){

		case R.id.bSearch:

			dialog = new MultichoiceSearchDialog(
					this,
					DBContentProvider.SEARCH_URI,
					R.string.string_search,
					item.getItemId());

			break;

		case R.id.bSubscribeUpdate:

			System.out.println("onOptionsItemSelected ID=" + item.getItemId());

			dialog = new MultichoiceSubscribeDialog(
					this,
					DBContentProvider.SUBSCRIPTION_URI,
					R.string.subscribeUpdate,
					item.getItemId());
			break;

		case R.id.bSubscribeDelete:

			dialog = new MultichoiceSubscribeDialog(
					this,
					DBContentProvider.SUBSCRIPTION_URI,
					R.string.subscribeDelete,
					item.getItemId());

			break;

		case R.id.bRemove:
			switch(type){

			case SEARCH:

				dialog = new MultichoiceRemoveDialog(
						this,
						DBContentProvider.SEARCH_URI,
						R.string.remove,
						item.getItemId());

				break;

			case SUBSCRIPTION:

				dialog = new MultichoiceRemoveDialog(
						this,
						DBContentProvider.SUBSCRIPTION_URI,
						R.string.remove,
						item.getItemId());

				break;
			}
			break;
		}

		if(!dialog.isEmpty()){
			dialog.create().show();
		}else {
			Toast.makeText(getBaseContext(), R.string.empty_list_view, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo, ListHierarchyType type) {
		switch(type){
		case SEARCH:
			menu.add(0, SEARCH_ID, 0, R.string.string_search);
			break;
		case SUBSCRIPTION:
			menu.add(0, R.id.bSubscribeUpdate, 0, R.string.subscribeUpdate);
			menu.add(0, R.id.bSubscribeDelete, 0, R.string.subscribeDelete);
			break;
		}
		menu.add(0, EDIT_ID, 0, R.string.edit);
		menu.add(0, REMOVE_ID, 0, R.string.remove);
	}

	@Override
	protected void onContextItemSelected(MenuItem item, ListHierarchyType type) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		String listItemId = Long.toString(info.id);
		switch (item.getItemId()) {
		case REMOVE_ID : remove(listItemId, type);
		break;
		case EDIT_ID : startEditActivity(listItemId);
		break;
		case SEARCH_ID : search(listItemId);
		break;
		case R.id.bSubscribeUpdate : subscribeUpdate(listItemId);
		break;
		case R.id.bSubscribeDelete: subscribeDelete(listItemId);
		break;
		}
	}

	@Override
	protected void remove(String selectedId, ListHierarchyType type) {
		Uri uri = null;
		switch(type){
		case SEARCH: uri = Uri.parse(DBContentProvider.SEARCH_URI + "/" + selectedId);
		break;
		case SUBSCRIPTION: uri = Uri.parse(DBContentProvider.SUBSCRIPTION_URI + "/" + selectedId);
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

		case R.id.bSearch: search(selectedRowIds);
		break;

		case R.id.bSubscribeUpdate: subscribeUpdate(selectedRowIds);
		break;

		case R.id.bSubscribeDelete: subscribeDelete(selectedRowIds);
		break;
		}
	}
}
