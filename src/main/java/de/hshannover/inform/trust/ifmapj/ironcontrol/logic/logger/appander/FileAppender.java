package de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.appander;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

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

	public static final String DEFAULT_FILENAME = "microlog.txt";

	private String fileName = DEFAULT_FILENAME;

	private PrintWriter writer;

	private boolean append = false;

	private File mSdCardLogFile = null;

	/**
	 * Create a file appender without application context.  The logging file will
	 * be placed in the root folder and will not be removed when your application is
	 * removed.  Use FileAppender(Context) to create a log that is automatically removed
	 * when your application is removed
	 * Note: your application must hold android.permission.WRITE_EXTERNAL_STORAGE
	 * to be able to access the SDCard.
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
	public synchronized void log(String name, Level level, Object message, Throwable throwable) {
		if (writer != null) {
			writer.println(name + " " + level.toString() + " "  + message.toString() + throwable.toString());
			writer.flush();
		}

		//			if (throwable != null) {
		//				throwable.printStackTrace();
		//			}

	}

	@Override
	public long getLogSize() {
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

		File externalStorageDirectory = Environment.getExternalStorageDirectory();

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
