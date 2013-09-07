package de.hshannover.inform.trust.ifmapj.ironcontrol.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ListView;
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
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.Operation;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.util.PromptSpinnerAdapter;

public class AdvancedRequestFragment extends Fragment  {

	private static final CharSequence START_IDENTIFIER_SPINNER_PROMPT = "Start Identifier";

	private boolean[] terminalIdentifierTypes =  new boolean[5];
	private Spinner sStartIdentifier;
	private EditText etStartIdentifier, etName, etMatchLinks, etResultFilter;
	private View mRoot;
	private TextView tvMaxDepth, tvMaxSize;
	private SeekBar sbMaxDepth, sbMaxSize;
	private ListView lvTerminalType;
	private PromptSpinnerAdapter identifier1Adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRoot = inflater.inflate(R.layout.fragment_advanced_request, null);

		etName = (EditText)mRoot.findViewById(R.id.etName);
		sStartIdentifier = (Spinner)mRoot.findViewById(R.id.sIdentifier1);
		etStartIdentifier = (EditText)mRoot.findViewById(R.id.etIdentifier1);
		tvMaxDepth = (TextView)mRoot.findViewById(R.id.textViewMaxDepth);
		etMatchLinks = (EditText)mRoot.findViewById(R.id.editTextMatchLinks);
		etResultFilter = (EditText)mRoot.findViewById(R.id.editTextResultFilter);
		tvMaxSize = (TextView)mRoot.findViewById(R.id.textViewMaxSize);
		sbMaxDepth = (SeekBar)mRoot.findViewById(R.id.seekBarMaxDepth);
		sbMaxSize = (SeekBar)mRoot.findViewById(R.id.seekBarMaxSize);
		lvTerminalType = (ListView)mRoot.findViewById(R.id.listViewTerminalType);
		configureView();
		lvTerminalType.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				createDialog().show();
			}
		});
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

		sbMaxSize.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				tvMaxSize.setText(""+progress);
			}
		});
	}

	private void configureView(){
		configureListEntries();
		addSpinnerListener();
		addSeekBarListener();
	}

	private Dialog createDialog(){
		return new AlertDialog.Builder( getActivity() )
		.setTitle(R.string.string_terminal_identifier_type)
		.setMultiChoiceItems( R.array.identifier1_list, terminalIdentifierTypes, new DialogSelectionClickHandler())
		.setPositiveButton( R.string.ok, new DialogButtonClickHandler() )
		.setNegativeButton(R.string.string_abort, new DialogButtonClickHandler())
		.create();
	}

	public class DialogSelectionClickHandler implements DialogInterface.OnMultiChoiceClickListener {
		@Override
		public void onClick( DialogInterface dialog, int clicked, boolean selected ){
			Log.i( "ME", R.array.identifier1_list + " selected: " + selected );
		}
	}

	public class DialogButtonClickHandler implements DialogInterface.OnClickListener{
		@Override
		public void onClick( DialogInterface dialog, int clicked ){
			switch( clicked ){
				case DialogInterface.BUTTON_POSITIVE:
					printTerminalIdentifierType();
					break;
			}
		}
	}

	protected void printTerminalIdentifierType(){
		for( int i = 0; i < 5; i++ ){
			Log.i( "ME", i + " selected: " + terminalIdentifierTypes[i] );
		}
	}

	public boolean[] getTerminalIdentifierTypes() {
		return terminalIdentifierTypes;
	}

	public void search(View view){
		String name = etName.getText().toString();
		String identifier = sStartIdentifier.getSelectedItem().toString();
		String identifierValue = etStartIdentifier.getText().toString();
		String matchLinks = etMatchLinks.getText().toString();
		String resultFilter = etResultFilter.getText().toString();
		int maxDepth = sbMaxDepth.getProgress();
		int maxSize = sbMaxSize.getProgress() * 1000;
		String terminalIdentifiers = terminalIdentifierTypesToString();

		String id = saveSearch(name);

		if(matchLinks.equals("")){
			matchLinks = null;
		}

		if(resultFilter.equals("")){
			resultFilter = null;
		}

		if(terminalIdentifiers.equals("")){
			terminalIdentifiers = null;
		}

		if(id != null){

			new SearchTask(name, identifier, identifierValue, matchLinks, resultFilter, maxDepth, maxSize, terminalIdentifiers, getActivity(), SearchFragmentActivity.MESSAGESEARCH).execute();

		}else {

			Toast.makeText(getActivity(), "no search", Toast.LENGTH_SHORT).show();

		}
	}

	private String terminalIdentifierTypesToString() {

		String[] identifiers = {"access-request", "ip-address", "mac-address", "device", "identity"};
		String terminalIdentifier = "";

		boolean first = true;
		for(int i=0; i< identifiers.length; i++){
			if(terminalIdentifierTypes[i]){
				if(first){
					terminalIdentifier += identifiers[i];
					first = false;
					continue;
				}
				terminalIdentifier += ","+identifiers[i];
			}
		}
		System.out.println("Terminal Identifier  =  "+terminalIdentifier);
		return terminalIdentifier;
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
		String matchLinks = etMatchLinks.getText().toString();
		String resultFilter = etResultFilter.getText().toString();
		int maxDepth = sbMaxDepth.getProgress();
		int maxSize = sbMaxSize.getProgress() * 1000;
		String terminalIdentifiers = terminalIdentifierTypesToString();

		ContentValues publishValues = new ContentValues();
		publishValues.put(Requests.COLUMN_NAME, savedName);
		publishValues.put(Requests.COLUMN_IDENTIFIER1, startIdentifier);
		publishValues.put(Requests.COLUMN_IDENTIFIER1_Value, startIdentifierValue);
		publishValues.put(Requests.COLUMN_MATCH_LINKS, matchLinks);
		publishValues.put(Requests.COLUMN_RESULT_FILTER, resultFilter);
		publishValues.put(Requests.COLUMN_MAX_DEPTH, maxDepth);
		publishValues.put(Requests.COLUMN_MAX_SITZ, maxSize);
		publishValues.put(Requests.COLUMN_TERMINAL_IDENTIFIER_TYPES, terminalIdentifiers);

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

	public void subscription(View view){
		String name = etName.getText().toString();
		String identifier = sStartIdentifier.getSelectedItem().toString();
		String identifierValue = etStartIdentifier.getText().toString();
		String matchLinks = etMatchLinks.getText().toString();
		String resultFilter = etResultFilter.getText().toString();
		int maxDepth = sbMaxDepth.getProgress();
		int maxSize = sbMaxSize.getProgress() * 1000;
		String terminalIdentifiers = terminalIdentifierTypesToString();

		String id = saveSubscribtion(name);

		if(matchLinks.equals("")){
			matchLinks = null;
		}

		if(resultFilter.equals("")){
			resultFilter = null;
		}

		if(terminalIdentifiers.equals("")){
			terminalIdentifiers = null;
		}

		if(id != null){

			new SubscriptionTask(getActivity(), name, identifier, identifierValue, maxDepth, maxSize, terminalIdentifiers, resultFilter, matchLinks, id, Operation.UPDATE).execute();

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
		String matchLinks = etMatchLinks.getText().toString();
		String resultFilter = etResultFilter.getText().toString();
		int maxDepth = sbMaxDepth.getProgress();
		int maxSize = sbMaxSize.getProgress() * 1000;
		String terminalIdentifiers = terminalIdentifierTypesToString();

		ContentValues publishValues = new ContentValues();
		publishValues.put(Requests.COLUMN_NAME, savedName);
		publishValues.put(Requests.COLUMN_IDENTIFIER1, startIdentifier);
		publishValues.put(Requests.COLUMN_IDENTIFIER1_Value, startIdentifierValue);
		publishValues.put(Requests.COLUMN_MATCH_LINKS, matchLinks);
		publishValues.put(Requests.COLUMN_RESULT_FILTER, resultFilter);
		publishValues.put(Requests.COLUMN_MAX_DEPTH, maxDepth);
		publishValues.put(Requests.COLUMN_MAX_SITZ, maxSize);
		publishValues.put(Requests.COLUMN_TERMINAL_IDENTIFIER_TYPES, terminalIdentifiers);


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

}
