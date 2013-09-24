package de.hshannover.inform.trust.ironcontrol.logger.appander;

import java.util.ArrayList;
import java.util.List;

import de.hshannover.inform.trust.ironcontrol.logger.Level;
import de.hshannover.inform.trust.ironcontrol.logger.LogData;
import de.hshannover.inform.trust.ironcontrol.logger.LogReceiver;

public class LogListAppender implements Appender {

	private List<LogData> logList = new ArrayList<LogData>();

	private List<LogReceiver> receivers = new ArrayList<LogReceiver>();

	@Override
	public void clear() {
		logList.clear();
	}

	@Override
	public void log(String name, long time, Level level, Object message, Throwable t) {
		LogData log = new LogData();
		if(name != null){
			int index = name.lastIndexOf(".");
			log.setName(name.substring(index+1));
		}else{
			log.setName("");
		}
		log.setTime(time);
		log.setLevel(level);
		log.setMessage(message);
		log.setThrowable(t);
		logList.add(log);

		// notivy receivers
		for(LogReceiver r: receivers){
			r.newLogData(log);
		}
	}

	@Override
	public long getLogSize() {
		return logList.size();
	}

	public List<LogData> getLogs(){
		return logList;
	}

	public void addLogReceiver(LogReceiver receiver){
		this.receivers.add(receiver);
	}

}
