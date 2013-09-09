package de.hshannover.inform.trust.ifmapj.ironcontrol.view.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.widget.Toast;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapErrorResult;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapException;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.Connection;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Level;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Logger;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LoggerFactory;

public class ConnectionTask extends AsyncTask<Long, Void, Boolean> {

	public static final String MASSAGE_CONNECTING = "Connecting...";

	private static final Logger logger = LoggerFactory.getLogger(ConnectionTask.class);

	private Context context;
	private Resources r;

	private ProgressDialog pd;

	private ConnectTaskEnum type;

	public enum ConnectTaskEnum {
		CONNECT,
		DISCONNECT;
	}


	public ConnectionTask(Activity context, ConnectTaskEnum type){
		this.context = context;
		this.type = type;
		this.r = context.getResources();

		pd = new ProgressDialog(context);
		switch(type){
		case CONNECT: pd.setMessage(r.getString(R.string.connecting));
		break;
		case DISCONNECT: pd.setMessage(r.getString(R.string.disconnecting));
		break;
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pd.show();
	}

	@Override
	protected Boolean doInBackground(Long... params) {
		logger.log(Level.DEBUG, r.getString(R.string.runConnectionTask));
		try {

			switch(type){
			case CONNECT: Connection.connect(params[0]);
			break;
			case DISCONNECT: Connection.disconnect();
			break;
			}

		} catch (IfmapErrorResult e) {
			return false;
		} catch (IfmapException e) {
			return false;
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		pd.dismiss();

		if(result){
			switch(type){
			case CONNECT: Toast.makeText(context, r.getString(R.string.newConnection), Toast.LENGTH_SHORT).show();
			break;
			case DISCONNECT: Toast.makeText(context, r.getString(R.string.disconnected), Toast.LENGTH_SHORT).show();
			break;
			}

		} else {
			switch(type){
			case CONNECT: Toast.makeText(context, r.getString(R.string.connectingFail), Toast.LENGTH_SHORT).show();
			break;
			case DISCONNECT: Toast.makeText(context, r.getString(R.string.disconnectedFail), Toast.LENGTH_SHORT).show();
			break;
			}
		}
	}

}
