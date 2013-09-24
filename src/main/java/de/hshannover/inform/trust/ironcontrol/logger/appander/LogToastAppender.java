package de.hshannover.inform.trust.ironcontrol.logger.appander;

import android.content.Context;
import android.widget.Toast;
import de.hshannover.inform.trust.ironcontrol.logger.Level;
import de.hshannover.inform.trust.ironcontrol.view.MainActivity;

public class LogToastAppender implements Appender {

	@Override
	public void clear() {
		// Nothing
	}

	@Override
	public void log(String name, long time, Level level, Object message, Throwable t) {
		switch (level) {
		case TOAST:
			if(message != null){
				Context c = MainActivity.getContext();
				if(c != null){
					Toast.makeText(c, message.toString(), Toast.LENGTH_SHORT).show();
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	public long getLogSize() {
		return -1;
	}

}
