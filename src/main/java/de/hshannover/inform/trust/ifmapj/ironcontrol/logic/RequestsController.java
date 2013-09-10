/**
 * Class for connection management
 * @author Daniel Wolf
 * @author Anton Saenko
 * @author Arne Loth
 * @since 0.5
 */

package de.hshannover.inform.trust.ifmapj.ironcontrol.logic;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;

import de.fhhannover.inform.trust.ifmapj.IfmapJ;
import de.fhhannover.inform.trust.ifmapj.binding.IfmapStrings;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapErrorResult;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapException;
import de.fhhannover.inform.trust.ifmapj.identifier.Identifier;
import de.fhhannover.inform.trust.ifmapj.identifier.Identifiers;
import de.fhhannover.inform.trust.ifmapj.identifier.IdentityType;
import de.fhhannover.inform.trust.ifmapj.messages.PublishDelete;
import de.fhhannover.inform.trust.ifmapj.messages.PublishElement;
import de.fhhannover.inform.trust.ifmapj.messages.PublishRequest;
import de.fhhannover.inform.trust.ifmapj.messages.Requests;
import de.fhhannover.inform.trust.ifmapj.messages.SearchRequest;
import de.fhhannover.inform.trust.ifmapj.messages.SearchResult;
import de.fhhannover.inform.trust.ifmapj.messages.SubscribeElement;
import de.fhhannover.inform.trust.ifmapj.messages.SubscribeRequest;
import de.fhhannover.inform.trust.ifmapj.messages.SubscribeUpdate;
import de.fhhannover.inform.trust.ifmapj.metadata.EnforcementAction;
import de.fhhannover.inform.trust.ifmapj.metadata.EventType;
import de.fhhannover.inform.trust.ifmapj.metadata.LocationInformation;
import de.fhhannover.inform.trust.ifmapj.metadata.Significance;
import de.fhhannover.inform.trust.ifmapj.metadata.StandardIfmapMetadataFactory;
import de.fhhannover.inform.trust.ifmapj.metadata.WlanSecurityEnum;
import de.fhhannover.inform.trust.ifmapj.metadata.WlanSecurityType;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Level;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Logger;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LoggerFactory;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.MainActivity;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.util.ResultNotificationManager;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.util.Util;

public class RequestsController {

	private static final Logger logger = LoggerFactory.getLogger(RequestsController.class);

	private static StandardIfmapMetadataFactory mF = IfmapJ.createStandardMetadataFactory();
	private static ResultNotificationManager mNotifier;
	private static SubscriptionPoller sPoller;
	private static List<String> metaList = getMetaList(R.array.metadaten_list);
	private static List<String> identifierList = getMetaList(R.array.identifier1_list);
	private static boolean firstSubscribtion = false;

	public static void purgePublisher(String publisherId) throws IfmapErrorResult, IfmapException{
		try {
			Connection.getSSRC().purgePublisher(publisherId);
		} catch (IfmapErrorResult e) {
			logger.log(Level.ERROR, e.getErrorString(), e);
			throw new IfmapErrorResult(e.getErrorCode(), e.getErrorString());
		} catch (IfmapException e) {
			logger.log(Level.ERROR, e.getDescription(), e);
			throw new IfmapException(e.getDescription(), e);
		}
	}

	/*
	 * ==============================================
	 * 							 _
	 *  ___	 		  _		 _	|_|        _
	 * |   |  _   _  | |	| |  _   ___  | |
	 * |  _| | | | | | |_	| | | | / __| | |_
	 * | |	 | |_| | | |  \	| | | | \__ \ | | |
	 * |_|	  \__,_| |_|__|	|_| |_| |___/ |_|_|
	 * 
	 * ===============================================
	 */

	public static void createPublish(PublishRequestData[] req) throws IfmapErrorResult, IfmapException, Exception{
		PublishRequest publishRequest = buildPublishRequest(req);

		sendPublishRequest(publishRequest);
	}

	public static void createPublish(PublishRequestData req) throws IfmapErrorResult, IfmapException, Exception {
		createPublish(new PublishRequestData[]{req});
	}

	private static PublishRequest buildPublishRequest(PublishRequestData[] req) throws Exception{
		PublishRequest request = Requests.createPublishReq();

		try {

			for (PublishRequestData element : req) {
				request.addPublishElement(buildPublishElement(element));
			}

		}	catch (Exception e){
			logger.log(Level.ERROR, e.toString(), e);
			throw new Exception(e);
		}

		return request;
	}

	private static PublishElement buildPublishElement(PublishRequestData req){

		PublishElement publishElement = null;

		Identifier identifier1 = buildIdentifier(req.getIdentifier1(), req.getIdentifierValue());
		Identifier identifier2 = buildIdentifier(req.getIdentifier2(), req.getIdentifier2Value());
		Document metadata = buildMetadata(req);


		switch (req.getOperation()){
		case UPDATE: publishElement = Requests.createPublishUpdate(identifier1, identifier2, metadata, req.getLifeTime());
		break;
		case NOTIFY: publishElement = Requests.createPublishNotify(identifier1, identifier2, metadata);
		break;
		case DELETE: publishElement = Requests.createPublishDelete(identifier1, identifier2, buildFilter(req));
		if(metaList.contains(req.getMetaName())){
			((PublishDelete)publishElement).addNamespaceDeclaration(IfmapStrings.STD_METADATA_PREFIX,IfmapStrings.STD_METADATA_NS_URI);
		}else{
			((PublishDelete)publishElement).addNamespaceDeclaration(req.getVendorMetaPrefix(), req.getVendorMetaUri());
		}

		break;
		}
		logger.log(Level.DEBUG, Util.getString(R.string.reqControlBuildPublishElement_returnPublishEl));
		return publishElement;
	}

	private static void sendPublishRequest(PublishRequest mPublishRequest) throws IfmapErrorResult, IfmapException{
		try {

			Connection.getSSRC().publish(mPublishRequest);

		} catch (IfmapErrorResult e) {
			logger.log(Level.ERROR, e.getErrorString(), e);
			throw new IfmapErrorResult(e.getErrorCode(),e.getErrorString());
		} catch (IfmapException e) {
			logger.log(Level.ERROR, e.getDescription(), e);
			throw new IfmapException(e.getDescription(), e);
		}
	}

	// Subscription

	public static void createSubscription(SubscribeRequestData[] req) throws IfmapErrorResult, IfmapException, Exception{

		if(!firstSubscribtion){
			startWorker();
			firstSubscribtion = true;
		}

		if(sPoller.isAlive()){
			System.out.println("SPoller ist am laufen");
			logger.log(Level.DEBUG, "SPoller ist am laufen");
		}else{
			System.out.println("SPoller ist NICHT!! am laufen");
			logger.log(Level.DEBUG, "SPoller ist NICHT!! am laufen");
		}

		SubscribeRequest mSubscribeRequest = buildSubscriptionRequest(req);

		sendSubscriptionRequest(mSubscribeRequest);

		if(sPoller.isAlive()){
			System.out.println("SPoller ist am laufen");
			logger.log(Level.DEBUG, "SPoller ist am laufen");
		}else{
			System.out.println("SPoller ist NICHT!! am laufen");
			logger.log(Level.DEBUG, "SPoller ist NICHT!! am laufen");
		}

		if(sPoller.isWaiting()){
			synchronized (sPoller){
				sPoller.notify();
			}
		}

		// wenn keine mehr aktive sind loeschen
		mNotifier.newSubscribeNotify(req[0].getName());
	}

	public static void createSubscription(SubscribeRequestData req) throws IfmapErrorResult, IfmapException, Exception{
		createSubscription(new SubscribeRequestData[]{req});
	}

	private static SubscribeRequest buildSubscriptionRequest(SubscribeRequestData[] req) throws Exception{
		SubscribeRequest mSubscribeRequest = Requests.createSubscribeReq();

		try{
			for (SubscribeRequestData element : req) {
				switch (element.getType()) {
				case UPDATE : mSubscribeRequest.addSubscribeElement(buildSubscribeUpdate(element));
				break;
				case DELETE : mSubscribeRequest.addSubscribeElement(buildSubscribeDelete(element));
				break;
				default : logger.log(Level.FATAL, "Wrong Operation type for subscription");
				break;
				}

			}

		}	catch (Exception e){
			logger.log(Level.ERROR, e.toString(), e);
			throw new Exception(e);
		}

		return mSubscribeRequest;
	}

	private static SubscribeElement buildSubscribeDelete(SubscribeRequestData req){
		return Requests.createSubscribeDelete(req.getName());
	}

	private static SubscribeElement buildSubscribeUpdate(SubscribeRequestData req){
		SubscribeUpdate su = Requests.createSubscribeUpdate();

		// set name
		su.setName(req.getName());

		// set start identifier
		su.setStartIdentifier(buildIdentifier(req.getIdentifier1(), req.getIdentifierValue()));

		// set match-links if necessary
		if (req.getMatchLinks() != null) {
			su.setMatchLinksFilter(req.getMatchLinks());
		}

		// set max-depth if necessary
		su.setMaxDepth(req.getMaxDepth());

		// set max-size if necessary
		if (req.getMaxSize() != 0) {
			su.setMaxSize(req.getMaxSize());
		}

		// set result-filter if necessary
		if (req.getResultFilter() != null) {
			su.setResultFilter(req.getResultFilter());
		}

		// set terminal-identifier-type if necessary
		if (req.getTerminalIdentifierTypes() != null) {
			su.setTerminalIdentifierTypes(req.getTerminalIdentifierTypes());
		}

		// add default namespaces
		su.addNamespaceDeclaration(
				IfmapStrings.BASE_PREFIX,
				IfmapStrings.BASE_NS_URI);
		su.addNamespaceDeclaration(
				IfmapStrings.STD_METADATA_PREFIX,
				IfmapStrings.STD_METADATA_NS_URI);

		// add custom namespaces
		if (req.getNameSpacePrefix() != null && req.getNameSpaceURI() != null) {
			su.addNamespaceDeclaration(
					req.getNameSpacePrefix(),
					req.getNameSpaceURI());
		}
		logger.log(Level.DEBUG, Util.getString(R.string.reqControlBuildSubscribeElement_returnSuEl));
		return su;
	}

	private static void sendSubscriptionRequest(SubscribeRequest mSubscribeRequest) throws IfmapErrorResult, IfmapException{
		try {
			Connection.getSSRC().subscribe(mSubscribeRequest);
		} catch (IfmapErrorResult e) {
			logger.log(Level.ERROR, e.getErrorString(), e);
			throw new IfmapErrorResult(e.getErrorCode(),e.getErrorString());
		} catch (IfmapException e) {
			logger.log(Level.ERROR, e.getDescription(), e);
			throw new IfmapException(e.getDescription(), e);
		}
	}

	// Search

	public static SearchResult createSearch(SearchRequestData req) throws IfmapErrorResult, IfmapException, Exception{
		logger.log(Level.DEBUG, Util.getString(R.string.reqControlCreateSearchRet));

		SearchRequest mSearchRequest = buildSearchRequest(req);

		SearchResult mSearchResult = startSearch(mSearchRequest);

		return mSearchResult;
	}

	private static SearchResult startSearch(SearchRequest search) throws IfmapErrorResult, IfmapException{
		logger.log(Level.DEBUG, Util.getString(R.string.reqControlStartSearchRet));

		SearchResult mSearchResult;

		try{
			mSearchResult = Connection.getSSRC().search(search);
		} catch (IfmapErrorResult e) {
			logger.log(Level.ERROR, e.getErrorString(), e);
			throw new IfmapErrorResult(e.getErrorCode(),e.getErrorString());
		} catch (IfmapException e) {
			logger.log(Level.ERROR, e.getDescription(), e);
			throw new IfmapException(e.getDescription(), e);
		}

		return mSearchResult;
	}

	private static SearchRequest buildSearchRequest(SearchRequestData req) throws Exception{
		SearchRequest mSearchRequest = Requests.createSearchReq();

		try {

			// set start identifier
			mSearchRequest.setStartIdentifier(buildIdentifier(req.getIdentifier1(), req.getIdentifierValue()));

			// set match-links if necessary
			if (req.getMatchLinks() != null) {
				mSearchRequest.setMatchLinksFilter(req.getMatchLinks());
			}

			// set max-depth if necessary
			mSearchRequest.setMaxDepth(req.getMaxDepth());

			// set max-size if necessary
			if (req.getMaxSize() != 0) {
				mSearchRequest.setMaxSize(req.getMaxSize());
			}

			// set result-filter if necessary
			if (req.getResultFilter() != null) {
				mSearchRequest.setResultFilter(req.getResultFilter());
			}

			// set terminal-identifier-type if necessary
			if (req.getTerminalIdentifierTypes() != null) {
				mSearchRequest.setTerminalIdentifierTypes(req.getTerminalIdentifierTypes());
			}

			// add default namespaces
			mSearchRequest.addNamespaceDeclaration(
					IfmapStrings.BASE_PREFIX,
					IfmapStrings.BASE_NS_URI);
			mSearchRequest.addNamespaceDeclaration(
					IfmapStrings.STD_METADATA_PREFIX,
					IfmapStrings.STD_METADATA_NS_URI);

			// add custom namespaces
			if (req.getNameSpacePrefix() != null && req.getNameSpaceURI() != null) {
				mSearchRequest.addNamespaceDeclaration(
						req.getNameSpacePrefix(),
						req.getNameSpaceURI());
			}
			logger.log(Level.DEBUG, Util.getString(R.string.reqControlBuildSearchReq_returnSeRe));

		}	catch (Exception e){
			logger.log(Level.ERROR, e.toString(), e);
			throw new Exception(e);
		}

		return mSearchRequest;
	}

	private static Identifier buildIdentifier(String sType, String value){
		Identifier type;
		if (sType.equals(identifierList.get(0))) {
			type = Identifiers.createAr(value);
		} else if (sType.equals(identifierList.get(1))) {
			type = Identifiers.createIp4(value);
		} else if (sType.equals(identifierList.get(2))) {
			type = Identifiers.createMac(value);
		} else if (sType.equals(identifierList.get(3))) {
			type = Identifiers.createDev(value);
		} else if (sType.equals(identifierList.get(4))) {
			type = Identifiers.createIdentity(IdentityType.userName, value);
		} else {
			return null;
		}
		logger.log(Level.DEBUG, "Build " + sType + " " + value);
		return type;
	}

	private static Document buildMetadata(PublishRequestData req) {
		Document metadata = null;
		if(req.getMetaName().equalsIgnoreCase(metaList.get(0))){
			metadata=mF.createArDev();//access-request-device
		}else if (req.getMetaName().equalsIgnoreCase(metaList.get(1))){
			metadata=mF.createArIp();//access-request-ip
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(2))){
			metadata=mF.createArMac();//access-request-mac
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(3))){
			metadata=mF.createAuthAs();//authenticated-as
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(4))){
			metadata=mF.createAuthBy();//authenticated-by
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(5))){
			if(req.getAttributes().containsKey("administrative-domain")){
				//exsits
				metadata = mF.createCapability(//capability
						req.getAttributes().get("name"),//req
						req.getAttributes().get("administrative-domain"));//opt
			}else{
				//not there
				mF.createCapability(req.getAttributes().get("name"));
			}
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(6))){
			metadata=mF.createDevAttr(//device-attribute
					req.getAttributes().get("name"));//req
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(7))){
			//device-characteristic
			String manufacturer,model,os,osVersion,deviceType;
			if(req.getAttributes().containsKey("manufacturer")){
				manufacturer = req.getAttributes().get("manufacturer");
			}else{
				manufacturer = null;
			}
			if(req.getAttributes().containsKey("model")){
				model = req.getAttributes().get("model");
			}else{
				model = null;
			}
			if(req.getAttributes().containsKey("os")){
				os = req.getAttributes().get("os");
			}else{
				os = null;
			}
			if(req.getAttributes().containsKey("os-version")){
				osVersion = req.getAttributes().get("os-version");
			}else{
				osVersion = null;
			}
			if(req.getAttributes().containsKey("device-type")){
				deviceType = req.getAttributes().get("device-type");
			}else{
				deviceType = null;
			}
			metadata=mF.createDevChar(
					manufacturer,//opt
					model,//opt
					os,//opt
					osVersion,//opt
					deviceType,//opt
					req.getAttributes().get("discovered-time"),//req
					req.getAttributes().get("discoverer-id"),//req
					req.getAttributes().get("discovery-method"));//req
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(8))){
			metadata=mF.createDevIp();//device-ip
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(9))){
			metadata=mF.createDiscoveredBy();//discovered-by
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(10))){
			String otherTypeDef,enfReason;
			if(req.getAttributes().containsKey("other-type-definition")){
				otherTypeDef = req.getAttributes().get("other-type-definition");
			}else{
				otherTypeDef = null;
			}
			if(req.getAttributes().containsKey("enforcement-reason")){
				enfReason = req.getAttributes().get("enforcement-reason");
			}else{
				enfReason = null;
			}
			metadata=mF.createEnforcementReport(//enforcement-report
					EnforcementAction.valueOf(
							req.getAttributes().get("enforcement-action")),//req
							otherTypeDef,//opt
							enfReason);//opt
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(11))){
			String type,otherTypeDefinition,information,vulnerabilityUri;
			if(req.getAttributes().containsKey("type")){
				type = req.getAttributes().get("type");
			}else{
				type = null;
			}
			if(req.getAttributes().containsKey("other-type-definition")){
				otherTypeDefinition = req.getAttributes().get("other-type-definition");
			}else{
				otherTypeDefinition = null;
			}
			if(req.getAttributes().containsKey("information")){
				information = req.getAttributes().get("information");
			}else{
				information = null;
			}
			if(req.getAttributes().containsKey("vulnerability-uri")){
				vulnerabilityUri = req.getAttributes().get("vulnerability-uri");
			}else{
				vulnerabilityUri = null;
			}
			metadata=mF.createEvent(//event
					req.getAttributes().get("name"),//req
					req.getAttributes().get("discovered-time"),//req
					req.getAttributes().get("discoverer-id"),//req
					Integer.valueOf(req.getAttributes().get("magnitude")),//req
					Integer.valueOf(req.getAttributes().get("confidence")),//req
					Significance.valueOf(req.getAttributes().get("significance")),//req
					EventType.valueOf(type),//opt
					otherTypeDefinition,//opt
					information,//opt
					vulnerabilityUri);//opt
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(12))){//ip-mac
			if(req.getAttributes().containsKey("start-time")&&
					req.getAttributes().containsKey("end-time")&&
					req.getAttributes().containsKey("dhcp-server")){
				//TODO AL evtl. noch weiter aufsplitten?
				metadata=mF.createIpMac(
						req.getAttributes().get("start-time"),//opt
						req.getAttributes().get("end-time"),//opt
						req.getAttributes().get("dhcp-server"));//opt
			}else{
				metadata=mF.createIpMac();
			}
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(13))){
			String vlan,vlanName,port,administrativeDomain;
			if(req.getAttributes().containsKey("vlan")){
				vlan = req.getAttributes().get("vlan");
			}else{vlan = null;}
			if(req.getAttributes().containsKey("vlan-name")){
				vlanName = req.getAttributes().get("vlan-name");
			}else{vlanName = null;}
			if(req.getAttributes().containsKey("port")){
				port = req.getAttributes().get("port");
			}else{port = null;}
			if(req.getAttributes().containsKey("administrative-domain")){
				administrativeDomain = req.getAttributes().get("administrative-domain");
			}else{administrativeDomain = null;}
			metadata=mF.createLayer2Information(//layer2-information
					Integer.valueOf(vlan),//opt
					vlanName,//opt
					Integer.valueOf(port),//opt
					administrativeDomain);//opt
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(14))){//location
			List<LocationInformation> l = new LinkedList<LocationInformation>();
			l.add(new LocationInformation(
					req.getAttributes().get("type"),
					req.getAttributes().get("value")));
			metadata=mF.createLocation(
					l,//req
					req.getAttributes().get("discovered-time"),//req
					req.getAttributes().get("discoverer-id"));//req
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(15))){
			//request-for-investigation
			metadata=mF.createRequestForInvestigation(
					req.getAttributes().get("qualifier"));//req
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(16))){
			if(req.getAttributes().containsKey("administrative-domain")){
				metadata=mF.createRole(//role
						req.getAttributes().get("administrative-domain"),//opt
						req.getAttributes().get("name"));//req
			}else{
				metadata=mF.createRole(req.getAttributes().get("name"));
			}
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(17))){
			String confidence,type;
			if(req.getAttributes().containsKey("confidence")){
				confidence = req.getAttributes().get("confidence");
			}else{confidence = null;}
			if(req.getAttributes().containsKey("type")){
				type = req.getAttributes().get("type");
			}else{type = null;}
			metadata=mF.createUnexpectedBehavior(//unexpected-behavior
					req.getAttributes().get("discovered-time"),//req
					req.getAttributes().get("discoverer-id"),//req
					//TODO MR UnexpectedBehaviour Feld Information raus nehmen, exsistiert nicht
					//req.getAttributes().get("information"), fehlt in IFMapJ //opt
					Integer.valueOf(req.getAttributes().get("magnitude")),//req
					Integer.valueOf(confidence),//opt
					Significance.valueOf(req.getAttributes().get("significance")),//req
					type);//opt
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(18))){
			List<WlanSecurityType> wlanSecurityType = new LinkedList<WlanSecurityType>();
			List<WlanSecurityType> wlanSSID = new LinkedList<WlanSecurityType>();
			wlanSecurityType.add(new WlanSecurityType(
					WlanSecurityEnum.valueOf(req.getAttributes().get("ssid-unicast-security")),
					null));
			wlanSSID.add(new WlanSecurityType(
					WlanSecurityEnum.valueOf(req.getAttributes().get("ssid-management-security")),
					null));
			metadata=mF.createWlanInformation(//wlan-information
					req.getAttributes().get("ssid"),//String ssid,
					wlanSecurityType,//optList<WlanSecurityType> ssidUnicastSecurity,
					new WlanSecurityType(
							WlanSecurityEnum.valueOf(
									req.getAttributes().get("ssid-group-security")),
									null) ,//req WlanSecurityType ssidGroupSecurity,
									wlanSSID);//List<WlanSecurityType> ssidManagementSecurity)
		}else{//Vendor-Specific!!!//
			metadata = mF.create(
					req.getMetaName(),
					req.getVendorMetaPrefix(),
					req.getVendorMetaUri(),
					req.getVendorCardinality(),
					req.getAttributes());
		}
		logger.log(Level.DEBUG, Util.getString(R.string.reqControlBuildMetadata_ret));
		return metadata;
	}

	private static List<String> getMetaList(int textArrayResId){
		String[] metaResouceList = MainActivity.getContext().getResources().getStringArray(textArrayResId);
		return Arrays.asList(metaResouceList);
	}

	private static String buildFilter(PublishRequestData req){
		//TODO MR DeleteFilter als String in der Gui abfragen
		StringBuilder sb = new StringBuilder();

		if(req.getMetaName().equalsIgnoreCase(metaList.get(0))){
			sb.append("meta:access-request-device");
		}else if (req.getMetaName().equalsIgnoreCase(metaList.get(1))){
			sb.append("meta:access-request-ip");
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(2))){
			sb.append("meta:access-request-mac");
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(3))){
			sb.append("meta:authenticated-as");
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(4))){
			sb.append("meta:authenticated-by");
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(5))){
			sb.append("meta:capability");
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(6))){
			sb.append("meta:device-attribute");
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(7))){
			sb.append("meta:device-characteristic");
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(8))){
			sb.append("meta:device-ip");
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(9))){
			sb.append("meta:discovered-by");
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(10))){
			sb.append("meta:enforcement-report");
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(11))){
			sb.append("meta:event");
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(12))){
			sb.append("meta:ip-mac");
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(13))){
			sb.append("meta:layer2-information");
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(14))){
			sb.append("meta:location");
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(15))){
			sb.append("meta:request-for-investigation");
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(16))){
			sb.append("meta:role");
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(17))){
			sb.append("meta:unexpected-behavior");
		}else if(req.getMetaName().equalsIgnoreCase(metaList.get(18))){
			sb.append("meta:wlan-security-enum");
		}else{//Vendor-Specific!!!//
			sb.append(req.getVendorMetaPrefix()+":"+req.getMetaName());
		}
		logger.log(Level.DEBUG, Util.getString(R.string.reqControlBuildFilter_ret));
		return sb.toString();
	}

	public static void startWorker(){
		sPoller = SubscriptionPoller.getInstance();

		StoredResponses mStorer = new StoredResponses(MainActivity.getContext());
		sPoller.addPollReceiver(mStorer);
		mStorer.start();

		mNotifier = new ResultNotificationManager(MainActivity.getContext());
		sPoller.addPollReceiver(mNotifier);
		mNotifier.start();

		sPoller.start();
	}
}