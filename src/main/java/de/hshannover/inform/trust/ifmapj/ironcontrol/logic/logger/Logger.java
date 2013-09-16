package de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.appander.Appender;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.appander.FileAppender;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.appander.ListAppender;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.appander.LogCatAppender;

public class Logger {

	private static final Logger logger = LoggerFactory.getLogger(Logger.class);

	private static List<Appender> appenderList;

	private String className;

	private static boolean firstLog = true;

	public Logger(String className){
		this.className = className;

		if(appenderList == null){
			appenderList = new ArrayList<Appender>();
			addAppender(new ListAppender());
			addAppender(new LogCatAppender());

			try {
				addAppender(new FileAppender());
			} catch (IOException e) {
				logger.log(Level.ERROR, "Failed to add the FileAppender");
			}
		}
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

	public void addAppender(Appender appender) throws IllegalArgumentException {
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

	public Appender getAppender(Class<?> clazz) {
		for(Appender a: appenderList){
			if(a.getClass() == clazz){
				return a;
			}
		}
		return null;
	}
}
