package de.hshannover.inform.trust.ironcontrol.view.irondetect;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapErrorResult;
import de.fhhannover.inform.trust.ifmapj.messages.PollResult;
import de.fhhannover.inform.trust.ifmapj.messages.ResultItem;
import de.fhhannover.inform.trust.ifmapj.messages.SearchResult;
import de.hshannover.inform.trust.ironcontrol.logger.Level;
import de.hshannover.inform.trust.ironcontrol.logger.Logger;
import de.hshannover.inform.trust.ironcontrol.logger.LoggerFactory;
import de.hshannover.inform.trust.ironcontrol.logic.SubscriptionPoller;
import de.hshannover.inform.trust.ironcontrol.logic.data.PollReceiver;
import de.hshannover.inform.trust.ironcontrol.view.irondetect.GuiData.GuiDataType;

/**
 * Class for build GuiData objects
 * 
 * @author Marcel Reichenbach
 * @version %I%, %G%
 * @since 0.1
 */

public class IrondetectRemoteReceiver extends Thread implements PollReceiver  {

	private static final Logger logger = LoggerFactory.getLogger(IrondetectRemoteReceiver.class);

	private PageAdapter pAdapter;

	private BlockingQueue<PollResult> newEvents;

	private SharedPreferences preferenceData;

	public IrondetectRemoteReceiver(Context context, PageAdapter pAdapter) {
		logger.log(Level.DEBUG, "New...");

		this.pAdapter = pAdapter;
		this.newEvents = new LinkedBlockingQueue<PollResult>();
		this.preferenceData = PreferenceManager.getDefaultSharedPreferences(context);

		logger.log(Level.DEBUG, "...New");
	}

	@Override
	public void run() {
		setName(IrondetectRemoteReceiver.class.getSimpleName());
		logger.log(Level.DEBUG, "run()...");

		// sign up
		SubscriptionPoller.getInstance().addPollReceiver(this);

		// work
		PollResult event;

		try {
			while (!Thread.currentThread().isInterrupted()) {
				event = this.newEvents.take();
				if (event != null) {
					buildGuiData(event);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		logger.log(Level.DEBUG, "...run()");
	}

	@Override
	public void submitNewPollResult(PollResult pr) {

		try {
			this.newEvents.put(pr);
		} catch (InterruptedException e) {
			logger.log(Level.DEBUG, e.getMessage(), e);
		}
	}

	private void newGuiData(final GuiData data){
		pAdapter.newGuiData(data);
	}

	private void buildGuiData(PollResult mPollResult){
		logger.log(Level.DEBUG, "buildGuiData...");

		String subscribeName = preferenceData.getString(
				IrondetectFragmentActivity.PREFERENCE_KEY_NAME,
				IrondetectFragmentActivity.PREFERENCE_DEF_NAME);


		Collection<IfmapErrorResult> errorRes = mPollResult.getErrorResults();
		Collection<SearchResult> allRes    = mPollResult.getResults();
		for(SearchResult sr: allRes){
			if(sr.getName().equals(subscribeName)){
				logger.log(Level.DEBUG, "irondetectPDP gefunden");
				for (ResultItem resultItem : sr.getResultItems()) {
					Collection<Document> meta = resultItem.getMetadata();
					if(!meta.isEmpty()){
						logger.log(Level.DEBUG, "is Not Empty ");
						for (Document document : meta) {
							if(document.hasChildNodes()){
								logger.log(Level.DEBUG, "hasChildNodes");
								NodeList nl = document.getChildNodes();
								for(int i=0; i<nl.getLength(); i++){
									NamedNodeMap attributes = nl.item(i).getAttributes();
									String localName = nl.item(i).getLocalName();

									if(nl.item(i).hasAttributes()){
										logger.log(Level.DEBUG, "hasAttributes");
										GuiData newData = new GuiData();
										for(int y=1; y<attributes.getLength(); y++){
											String nodeName = attributes.item(y).getNodeName();
											String nodeValue = attributes.item(y).getNodeValue();
											if(nodeName.equals("Index")){
												newData.setRowCount(nodeValue);
											}else if(nodeName.equals("Device")){
												newData.setDevice(nodeValue);
											}else if(nodeName.equals("ID")){
												newData.setId(nodeValue);
											}else if(nodeName.equals("Value")){
												newData.setValue(nodeValue);
											}else if(nodeName.equals("TimeStamp")){
												newData.setTimeStamp(nodeValue);
											}
										}

										if(localName.equals("Rule")){
											newData.setType(GuiDataType.RULES);
											newGuiData(newData);
										}else if(localName.equals("Signature")){
											newData.setType(GuiDataType.SIGNATURES);
											newGuiData(newData);
										}else if(localName.equals("Anomaly")){
											newData.setType(GuiDataType.ANOMALY);
											newGuiData(newData);
										}else if(localName.equals("Condition")){
											newData.setType(GuiDataType.CONDITIONS);
											newGuiData(newData);
										}
										logger.log(Level.DEBUG, "...buildGuiData");
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
