package org.async.web.http.security;

public interface SessionManager {

	public Session getSession(String sid);

	public Session startSession();

	public void endSession(String sid);

	public void expire();

	public void prolongateSession(Session s);
}
