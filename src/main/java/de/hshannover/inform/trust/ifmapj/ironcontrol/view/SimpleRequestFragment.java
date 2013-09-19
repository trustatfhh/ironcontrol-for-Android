package de.hshannover.inform.trust.ifmapj.ironcontrol.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.asynctask.SearchTask;
import de.hshannover.inform.trust.ifmapj.ironcontrol.asynctask.SubscriptionTask;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.DBContentProvider;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.Requests;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.data.Operation;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.util.PromptSpinnerAdapter;

public class SimpleRequestFragment extends Fragment  {

	private static final CharSequence START_IDENTIFIER_SPINNER_PROMPT = "Start Identifier";

	private Spinner sStartIdentifier;
	private EditText etStartIdentifier, etName;
	private View mRoot;
	private TextView tvMaxDepth;
	private SeekBar sbMaxDepth;
	private PromptSpinnerAdapter identifier1Adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRoot = inflater.inflate(R.layout.fragment_simple_request, null);

		etName = (EditText)mRoot.findViewById(R.id.etName);
		sStartIdentifier = (Spinner)mRoot.findViewById(R.id.sIdentifier1);
		etStartIdentifier = (EditText)mRoot.findViewById(R.id.etIdentifier1);
		tvMaxDepth = (TextView)mRoot.findViewById(R.id.textViewMaxDepth);
		sbMaxDepth = (SeekBar)mRoot.findViewById(R.id.seekBarMaxDepth);

		configureView();
		return mRoot;
	}

	private void configureListEntries(){
		identifier1Adapter = new PromptSpinnerAdapter(getActivity(), START_IDENTIFIER_SPINNER_PROMPT, R.array.identifier1_list);

		sStartIdentifier.setAdapter(identifier1Adapter);
	}

	private void addSpinnerListener(){
		sStartIdentifier.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				etStartIdentifier.setHint((CharSequence) sStartIdentifier.getSelectedItem());
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	private void addSeekBarListener(){
		sbMaxDepth.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				tvMaxDepth.setText(""+progress);
			}
		});
	}

	private void configureView(){
		configureListEntries();
		addSpinnerListener();
		addSeekBarListener();
	}

	public void subscription(View view){
		String name = etName.getText().toString();
		String identifier = sStartIdentifier.getSelectedItem().toString();
		String identifierValue = etStartIdentifier.getText().toString();
		int maxDepth = sbMaxDepth.getProgress();

		String id = saveSubscribtion(name);

		if(id != null){

			new SubscriptionTask(getActivity(), name, identifier, identifierValue, maxDepth, id, Operation.UPDATE).execute();

		}else {

			Toast.makeText(getActivity(), "no subscription", Toast.LENGTH_SHORT).show();

		}
	}

	public String saveSubscribtion(String savedName){
		if(!isNameValid(savedName)){
			return null;
		}

		String id = getExistSubscriptionId(savedName);

		if(id != null){
			return id;
		}

		String startIdentifier = sStartIdentifier.getSelectedItem().toString();
		String startIdentifierValue = etStartIdentifier.getText().toString();
		int maxDepth = sbMaxDepth.getProgress();

		ContentValues publishValues = new ContentValues();
		publishValues.put(Requests.COLUMN_NAME, savedName);
		publishValues.put(Requests.COLUMN_IDENTIFIER1, startIdentifier);
		publishValues.put(Requests.COLUMN_IDENTIFIER1_Value, startIdentifierValue);
		publishValues.put(Requests.COLUMN_MAX_DEPTH, maxDepth);

		Uri returnUri = getActivity().getContentResolver().insert(DBContentProvider.SUBSCRIPTION_URI, publishValues);
		return returnUri.getLastPathSegment();

	}

	public Dialog createSubscribeSaveDialog(){
		AlertDialog.Builder publishSaveDialog = new AlertDialog.Builder(getActivity());

		publishSaveDialog.setTitle(R.string.save);
		publishSaveDialog.setMessage(R.string.saving_subscribe_message);

		final EditText input = new EditText(getActivity());
		input.setText(etName.getText().toString());
		publishSaveDialog.setView(input);

		publishSaveDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				if(saveSubscribtion(input.getText().toString()) == null){
					Toast.makeText(getActivity(), "not saved", Toast.LENGTH_LONG).show();
				}else {
					Toast.makeText(getActivity(), "Subscription: " + input.getText().toString() + " is saved", Toast.LENGTH_SHORT).show();
				}
			}
		});

		publishSaveDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {}});

		return publishSaveDialog.create();
	}

	private String getExistSubscriptionId(String savedName){
		String selectionArgs[] = {savedName};
		String selection = Requests.COLUMN_NAME + "=?";

		Cursor cursor = getActivity().getContentResolver().query(DBContentProvider.SUBSCRIPTION_URI, null, selection, selectionArgs, null);

		if(cursor.getCount() == 1){
			cursor.moveToFirst();
			String id = cursor.getString(cursor.getColumnIndex(Requests.COLUMN_ID));
			cursor.close();
			return id;
		}
		cursor.close();
		return null;
	}

	public void search(View view){
		String name = etName.getText().toString();
		String identifier = sStartIdentifier.getSelectedItem().toString();
		String identifierValue = etStartIdentifier.getText().toString();
		int maxDepth = sbMaxDepth.getProgress();

		String id = saveSearch(name);

		if(id != null){

			new SearchTask(name, identifier, identifierValue, maxDepth, getActivity(), SearchFragmentActivity.MESSAGESEARCH).execute();

		}else {

			Toast.makeText(getActivity(), "no search", Toast.LENGTH_SHORT).show();

		}
	}

	public String saveSearch(String savedName){
		if(!isNameValid(savedName)){
			return null;
		}

		String id = getExistSearchId(savedName);

		if(id != null){
			return id;
		}

		String startIdentifier = sStartIdentifier.getSelectedItem().toString();
		String startIdentifierValue = etStartIdentifier.getText().toString();
		int maxDepth = sbMaxDepth.getProgress();

		ContentValues publishValues = new ContentValues();
		publishValues.put(Requests.COLUMN_NAME, savedName);
		publishValues.put(Requests.COLUMN_IDENTIFIER1, startIdentifier);
		publishValues.put(Requests.COLUMN_IDENTIFIER1_Value, startIdentifierValue);
		publishValues.put(Requests.COLUMN_MAX_DEPTH, maxDepth);

		Uri returnUri = getActivity().getContentResolver().insert(DBContentProvider.SEARCH_URI, publishValues);
		return returnUri.getLastPathSegment();
	}

	public Dialog createSearchSaveDialog(){
		AlertDialog.Builder publishSaveDialog = new AlertDialog.Builder(getActivity());

		publishSaveDialog.setTitle(R.string.save);
		publishSaveDialog.setMessage(R.string.saving_search_message);

		final EditText input = new EditText(getActivity());
		input.setText(etName.getText().toString());
		publishSaveDialog.setView(input);

		publishSaveDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				if(saveSearch(input.getText().toString()) == null){
					Toast.makeText(getActivity(), "not saved", Toast.LENGTH_LONG).show();
				}else {
					Toast.makeText(getActivity(), "Search: " + input.getText().toString() + " is saved", Toast.LENGTH_SHORT).show();
				}

			}
		});

		publishSaveDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {}});

		return publishSaveDialog.create();
	}

	private String getExistSearchId(String savedName){
		String selectionArgs[] = {savedName};
		String selection = Requests.COLUMN_NAME + "=?";

		Cursor cursor = getActivity().getContentResolver().query(DBContentProvider.SEARCH_URI, null, selection, selectionArgs, null);

		if(cursor.getCount() == 1){
			cursor.moveToFirst();
			String id = cursor.getString(cursor.getColumnIndex(Requests.COLUMN_ID));
			cursor.close();
			return id;
		}
		cursor.close();
		return null;
	}

	private boolean isNameValid(String savedName) {
		if(savedName.equals("")){
			Toast.makeText(getActivity().getBaseContext(), "empty name", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
}
