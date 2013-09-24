package de.hshannover.inform.trust.ironcontrol.logic.data;

import de.fhhannover.inform.trust.ifmapj.messages.PollResult;

public interface PollReceiver {
	public abstract void submitNewPollResult(PollResult pr);
}
