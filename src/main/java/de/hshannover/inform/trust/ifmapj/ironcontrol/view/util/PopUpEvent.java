package de.hshannover.inform.trust.ifmapj.ironcontrol.view.util;

public interface PopUpEvent {

	public void onClickePopUp();
	public boolean onClickeSavePopUp(String savedName);
	public void onClickeSubscriptionPopUp(String subscribeName, String startIdentifier, String identifierValue);

}
