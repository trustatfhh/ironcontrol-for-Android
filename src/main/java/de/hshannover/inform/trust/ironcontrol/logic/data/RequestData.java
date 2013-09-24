package de.hshannover.inform.trust.ironcontrol.logic.data;

/**
 * Request data class
 * 
 * @author Daniel Wolf
 * @author Anton Saenko
 * @author Arne Loth
 * @since 0.5
 */

public class RequestData {

	private String identifier1, identifier1Value, name;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getIdentifier1() {
		return identifier1;
	}

	public void setIdentifier1(String identifier1) {
		this.identifier1 = identifier1;
	}

	public String getIdentifierValue() {
		return identifier1Value;
	}

	public void setIdentifier1Value(String identifier1Value) {
		this.identifier1Value = identifier1Value;
	}
}
