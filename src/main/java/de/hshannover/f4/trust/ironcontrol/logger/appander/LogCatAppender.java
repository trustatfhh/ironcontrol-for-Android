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
package de.hshannover.inform.trust.ironcontrol.logger.appander;

import android.util.Log;
import de.hshannover.inform.trust.ironcontrol.logger.Level;

public class LogCatAppender implements Appender {

	private static final String clientID = "ironcontrol";

	private static final int INITIAL_BUFFER_SIZE = 64;

	@Override
	public void clear() {
		// Nothing
	}

	@Override
	public void log(String name, long time, Level level, Object message, Throwable t) {
		switch (level) {
		case FATAL:
			Log.wtf(clientID, toString(name, message), t);
			break;

		case ERROR:
			Log.e(clientID, toString(name, message), t);
			break;

		case WARN:
			Log.w(clientID, toString(name, message));
			break;

		case TOAST:
		case INFO:
			Log.i(clientID, toString(name, message));
			break;

		case DEBUG:
			Log.d(clientID, toString(name, message));
			break;

		default:
			break;
		}
	}

	public String toString(String name, Object message) {
		StringBuffer buffer = new StringBuffer(INITIAL_BUFFER_SIZE);

		buffer.append('[');
		buffer.append(Thread.currentThread().getName());
		buffer.append("] [");

		if(name != null){
			int index = name.lastIndexOf(".");
			buffer.append(name.substring(index+1));
		}

		buffer.append("] ");

		if(message != null){
			buffer.append(message.toString());
		}

		return buffer.toString();
	}

	@Override
	public long getLogSize() {
		return -1;
	}

}
