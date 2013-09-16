package de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.appander;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.os.Environment;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Level;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Logger;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LoggerFactory;

/**
 * Appender to log on the SDCard.
 * 
 * @author Marcel Reichenbach
 * 
 */
public class FileAppender implements Appender {

	private static final Logger logger = LoggerFactory.getLogger(FileAppender.class);

	private static final String IRONCONTROL_PATH = "/ironcontrol/";

	private static final String fileName = "ironcontrol-log.txt";

	private PrintWriter writer;

	private static final int INITIAL_BUFFER_SIZE = 64;

	public static final String DEFAULT_DELIMITER = "-";

	private boolean append = true;

	/**
	 * The logging file will be placed in the root/IRONCONTROL_PATH folder.
	 * 
	 * @throws IOException
	 */
	public FileAppender() throws IOException {
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

		SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM", Locale.GERMANY);
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

		if( mSdCardLogFile == null ) {
			String externalStorageState = Environment.getExternalStorageState();
			if(externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
				File externalStorageDirectory = getExternalStorageDirectory();

				if (externalStorageDirectory != null) {
					mSdCardLogFile = new File(externalStorageDirectory, fileName);
				}
			}

			if(mSdCardLogFile == null) {
				logger.log(Level.ERROR, "Unable to open log file from external storage");
			}
		}

		return mSdCardLogFile;
	}

}
