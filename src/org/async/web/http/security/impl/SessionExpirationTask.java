package org.async.web.http.security.impl;

import org.async.web.http.security.SessionManager;
import org.async.web.time.TimerTask;

public class SessionExpirationTask extends TimerTask {

	private SessionManager sessionManager;

	public SessionExpirationTask() {
		super();
	}

	public SessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	@Override
	public void run() {
		sessionManager.expire();

	}

}
