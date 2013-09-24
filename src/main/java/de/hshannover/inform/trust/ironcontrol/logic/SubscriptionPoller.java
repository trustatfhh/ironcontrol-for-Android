package de.hshannover.inform.trust.ironcontrol.logic;

import java.util.ArrayList;

import de.fhhannover.inform.trust.ifmapj.channel.ARC;
import de.fhhannover.inform.trust.ifmapj.exception.EndSessionException;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapErrorResult;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapException;
import de.fhhannover.inform.trust.ifmapj.exception.InitializationException;
import de.fhhannover.inform.trust.ifmapj.messages.PollResult;
import de.hshannover.inform.trust.ironcontrol.logger.Level;
import de.hshannover.inform.trust.ironcontrol.logger.Logger;
import de.hshannover.inform.trust.ironcontrol.logger.LoggerFactory;
import de.hshannover.inform.trust.ironcontrol.logic.data.PollReceiver;
import de.hshannover.inform.trust.ironcontrol.logic.data.PollSender;

/**
 * Class for connection management
 * @author Daniel Wolf
 * @author Anton Saenko
 * @author Arne Loth
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class SubscriptionPoller extends Thread implements PollSender {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionPoller.class);

	private ArrayList<PollReceiver> mPollReceiver;

	private PollResult mPollResult;

	private static SubscriptionPoller mInstance;


	private SubscriptionPoller() {
		logger.log(Level.DEBUG, "New SubscriptionPoller()");
		mPollReceiver = new ArrayList<PollReceiver>();
	}

	public static synchronized SubscriptionPoller getInstance(){
		if(mInstance == null){
			mInstance = new SubscriptionPoller();
		}
		return mInstance;
	}

	/**
	 * Is a Thread method create a pollresult
	 * 
	 * @since 0.1
	 */
	@Override
	public void run() {
		setName(SubscriptionPoller.class.getSimpleName());
		logger.log(Level.DEBUG, "run()...");

		while (!interrupted()) {
			ARC mArc = getARC();
			if(mArc != null){
				try {
					logger.log(Level.DEBUG, "new poll...");
					mPollResult = mArc.poll();
					logger.log(Level.DEBUG, "...poll OK");

					// submit the result
					onNewPollResult(mPollResult);

					// forget the result
					mPollResult = null;

				} catch (IfmapErrorResult e) {
					logger.log(Level.ERROR, "IfmapErrorResult: STOP Poll" + e.getErrorString(), e);
					waitForNewConnection();

				} catch (EndSessionException e) {
					logger.log(Level.ERROR, "EndSessionException: STOP Poll", e);
					waitForNewConnection();

				} catch (IfmapException e) {
					logger.log(Level.ERROR, "IfmapException: STOP Poll " + e.getDescription(), e);
					waitForNewConnection();
				}
			}else{
				waitForNewConnection();
			}
		}
		logger.log(Level.DEBUG, "...run()");
	}

	@Override
	public void addPollReceiver(PollReceiver pr) {
		if(pr != null){
			logger.log(Level.DEBUG, "add new PollReceiver()");
			mPollReceiver.add(pr);
		}else{
			logger.log(Level.WARN, "PollReceiver is null, dosen't add");
		}
	}

	public void onNewPollResult(PollResult pr) {
		logger.log(Level.DEBUG, "onNewPollResult()...");
		for(PollReceiver receiver: mPollReceiver){
			receiver.submitNewPollResult(pr);
		}
		logger.log(Level.DEBUG, "...onNewPollResult()");
	}

	private void waitForNewConnection() {
		logger.log(Level.DEBUG, "waitForNewConnection()...");
		try {

			synchronized (this){
				wait();
			}

		} catch (InterruptedException e) {
			logger.log(Level.DEBUG, e.getMessage(), e);
		}
		logger.log(Level.DEBUG, "...waitForNewConnection()");
	}

	private ARC getARC() {
		try {
			return Connection.getARC();
		} catch (InitializationException e) {
			logger.log(Level.ERROR, e.getDescription(),e);
		}
		return null;
	}
}
