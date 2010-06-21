package org.async.web.http.core;

import java.util.HashMap;
import java.util.Map;

import org.async.web.http.servlet.Controller;
import org.async.web.http.servlet.Filter;
import org.async.web.http.servlet.FilterChain;
import org.async.web.http.servlet.StateData;

public class Request {
	private Map<String, String> headers;
	private String method;
	private String uri;
	private String remoteAddr;
	private Controller<StateData> controller;
	private Filter filter;
	private FilterChain chain;
	private Map<String, String> queryParams;
	private Map<String, Object> attachments;
	private boolean postParsed;

	public Request() {
		super();
	}

	public Request(RequestData data) {
		super();
		this.method = data.method;
		this.uri = data.uri;
		this.headers = data.headers;
		this.controller = data.controller;
		this.queryParams = data.queryParams;
		this.postParsed = data.postParsed;
		this.remoteAddr = data.remoteAddr;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public String getMethod() {
		return method;
	}

	public String getUri() {
		return uri;
	}

	public Controller<StateData> getController() {
		return controller;
	}

	public String getHeaderValue(String headerName) {
		return headers.get(headerName);
	}

	public String getCookie() {
		return getHeaderValue("Cookie");
	}

	public String getCookieValue(String name) {

		String cookie = getCookie();
		if (cookie != null) {
			int i = cookie.indexOf(name);
			if (i > 0) {
				for (int j = i - 1; j >= 0; j--) {
					char c = cookie.charAt(j);
					if (!Character.isWhitespace(c)) {
						if (c == ';')
							break;
						else
							return null;
					}
				}
			}
			i = i + name.length() + 1;
			if (i < cookie.length()) {
				String s = cookie.substring(i);
				return s.indexOf(';') > 0 ? s.substring(0, s.indexOf(';')) : s;
			}
		}
		return null;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public FilterChain getChain() {
		return chain;
	}

	public void setChain(FilterChain chain) {
		this.chain = chain;
	}

	public Map<String, String> getQueryParams() {
		return queryParams;
	}

	public boolean isPostParsed() {
		return postParsed;
	}

	public String getQueryParam(String name) {
		if (queryParams == null) {
			return null;
		}
		return queryParams.get(name);

	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public void attach(String name, Object value) {
		if (attachments == null) {
			attachments = new HashMap<String, Object>();
		}
		attachments.put(name, value);
	}

	public Object getAttach(String name) {
		if (attachments != null) {
			return attachments.get(name);
		}
		return null;
	}

	public void dettach(String name) {
		if (attachments != null) {
			attachments.remove(name);
		}
	}

}
