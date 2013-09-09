package de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger;

import java.util.ArrayList;
import java.util.List;

import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.appander.Appender;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.appander.ListAppender;

public class Logger {

	private static List<Appender> appenderList;

	private String className;

	public Logger(String className){
		this.className = className;

		if(appenderList == null){
			appenderList = new ArrayList<Appender>();
			addAppender(new ListAppender());
		}
	}

	public void log(Level level, Object message) throws IllegalArgumentException {
		log(level, message, null);
	}

	public void log(Level level, Object message, Throwable t) throws IllegalArgumentException {
		if (level == null) {
			throw new IllegalArgumentException("The level must not be null.");
		}

		for (Appender appender : appenderList) {
			appender.log(className, level, message, t);
		}
	}

	public void addAppender(Appender appender) throws IllegalArgumentException {
		if (appender == null) {
			throw new IllegalArgumentException("Appender not allowed to be null");
		}

		if (!appenderList.contains(appender)) {
			appenderList.add(appender);
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
