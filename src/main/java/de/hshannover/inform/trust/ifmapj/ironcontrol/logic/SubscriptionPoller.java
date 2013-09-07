/**
 * Class for connection management
 * @author Daniel Wolf
 * @author Anton Saenko
 * @author Arne Loth
 * @version %I%, %G%
 * @since 0.1
 */

package de.hshannover.inform.trust.ifmapj.ironcontrol.logic;


import java.util.ArrayList;

import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

import de.fhhannover.inform.trust.ifmapj.channel.ARC;
import de.fhhannover.inform.trust.ifmapj.exception.EndSessionException;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapErrorResult;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapException;
import de.fhhannover.inform.trust.ifmapj.exception.InitializationException;
import de.fhhannover.inform.trust.ifmapj.messages.PollResult;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.util.Util;

public class SubscriptionPoller extends Thread implements PollSender {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionPoller.class);

	private ArrayList<PollReceiver> mPollReceiver;
	private PollResult mPollResult;
	private ARC mArc;
	private static SubscriptionPoller mInstance;
	private static boolean wait;


	private SubscriptionPoller() {
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
		logger.debug(Util.getString(R.string.enter) + "run()...");
		Thread.currentThread().setName(SubscriptionPoller.class.getSimpleName());

		while (!Thread.currentThread().isInterrupted()) {

			while (mArc == null){

				getARC();

				try {
					Thread.sleep(180);
				} catch (InterruptedException e) {

				}
			}

			logger.debug("new poll...");

			try {
				mPollResult = mArc.poll();
			} catch (IfmapErrorResult e) {
				logger.error(e.getErrorString(), e);
				break;
			} catch (EndSessionException e) {
				logger.debug("EndSessionException STOP Poll, wait for new subscribe...", e);
				mPollResult = null;

				waitForNewSubscribesAfterConnectionLost();

			} catch (IfmapException e) {
				logger.error(e.getDescription(), e);
				break;
			}

			if (mPollResult != null) {
				logger.debug("...poll OK");
				onNewPollResult(mPollResult);
				mPollResult = null;
			} else {
				logger.debug(Util.getString(R.string.pollresult_is_null));
			}
		}
		logger.debug(Util.getString(R.string.exit) + "...run()");
	}

	@Override
	public void addPollReceiver(PollReceiver pr) {
		logger.debug(Util.getString(R.string.enter) + "addPollReceiver()...");
		mPollReceiver.add(pr);
		logger.debug(Util.getString(R.string.exit) + "...addPollReceiver()");
	}

	public void onNewPollResult(PollResult pr) {
		logger.debug(Util.getString(R.string.enter) + "onNewPollResult()...");
		for(PollReceiver receiver: mPollReceiver){
			receiver.submitNewPollResult(pr);
		}
		logger.debug(Util.getString(R.string.exit) + "...onNewPollResult()");
	}

	public boolean isWaiting() {
		return wait;
	}

	private void waitForNewSubscribesAfterConnectionLost() {
		try {
			wait = true;

			synchronized (Thread.currentThread()){
				Thread.currentThread().wait();
			}

			wait = false;

			getARC();

			logger.debug("... new subscribe");
		} catch (InterruptedException e) {
			logger.debug(e.getMessage(), e);
		}
	}

	private void getARC() {
		try {

			this.mArc = Connection.getARC();

		} catch (InitializationException e) {
			logger.error(e.getDescription(),e);
		}
	}
}
