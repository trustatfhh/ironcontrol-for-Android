package de.hshannover.inform.trust.ifmapj.ironcontrol.logic;

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
import android.util.Log;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapErrorResult;
import de.fhhannover.inform.trust.ifmapj.messages.PollResult;
import de.fhhannover.inform.trust.ifmapj.messages.ResultItem;
import de.fhhannover.inform.trust.ifmapj.messages.SearchResult;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.DBContentProvider;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.Requests;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.Responses;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.ResultItems;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.ResultMetaAttributes;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.ResultMetadata;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.data.PollReceiver;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Level;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Logger;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LoggerFactory;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.util.Util;

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

	private Context context;

	private BlockingQueue<PollResult> newEvents;

	private static final Logger logger = LoggerFactory.getLogger(StoredResponses.class);

	public StoredResponses(Context context) {
		logger.log(Level.DEBUG, Util.getString(R.string.enter));

		this.context = context;
		this.newEvents = new LinkedBlockingQueue<PollResult>();

		logger.log(Level.DEBUG, Util.getString(R.string.exit));
	}

	@Override
	public void run() {
		Thread.currentThread().setName(StoredResponses.class.getSimpleName());
		logger.log(Level.DEBUG, Util.getString(R.string.enter) + "run()...");

		PollResult event;
		try {
			while (!Thread.currentThread().isInterrupted()) {
				event = this.newEvents.take();
				if (event != null) {
					persistResult(event);
				}
			}
		} catch (InterruptedException e) {
			logger.log(Level.DEBUG, e.getMessage(), e);
		}
		logger.log(Level.DEBUG, Util.getString(R.string.exit) + "...run()");
	}

	public void persistResult(PollResult pr) {
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
					Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol",
							"[SubscripThred]  Saved Subscription was found, persist ...");


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
	}

	@Override
	public void submitNewPollResult(PollResult pr) {
		logger.log(Level.DEBUG, Util.getString(R.string.enter) + "submitNewPollResult()...");
		try {
			this.newEvents.put(pr);
		} catch (InterruptedException e) {
			logger.log(Level.DEBUG, e.getMessage());
		}
		logger.log(Level.DEBUG, Util.getString(R.string.exit) + "...submitNewPollResult()");
	}
}
