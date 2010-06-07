package org.async.web.time;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.async.web.http.core.IdleHandler;

public class TimerTaskManager implements IdleHandler {
	Logger logger = Logger.getLogger("org.async.web.time.TimerManager");
	List<TimerTask> tasks;

	public TimerTaskManager() {

	}

	public List<TimerTask> getTasks() {
		return tasks;
	}

	public void setTasks(List<TimerTask> tasks) {
		this.tasks = tasks;
	}

	@Override
	public void onIdle() {
		for (TimerTask task : tasks) {
			long currentTimeMillis = System.currentTimeMillis();
			if (task.state == TimerTask.VIRGIN) {
				task.nextExecutionTime = currentTimeMillis + task.delay;
				task.state = TimerTask.SCHEDULED;
			}
			if (task.state != TimerTask.CANCELED
					&& task.nextExecutionTime <= currentTimeMillis) {
				task.nextExecutionTime += task.interval;
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Starting task " + task);
				}
				task.run();
			}

		}
	}
}
