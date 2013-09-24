package de.hshannover.inform.trust.ironcontrol.view.list_activities;

import java.util.List;

import android.app.ListActivity;
import android.app.LoaderManager;
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
import de.hshannover.inform.trust.ironcontrol.asynctask.SearchTask;
import de.hshannover.inform.trust.ironcontrol.asynctask.SubscriptionTask;
import de.hshannover.inform.trust.ironcontrol.database.DBContentProvider;
import de.hshannover.inform.trust.ironcontrol.database.entities.Requests;
import de.hshannover.inform.trust.ironcontrol.database.entities.Responses;
import de.hshannover.inform.trust.ironcontrol.database.entities.ResultItems;
import de.hshannover.inform.trust.ironcontrol.database.entities.ResultMetaAttributes;
import de.hshannover.inform.trust.ironcontrol.database.entities.ResultMetadata;
import de.hshannover.inform.trust.ironcontrol.logic.data.Operation;
import de.hshannover.inform.trust.ironcontrol.view.SearchFragmentActivity;
import de.hshannover.inform.trust.ironcontrol.view.SubscribeFragmentActivity;

public abstract class ListHierarchyActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	protected static final String[] FROM_REQUESTS = {Requests.COLUMN_NAME, Requests.COLUMN_IDENTIFIER1, Requests.COLUMN_IDENTIFIER1_Value, Requests.COLUMN_MAX_DEPTH};
	protected static final String[] FROM_RESPONSES = {Responses.COLUMN_DATE, Responses.COLUMN_TIME, Responses.COLUMN_NEW};
	protected static final String[] FROM_RESULT_ITEMS = {ResultItems.COLUMN_IDENTIFIER1, ResultItems.COLUMN_IDENTIFIER2};
	protected static final String[] FROM_RESULT_META = {ResultMetadata.COLUMN_LOCAL_NAME, ResultMetadata.COLUMN_CARDINALITY, ResultMetadata.COLUMN_PUBLISHERID, ResultMetadata.COLUMN_TIMESTAMP};
	protected static final String[] FROM_META_ATTRIBUTES = {ResultMetaAttributes.COLUMN_NODE_NAME, ResultMetaAttributes.COLUMN_NODE_VALUE};

	protected static final int[] TO_LIST1_ROW = {R.id.label, R.id.label_info1, R.id.label_info2, R.id.label_info3};
	protected static final int[] TO_LIST2_ROW = {R.id.tvLabel, R.id.tvInfo1, R.id.tvNew};

	public static final String EXTRA_ID_KEY = "ID";

	private ListHierarchyType mType;

	private SimpleCursorAdapter adapter;

	protected String mAction;

	protected String lastID;

	enum ListHierarchyType{
		SEARCH,
		SUBSCRIPTION;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view);

		// get selected Item id
		if(getIntent().getExtras() != null){
			lastID = getIntent().getExtras().getString("ID");
		}else {
			lastID = "-1";
		}

		// get ACTION
		mAction = getIntent().getAction();

		if(mAction.equals(getResources().getString(R.string.ACTION_SAVED_SEARCHS))){
			mType = ListHierarchyType.SEARCH;
		}else if(mAction.equals(getResources().getString(R.string.ACTION_SAVED_SUBSCRIPTIONS))){
			mType = ListHierarchyType.SUBSCRIPTION;
		}

		// initCursorAdapter
		adapter = setListAdapter(mType);
		getLoaderManager().initLoader(Integer.valueOf(lastID), null, this);
		super.setListAdapter(adapter);
	}

	protected abstract SimpleCursorAdapter setListAdapter(ListHierarchyType type);

	@Override
	protected abstract void onListItemClick(ListView l, View v, int position, long id);

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args){
		return onCreateLoader(id,args,mType);
	}

	protected abstract Loader<Cursor> onCreateLoader(int id, Bundle args, ListHierarchyType type);

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return onCreateOptionsMenu(menu, mType);
	}

	protected abstract boolean onCreateOptionsMenu(Menu menu, ListHierarchyType type);

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		onOptionsItemSelected(item, mType);
		return super.onOptionsItemSelected(item);
	}

	protected abstract void onOptionsItemSelected(MenuItem item, ListHierarchyType type);

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Context Menu");
		onCreateContextMenu(menu, v, menuInfo, mType);
	}

	protected abstract void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo, ListHierarchyType type);

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		onContextItemSelected(item, mType);
		return super.onContextItemSelected(item);
	}

	protected abstract void onContextItemSelected(MenuItem item, ListHierarchyType type);

	protected void startEditActivity(String listItemId) {
		Intent intent = null;
		switch (mType) {
		case SEARCH : intent = new Intent(getBaseContext(), SearchFragmentActivity.class);
		break;
		case SUBSCRIPTION: intent = new Intent(getBaseContext(), SubscribeFragmentActivity.class);
		break;
		}
		intent.putExtra("listItemId", listItemId);
		startActivity(intent);
	}

	protected void remove(List<String> selectedRowIds) {
		for (String selectedRowId : selectedRowIds) {
			remove(selectedRowId, mType);
		}
	}

	protected abstract void remove(String selectedId, ListHierarchyType type);

	protected void search(List<String> selectedRowIds) {
		for (String selectedRowId : selectedRowIds) {
			search(selectedRowId);
		}
	}

	protected void search(String id){
		Uri publish_uri = Uri.parse(DBContentProvider.SEARCH_URI + "/"+ id);

		Cursor search_cursor = getContentResolver().query(publish_uri, null, null, null, null);

		search_cursor.moveToNext();

		String name = search_cursor.getString(search_cursor.getColumnIndexOrThrow(Requests.COLUMN_NAME));
		String identifier = search_cursor.getString(search_cursor.getColumnIndexOrThrow(Requests.COLUMN_IDENTIFIER1));
		String identifierValue = search_cursor.getString(search_cursor.getColumnIndexOrThrow(Requests.COLUMN_IDENTIFIER1_Value));
		int maxDepth = search_cursor.getInt(search_cursor.getColumnIndexOrThrow(Requests.COLUMN_MAX_DEPTH));
		int maxSize = search_cursor.getInt(search_cursor.getColumnIndexOrThrow(Requests.COLUMN_MAX_SITZ));
		String matchLinks = search_cursor.getString(search_cursor.getColumnIndexOrThrow(Requests.COLUMN_MATCH_LINKS));
		String resultFilter = search_cursor.getString(search_cursor.getColumnIndexOrThrow(Requests.COLUMN_RESULT_FILTER));
		String terminalIdentifiers = search_cursor.getString(search_cursor.getColumnIndexOrThrow(Requests.COLUMN_TERMINAL_IDENTIFIER_TYPES));

		search_cursor.close();

		if(maxSize == 0){
			new SearchTask(name, identifier, identifierValue, maxDepth, this, SearchFragmentActivity.MESSAGESEARCH).execute();
		} else{
			new SearchTask(name, identifier, identifierValue, matchLinks, resultFilter, maxDepth, maxSize, terminalIdentifiers, this, SearchFragmentActivity.MESSAGESEARCH).execute();
		}
	}

	protected void subscribeUpdate(List<String> selectedRowIds){
		for (String element : selectedRowIds) {
			subscribeUpdate(element);
		}
	}

	protected void subscribeUpdate(String id){
		Uri subscription_uri = Uri.parse(DBContentProvider.SUBSCRIPTION_URI + "/"+ id);

		Cursor subscription_cursor = getContentResolver().query(subscription_uri, null, null, null, null);

		subscription_cursor.moveToNext();

		String name = subscription_cursor.getString(subscription_cursor.getColumnIndexOrThrow(Requests.COLUMN_NAME));
		String identifier = subscription_cursor.getString(subscription_cursor.getColumnIndexOrThrow(Requests.COLUMN_IDENTIFIER1));
		String identifierValue = subscription_cursor.getString(subscription_cursor.getColumnIndexOrThrow(Requests.COLUMN_IDENTIFIER1_Value));
		int maxDepth = subscription_cursor.getInt(subscription_cursor.getColumnIndexOrThrow(Requests.COLUMN_MAX_DEPTH));
		int maxSize = subscription_cursor.getInt(subscription_cursor.getColumnIndexOrThrow(Requests.COLUMN_MAX_SITZ));
		String matchLinks = subscription_cursor.getString(subscription_cursor.getColumnIndexOrThrow(Requests.COLUMN_MATCH_LINKS));
		String resultFilter = subscription_cursor.getString(subscription_cursor.getColumnIndexOrThrow(Requests.COLUMN_RESULT_FILTER));
		String terminalIdentifiers = subscription_cursor.getString(subscription_cursor.getColumnIndexOrThrow(Requests.COLUMN_TERMINAL_IDENTIFIER_TYPES));

		subscription_cursor.close();

		if(maxSize == 0){
			new SubscriptionTask(this, name, identifier, identifierValue, maxDepth, id, Operation.UPDATE).execute();
		} else{
			new SubscriptionTask(this, name, identifier, identifierValue, maxDepth, maxSize, terminalIdentifiers, resultFilter, matchLinks, id, Operation.UPDATE).execute();
		}
	}

	protected void subscribeDelete(List<String> selectedRowIds){
		for (String element : selectedRowIds) {
			subscribeDelete(element);
		}
	}

	protected void subscribeDelete(String id){
		Uri subscription_uri = Uri.parse(DBContentProvider.SUBSCRIPTION_URI + "/"+ id);

		Cursor subscription_cursor = getContentResolver().query(subscription_uri, null, null, null, null);

		subscription_cursor.moveToNext();

		String name = subscription_cursor.getString(subscription_cursor.getColumnIndexOrThrow(Requests.COLUMN_NAME));

		new SubscriptionTask(this, name, null, null, 0, id, Operation.DELETE).execute();
	}

}
