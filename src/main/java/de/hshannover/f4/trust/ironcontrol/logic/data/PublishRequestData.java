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
package de.hshannover.f4.trust.ironcontrol.logic.data;

/**
 * Class for connection management
 * @author Daniel Wolf
 * @author Anton Saenko
 * @author Arne Loth
 * @since 0.5
 */

import java.util.HashMap;

import de.hshannover.f4.trust.ifmapj.messages.MetadataLifetime;
import de.hshannover.f4.trust.ifmapj.metadata.Cardinality;
import de.hshannover.f4.trust.ifmapj.metadata.WlanSecurityType;

public class PublishRequestData extends RequestData{

	private HashMap<String,String> attributes;
	private MetadataLifetime lifeTime;
	private Operation operation;
	//Vendor-Specific
	private String metaName, vendorMetaPrefix, vendorMetaUri, identifier2, identifier2Value;
	private Cardinality vendorCardinality;

	private WlanSecurityType secType;

	public HashMap<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(HashMap<String, String> attributes) {
		this.attributes = attributes;
	}

	public MetadataLifetime getLifeTime() {
		return lifeTime;
	}

	public void setLifeTime(MetadataLifetime lifeTime) {
		this.lifeTime = lifeTime;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public String getMetaName() {
		return metaName;
	}

	public void setMetaName(String metaName) {
		this.metaName = metaName;
	}

	public String getVendorMetaPrefix() {
		return vendorMetaPrefix;
	}

	public void setVendorMetaPrefix(String vendorMetaPrefix) {
		this.vendorMetaPrefix = vendorMetaPrefix;
	}

	public String getVendorMetaUri() {
		return vendorMetaUri;
	}

	public void setVendorMetaUri(String vendorMetaUri) {
		this.vendorMetaUri = vendorMetaUri;
	}

	public Cardinality getVendorCardinality() {
		return vendorCardinality;
	}

	public void setVendorCardinality(Cardinality vendorCardinality) {
		this.vendorCardinality = vendorCardinality;
	}

	public String getIdentifier2Value() {
		return identifier2Value;
	}

	public void setIdentifier2Value(String identifier2Value) {
		this.identifier2Value = identifier2Value;
	}

	public String getIdentifier2() {
		return identifier2;
	}

	public void setIdentifier2(String identifier2Value) {
		this.identifier2 = identifier2Value;
	}
}
