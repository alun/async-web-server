package org.async.web.http.servlet;

import java.io.IOException;

import org.async.web.http.core.ConnectionStatus;
import org.async.web.http.core.Request;
import org.async.web.http.core.Response;

public interface  Controller<T extends StateData> {
	public  T handle(Request request, Response response, ConnectionStatus status, T data) throws IOException;
	public T createStateData();
}
