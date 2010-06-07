package org.async.web.http.servlet;

import java.io.IOException;

import org.async.web.http.core.ConnectionStatus;
import org.async.web.http.core.Request;
import org.async.web.http.core.Response;

public interface State<T extends StateData> {

	T run(Request request, Response response, T data, ConnectionStatus status) throws IOException;
}
