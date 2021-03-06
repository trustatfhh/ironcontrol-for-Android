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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import de.hshannover.f4.trust.ifmapj.exception.IfmapErrorResult;
import de.hshannover.f4.trust.ifmapj.messages.PollResult;
import de.hshannover.f4.trust.ifmapj.messages.ResultItem;
import de.hshannover.f4.trust.ifmapj.messages.SearchResult;
import de.hshannover.f4.trust.ironcontrol.database.DBContentProvider;
import de.hshannover.f4.trust.ironcontrol.database.entities.Requests;
import de.hshannover.f4.trust.ironcontrol.database.entities.Responses;
import de.hshannover.f4.trust.ironcontrol.database.entities.ResultItems;
import de.hshannover.f4.trust.ironcontrol.database.entities.ResultMetaAttributes;
import de.hshannover.f4.trust.ironcontrol.database.entities.ResultMetadata;
import de.hshannover.f4.trust.ironcontrol.logger.Level;
import de.hshannover.f4.trust.ironcontrol.logger.Logger;
import de.hshannover.f4.trust.ironcontrol.logger.LoggerFactory;
import de.hshannover.f4.trust.ironcontrol.logic.data.PollReceiver;

/**
 * Class for save search or subscription results.
 * 
 * @author Daniel Wolf
 * @author Marcel Reichenbach
 * @author Anton Saenko
 * @author Arne Loth
 * @version 1.0
 */

public class StoredResponses extends Thread implements PollReceiver  {

	private static final Logger logger = LoggerFactory.getLogger(StoredResponses.class);

	private Context context;

	private BlockingQueue<PollResult> newEvents;


	public StoredResponses(Context context) {
		logger.log(Level.DEBUG, "New StoredResponses()");

		this.context = context;
		this.newEvents = new LinkedBlockingQueue<PollResult>();
	}

	@Override
	public void run() {
		setName(StoredResponses.class.getSimpleName());
		logger.log(Level.DEBUG, "run()...");

		PollResult event;
		try {
			while (!interrupted()) {
				event = this.newEvents.take();
				if (event != null) {
					persistResult(event);
				}
			}
		} catch (InterruptedException e) {
			logger.log(Level.DEBUG, e.getMessage(), e);
		}
		logger.log(Level.DEBUG, "...run()");
	}

	public void persistResult(PollResult pr) {
		logger.log(Level.DEBUG, "persistResult()...");
		//TODO
		Collection<IfmapErrorResult> errorRes = pr.getErrorResults();
		Collection<SearchResult> allRes = pr.getResults();

		for (SearchResult sr : allRes) {
			int requestId = -1;
			if (!sr.getName().equals("")) {
				String selectionArgs[] = {sr.getName()};
				String selection = Requests.COLUMN_NAME + "=?";
				String[] projection = new String[]{Requests.COLUMN_ID};
				Cursor cursor = context.getContentResolver().query(
						DBContentProvider.SUBSCRIPTION_URI, projection,
						selection, selectionArgs, null);
				if (cursor.getCount() == 1) {
					cursor.moveToNext();
					requestId = cursor.getInt(cursor
							.getColumnIndexOrThrow(Requests.COLUMN_ID));
					logger.log(Level.DEBUG, "Saved Subscription was found, persist ...");

					Collection<ResultItem> resultItems = sr.getResultItems();
					SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
					SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.y");
					String time = timeFormat.format(Calendar.getInstance()
							.getTime());
					String date = dateFormat.format(Calendar.getInstance()
							.getTime());

					ContentValues respons = new ContentValues();
					respons.put(Responses.COLUMN_DATE, date);
					respons.put(Responses.COLUMN_TIME, time);
					Uri responsUri = context.getContentResolver()
							.insert(Uri.parse(DBContentProvider.SUBSCRIPTION_URI
									+ "/" + requestId + "/"
									+ DBContentProvider.RESPONSES), respons);

					for (ResultItem resultItem : resultItems) {
						ContentValues resultItemsValues = new ContentValues();
						String responsId = responsUri.getLastPathSegment();
						resultItemsValues.put(ResultItems.COLUMN_IDENTIFIER1,
								resultItem.getIdentifier1().toString());
						if (resultItem.getIdentifier2() != null) {
							resultItemsValues.put(ResultItems.COLUMN_IDENTIFIER2,
									resultItem.getIdentifier2().toString());
						}
						Uri resultItemUri = context.getContentResolver().insert(
								Uri.parse(DBContentProvider.RESPONSES_URI + "/"
										+ responsId + "/"
										+ DBContentProvider.RESULT_ITEMS),
										resultItemsValues);

						Collection<Document> meta = resultItem.getMetadata();
						if (!meta.isEmpty()) {
							for (Document document : meta) {
								if (document.hasChildNodes()) {
									NodeList nl = document.getChildNodes();
									for (int i = 0; i < nl.getLength(); i++) {
										NamedNodeMap attributes = nl.item(i)
												.getAttributes();
										Node ifmap_cardinality = attributes
												.getNamedItem("ifmap-cardinality");
										Node ifmap_publisher_id = attributes
												.getNamedItem("ifmap-publisher-id");
										Node ifmap_timestamp = attributes
												.getNamedItem("ifmap-timestamp");

										String resultItemId = resultItemUri
												.getLastPathSegment();
										String localName = nl.item(i)
												.getLocalName();
										String nameSpaceUri = nl.item(i)
												.getNamespaceURI();
										String prefix = nl.item(i).getPrefix();
										String cardinality = "";
										String publisherID = "";
										String timestamp = "";

										if (ifmap_cardinality != null) {
											cardinality = attributes.getNamedItem(
													"ifmap-cardinality")
													.getNodeValue();
										}
										if (ifmap_publisher_id != null) {
											publisherID = attributes.getNamedItem(
													"ifmap-publisher-id")
													.getNodeValue();
										}
										if (ifmap_timestamp != null) {
											timestamp = attributes.getNamedItem(
													"ifmap-timestamp")
													.getNodeValue();
											// TODO ANTON
										}

										ContentValues metaValues = new ContentValues();
										metaValues.put(
												ResultMetadata.COLUMN_LOCAL_NAME,
												localName);
										metaValues.put(
												ResultMetadata.COLUMN_NAMESPACEURI,
												nameSpaceUri);
										metaValues.put(
												ResultMetadata.COLUMN_PREFIX,
												prefix);
										metaValues.put(
												ResultMetadata.COLUMN_CARDINALITY,
												cardinality);
										metaValues.put(
												ResultMetadata.COLUMN_PUBLISHERID,
												publisherID);
										metaValues.put(
												ResultMetadata.COLUMN_TIMESTAMP,
												timestamp);

										Uri resultIMetadataUri = context
												.getContentResolver()
												.insert(Uri
														.parse(DBContentProvider.RESULT_ITEMS_URI
																+ "/"
																+ resultItemId
																+ "/"
																+ DBContentProvider.RESULT_METADATA),
																metaValues);

										String resultMetadataId = resultIMetadataUri
												.getLastPathSegment();

										if (nl.item(i).hasAttributes()) {
											for (int y = 1; y < attributes
													.getLength(); y++) {
												String nodeName = attributes
														.item(y).getNodeName();
												if (!(nodeName
														.equals("ifmap-cardinality")
														|| nodeName
														.equals("ifmap-publisher-id") || nodeName
														.equals("ifmap-timestamp"))) {

													String nodeValue = attributes
															.item(y).getNodeValue();

													ContentValues metaAttributes = new ContentValues();
													metaAttributes
													.put(ResultMetaAttributes.COLUMN_NODE_NAME,
															nodeName);
													metaAttributes
													.put(ResultMetaAttributes.COLUMN_NODE_VALUE,
															nodeValue);

													context.getContentResolver()
													.insert(Uri
															.parse(DBContentProvider.RESULT_METADATA_URI
																	+ "/"
																	+ resultMetadataId
																	+ "/"
																	+ DBContentProvider.RESULT_META_ATTRIBUTES),
																	metaAttributes);
												}
											}
										}

										if(nl.item(i).hasChildNodes()){
											for(int xx=0; xx<nl.item(i).getChildNodes().getLength(); xx++){
												Node n = nl.item(i).getChildNodes().item(xx);
												if (n.getNodeType() == Node.ELEMENT_NODE) {
													String nodeName = n.getNodeName();
													String nodeValue = n.getTextContent();

													ContentValues metaAttributes = new ContentValues();
													metaAttributes.put(ResultMetaAttributes.COLUMN_NODE_NAME, nodeName);
													metaAttributes.put(ResultMetaAttributes.COLUMN_NODE_VALUE, nodeValue);

													context.getContentResolver().insert(Uri.parse(DBContentProvider.RESULT_METADATA_URI + "/" + resultMetadataId + "/" + DBContentProvider.RESULT_META_ATTRIBUTES) , metaAttributes);

												}

											}
										}
									}
								}
							}
						}
					}
					logger.log(Level.DEBUG, "Subscription result is saved");
				} else {
					logger.log(Level.DEBUG, "No uniquely Subscription found");
				}
			}
		}
		logger.log(Level.DEBUG, "...persistResult()");
	}

	@Override
	public void submitNewPollResult(PollResult pr) {
		logger.log(Level.DEBUG, "submitNewPollResult()...");
		try {
			this.newEvents.put(pr);
		} catch (InterruptedException e) {
			logger.log(Level.DEBUG, e.getMessage());
		}
		logger.log(Level.DEBUG, "...submitNewPollResult()");
	}
}
