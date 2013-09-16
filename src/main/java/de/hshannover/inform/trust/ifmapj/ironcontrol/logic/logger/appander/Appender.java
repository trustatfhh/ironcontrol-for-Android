package de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.appander;

import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Level;

/**
 * Appender interface, all appenders must be implement this.
 * 
 * @author Marcel Reichenbach
 */
public interface Appender {

	/**
	 * Do the logging.
	 * 
	 * @param name		The name of the logger.
	 * @param time		The current system time.
	 * @param level		The logging level.
	 * @param message	The message to log.
	 * @param throwable The exception to log.
	 */
	void log(String name, long time, Level level, Object message, Throwable throwable);

	/**
	 * Clear the log.
	 */
	void clear();

	/**
	 * Get the size of the log.
	 * 
	 * @return the size of the log.
	 */
	long getLogSize();

}