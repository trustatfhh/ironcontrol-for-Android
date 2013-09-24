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
