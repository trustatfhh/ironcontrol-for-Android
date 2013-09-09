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
	 * @param clientID the id of the client.
	 * @param name the name of the logger.
	 * @param time the time since the first logging has done (in milliseconds).
	 * @param level the logging level.
	 * @param message the message to log.
	 * @param t the exception to log.
	 */
	void log(String name, Level level, Object message, Throwable t);

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