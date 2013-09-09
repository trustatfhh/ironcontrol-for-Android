package de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger;

public class LogData {

	public String name;
	public long time;
	public Level level;
	public Object message;
	public Throwable t;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public Level getLevel() {
		return level;
	}
	public void setLevel(Level level) {
		this.level = level;
	}
	public Object getMessage() {
		return message;
	}
	public void setMessage(Object message) {
		this.message = message;
	}
	public Throwable getThrowable() {
		return t;
	}
	public void setThrowable(Throwable t) {
		this.t = t;
	}
}