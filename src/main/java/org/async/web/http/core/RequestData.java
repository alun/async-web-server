package org.async.web.http.core;

import java.util.HashMap;
import java.util.Map;

import org.async.web.http.parser.StreamData;
import org.async.web.http.servlet.Controller;
import org.async.web.http.servlet.StateData;
import org.async.web.utils.IgnoreCaseHashMap;

public class RequestData extends StreamData {
	Map<String, String> headers;
	String method;
	String uri;
	String headerName;
	Controller<StateData> controller;
	Map<String, String> queryParams;
	boolean postParsed;
	String remoteAddr;
	String lastQueryParamName;


	public RequestData(int size) {
		super(size);
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getHeaderName() {
		return headerName;
	}

	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}

	public void addHeader(String headerName, String value) {
		if (headers == null) {
			headers = new IgnoreCaseHashMap<String>();
		}
		headers.put(headerName, value);

	}

	public Controller<StateData> getController() {
		return controller;
	}

	public void setController(Controller<StateData> controller) {
		this.controller = controller;
	}

	public Map<String, String> getQueryParams() {
		return queryParams;
	}

	public void setQueryParams(Map<String, String> queryParams) {
		this.queryParams = queryParams;
	}

	public void addQueryParam(String key, String value) {
		if (queryParams == null) {
			queryParams = new HashMap<String, String>();
		}
		queryParams.put(key, value);

	}

	public boolean isPostParsed() {
		return postParsed;
	}

	public void setPostParsed(boolean postParsed) {
		this.postParsed = postParsed;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}




}
