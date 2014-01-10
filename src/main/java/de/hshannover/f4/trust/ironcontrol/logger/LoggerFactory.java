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
 * Copyright (C) 2013 Trust@HsH
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
	 * Set the context or only the the ListAppender was add.
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
