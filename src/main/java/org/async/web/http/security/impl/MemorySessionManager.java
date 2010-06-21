package org.async.web.http.security.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.async.web.http.security.Session;
import org.async.web.http.security.SessionManager;

public class MemorySessionManager implements SessionManager {

	Map<String, Session> sessions = new HashMap<String, Session>();
	int sessionLifeTime = 60000*10;

	private String createSID() {
		UUID sid = UUID.randomUUID();
		//TODO compact
		return sid.toString();
	}

	@Override
	public Session startSession() {
		Session s = new Session();
		s.setSid(createSID());
		s.setLifetime(sessionLifeTime);
		s.setExpire(System.currentTimeMillis() + sessionLifeTime);
		s.setPath("/");
		sessions.put(s.getSid(), s);
		return s;
	}

	public Session startSession(String sid) {
		Session s = new Session();
		s.setSid(sid);
		s.setLifetime(sessionLifeTime);
		s.setExpire(System.currentTimeMillis() + sessionLifeTime);
		s.setPath("/");
		sessions.put(s.getSid(), s);
		return s;
	}

	@Override
	public void endSession(String sid) {
		sessions.remove(sid);
	}

	public void endSession(Session s) {
		endSession(s.getSid());
	}

	@Override
	public Session getSession(String sid) {
		return sessions.get(sid);
	}

	public void prolongateSession(Session s) {
		s.prolongate(sessionLifeTime);
	}

	public int getSessionLifeTime() {
		return sessionLifeTime;
	}

	public void setSessionLifeTime(int sessionLifeTime) {
		this.sessionLifeTime = sessionLifeTime;
	}

	@Override
	public void expire() {
		Iterator<Entry<String, Session>> iterator = sessions.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Entry<String, Session> entry = iterator.next();
			if (entry.getValue().isExpired()) {
				iterator.remove();
			}
		}

	}
}
