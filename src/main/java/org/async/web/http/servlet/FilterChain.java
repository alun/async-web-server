package org.async.web.http.servlet;

import org.async.web.http.core.ConnectionStatus;
import org.async.web.http.core.Request;
import org.async.web.http.core.Response;

public interface FilterChain {
	public Filter next(Request request,Response response,ConnectionStatus status);

	public Filter initChain(Request request,Response response,ConnectionStatus status);
}
