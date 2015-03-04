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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import de.hshannover.f4.trust.ifmapj.exception.IfmapErrorResult;
import de.hshannover.f4.trust.ifmapj.exception.IfmapException;
import de.hshannover.f4.trust.ironcontrol.R;
import de.hshannover.f4.trust.ironcontrol.logger.Level;
import de.hshannover.f4.trust.ironcontrol.logger.Logger;
import de.hshannover.f4.trust.ironcontrol.logger.LoggerFactory;
import de.hshannover.f4.trust.ironcontrol.logic.RequestsController;

/**
 * AsyncTask to remove all publishes in background and inform the user.
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class PurgePublisherTask extends AsyncTask<Void, Void, Void> {

	private static final Logger logger = LoggerFactory.getLogger(PurgePublisherTask.class);

	public static final String MASSAGEUPDATE = "PurgePublisher...";

	private ProgressDialog pd;
	private Context context;

	private String publisherId;

	private String error;

	public PurgePublisherTask(Context context, String publisherId){
		init(context);

		this.publisherId = publisherId;

		logger.log(Level.DEBUG, "...NEW");
	}

	private void init(Context context){
		this.context = context;
		pd= new ProgressDialog(context);
	}

	@Override
	protected void onPreExecute() {
		logger.log(Level.DEBUG, "onPreExecute()...");
		super.onPreExecute();
		pd.show();
		logger.log(Level.DEBUG, "...onPreExecute()");
	}

	@Override
	protected Void doInBackground(Void... params) {
		Thread.currentThread().setName(PurgePublisherTask.class.getSimpleName());
		logger.log(Level.DEBUG, "doInBackground()...");

		try {

			RequestsController.purgePublisher(publisherId);

		} catch (IfmapErrorResult e) {
			error = e.getErrorCode().toString();
		} catch (IfmapException e) {
			error = e.getDescription();
		} catch (Exception e) {
			error = e.getMessage();
		}

		logger.log(Level.DEBUG, "...doInBackground()");
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		logger.log(Level.DEBUG, "onPostExecute()...");

		pd.dismiss();

		if(error == null){
			Toast.makeText(context, R.string.publishReceived, Toast.LENGTH_SHORT).show();
			logger.log(Level.INFO, context.getResources().getString(R.string.publishReceived));
		}else {
			Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
		}

		logger.log(Level.DEBUG, "...onPostExecute()");
	}
}
