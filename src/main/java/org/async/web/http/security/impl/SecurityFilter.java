package org.async.web.http.security.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.async.web.http.core.ConnectionStatus;
import org.async.web.http.core.Request;
import org.async.web.http.core.Response;
import org.async.web.http.security.Session;
import org.async.web.http.security.User;
import org.async.web.http.servlet.Filter;
import org.async.web.http.servlet.FilterChain;
import org.async.web.http.servlet.impl.WildCardPattern;

public class SecurityFilter extends Filter {

	private String redirectUrl;
	private Map<WildCardPattern, List<String>> mapping;

	public SecurityFilter() {

	}

	@Override
	public void filter(Request request, Response response,
			ConnectionStatus status, FilterChain chain) throws IOException {
		List<String> roles = matches(request.getUri());
		Session session = (Session)request.getAttach("_SESSION");
		User user = session==null?null:(User) (session).get("user");
		if (roles != null) {
			if (user == null || !user.is(roles)) {
				response.sendRedirect(redirectUrl);
				return;
			}
		}
		chain.next(request, response, status);
	}

	public Map<WildCardPattern, List<String>> getMapping() {
		return mapping;
	}

	public void setMapping(Map<String, List<String>> mapping) {
		this.mapping = new HashMap<WildCardPattern, List<String>>(mapping
				.size());
		for (String key : mapping.keySet()) {
			this.mapping.put(new WildCardPattern(key), mapping.get(key));
		}
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public List<String> matches(String uri) {
		for (WildCardPattern pattern : mapping.keySet()) {
			if (pattern.matches(uri)) {
				return mapping.get(pattern);
			}
		}
		return null;
	}
}
