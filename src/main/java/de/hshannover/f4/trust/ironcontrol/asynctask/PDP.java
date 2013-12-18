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

import java.util.HashMap;

import org.w3c.dom.Document;

import de.hshannover.f4.trust.ifmapj.IfmapJ;
import de.hshannover.f4.trust.ifmapj.binding.IfmapStrings;
import de.hshannover.f4.trust.ifmapj.channel.SSRC;
import de.hshannover.f4.trust.ifmapj.exception.IfmapErrorResult;
import de.hshannover.f4.trust.ifmapj.exception.IfmapException;
import de.hshannover.f4.trust.ifmapj.exception.InitializationException;
import de.hshannover.f4.trust.ifmapj.identifier.Identifier;
import de.hshannover.f4.trust.ifmapj.identifier.Identifiers;
import de.hshannover.f4.trust.ifmapj.identifier.IdentityType;
import de.hshannover.f4.trust.ifmapj.messages.MetadataLifetime;
import de.hshannover.f4.trust.ifmapj.messages.PublishDelete;
import de.hshannover.f4.trust.ifmapj.messages.PublishRequest;
import de.hshannover.f4.trust.ifmapj.messages.PublishUpdate;
import de.hshannover.f4.trust.ifmapj.messages.Requests;
import de.hshannover.f4.trust.ifmapj.metadata.Cardinality;
import de.hshannover.f4.trust.ifmapj.metadata.StandardIfmapMetadataFactory;
import de.hshannover.inform.trust.ironcontrol.logger.Level;
import de.hshannover.inform.trust.ironcontrol.logger.Logger;
import de.hshannover.inform.trust.ironcontrol.logger.LoggerFactory;

@Deprecated
public class PDP {
	private SSRC ssrc;
	private PublishRequest updateArDev;
	private PublishRequest updateArVendorMeta;
	private PublishRequest updateArVendorMeta2;
	private PublishRequest updateAuthBy;
	private PublishRequest updateMac;
	private PublishRequest updateIp;
	private PublishRequest deletArDev;
	private PublishRequest deletArVendorMeta;
	private PublishRequest deletArVendorMeta2;
	private PublishRequest deletAuthBy;
	private PublishRequest deletMac;
	private PublishRequest deletIp;

	private static final Logger logger = LoggerFactory.getLogger(PDP.class);

	public PDP(SSRC ssrc) throws InitializationException {
		logger.log(Level.DEBUG, "NEW...");
		this.ssrc = ssrc;

		StandardIfmapMetadataFactory mF = IfmapJ.createStandardMetadataFactory();

		Identifier dev = Identifiers.createDev("endpoint1");
		Identifier mac = Identifiers.createMac("99:88:77:66:55:44");
		Identifier ip = Identifiers.createIp4("192.168.1.1");
		Identifier id = Identifiers.createIdentity(IdentityType.userName,"User_01");

		Identifier pdp = Identifiers.createDev("pdp-99");
		Identifier ar = Identifiers.createAr("dev");

		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("TEST-Device", "Dev222");
		attributes.put("TES-ID", "55");
		attributes.put("TES-Value", ""+"ka mir f�llt nicx ein");
		attributes.put("TEST-TimeStamp", "eine Zeit");
		Document metadata = mF.create("TEST-Meta", "meta", "http:\\####TESTMeta-URL-Platzhalter###", Cardinality.multiValue, attributes);
		Document metadata2 = mF.create("TEST2-Meta2", "meta", "http:\\####TESTMeta222-URL-Platzhalter###", Cardinality.multiValue, attributes);

		// erstellen
		PublishUpdate pUpArDev = Requests.createPublishUpdate(ar, dev,mF.createArDev());
		PublishUpdate pUpArVendorMeta = Requests.createPublishUpdate(ar, null,metadata);
		PublishUpdate pUpArVendorMeta2 = Requests.createPublishUpdate(ar, null,metadata2);
		PublishUpdate pUpAuthBy = Requests.createPublishUpdate(ar, pdp,mF.createAuthBy());
		PublishUpdate pUpMac = Requests.createPublishUpdate(ar, mac,mF.createArMac());
		PublishUpdate pUpIp = Requests.createPublishUpdate(ar, ip,mF.createArIp());
		PublishUpdate pUpId = Requests.createPublishUpdate(ar, id,mF.createAuthAs());

		// entfernen
		PublishDelete pdArDev = Requests.createPublishDelete(dev, ar,"meta:access-request-device");
		PublishDelete pdArVendorMeta = Requests.createPublishDelete(ar);
		PublishDelete pdArVendorMeta2 = Requests.createPublishDelete(ar);
		PublishDelete pdAuthBy = Requests.createPublishDelete(pdp, ar,"meta:authenticated-by");
		PublishDelete pdMac = Requests.createPublishDelete(mac, ar,"meta:access-request-mac");
		PublishDelete pdIp = Requests.createPublishDelete(ip, ar,"meta:access-request-ip");
		PublishDelete pdId = Requests.createPublishDelete(id, ar,"meta:authenticated-as");



		pdArDev.addNamespaceDeclaration(IfmapStrings.STD_METADATA_PREFIX,IfmapStrings.STD_METADATA_NS_URI);
		pdArVendorMeta.addNamespaceDeclaration(IfmapStrings.STD_METADATA_PREFIX,IfmapStrings.STD_METADATA_NS_URI);
		pdArVendorMeta2.addNamespaceDeclaration(IfmapStrings.STD_METADATA_PREFIX,IfmapStrings.STD_METADATA_NS_URI);
		pdAuthBy.addNamespaceDeclaration("meta",IfmapStrings.STD_METADATA_NS_URI);
		pdMac.addNamespaceDeclaration("meta", IfmapStrings.STD_METADATA_NS_URI);
		pdIp.addNamespaceDeclaration("meta", IfmapStrings.STD_METADATA_NS_URI);
		pdId.addNamespaceDeclaration("meta", IfmapStrings.STD_METADATA_NS_URI);

		pUpArDev.setLifeTime(MetadataLifetime.forever);
		pUpArVendorMeta.setLifeTime(MetadataLifetime.forever);
		pUpArVendorMeta2.setLifeTime(MetadataLifetime.forever);
		pUpAuthBy.setLifeTime(MetadataLifetime.forever);
		pUpMac.setLifeTime(MetadataLifetime.forever);
		pUpIp.setLifeTime(MetadataLifetime.forever);
		pUpId.setLifeTime(MetadataLifetime.forever);

		updateArDev = Requests.createPublishReq(pUpArDev);
		updateArVendorMeta = Requests.createPublishReq(pUpArVendorMeta);
		updateArVendorMeta2 = Requests.createPublishReq(pUpArVendorMeta2);
		updateAuthBy = Requests.createPublishReq(pUpAuthBy);
		updateMac = Requests.createPublishReq(pUpMac);
		updateIp = Requests.createPublishReq(pUpIp);

		deletArDev = Requests.createPublishReq(pdArDev);
		deletArVendorMeta = Requests.createPublishReq(pdArVendorMeta);
		deletArVendorMeta2 = Requests.createPublishReq(pdArVendorMeta2);
		deletAuthBy = Requests.createPublishReq(pdAuthBy);
		deletMac = Requests.createPublishReq(pdMac);
		deletIp = Requests.createPublishReq(pdIp);
		logger.log(Level.DEBUG, "...NEW");
	}

	public void delete() throws IfmapErrorResult, IfmapException {
		logger.log(Level.DEBUG, "delete()...");
		ssrc.publish(deletArDev);
		ssrc.publish(deletArVendorMeta);
		ssrc.publish(deletArVendorMeta2);
		ssrc.publish(deletAuthBy);
		ssrc.publish(deletMac);
		ssrc.publish(deletIp);
		logger.log(Level.DEBUG, "...delete()");
	}

	public void update() throws IfmapErrorResult, IfmapException {
		logger.log(Level.DEBUG, "update()...");
		ssrc.publish(updateArDev);
		ssrc.publish(updateArVendorMeta);
		ssrc.publish(updateArVendorMeta2);
		ssrc.publish(updateAuthBy);
		ssrc.publish(updateMac);
		ssrc.publish(updateIp);
		logger.log(Level.DEBUG, "...update()");
	}
}
