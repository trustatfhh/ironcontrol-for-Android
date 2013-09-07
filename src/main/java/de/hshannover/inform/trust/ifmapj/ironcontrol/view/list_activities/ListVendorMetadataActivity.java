package de.hshannover.inform.trust.ifmapj.ironcontrol.view.list_activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.DBContentProvider;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.MetaAttributes;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.VendorMetadata;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.MetadataBuilderActivity;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.dialogs.MultichoiceDialog;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.dialogs.MultichoiceDialogEvent;

public class ListVendorMetadataActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>, MultichoiceDialogEvent {

	private static final Logger logger = LoggerFactory.getLogger(ListVendorMetadataActivity.class);

	private static final int REMOVE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = Menu.FIRST + 2;

	private SimpleCursorAdapter adapter;

	private final int OVERVIEW = 10;
	public static final int ATTRIBUTES_VIEW = 20;
	private int ACTIVE_VIEW = 10;

	private int lastMetadataID;
	private int lastLoaderID;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view);
		initCursorAdapter(-1);
		registerForContextMenu(getListView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_vendor_metadata, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (ACTIVE_VIEW) {
			case OVERVIEW :
				switch (item.getItemId()) {
					case R.id.buttonRemove :
						showMultichoiceDialog(DBContentProvider.VENDOR_METADATA_URI, R.id.bRemove);
						break;
					case R.id.buttonAdd :
						Intent intent = new Intent(this, MetadataBuilderActivity.class);
						startActivity(intent);
						break;
				}
				break;
			case ATTRIBUTES_VIEW :
				switch (item.getItemId()) {
					case R.id.buttonRemove :
						showMultichoiceDialog(Uri.parse(DBContentProvider.VENDOR_METADATA_URI + "/" + lastMetadataID + "/" + DBContentProvider.VENDOR_META_ATTRIBUTES), R.id.bRemove);
						break;
					case R.id.buttonAdd :
						addMetaAttribute().show();
						break;
				}
		}
		return super.onOptionsItemSelected(item);
	}

	private Dialog addMetaAttribute(){
		AlertDialog.Builder publishSaveDialog = createDialog(R.string.add);

		final EditText input = new EditText(this);
		publishSaveDialog.setView(input);

		publishSaveDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				Editable value = input.getText();

				String name = value.toString();
				ContentValues publishValues = new ContentValues();
				publishValues.put(MetaAttributes.COLUMN_NAME, name);
				getContentResolver().insert(Uri.parse(DBContentProvider.VENDOR_METADATA_URI + "/" + lastMetadataID + "/" + DBContentProvider.VENDOR_META_ATTRIBUTES), publishValues);
			}
		});

		publishSaveDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {}});

		return publishSaveDialog.create();
	}

	private AlertDialog.Builder createDialog(int titleId){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(titleId);
		return alert;
	}

	private void showMultichoiceDialog(Uri uri, int button){
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		String[] ids = new String[cursor.getCount()];
		String[] labels = new String[cursor.getCount()];
		int index = 0;
		while(cursor.moveToNext()){
			switch (ACTIVE_VIEW) {
				case OVERVIEW :
					ids[index] = cursor.getString(cursor.getColumnIndexOrThrow(VendorMetadata.COLUMN_ID));
					labels[index] = cursor.getString(cursor.getColumnIndexOrThrow(VendorMetadata.COLUMN_NAME));
					break;
				case ATTRIBUTES_VIEW:
					ids[index] = cursor.getString(cursor.getColumnIndexOrThrow(MetaAttributes.COLUMN_ID));
					labels[index] = cursor.getString(cursor.getColumnIndexOrThrow(MetaAttributes.COLUMN_NAME));
					break;
			}
			index++;
		}
		if(labels.length != 0){
			new MultichoiceDialog(this, ids, labels, button).create().show();
		}else {
			Toast.makeText(getBaseContext(), R.string.empty_list_view, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Context Menu");
		switch (ACTIVE_VIEW) {
			case OVERVIEW : menu.add(0, EDIT_ID, 0, R.string.edit);
			break;
			case ATTRIBUTES_VIEW:
				break;
		}
		menu.add(0, REMOVE_ID, 0, R.string.remove);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		String listItemId = Long.toString(info.id);
		switch (item.getItemId()) {
			case REMOVE_ID : removeMetadata(listItemId);
			break;
			case EDIT_ID : startEditActivity(listItemId);
			break;
		}
		return super.onContextItemSelected(item);
	}

	private void startEditActivity(String listItemId) {
		Intent intent = new Intent(getBaseContext(), MetadataBuilderActivity.class);
		intent.putExtra("listItemId", listItemId);
		startActivity(intent);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		switch(ACTIVE_VIEW){
			case OVERVIEW: switchView(ATTRIBUTES_VIEW, R.string.metadata_attributes); lastMetadataID=(int) id; initCursorAdapter((int) id);
			break;
			case ATTRIBUTES_VIEW:
				break;
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri = null;
		switch(ACTIVE_VIEW){
			case OVERVIEW:
				uri = DBContentProvider.VENDOR_METADATA_URI;
				System.out.println("onCreateLoader OVERVIEW");
				break;
			case ATTRIBUTES_VIEW:
				uri = Uri.parse(DBContentProvider.VENDOR_METADATA_URI + "/" + id + "/" + DBContentProvider.VENDOR_META_ATTRIBUTES);
				System.out.println("onCreateLoader ATTRIBUTES_VIEW");
				break;
		}
		CursorLoader cursorLoader = new CursorLoader(this, uri, null, null, null, null);
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

	@Override
	public void onClickeMultichoiceDialogButton(String[] selectedRowIds, int buttonType, boolean multi) {
		switch(buttonType){
			case R.id.bRemove: removeMetadata(selectedRowIds);
			break;
		}
	}

	private void removeMetadata(String[] selectedRowIds) {
		for (String selectedRowId : selectedRowIds) {
			removeMetadata(selectedRowId);
		}
	}

	private void removeMetadata(String selectedId){
		Uri uri = null;
		switch(ACTIVE_VIEW){
			case OVERVIEW: uri = Uri.parse(DBContentProvider.VENDOR_METADATA_URI + "/" + selectedId);
			break;
			case ATTRIBUTES_VIEW: uri = Uri.parse(DBContentProvider.VENDOR_METADATA_URI + "/" + lastMetadataID + "/" + DBContentProvider.VENDOR_META_ATTRIBUTES + "/" + selectedId);
			break;
		}
		try{
			getContentResolver().delete(uri, null, null);
		} catch (IllegalArgumentException e){
			logger.fatal(e.getMessage(), e);
			Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onBackPressed(){
		switch(ACTIVE_VIEW){
			case OVERVIEW: super.onBackPressed();
			break;
			case ATTRIBUTES_VIEW: switchView(OVERVIEW, R.string.vendor_specific_metadata); initCursorAdapter(-1);
			break;
		}
	}

	private void switchView(int view, int resId){
		ACTIVE_VIEW = view;
		setTitle(resId);
	}

	private void initCursorAdapter(int id) {
		getLoaderManager().destroyLoader(lastLoaderID);
		getLoaderManager().initLoader(id, null, this);
		setListAdapter();
		lastLoaderID = id;
	}

	private void setListAdapter(){
		String[] from = new String[]{VendorMetadata.COLUMN_NAME, VendorMetadata.COLUMN_CARDINALITY};
		int[] to = new int[]{R.id.tvLabel, R.id.tvInfo1};
		switch(ACTIVE_VIEW){
			case OVERVIEW: adapter = new SimpleCursorAdapter(this, R.layout.responses_list_row, null, from, to, 0);
			break;
			case ATTRIBUTES_VIEW: adapter = new SimpleCursorAdapter(this, R.layout.responses_list_row, null, new String[]{MetaAttributes.COLUMN_NAME}, new int[]{R.id.tvLabel}, 0);
			break;
		}
		super.setListAdapter(adapter);
	}
}
