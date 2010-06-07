package org.async.web.http.servlet;

public interface URIDistpatcher {
	Controller<StateData> dispatch(String uri);
}
