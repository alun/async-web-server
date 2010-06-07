package org.async.web.http.parser;

public interface Callback {
	void method(String method);

	void uri(String uri);

	void queryParamName(String name);

	void queryParamValue(String value);

	void protocol(String protocol);

	void headerName(String name);

	void headerValue(String value);

	void error(Exception e);
	
	int filter(int value);

}
