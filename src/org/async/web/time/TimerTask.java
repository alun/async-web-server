package org.async.web.time;

public abstract class TimerTask implements Runnable {

	protected int delay;
	protected int interval;
	protected long nextExecutionTime;
	protected int state = VIRGIN;
	public static final int VIRGIN = 0;
	public static final int SCHEDULED = 1;
	public static final int CANCELED = 3;
	public int getDelay() {
		return delay;
	}
	public void setDelay(int delay) {
		this.delay = delay;
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}




}
