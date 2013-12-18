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
package de.hshannover.inform.trust.ironcontrol.logic;

import java.util.ArrayList;

import de.hshannover.f4.trust.ifmapj.channel.ARC;
import de.hshannover.f4.trust.ifmapj.exception.EndSessionException;
import de.hshannover.f4.trust.ifmapj.exception.IfmapErrorResult;
import de.hshannover.f4.trust.ifmapj.exception.IfmapException;
import de.hshannover.f4.trust.ifmapj.exception.InitializationException;
import de.hshannover.f4.trust.ifmapj.messages.PollResult;
import de.hshannover.inform.trust.ironcontrol.logger.Level;
import de.hshannover.inform.trust.ironcontrol.logger.Logger;
import de.hshannover.inform.trust.ironcontrol.logger.LoggerFactory;
import de.hshannover.inform.trust.ironcontrol.logic.data.PollReceiver;
import de.hshannover.inform.trust.ironcontrol.logic.data.PollSender;

/**
 * Class for connection management
 * @author Daniel Wolf
 * @author Anton Saenko
 * @author Arne Loth
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class SubscriptionPoller extends Thread implements PollSender {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionPoller.class);

	private ArrayList<PollReceiver> mPollReceiver;

	private PollResult mPollResult;

	private static SubscriptionPoller mInstance;


	private SubscriptionPoller() {
		logger.log(Level.DEBUG, "New SubscriptionPoller()");
		mPollReceiver = new ArrayList<PollReceiver>();
	}

	public static synchronized SubscriptionPoller getInstance(){
		if(mInstance == null){
			mInstance = new SubscriptionPoller();
		}
		return mInstance;
	}

	/**
	 * Is a Thread method create a pollresult
	 * 
	 * @since 0.1
	 */
	@Override
	public void run() {
		setName(SubscriptionPoller.class.getSimpleName());
		logger.log(Level.DEBUG, "run()...");

		while (!interrupted()) {
			ARC mArc = getARC();
			if(mArc != null){
				try {
					logger.log(Level.DEBUG, "new poll...");
					mPollResult = mArc.poll();
					logger.log(Level.DEBUG, "...poll OK");

					// submit the result
					onNewPollResult(mPollResult);

					// forget the result
					mPollResult = null;

				} catch (IfmapErrorResult e) {
					logger.log(Level.ERROR, "IfmapErrorResult: STOP Poll" + e.getErrorString(), e);
					waitForNewConnection();

				} catch (EndSessionException e) {
					logger.log(Level.ERROR, "EndSessionException: STOP Poll", e);
					waitForNewConnection();

				} catch (IfmapException e) {
					logger.log(Level.ERROR, "IfmapException: STOP Poll " + e.getDescription(), e);
					waitForNewConnection();
				}
			}else{
				waitForNewConnection();
			}
		}
		logger.log(Level.DEBUG, "...run()");
	}

	@Override
	public void addPollReceiver(PollReceiver pr) {
		if(pr != null){
			logger.log(Level.DEBUG, "add new PollReceiver()");
			mPollReceiver.add(pr);
		}else{
			logger.log(Level.WARN, "PollReceiver is null, dosen't add");
		}
	}

	public void onNewPollResult(PollResult pr) {
		logger.log(Level.DEBUG, "onNewPollResult()...");
		for(PollReceiver receiver: mPollReceiver){
			receiver.submitNewPollResult(pr);
		}
		logger.log(Level.DEBUG, "...onNewPollResult()");
	}

	private void waitForNewConnection() {
		logger.log(Level.DEBUG, "waitForNewConnection()...");
		try {

			synchronized (this){
				wait();
			}

		} catch (InterruptedException e) {
			logger.log(Level.DEBUG, e.getMessage(), e);
		}
		logger.log(Level.DEBUG, "...waitForNewConnection()");
	}

	private ARC getARC() {
		try {
			return Connection.getARC();
		} catch (InitializationException e) {
			logger.log(Level.ERROR, e.getDescription(),e);
		}
		return null;
	}
}
