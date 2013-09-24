package de.hshannover.inform.trust.ironcontrol.view.list_activities;

import android.content.CursorLoader;
import android.content.Intent;
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
import de.hshannover.inform.trust.ironcontrol.R;
import de.hshannover.inform.trust.ironcontrol.database.DBContentProvider;

public class ListResultItemsActivity extends ListHierarchyActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerForContextMenu(getListView());
	}

	@Override
	protected SimpleCursorAdapter setListAdapter(ListHierarchyType type) {
		return new SimpleCursorAdapter(this, R.layout.result_items_list_row, null, FROM_RESULT_ITEMS, TO_LIST2_ROW, 0);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this, ListResultMetaActivity.class);
		intent.setAction(mAction);
		intent.putExtra(EXTRA_ID_KEY, String.valueOf(id));
		startActivity(intent);
	}

	@Override
	protected Loader<Cursor> onCreateLoader(int id, Bundle args, ListHierarchyType type) {
		Uri uri = Uri.parse(DBContentProvider.RESPONSES_URI + "/" + id + "/" + DBContentProvider.RESULT_ITEMS);
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