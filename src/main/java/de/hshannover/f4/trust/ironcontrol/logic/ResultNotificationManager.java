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
package de.hshannover.f4.trust.ironcontrol.logic;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import de.hshannover.f4.trust.ifmapj.messages.PollResult;
import de.hshannover.f4.trust.ifmapj.messages.SearchResult;
import de.hshannover.f4.trust.ironcontrol.R;
import de.hshannover.f4.trust.ironcontrol.database.DBContentProvider;
import de.hshannover.f4.trust.ironcontrol.database.entities.Requests;
import de.hshannover.f4.trust.ironcontrol.logger.Level;
import de.hshannover.f4.trust.ironcontrol.logger.Logger;
import de.hshannover.f4.trust.ironcontrol.logger.LoggerFactory;
import de.hshannover.f4.trust.ironcontrol.logic.data.PollReceiver;
import de.hshannover.f4.trust.ironcontrol.view.list_activities.ListHierarchyActivity;
import de.hshannover.f4.trust.ironcontrol.view.list_activities.ListOverviewActivity;
import de.hshannover.f4.trust.ironcontrol.view.list_activities.ListResponsesActivity;

public class ResultNotificationManager extends Thread implements PollReceiver{

	private static final Logger logger = LoggerFactory.getLogger(ResultNotificationManager.class);

	private static final int SUBSCRIBE_NOTIFY_ID = 0;

	private SharedPreferences prefData;

	private Context context;

	private Resources r;

	private NotificationManager mNotificationManager;

	private BlockingQueue<PollResult> newEvents;

	private int notifyId;


	public ResultNotificationManager(Context context) {
		logger.log(Level.DEBUG, "New ResultNotificationManager()");

		this.context = context;
		this.r = context.getResources();
		this.mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		this.prefData =  PreferenceManager.getDefaultSharedPreferences(context);
		this.newEvents = new LinkedBlockingQueue<PollResult>();
		this.notifyId = 10;
	}

	@Override
	public void run() {
		setName(ResultNotificationManager.class.getSimpleName());
		logger.log(Level.DEBUG, "run()...");

		PollResult event;
		try {
			while (!interrupted()) {
				event = this.newEvents.take();
				if (event != null) {
					for(SearchResult sr: event.getResults()){
						if(prefData.getBoolean(r.getString(R.string.aboutNewResults), false)){
							resultNotify(sr.getName(), sr.getResultItems().size());

							if(prefData.getBoolean(r.getString(R.string.vibration), false)){
								vibrator();
							}
							if(prefData.getBoolean(r.getString(R.string.sound), false)){
								ton();
							}
						}
					}
				}else{
					logger.log(Level.WARN, "event is null");
				}
			}
		} catch (InterruptedException e) {
			logger.log(Level.DEBUG, e.getMessage(), e);
		}
		logger.log(Level.DEBUG, "...run()");
	}

	private void ton(){
		//		Field[] fields = R.raw.class.getFields();
		//		for (Field field : fields) {
		//			int rid = 0;
		//			try {
		//				rid = field.getInt(field);
		//			} catch (IllegalArgumentException e) {
		//				e.printStackTrace();
		//			} catch (IllegalAccessException e) {
		//				e.printStackTrace();
		//			}
		//
		//			// Use that if you just need the file name
		//			String filename = field.getName();
		//
		//			System.out.println("rid=" + rid +" name=" + filename);
		//		}
		MediaPlayer test = MediaPlayer.create(context, R.raw.star_struck);
		test.start();
		//		get
	}

	private void vibrator(){

		Vibrator mVibratorService =(Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		mVibratorService.vibrate(new long[]{0, 250, 500, 500}, -1);

	}

	public void newSubscribeNotify(String subscribeName){
		if(prefData.getBoolean(r.getString(R.string.activeSubscriptions), false)){
			NotificationCompat.Builder mBuilder =new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.ic_rss_feeds)
			.setContentTitle(r.getString(R.string.subscription))
			.setAutoCancel(false)
			.setOngoing(true)
			.setTicker(r.getString(R.string.newSubscription) + " " +subscribeName);

			Intent resultIntent = new Intent(context, ListOverviewActivity.class);
			resultIntent.setAction(r.getString(R.string.ACTION_SAVED_SUBSCRIPTIONS));

			PendingIntent contentIntent = PendingIntent.getActivity(
					context, 0,
					resultIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			mBuilder.setContentIntent(contentIntent);

			mNotificationManager.notify(SUBSCRIBE_NOTIFY_ID, mBuilder.getNotification());
		}
	}

	private void resultNotify(String subscribeName, int itemCount){
		NotificationCompat.Builder mBuilder =new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.ic_rss_feeds)
		.setContentTitle(r.getString(R.string.newSubscriptionResult))
		.setContentText(subscribeName)
		.setAutoCancel(true)
		.setTicker(r.getString(R.string.newIncomingResult))
		.setLights(0, 2000, 1000)
		.setContentInfo(itemCount +" "+ r.getString(R.string.result_items));


		Intent resultIntent = new Intent(context, ListResponsesActivity.class);
		resultIntent.setAction(r.getString(R.string.ACTION_SAVED_SUBSCRIPTIONS));
		resultIntent.putExtra(ListHierarchyActivity.EXTRA_ID_KEY, getSavedResultId(subscribeName));


		PendingIntent contentIntent = PendingIntent.getActivity(
				context, 0,
				resultIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		mBuilder.setContentIntent(contentIntent);

		mNotificationManager.notify(notifyId, mBuilder.getNotification());

	}

	private String getSavedResultId(String subscribeName){
		Cursor cData = context.getContentResolver().query(
				DBContentProvider.SUBSCRIPTION_URI,
				null,
				Requests.COLUMN_NAME + "=?",
				new String[]{subscribeName},
				null);

		if(cData.moveToFirst()){
			if(cData.getCount() > 1){
				logger.log(Level.WARN, "For " + subscribeName + " there are more IDs");
			}
			return cData.getString(cData.getColumnIndexOrThrow(Requests.COLUMN_ID));
		}
		return "";
	}

	@Override
	public void submitNewPollResult(PollResult pr) {
		logger.log(Level.DEBUG, "newPollResult()...");
		try {
			this.newEvents.put(pr);
		} catch (InterruptedException e) {
			logger.log(Level.DEBUG, e.getMessage());
		}
		logger.log(Level.DEBUG, "...newPollResult()");
	}

}
