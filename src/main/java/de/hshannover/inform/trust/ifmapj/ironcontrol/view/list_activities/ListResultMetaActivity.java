package de.hshannover.inform.trust.ifmapj.ironcontrol.view.list_activities;

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
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.DBContentProvider;

/**
 * Class for connection management
 * @author Marcel Reichenbach
 * @version %I%, %G%
 * @since 0.1
 */

public class ListResultMetaActivity extends ListHierarchyActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerForContextMenu(getListView());
	}

	@Override
	protected SimpleCursorAdapter setListAdapter(ListHierarchyType type) {
		return new SimpleCursorAdapter(this, R.layout.metadata_list_row, null, FROM_RESULT_META, TO_LIST1_ROW, 0);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this, ListResultMetaAttributesActivity.class);
		intent.setAction(mAction);
		intent.putExtra(EXTRA_ID_KEY, String.valueOf(id));
		startActivity(intent);
	}

	@Override
	protected Loader<Cursor> onCreateLoader(int id, Bundle args, ListHierarchyType type) {
		Uri uri = Uri.parse(DBContentProvider.RESULT_ITEMS_URI + "/" + id + "/" + DBContentProvider.RESULT_METADATA);
		return new CursorLoader(this, uri, null, null, null, null);
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
