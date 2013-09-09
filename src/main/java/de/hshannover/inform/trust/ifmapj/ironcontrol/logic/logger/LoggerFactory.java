package de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Used this factory to get the logger.
 * 
 * @author Marcel Reichenbach
 * 
 */
public class LoggerFactory {

	private final static List<Logger> loggerList = new ArrayList<Logger>();

	/**
	 * Get a <code>Logger</code> object with the specified name.
	 * 
	 * @param className the class name of the logger.
	 * @return the <code>Logger</code> object.
	 */
	public static Logger getLogger(Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("clazz must not be null.");
		}

		String className = clazz.getName();

		for(Logger l: loggerList){
			if(l.getClassName().equals(className)){
				return l;
			}
		}

		Logger newLogger = new Logger(className);
		loggerList.add(newLogger);

		return newLogger;
	}

}
