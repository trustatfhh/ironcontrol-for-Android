package de.hshannover.inform.trust.ifmapj.ironcontrol.logic.data;

/**
 * Class for connection management
 * @author Daniel Wolf
 * @author Anton Saenko
 * @author Arne Loth
 * @since 0.5
 */

import java.util.HashMap;

import de.fhhannover.inform.trust.ifmapj.messages.MetadataLifetime;
import de.fhhannover.inform.trust.ifmapj.metadata.Cardinality;
import de.fhhannover.inform.trust.ifmapj.metadata.WlanSecurityType;

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
