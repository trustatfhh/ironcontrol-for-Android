package de.hshannover.inform.trust.ifmapj.ironcontrol.view;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.DBContentProvider;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.Connections;

public class ConnectionFragmentActivity extends FragmentActivity {

	private EditText etServerName, etServerAddress, etServerPort, etUserName, etUserPW, etUrl;
	private String urlPrefix, portPrefix;
	private Button bSave;
	private boolean isServerUpdate = false;
	Editable url;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connection);

		urlPrefix = "http://";
		portPrefix = ":";

		readResources();
		addButtonListener();

		etUrl.setText(urlPrefix);

		if(isServerUpdate = getIntent().getExtras() != null){
			fillData(getIntent().getExtras().getString("listItemId"));
		}

		etServerPort.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

			@Override
			public void afterTextChanged(Editable s) {
				if(s.toString().equals("")){
					etUrl.setText(urlPrefix+etServerAddress.getText()+s.toString());
				}else {
					etUrl.setText(urlPrefix+etServerAddress.getText()+portPrefix+s.toString());
				}

			}
		});

		etServerAddress.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

			@Override
			public void afterTextChanged(Editable s) {
				String port = etServerPort.getText().toString();
				if(port.equals("")){
					etUrl.setText(urlPrefix+s.toString());
				}else {
					etUrl.setText(urlPrefix+s.toString()+portPrefix+etServerPort.getText());
				}
			}
		});
	}

	private void fillData(String listItemId) {
		Uri connection_uri = Uri.parse(DBContentProvider.CONNECTIONS_URI + "/"+ listItemId);
		Cursor connection_cursor = getContentResolver().query(connection_uri, null, null, null, null);

		connection_cursor.moveToNext();

		String name = connection_cursor.getString(connection_cursor.getColumnIndexOrThrow(Connections.COLUMN_NAME));
		String address = connection_cursor.getString(connection_cursor.getColumnIndexOrThrow(Connections.COLUMN_ADDRESS));
		String port = connection_cursor.getString(connection_cursor.getColumnIndexOrThrow(Connections.COLUMN_PORT));
		String userName = connection_cursor.getString(connection_cursor.getColumnIndexOrThrow(Connections.COLUMN_USER));
		String userPass = connection_cursor.getString(connection_cursor.getColumnIndexOrThrow(Connections.COLUMN_PASS));
		String userUrl = connection_cursor.getString(connection_cursor.getColumnIndex(Connections.COLUMN_URL));

		connection_cursor.close();

		etServerName.setText(name);
		etServerAddress.setText(address);
		etServerPort.setText(port);
		etUserName.setText(userName);
		etUserPW.setText(userPass);
		etUrl.setText(userUrl);
	}

	private void addButtonListener() {
		bSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!etServerName.getText().toString().equals("") && !etServerAddress.getText().toString().equals("") && !etServerPort.getText().toString().equals("")
						&& !etUserName.getText().toString().equals("") && !etUserPW.getText().toString().equals("")){
					if(!isServerUpdate){
						// New

						Cursor conn = getContentResolver().query(DBContentProvider.CONNECTIONS_URI, new String[]{Connections.COLUMN_NAME}, null, null, null);

						ContentValues connectionValues = new ContentValues();
						connectionValues.put(Connections.COLUMN_NAME, etServerName.getText().toString());
						connectionValues.put(Connections.COLUMN_ADDRESS, etServerAddress.getText().toString());
						connectionValues.put(Connections.COLUMN_PORT, etServerPort.getText().toString());
						connectionValues.put(Connections.COLUMN_USER, etUserName.getText().toString());
						connectionValues.put(Connections.COLUMN_PASS, etUserPW.getText().toString());
						connectionValues.put(Connections.COLUMN_URL, etUrl.getText().toString());
						if(conn.getCount() == 0){
							connectionValues.put(Connections.COLUMN_DEFAULT, 1);
						}

						getContentResolver().insert(DBContentProvider.CONNECTIONS_URI, connectionValues);
						Toast.makeText(ConnectionFragmentActivity.this, etServerName.getText() + " "+getResources().getString(R.string.is_saved), Toast.LENGTH_SHORT).show();
					}else{
						// Update
						String id = getIntent().getExtras().getString("listItemId");

						ContentValues connectionValues = new ContentValues();
						connectionValues.put(Connections.COLUMN_NAME, etServerName.getText().toString());
						connectionValues.put(Connections.COLUMN_ADDRESS, etServerAddress.getText().toString());
						connectionValues.put(Connections.COLUMN_PORT, etServerPort.getText().toString());
						connectionValues.put(Connections.COLUMN_USER, etUserName.getText().toString());
						connectionValues.put(Connections.COLUMN_PASS, etUserPW.getText().toString());
						connectionValues.put(Connections.COLUMN_URL, etUrl.getText().toString());

						getContentResolver().update(Uri.parse(DBContentProvider.CONNECTIONS_URI + "/" + id), connectionValues, null, null);

						Toast.makeText(ConnectionFragmentActivity.this, etServerName.getText() + " "+getResources().getString(R.string.was_update), Toast.LENGTH_SHORT).show();
					}
					finish();
				}else{
					Toast.makeText(ConnectionFragmentActivity.this, "Not saved !", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void readResources(){

		etServerName = (EditText)findViewById(R.id.editTextServerName);
		etServerAddress = (EditText)findViewById(R.id.editTextServerAddress);
		etServerPort = (EditText)findViewById(R.id.editTextServerPort);
		etUserName = (EditText)findViewById(R.id.EditTextUserName);
		etUserPW = (EditText)findViewById(R.id.EditTextUserPW);
		etUrl = (EditText)findViewById(R.id.etUrl);

		bSave = (Button)findViewById(R.id.buttonSave);
	}
}
