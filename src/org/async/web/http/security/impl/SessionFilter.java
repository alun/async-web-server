package org.async.web.http.security.impl;

import java.io.IOException;
import java.util.Date;

import org.async.web.http.core.ConnectionStatus;
import org.async.web.http.core.Request;
import org.async.web.http.core.Response;
import org.async.web.http.security.Session;
import org.async.web.http.security.SessionManager;
import org.async.web.http.servlet.Filter;
import org.async.web.http.servlet.FilterChain;

public class SessionFilter extends Filter {
	private static final String PATH = "/";
	private static final String _SESSION_ATTACH_NAME = "_SESSION";
	private SessionManager sessionManager;
	private String sessionCookieName = "SID";

	@Override
	public void filter(Request request, Response response,
			ConnectionStatus status, FilterChain chain) throws IOException {
		String cookieValue = request.getCookieValue(sessionCookieName);
		Session s = sessionManager.getSession(cookieValue);
		if (s != null && !s.isExpired()) {
			sessionManager.prolongateSession(s);
		} else {
			s = sessionManager.startSession();
		}
		request.attach(_SESSION_ATTACH_NAME,s);
		//TODO create date in session
		response.addCookie(sessionCookieName,s.getSid(),new Date(s.getExpire()),PATH);
		chain.next(request, response, status);

	}

	public SessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public String getSessionCookieName() {
		return sessionCookieName;
	}

	public void setSessionCookieName(String sessionCookieName) {
		this.sessionCookieName = sessionCookieName;
	}


}
