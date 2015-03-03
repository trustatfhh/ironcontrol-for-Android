/*
 * #%L
 * =====================================================
 *   _____                _     ____  _   _       _   _
 *  |_   _|_ __ _   _ ___| |_  / __ \| | | | ___ | | | |
 *    | | | '__| | | / __| __|/ / _` | |_| |/ __|| |_| |
 *    | | | |  | |_| \__ \ |_| | (_| |  _  |\__ \|  _  |
 *    |_| |_|   \__,_|___/\__|\ \__,_|_| |_||___/|_| |_|
 *                             \____/
 * 
 * =====================================================
 * 
 * Hochschule Hannover
 * (University of Applied Sciences and Arts, Hannover)
 * Faculty IV, Dept. of Computer Science
 * Ricklinger Stadtweg 118, 30459 Hannover, Germany
 * 
 * Email: trust@f4-i.fh-hannover.de
 * Website: http://trust.f4.hs-hannover.de/
 * 
 * This file is part of ironcontrol for android, version 1.0.1, implemented by the Trust@HsH research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2013 - 2015 Trust@HsH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package de.hshannover.f4.trust.ironcontrol.logger;

import java.util.ArrayList;
import java.util.List;

import de.hshannover.f4.trust.ironcontrol.logger.appander.Appender;

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
			logger.log(Level.TOAST, appender.getClass().getSimpleName() + " add!");
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
		Appender a = getAppender(clazz);
		if(a != null){
			appenderList.remove(a);
			logger.log(Level.TOAST, clazz.getSimpleName() + " removed!");
		}
	}
}
