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
 * This file is part of ironcontrol for android, version 1.0.1, implemented by the Trust@HsH research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2013 Trust@HsH
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
package de.hshannover.inform.trust.ironcontrol.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import de.hshannover.f4.trust.ifmapj.exception.IfmapErrorResult;
import de.hshannover.f4.trust.ifmapj.exception.IfmapException;
import de.hshannover.f4.trust.ifmapj.exception.InitializationException;
import de.hshannover.inform.trust.ironcontrol.R;
import de.hshannover.inform.trust.ironcontrol.logger.Level;
import de.hshannover.inform.trust.ironcontrol.logger.Logger;
import de.hshannover.inform.trust.ironcontrol.logger.LoggerFactory;
import de.hshannover.inform.trust.ironcontrol.logic.Connection;
import de.hshannover.inform.trust.ironcontrol.logic.data.Operation;
@Deprecated
public class PublishTestTask extends AsyncTask<Void, Void, Void> {

	private static final Logger logger = LoggerFactory.getLogger(PublishTestTask.class);
	private Operation publishEnum;

	private Context context;

	private InitializationException myIe;
	private IfmapErrorResult myEr;
	private IfmapException myE;

	public PublishTestTask(Context context, Operation publishEnum) {
		this.context = context;
		logger.log(Level.DEBUG, "New...");
		this.publishEnum = publishEnum;
		logger.log(Level.DEBUG, "...New");
	}

	@Override
	protected Void doInBackground(Void... params) {
		Thread.currentThread().setName(PublishTestTask.class.getSimpleName());
		logger.log(Level.DEBUG, "doInBackground()...");
		try {
			PDP testPDP = new PDP(Connection.getSSRC());
			switch(publishEnum){
			case UPDATE: testPDP.update();
			break;
			case DELETE: testPDP.delete();
			break;
			case NOTIFY:
				break;
			}
		} catch (InitializationException e) {
			logger.log(Level.ERROR, e.getMessage(), e);
			myIe = e;
		} catch (IfmapErrorResult e) {
			logger.log(Level.ERROR, e.getErrorString(), e);
			myEr = e;
		} catch (IfmapException e) {
			logger.log(Level.ERROR, e.getMessage(), e);
			myE = e;
		}
		logger.log(Level.DEBUG, "...doInBackground()");
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		logger.log(Level.DEBUG, "onPostExecute()...");

		if(myEr == null && myIe == null && myE == null){
			Toast.makeText(context, R.string.publishReceived, Toast.LENGTH_SHORT).show();
		}else if(myEr != null){
			Toast.makeText(context, myEr.getErrorCode().toString(), Toast.LENGTH_LONG).show();
		}else if(myIe != null){
			Toast.makeText(context, myIe.getDescription(), Toast.LENGTH_LONG).show();
		}else if(myE != null){
			Toast.makeText(context, myE.getDescription(), Toast.LENGTH_LONG).show();
		}

		logger.log(Level.DEBUG, "...onPostExecute()");
	}

}
