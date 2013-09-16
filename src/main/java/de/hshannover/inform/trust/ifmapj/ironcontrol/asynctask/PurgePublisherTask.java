package de.hshannover.inform.trust.ifmapj.ironcontrol.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapErrorResult;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapException;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.RequestsController;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Level;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Logger;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LoggerFactory;

/**
 * AsyncTask to remove all publishes in background and inform the user.
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class PurgePublisherTask extends AsyncTask<Void, Void, Void> {

	private static final Logger logger = LoggerFactory.getLogger(PurgePublisherTask.class);

	public static final String MASSAGEUPDATE = "PurgePublisher...";

	private ProgressDialog pd;
	private Context context;

	private String publisherId;

	private String error;

	public PurgePublisherTask(Context context, String publisherId){
		init(context);

		this.publisherId = publisherId;

		logger.log(Level.DEBUG, "...NEW");
	}

	private void init(Context context){
		this.context = context;
		pd= new ProgressDialog(context);
	}

	@Override
	protected void onPreExecute() {
		logger.log(Level.DEBUG, "onPreExecute()...");
		super.onPreExecute();
		pd.show();
		logger.log(Level.DEBUG, "...onPreExecute()");
	}

	@Override
	protected Void doInBackground(Void... params) {
		Thread.currentThread().setName(PurgePublisherTask.class.getSimpleName());
		logger.log(Level.DEBUG, "doInBackground()...");

		try {

			RequestsController.purgePublisher(publisherId);

		} catch (IfmapErrorResult e) {
			error = e.getErrorCode().toString();
		} catch (IfmapException e) {
			error = e.getDescription();
		} catch (Exception e) {
			error = e.getMessage();
		}

		logger.log(Level.DEBUG, "...doInBackground()");
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		logger.log(Level.DEBUG, "onPostExecute()...");

		pd.dismiss();

		if(error == null){
			Toast.makeText(context, R.string.publishReceived, Toast.LENGTH_SHORT).show();
			logger.log(Level.INFO, context.getResources().getString(R.string.publishReceived));
		}else {
			Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
		}

		logger.log(Level.DEBUG, "...onPostExecute()");
	}
}
