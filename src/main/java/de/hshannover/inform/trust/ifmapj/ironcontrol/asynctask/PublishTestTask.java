package de.hshannover.inform.trust.ifmapj.ironcontrol.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapErrorResult;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapException;
import de.fhhannover.inform.trust.ifmapj.exception.InitializationException;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.Connection;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.data.Operation;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Level;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Logger;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LoggerFactory;
@Deprecated
public class PublishTestTask extends AsyncTask<Void, Void, Void> {

	private static final Logger logger = LoggerFactory.getLogger(PublishTestTask.class);
	private Operation publishEnum;

	private Context context;

	private InitializationException myIe;
	private IfmapErrorResult myEr;
	private IfmapException myE;

	public PublishTestTask(Context context, Operation publishEnum) {
		this.context = context;
		logger.log(Level.DEBUG, "New...");
		this.publishEnum = publishEnum;
		logger.log(Level.DEBUG, "...New");
	}

	@Override
	protected Void doInBackground(Void... params) {
		Thread.currentThread().setName(PublishTestTask.class.getSimpleName());
		logger.log(Level.DEBUG, "doInBackground()...");
		try {
			PDP testPDP = new PDP(Connection.getSSRC());
			switch(publishEnum){
			case UPDATE: testPDP.update();
			break;
			case DELETE: testPDP.delete();
			break;
			case NOTIFY:
				break;
			}
		} catch (InitializationException e) {
			logger.log(Level.ERROR, e.getMessage(), e);
			myIe = e;
		} catch (IfmapErrorResult e) {
			logger.log(Level.ERROR, e.getErrorString(), e);
			myEr = e;
		} catch (IfmapException e) {
			logger.log(Level.ERROR, e.getMessage(), e);
			myE = e;
		}
		logger.log(Level.DEBUG, "...doInBackground()");
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		logger.log(Level.DEBUG, "onPostExecute()...");

		if(myEr == null && myIe == null && myE == null){
			Toast.makeText(context, R.string.publishReceived, Toast.LENGTH_SHORT).show();
		}else if(myEr != null){
			Toast.makeText(context, myEr.getErrorCode().toString(), Toast.LENGTH_LONG).show();
		}else if(myIe != null){
			Toast.makeText(context, myIe.getDescription(), Toast.LENGTH_LONG).show();
		}else if(myE != null){
			Toast.makeText(context, myE.getDescription(), Toast.LENGTH_LONG).show();
		}

		logger.log(Level.DEBUG, "...onPostExecute()");
	}

}
