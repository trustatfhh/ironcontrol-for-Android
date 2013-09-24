package de.hshannover.inform.trust.ironcontrol.exceptions;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import de.hshannover.inform.trust.ironcontrol.logger.Level;
import de.hshannover.inform.trust.ironcontrol.logger.Logger;
import de.hshannover.inform.trust.ironcontrol.logger.LoggerFactory;
import de.hshannover.inform.trust.ironcontrol.view.MainActivity;

/**
 * Log the error
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class IronControlUncaughtExceptionHandler implements UncaughtExceptionHandler{

	private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);

	private Context context;

	private UncaughtExceptionHandler originalHandler;

	public IronControlUncaughtExceptionHandler(Context context, UncaughtExceptionHandler originalHandler){
		this.originalHandler = originalHandler;
		this.context = context;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		logger.log(Level.FATAL, ex.getMessage(), ex);

		// lets android :-)
		originalHandler.uncaughtException(thread, ex);
	}

}
