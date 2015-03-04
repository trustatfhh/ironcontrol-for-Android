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
 * This file is part of ironcontrol for android, version 1.0.2, implemented by the Trust@HsH research group at the Hochschule Hannover.
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
package de.hshannover.f4.trust.ironcontrol.logger.appander;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.os.Environment;
import de.hshannover.f4.trust.ironcontrol.logger.Level;
import de.hshannover.f4.trust.ironcontrol.logger.Logger;
import de.hshannover.f4.trust.ironcontrol.logger.LoggerFactory;

/**
 * Appender to log on the SDCard.
 * 
 * @author Marcel Reichenbach
 * 
 */
public class LogFileAppender implements Appender {

	private static final Logger logger = LoggerFactory.getLogger(LogFileAppender.class);

	private static final String IRONCONTROL_PATH = "/ironcontrol/logs/";

	private static String fileName = "ironcontrol-log_";

	private PrintWriter writer;

	private static final int INITIAL_BUFFER_SIZE = 64;

	public static final String DEFAULT_DELIMITER = "-";

	private boolean append = true;

	/**
	 * The logging file will be placed in the root/IRONCONTROL_PATH folder.
	 * 
	 * @throws IOException
	 */
	public LogFileAppender() throws IOException {
		File logFile = getLogFile();

		if (logFile != null) {
			if (!logFile.exists()) {
				if(!logFile.createNewFile()) {
					logger.log(Level.ERROR, "Unable to create new log file");
				}
			}

			FileOutputStream fileOutputStream = new FileOutputStream(logFile, append);

			writer = new PrintWriter(fileOutputStream);
		}
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	public synchronized void close() throws IOException {
		logger.log(Level.INFO, "Closing the FileAppender");
		if (writer != null) {
			writer.close();
		}
	}

	@Override
	public synchronized void log(String name, long time, Level level, Object message, Throwable throwable) {
		if (writer != null) {
			writer.println(toString(name, time, level, message, throwable));
			writer.flush();
		}
	}

	/**
	 * Return the message and the Throwable object as a String.
	 * 
	 * @param time		The log-time.
	 * @param level		The logging level. If null, it is not appended to the String.
	 * @param message	The message. If null, it is not appended to the String.
	 * @param throwable	The exception. If null, it is not appended to the String.
	 * @return 			The log-String, that is not null.
	 */
	public String toString(String name, long time, Level level, Object message, Throwable throwable) {
		StringBuffer buffer = new StringBuffer(INITIAL_BUFFER_SIZE);

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM", Locale.GERMANY);
		buffer.append(dateFormat.format(time));

		buffer.append(DEFAULT_DELIMITER);

		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.GERMANY);
		buffer.append(timeFormat.format(time));

		buffer.append(" [");
		buffer.append(Thread.currentThread().getName());
		buffer.append("] [");

		if(name != null){
			int index = name.lastIndexOf(".");
			buffer.append(name.substring(index+1));
		}

		buffer.append("] [");

		if(level != null){
			buffer.append(level.toString());
		}

		buffer.append("] ");

		if(message != null){
			buffer.append(message.toString());
		}

		buffer.append(' ');

		if(throwable != null){
			buffer.append(throwable.toString());
			StackTraceElement[] stackTrace = throwable.getStackTrace();
			for (int i = 0; i < stackTrace.length; i++) {
				StackTraceElement element = stackTrace[i];
				buffer.append(System.getProperty("line.separator"));
				buffer.append("\tat ");
				buffer.append(element.toString());
			}
		}

		return buffer.toString();
	}


	@Override
	public long getLogSize() {
		File logFile = getLogFile();

		if (logFile != null) {
			if (!logFile.exists()) {
				return logFile.length();
			}
		}

		return 0;
	}

	/**
	 * Android 1.6-2.1 used {@link Environment#getExternalStorageDirectory()}
	 *  to return the (root)
	 * external storage directory.  Folders in this subdir were shared by all applications
	 * and were not removed when the application was deleted.
	 * Starting with andriod 2.2, Context.getExternalFilesDir() is available.
	 * This is an external directory available to the application which is removed when the application
	 * is removed.
	 * 
	 * This implementation uses Context.getExternalFilesDir() if available, if not available uses
	 * {@link Environment#getExternalStorageDirectory()}.
	 * 
	 * @return a File object representing the external storage directory
	 * used by this device or null if the subdir could not be created or proven to exist
	 */
	protected synchronized File getExternalStorageDirectory() {

		File externalStorageDirectory = new File(Environment.getExternalStorageDirectory().getPath() + IRONCONTROL_PATH);

		if( externalStorageDirectory != null) {
			if(!externalStorageDirectory.exists()) {
				if(!externalStorageDirectory.mkdirs()) {
					externalStorageDirectory = null;
					logger.log(Level.ERROR, "mkdirs failed on externalStorageDirectory " + externalStorageDirectory);
				}
			}
		}
		return externalStorageDirectory;
	}

	/**
	 * @return the log file used to log to external storage
	 */
	public synchronized File getLogFile() {
		File mSdCardLogFile = null;
		long time = System.currentTimeMillis();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM", Locale.GERMANY);

		if( mSdCardLogFile == null ) {
			String externalStorageState = Environment.getExternalStorageState();
			if(externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
				File externalStorageDirectory = getExternalStorageDirectory();

				if (externalStorageDirectory != null) {
					mSdCardLogFile = new File(externalStorageDirectory, fileName + dateFormat.format(time) + ".txt");
				}
			}

			if(mSdCardLogFile == null) {
				logger.log(Level.ERROR, "Unable to open log file from external storage");
			}
		}

		return mSdCardLogFile;
	}

}
