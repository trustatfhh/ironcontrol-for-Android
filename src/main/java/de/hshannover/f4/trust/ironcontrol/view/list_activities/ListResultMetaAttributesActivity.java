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

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import de.hshannover.f4.trust.ironcontrol.R;
import de.hshannover.f4.trust.ironcontrol.database.DBContentProvider;

public class ListResultMetaAttributesActivity extends ListHierarchyActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerForContextMenu(getListView());
	}

	@Override
	protected SimpleCursorAdapter setListAdapter(ListHierarchyType type) {
		return new SimpleCursorAdapter(this, R.layout.meta_attributes_list_row, null, FROM_META_ATTRIBUTES, TO_LIST2_ROW, 0);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {} 	// nothing to do

	@Override
	protected Loader<Cursor> onCreateLoader(int id, Bundle args, ListHierarchyType type) {
		Uri uri = Uri.parse(DBContentProvider.RESULT_METADATA_URI + "/" + id + "/" + DBContentProvider.RESULT_META_ATTRIBUTES);
		CursorLoader cursorLoader = new CursorLoader(this, uri, null, null, null, null);
		return cursorLoader;
	}

	@Override
	protected boolean onCreateOptionsMenu(Menu menu, ListHierarchyType type) { return false; }	// NO OptionsMenu

	@Override
	protected void onOptionsItemSelected(MenuItem item, ListHierarchyType type) {}	// nothing to do

	@Override
	protected void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo, ListHierarchyType type) {}	// NO ContextMenu

	@Override
	protected void onContextItemSelected(MenuItem item, ListHierarchyType type) {}	// nothing to do

	@Override
	protected void remove(String selectedId, ListHierarchyType type) {}	// nothing to do

}
