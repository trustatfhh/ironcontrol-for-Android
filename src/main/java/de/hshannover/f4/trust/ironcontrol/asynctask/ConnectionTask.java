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
package de.hshannover.f4.trust.ironcontrol.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.widget.Toast;
import de.hshannover.f4.trust.ifmapj.exception.IfmapErrorResult;
import de.hshannover.f4.trust.ifmapj.exception.IfmapException;
import de.hshannover.f4.trust.ironcontrol.R;
import de.hshannover.f4.trust.ironcontrol.logger.Level;
import de.hshannover.f4.trust.ironcontrol.logger.Logger;
import de.hshannover.f4.trust.ironcontrol.logger.LoggerFactory;
import de.hshannover.f4.trust.ironcontrol.logic.Connection;

public class ConnectionTask extends AsyncTask<Long, Void, Boolean> {

	public static final String MASSAGE_CONNECTING = "Connecting...";

	private static final Logger logger = LoggerFactory.getLogger(ConnectionTask.class);

	private Context context;
	private Resources r;

	private ProgressDialog pd;

	private ConnectTaskEnum type;

	private String error;

	public enum ConnectTaskEnum {
		CONNECT,
		DISCONNECT;
	}


	public ConnectionTask(Activity context, ConnectTaskEnum type){
		this.context = context;
		this.type = type;
		this.r = context.getResources();

		pd = new ProgressDialog(context);
		switch(type){
		case CONNECT: pd.setMessage(r.getString(R.string.connecting));
		break;
		case DISCONNECT: pd.setMessage(r.getString(R.string.disconnecting));
		break;
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pd.show();
	}

	@Override
	protected Boolean doInBackground(Long... params) {
		Thread.currentThread().setName(ConnectionTask.class.getSimpleName());
		logger.log(Level.DEBUG, r.getString(R.string.runConnectionTask));
		try {

			switch(type){
			case CONNECT:

				if(params.length == 0){
					Connection.connect();
				}else{
					Connection.connect(params[0]);
				}

				break;
			case DISCONNECT: Connection.disconnect();
			break;
			}

		} catch (IfmapErrorResult e) {
			error = e.getErrorCode().toString();
			return false;
		} catch (IfmapException e) {
			error = e.getDescription();
			return false;
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		pd.dismiss();

		if(result){
			switch(type){
			case CONNECT: Toast.makeText(context, r.getString(R.string.newConnection), Toast.LENGTH_SHORT).show();
			break;
			case DISCONNECT: Toast.makeText(context, r.getString(R.string.disconnected), Toast.LENGTH_SHORT).show();
			break;
			}

		} else {
			switch(type){
			case CONNECT: Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
			break;
			case DISCONNECT: Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
			break;
			}
		}
	}

}
