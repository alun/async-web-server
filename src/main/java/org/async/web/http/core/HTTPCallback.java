package org.async.web.http.core;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.async.web.http.parser.Callback;
import org.async.web.http.parser.RootParser;
import org.async.web.http.servlet.Controller;
import org.async.web.http.servlet.StateData;
import org.async.web.http.servlet.URIDistpatcher;
import org.async.web.log.AccessLogFormatter;
import org.async.web.log.LogConfHelper;

public class HTTPCallback implements Callback {
	private static final String URLDECODE_CHARSET = "UTF-8";
	private Logger accessLogger = LogConfHelper.getCustomFileLogger(
			"access.log", "access.log.%g", new AccessLogFormatter(LogConfHelper
					.getStringProperty("access.log.date.format",
							"yy/MM/dd HH:mm:ss.SSS")));
	RequestData data;
	List<URIDistpatcher> dispatchers;
	RootParser httpParser;

	public RootParser getHttpParser() {
		return httpParser;
	}

	public void setHttpParser(RootParser httpParser) {
		this.httpParser = httpParser;
	}


	public RequestData getData() {
		return data;
	}

	public void setData(RequestData data) {
		this.data = data;
	}

	@Override
	public void headerName(String name) {
		data.headerName = name;
	}

	@Override
	public void headerValue(String value) {
		data.addHeader(data.headerName, value);
	}

	@Override
	public void method(String method) {
		if (!(method.equals("GET") || method.equals("POST"))) {
			throw new HTTPException(405);
		}
		data.setMethod(method);
	}

	@Override
	public void protocol(String protocol) {
		if (!(protocol.equals("HTTP/1.1") || protocol.equals("HTTP/1.0"))) {
			throw new HTTPException(400);
		}
	}

	@Override
	public void uri(String uri) {
		if (accessLogger.isLoggable(Level.INFO)) {
			accessLogger.info(data.getMethod() + " " + uri + " "
					+ data.remoteAddr);
		}
		data.setUri(uri);
		Controller<StateData> controller = null;
		try {
		for (URIDistpatcher distpatcher : dispatchers) {
			if ((controller = (Controller<StateData>) distpatcher.dispatch(uri)) != null) {
				data.setController(controller);
				return;
			}
		}
		}catch (RuntimeException e) {
			throw new HTTPException(500);
		}
		if (controller == null) {
			throw new HTTPException(404,uri);
		}
		if (accessLogger.isLoggable(Level.FINE)) {
			accessLogger.fine("Controller: "+controller);
		}
	}

	@Override
	public void error(Exception e) {
		if (e instanceof HTTPException) {
			throw (HTTPException) e;
		} else {
			throw new HTTPException(400);
		}
	}

	public List<URIDistpatcher> getDispatchers() {
		return dispatchers;
	}

	public void setDispatchers(List<URIDistpatcher> dispatchers) {
		this.dispatchers = dispatchers;
	}

	@Override
	public void queryParamName(String name) {
		data.lastQueryParamName = name;
	}

	@Override
	public void queryParamValue(String value) {
		try {
			data.addQueryParam(URLDecoder.decode(data.lastQueryParamName,
					URLDECODE_CHARSET), URLDecoder.decode(value,
					URLDECODE_CHARSET));
		} catch (UnsupportedEncodingException e) {
			error(e);
		}
	}

	@Override
	public int filter(int value) {
		if (value == RootParser.PARSED && data.getMethod().equals("POST")
				&& !data.isPostParsed()) {
			String contentLength = data.headers.get("Content-Length");
			if (contentLength != null) {
				data.setPostParsed(true);
				int len = Integer.parseInt(contentLength);
				if (len > 0xFFFFFF)
					throw new IllegalArgumentException();
				return RootParser.POST_QUERY_PARAM_NAME+(len<<8);
			}
		} else if(value == RootParser.PARSED) {
			if (accessLogger.isLoggable(Level.FINEST)) {
				StringBuilder builder=new StringBuilder();
				builder.append(data.getRemoteAddr());
				builder.append('\n');
				builder.append('\t');
				builder.append(data.getMethod());
				builder.append(' ');
				builder.append(data.getUri());
				builder.append('\n');
				appendMap(builder, data.getHeaders());
				builder.append('\n');
				appendMap(builder, data.getQueryParams());
				accessLogger.finest(builder.toString());
			}
		}
		return value;
	}

	private void appendMap(StringBuilder builder, Map<String, String> headers) {
		if(headers!=null) {
			for(Entry<String,String> h:headers.entrySet()) {
				builder.append('\t');
				builder.append(h.getKey());
				builder.append(':');
				builder.append(h.getValue());
				builder.append('\n');
			}
		}
	}

}
