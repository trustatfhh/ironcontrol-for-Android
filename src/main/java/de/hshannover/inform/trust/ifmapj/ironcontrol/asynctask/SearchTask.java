package de.hshannover.inform.trust.ifmapj.ironcontrol.asynctask;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapErrorResult;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapException;
import de.fhhannover.inform.trust.ifmapj.messages.ResultItem;
import de.fhhannover.inform.trust.ifmapj.messages.SearchResult;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.DBContentProvider;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.Requests;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.Responses;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.ResultItems;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.ResultMetaAttributes;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.ResultMetadata;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.RequestsController;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.SearchRequestData;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Level;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Logger;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LoggerFactory;

/**
 * AsyncTask to search and save the result in background and inform the user.
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class SearchTask extends AsyncTask<Void, Void, Void> {

	private static final Logger logger = LoggerFactory.getLogger(SearchTask.class);

	private ProgressDialog pd;
	private Context context;
	private String name, startIdentifier, startIdentifierValue, matchLinks, resultFilter, terminalIdentifierTypes;
	private int maxDepth, maxSitze;

	private String error;

	private SearchResult mSearchResult;

	public SearchTask(String name, String startIdentifier, String startIdentifierValue, int maxDepth, Context context, String message){
		this(name, startIdentifier, startIdentifierValue, null, null, maxDepth, 0, null, context, message);
	}

	public SearchTask(String name, String startIdentifier, String startIdentifierValue, String matchLinks, String resultFilter, int maxDepth, int maxSitze, String terminalIdentifierTypes, Context context, String message) {
		logger.log(Level.DEBUG, "NEW...");
		this.name = name;
		this.context = context;
		this.startIdentifier = startIdentifier;
		this.startIdentifierValue = startIdentifierValue;
		this.matchLinks = matchLinks;
		this.resultFilter = resultFilter;
		this.maxDepth = maxDepth;
		this.maxSitze = maxSitze;
		this.terminalIdentifierTypes = terminalIdentifierTypes;

		pd= new ProgressDialog(this.context);
		pd.setMessage(message);
		logger.log(Level.DEBUG, "...NEW");
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
		logger.log(Level.DEBUG, "doInBackground()...");
		try {

			mSearchResult = RequestsController.createSearch(buildRequestData());

		} catch (IfmapErrorResult e) {
			error = e.getErrorCode().toString();
		} catch (IfmapException e) {
			error = e.getDescription();
		} catch (Exception e) {
			error = e.getMessage();
		}

		String selectionArgs[] = {name};
		String selection = Requests.COLUMN_NAME + "=?";
		String[] projection = new String[]{Requests.COLUMN_ID};
		Cursor cursor = context.getContentResolver().query(DBContentProvider.SEARCH_URI, projection, selection, selectionArgs, null);
		if(cursor.getCount() == 1){
			cursor.moveToNext();
			int requestId = cursor.getInt(cursor.getColumnIndexOrThrow(Requests.COLUMN_ID));
			Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "[SearchTask] Saved Seach was found, persist ...");
			persistSearchResult(requestId);
		}else{
			Log.d("de.hshannover.inform.trust.ifmapj.ironcontrol", "[SearchTask] Too much Search was found");
		}

		logger.log(Level.DEBUG, "...doInBackground()");
		return null;
	}

	private SearchRequestData buildRequestData() {
		logger.log(Level.DEBUG, "buildRequestData()...");
		SearchRequestData data = new SearchRequestData();
		data.setIdentifier1(startIdentifier);
		data.setIdentifier1Value(startIdentifierValue);
		data.setMaxDepth(maxDepth);
		data.setMaxSize(maxSitze);
		data.setResultFilter(resultFilter);
		data.setTerminalIdentifierTypes(terminalIdentifierTypes);
		data.setMatchLinks(matchLinks);
		logger.log(Level.DEBUG, "...buildRequestData()");
		return data;
	}

	private void persistSearchResult(int requestId){
		logger.log(Level.DEBUG, "persistSearchResult()...");
		Collection<ResultItem> resultItems = mSearchResult.getResultItems();
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.GERMANY);
		SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.y", Locale.GERMANY);
		String time = timeFormat.format(System.currentTimeMillis());
		String date = dateFormat.format(System.currentTimeMillis());

		ContentValues respons = new ContentValues();
		respons.put(Responses.COLUMN_DATE, date);
		respons.put(Responses.COLUMN_TIME, time);
		Uri responsUri = context.getContentResolver().insert(Uri.parse(DBContentProvider.SEARCH_URI + "/" + requestId + "/" + DBContentProvider.RESPONSES), respons);

		for (ResultItem resultItem : resultItems) {
			ContentValues resultItemsValues = new ContentValues();
			String responsId = responsUri.getLastPathSegment();
			resultItemsValues.put(ResultItems.COLUMN_IDENTIFIER1, resultItem.getIdentifier1().toString());
			if(resultItem.getIdentifier2() != null){
				resultItemsValues.put(ResultItems.COLUMN_IDENTIFIER2, resultItem.getIdentifier2().toString());
			}
			Uri resultItemUri = context.getContentResolver().insert(Uri.parse(DBContentProvider.RESPONSES_URI + "/" + responsId + "/" + DBContentProvider.RESULT_ITEMS), resultItemsValues);

			Collection<Document> meta = resultItem.getMetadata();
			if(!meta.isEmpty()){
				for (Document document : meta) {
					if(document.hasChildNodes()){
						NodeList nl = document.getChildNodes();
						for(int i=0; i<nl.getLength(); i++){
							NamedNodeMap attributes = nl.item(i).getAttributes();
							Node ifmap_cardinality = attributes.getNamedItem("ifmap-cardinality");
							Node ifmap_publisher_id = attributes.getNamedItem("ifmap-publisher-id");
							Node ifmap_timestamp = attributes.getNamedItem("ifmap-timestamp");

							String resultItemId = resultItemUri.getLastPathSegment();
							String localName = nl.item(i).getLocalName();
							String nameSpaceUri = nl.item(i).getNamespaceURI();
							String prefix = nl.item(i).getPrefix();
							String cardinality = "";
							String publisherID = "";
							String timestamp = "";

							if(ifmap_cardinality !=null ){
								cardinality = attributes.getNamedItem("ifmap-cardinality").getNodeValue();
							}
							if(ifmap_publisher_id !=null ){
								publisherID = attributes.getNamedItem("ifmap-publisher-id").getNodeValue();
							}
							if(ifmap_timestamp !=null ){
								timestamp = attributes.getNamedItem("ifmap-timestamp").getNodeValue();
								// TODO ANTON

							}

							ContentValues metaValues = new ContentValues();
							metaValues.put(ResultMetadata.COLUMN_LOCAL_NAME, localName);
							metaValues.put(ResultMetadata.COLUMN_NAMESPACEURI, nameSpaceUri);
							metaValues.put(ResultMetadata.COLUMN_PREFIX, prefix);
							metaValues.put(ResultMetadata.COLUMN_CARDINALITY, cardinality);
							metaValues.put(ResultMetadata.COLUMN_PUBLISHERID, publisherID);
							metaValues.put(ResultMetadata.COLUMN_TIMESTAMP, timestamp);

							Uri resultIMetadataUri = context.getContentResolver().insert(Uri.parse(DBContentProvider.RESULT_ITEMS_URI + "/" + resultItemId +"/" + DBContentProvider.RESULT_METADATA), metaValues);
							String resultMetadataId = resultIMetadataUri.getLastPathSegment();

							if(nl.item(i).hasAttributes()){
								for(int y=1; y<attributes.getLength(); y++){
									String nodeName = attributes.item(y).getNodeName();
									if(!((nodeName.equals("ifmap-cardinality") || nodeName.equals("ifmap-publisher-id") || nodeName.equals("ifmap-timestamp")))){

										String nodeValue = attributes.item(y).getNodeValue();

										ContentValues metaAttributes = new ContentValues();
										metaAttributes.put(ResultMetaAttributes.COLUMN_NODE_NAME, nodeName);
										metaAttributes.put(ResultMetaAttributes.COLUMN_NODE_VALUE, nodeValue);

										context.getContentResolver().insert(Uri.parse(DBContentProvider.RESULT_METADATA_URI + "/" + resultMetadataId + "/" + DBContentProvider.RESULT_META_ATTRIBUTES) , metaAttributes);
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
		logger.log(Level.DEBUG, "...saved SearchResult");
	}

	@Override
	protected void onPostExecute(Void result) {
		logger.log(Level.DEBUG, "onPostExecute()...");
		pd.dismiss();
		if(error != null){
			Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
		}else if(mSearchResult == null){
			logger.log(Level.WARN, context.getResources().getString(R.string.searchresult_is_null));
			Toast.makeText(context, context.getResources().getString(R.string.searchresult_is_null), Toast.LENGTH_SHORT).show();
		}else{
			if(!name.equals("")){
				Toast.makeText(context, "NEW Search Results for "+ name + " was saved.", Toast.LENGTH_SHORT).show();
			}
		}
		logger.log(Level.DEBUG, "...onPostExecute()");
	}
}
