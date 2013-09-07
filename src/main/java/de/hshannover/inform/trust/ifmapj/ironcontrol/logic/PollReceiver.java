package de.hshannover.inform.trust.ifmapj.ironcontrol.logic;

import de.fhhannover.inform.trust.ifmapj.messages.PollResult;

public interface PollReceiver {
	public abstract void submitNewPollResult(PollResult pr);
}
