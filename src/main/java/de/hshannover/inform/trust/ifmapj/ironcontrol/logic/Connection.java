
package de.hshannover.inform.trust.ifmapj.ironcontrol.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.net.ssl.TrustManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Environment;
import de.fhhannover.inform.trust.ifmapj.IfmapJ;
import de.fhhannover.inform.trust.ifmapj.IfmapJHelper;
import de.fhhannover.inform.trust.ifmapj.channel.ARC;
import de.fhhannover.inform.trust.ifmapj.channel.SSRC;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapErrorResult;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapException;
import de.fhhannover.inform.trust.ifmapj.exception.InitializationException;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.DBContentProvider;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.Connections;
import de.hshannover.inform.trust.ifmapj.ironcontrol.database.entities.Requests;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Level;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Logger;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LoggerFactory;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.MainActivity;

/**
 * Class for connection management
 * @author Anton Saenko
 * @author Arne Loth
 * @version 0.9
 * @since 0.1
 */
public class Connection{
	private static final Logger logger = LoggerFactory.getLogger(Connection.class);
	private static SSRC mSsrc;
	private static ARC mArc;

	/**
	 * Build a custom SSRC connection to a Server.
	 * 
	 * @param settings	Specified settings in a ConnectionSettings object.
	 * @throws IfmapException
	 * @throws IfmapErrorResult
	 * @throws FileNotFoundException
	 * @since 0.1
	 */
	public static SSRC getSSRC() throws IfmapErrorResult, IfmapException{
		if(mSsrc == null){
			connect();
		}
		return mSsrc;
	}

	public static String getPublisherId(){
		if(mSsrc != null){
			return mSsrc.getPublisherId();
		}else {
			return null;
		}
	}

	/**
	 * Build a custom ARC connection to a Server.
	 * 
	 * @param settings	Specified settings in a ConnectionSettings object.
	 * @throws InitializationException
	 * @since 0.1
	 */
	public static ARC getARC() throws InitializationException{
		if(mSsrc != null && mArc == null){
			try {
				mArc = mSsrc.getArc();
			} catch (InitializationException e) {
				logger.log(Level.ERROR, e.getMessage(), e);
				throw new InitializationException(e.getMessage());
			}
		}
		return mArc;
	}

	/**
	 * Closes a connection to a Server.
	 * 
	 * @param settings	Specified settings, such as Username, IP, Password for the Server to connect to
	 * @throws IfmapException
	 * @throws IfmapErrorResult
	 * @throws InitializationException
	 */
	public static void disconnect() throws IfmapException, IfmapErrorResult{
		logger.log(Level.DEBUG, "endSession()...");
		Context context = MainActivity.getContext();
		if(mSsrc != null){
			mSsrc.endSession();
			logger.log(Level.DEBUG, "closeTcpConnection()...");
			mSsrc.closeTcpConnection();
			logger.log(Level.INFO, "Disconnected");
			mSsrc = null;
			mArc = null;

			// reset connections
			ContentValues value = new ContentValues();
			value.put(Connections.COLUMN_ACTIVE, 0);
			context.getContentResolver().update(DBContentProvider.CONNECTIONS_URI, value, null, null);

			// reset subscriptions
			ContentValues value2 = new ContentValues();
			value2.put(Requests.COLUMN_ACTIVE, 0);
			context.getContentResolver().update(DBContentProvider.SUBSCRIPTION_URI, value, null, null);

		}else {
			logger.log(Level.ERROR, "No connection, mSsrc is null!");
			throw new IfmapException("mSsrc is null!!", "No connection!");
		}

	}

	/**
	 * Renew session after close tcp (not used)
	 * 
	 */
	public static void renewSession(){
		logger.log(Level.DEBUG, "enter the renewSession()");
		new Thread(){
			@Override
			public void run(){
				Thread.currentThread().setName("renewSession thread");
				while (!Thread.currentThread().isInterrupted()){
					try {
						Thread.sleep(1500);
						try {
							mSsrc.renewSession();
						} catch (IfmapException e) {
							logger.log(Level.ERROR, e.getMessage(), e);
							//throw new IfmapException(e.getDescription(), e);
						} catch (IfmapErrorResult e) {
							logger.log(Level.ERROR, e.getMessage(), e);
							//throw new IfmapErrorResult(e.getErrorCode(),e.getMessage());
						}
					} catch (InterruptedException e2) {
						logger.log(Level.ERROR, "renewSession thread", e2);
					}
				}
			}
		}.start();
		logger.log(Level.DEBUG, "exit the renewSession()");
	}

	private static long getDefaultConnectionId() {
		Context context = MainActivity.getContext();

		String selection = Connections.COLUMN_DEFAULT + " = 1";
		Cursor cConn = context.getContentResolver().query(DBContentProvider.CONNECTIONS_URI, null, selection, null, null);

		long defaultId = -1;

		if(cConn.moveToFirst()){

			defaultId = cConn.getLong(cConn.getColumnIndexOrThrow(Connections.COLUMN_ID));

		} else {

			throw new CursorIndexOutOfBoundsException("no default connection");

		}

		cConn.close();
		return defaultId;
	}

	public static void connect() throws IfmapErrorResult, IfmapException{
		long connId = getDefaultConnectionId();

		connect(connId);
	}

	public static void connect(long id) throws IfmapErrorResult, IfmapException{
		if(mSsrc == null){

			initSsrc(id);
			initSession(id);

		}else {
			throw new InitializationException("mSsrc is not null, A connection exists!");
		}
	}

	private static void initSsrc(long id) throws InitializationException {
		logger.log(Level.DEBUG, "init SSRC...");

		// get connection data
		Context context = MainActivity.getContext();
		Cursor conn = context.getContentResolver().query(Uri.parse(DBContentProvider.CONNECTIONS_URI + "/" +id), null, null, null, null);

		String address = "";
		String port = "";
		String user = "";
		String pass = "";

		if(conn.moveToFirst()){

			address = conn.getString(conn.getColumnIndexOrThrow(Connections.COLUMN_ADDRESS));
			port = conn.getString(conn.getColumnIndexOrThrow(Connections.COLUMN_PORT));
			user = conn.getString(conn.getColumnIndexOrThrow(Connections.COLUMN_USER));
			pass = conn.getString(conn.getColumnIndexOrThrow(Connections.COLUMN_PASS));

		}else {
			throw new CursorIndexOutOfBoundsException("Error with connection id: " + id);
		}

		conn.close();

		// connect
		InputStream isTrustManager = null;

		try {
			isTrustManager = getKeystoreAsInpustream();
		} catch (FileNotFoundException e1) {
			logger.log(Level.ERROR, e1.getMessage(), e1);
		} catch (IOException e2) {
			logger.log(Level.ERROR, e2.getMessage(), e2);
		}
		TrustManager[] tms = null;
		tms = IfmapJHelper.getTrustManagers(isTrustManager, "ironcontrol");
		try {

			logger.log(Level.INFO, "Creating SSRC using basic authentication to http://"+address+":"+port);
			mSsrc = IfmapJ.createSSRC("http://"+address+":"+port, user, pass, tms);
		} catch (InitializationException e) {
			logger.log(Level.ERROR, "Could not initialize ifmapj: " + e.getMessage() + ", " + e.getCause(), e);
			mSsrc = null;
			throw new InitializationException(e);
		}
	}

	private static void initSession(long id) throws IfmapErrorResult, IfmapException {
		Context context = MainActivity.getContext();
		try {
			logger.log(Level.INFO, "New session...");
			mSsrc.newSession();		// TODO ifmapMaxResultSize noch setzen ?
			logger.log(Level.INFO, "...session established");
			logger.log(Level.DEBUG, "Session ID: " + mSsrc.getSessionId() + " - Publisher ID : " + mSsrc.getPublisherId()+ ")");

			// set connection active
			ContentValues value = new ContentValues();
			value.put(Connections.COLUMN_ACTIVE, 1);
			context.getContentResolver().update(Uri.parse(DBContentProvider.CONNECTIONS_URI + "/" +id), value, null, null);

		} catch (IfmapErrorResult e) {
			logger.log(Level.ERROR, "Got IfmapErrorResult: " + e.getMessage() + ", " + e.getCause(), e);
			mSsrc = null;
			throw new IfmapErrorResult(e.getErrorCode(),e.getErrorString());

		} catch (IfmapException e) {
			logger.log(Level.ERROR, "Got IfmapException: " + e.getMessage() + ", " + e.getCause(), e);
			mSsrc = null;
			throw new IfmapException(e.getDescription(), e);
		}
	}


	/**
	 * Get the keystore.
	 * 
	 * @returnInputstream
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static InputStream getKeystoreAsInpustream() throws FileNotFoundException, IOException{
		InputStream isTrustManager = null;
		if (!KeystoreManager.isSDMounted()){
			isTrustManager = KeystoreManager.getKeystoreFromRaw();
			logger.log(Level.WARN, Environment.getExternalStorageDirectory().toString() +"State: "+
					Environment.getExternalStorageState()+" -> loaded internal keystore (connect just to irond possible)!" );
		}else{
			try {
				isTrustManager = new FileInputStream(new File(KeystoreManager.getPATH_TO_KEYSTORE()));
				logger.log(Level.INFO, "Load the keystore from SD card!");
			} catch (FileNotFoundException e) {
				logger.log(Level.ERROR, e.getMessage(), e);
				throw new FileNotFoundException(e.getMessage());
			}
		}
		return isTrustManager;
	}
}
