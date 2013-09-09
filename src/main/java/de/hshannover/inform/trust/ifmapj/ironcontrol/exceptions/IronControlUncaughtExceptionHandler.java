package de.hshannover.inform.trust.ifmapj.ironcontrol.exceptions;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Level;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Logger;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LoggerFactory;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.MainActivity;

public class IronControlUncaughtExceptionHandler implements UncaughtExceptionHandler{

	/**
	 * Class for connection management
	 * @author Marcel Reichenbach
	 * @version %I%, %G%
	 * @since 0.1
	 */

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
