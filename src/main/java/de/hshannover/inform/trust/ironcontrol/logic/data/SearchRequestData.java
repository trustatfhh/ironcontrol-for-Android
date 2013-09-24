package de.hshannover.inform.trust.ironcontrol.logic.data;

/**
 * Search-Request data class
 * 
 * @author Daniel Wolf
 * @author Anton Saenko
 * @author Arne Loth
 * @since 0.5
 */

public class SearchRequestData extends RequestData{

	private String matchLinks;
	private String resultFilter;
	private int maxDepth;
	private int maxSize;
	private String terminalIdentifierTypes;
	private String mNameSpacePrefix;
	private String mNameSpaceURI;
	private boolean advancedSearchRequested;

	public boolean isAdvancedSearchRequested() {
		return advancedSearchRequested;
	}

	public void setAdvancedSearchRequested(boolean advancedSearchRequested) {
		this.advancedSearchRequested = advancedSearchRequested;
	}

	public String getMatchLinks() {
		return matchLinks;
	}

	public void setMatchLinks(String matchLinks) {
		this.matchLinks = matchLinks;
	}

	public String getResultFilter() {
		return resultFilter;
	}

	public void setResultFilter(String resultFilter) {
		this.resultFilter = resultFilter;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public String getTerminalIdentifierTypes() {
		return terminalIdentifierTypes;
	}

	public void setTerminalIdentifierTypes(String terminalIdentifierTypes) {
		this.terminalIdentifierTypes = terminalIdentifierTypes;
	}

	public String getNameSpacePrefix() {
		return mNameSpacePrefix;
	}

	public void setNameSpacePrefix(String mNameSpacePrefix) {
		this.mNameSpacePrefix = mNameSpacePrefix;
	}

	public String getNameSpaceURI() {
		return mNameSpaceURI;
	}

	public void setNameSpaceURI(String mNameSpaceURI) {
		this.mNameSpaceURI = mNameSpaceURI;
	}
}
