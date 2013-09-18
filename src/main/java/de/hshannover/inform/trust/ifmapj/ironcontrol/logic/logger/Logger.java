package de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger;

import java.util.ArrayList;
import java.util.List;

import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.appander.Appender;

public class Logger {

	private static final Logger logger = LoggerFactory.getLogger(Logger.class);

	private static List<Appender> appenderList = new ArrayList<Appender>();

	private String className;

	private static boolean firstLog = true;

	public Logger(String className){
		this.className = className;
	}

	public void log(Level level, Object message) throws IllegalArgumentException {
		log(level, message, null);
	}

	public void log(Level level, Object message, Throwable t) throws IllegalArgumentException {
		if (level == null) {
			throw new IllegalArgumentException("The level must not be null.");
		}
		long time = System.currentTimeMillis();

		if (firstLog) {

			for (Appender appender : appenderList) {
				appender.log(className, time, Level.DEBUG, "===========================", null);
				appender.log(className, time, Level.DEBUG, "=====START=IRONCONTROL=====", null);
				appender.log(className, time, Level.DEBUG, "===========================", null);
			}

			firstLog = false;
		}

		for (Appender appender : appenderList) {
			appender.log(className, time, level, message, t);
		}
	}

	public static void addAppender(Appender appender) throws IllegalArgumentException {
		if (appender == null) {
			throw new IllegalArgumentException("Appender not allowed to be null");
		}

		boolean contains = false;

		for(Appender a: appenderList){
			if(a.getClass() == appender.getClass()){
				contains = true;
				break;
			}
		}

		if(!contains){
			appenderList.add(appender);
		}else{
			logger.log(Level.WARN, "Only one " + appender.getClass().toString() + " is allowed!");
		}
	}

	public String getClassName() {
		return className;
	}

	public static Appender getAppender(Class<?> clazz) {
		for(Appender a: appenderList){
			if(a.getClass() == clazz){
				return a;
			}
		}
		return null;
	}

	public static void removeAppender(Class<?> clazz) {
		appenderList.remove(getAppender(clazz));
	}
}
