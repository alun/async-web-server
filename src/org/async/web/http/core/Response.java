package org.async.web.http.core;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.async.net.io.ByteBufferWriter;
import org.async.web.http.io.HTTPGZipByteBufferWriter;

public class Response {

	private static final String CONTENT_ENCODING_GZIP = "gzip";
	private static final String TRANSFER_ENCODING_CHUNKED = "Chunked";
	private static final String CONTENT_ENCODING_HEADER = "Content-Encoding";
	private static final String TRANSFER_ENCODING_HEADER = "Transfer-Encoding";
	private static final String HEADER_DELIMETER = ": ";
	private static final String CONNECTION_CLOSE = "Close";
	private static final String CONNECTION_HEADER = "Connection";
	private static final String CONTENT_TYPE_HEADER = "Content-Type";
	private static final String SET_COOKIE_HEADER = "Set-Cookie";
	private static final String CLRF = "\r\n";
	private static final int START = -1;
	private static final int HEADERS = 0;
	private static final int BODY = 1;
	private static final int CLOSED = 2;
	private ByteBufferWriter writer;
	private int status = -1;
	private int statusCode = 200;
	private String mimeType = "text/html; charset=utf-8";
	private List<Cookie> cookies;

	public Response(ByteBufferWriter writer) {
		super();
		this.writer = writer;
	}

	public Writer startBody() throws IOException {
		if (status == START) {
			startHeaders();
		}
		if (status == HEADERS) {
			endHeaders();
			status = BODY;
			writer.flush();
			return writer;
		} else {
			throw new IllegalStateException();
		}

	}

	public Writer startGZippedBody() throws IOException {
		addHeader(TRANSFER_ENCODING_HEADER, TRANSFER_ENCODING_CHUNKED);
		addHeader(CONTENT_ENCODING_HEADER, CONTENT_ENCODING_GZIP);
		return new HTTPGZipByteBufferWriter((ByteBufferWriter) startBody());
	}

	public void setWriter(ByteBufferWriter writer) {
		this.writer = writer;
	}

	public void sendError(int code) throws IOException {
		HTTPStatusCodes.sendError(writer, code);
	}

	public void sendRedirect(String uri) throws IOException {
		HTTPStatusCodes.sendError(writer, 301, uri);
	}

	public void addHeader(String name, String value) throws IOException {
		if (status == START) {
			startHeaders();
		}
		if (status == HEADERS) {
			writer.write(name);
			writer.write(HEADER_DELIMETER);
			writer.write(value);
			writer.write(CLRF);
		} else {
			throw new IllegalStateException();
		}
	}

	public void close() throws IOException {
		writer.close();
		status = CLOSED;
	}

	public boolean isClosed() {
		return status == CLOSED;
	}

	public boolean isHeadersSent() {
		return status >= HEADERS;
	}

	private void startHeaders() throws IOException {
		status = HEADERS;
		HTTPStatusCodes.send(writer, statusCode);
	}

	private void endHeaders() throws IOException {
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				addHeader(SET_COOKIE_HEADER, cookie.toString());
			}
		}
		addHeader(CONTENT_TYPE_HEADER, mimeType);
		addHeader(CONNECTION_HEADER, CONNECTION_CLOSE);
		writer.write(CLRF);
		status = BODY;

	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public void addCookie(String name, String value) {
		addCookie(new Cookie(name, value));
	}

	public void addCookie(String name, String value, Date expire) {
		addCookie(new Cookie(name, value, expire));
	}

	public void addCookie(String name, String value, Date expire, String path) {
		addCookie(new Cookie(name, value, expire, path));
	}

	public void addCookie(String name, String value, Date expire, String path,
			String domain) {
		addCookie(new Cookie(name, value, expire, path, domain));
	}

	private void addCookie(Cookie cookie) {
		if (cookies == null) {
			cookies = new LinkedList<Cookie>();
		}
		cookies.add(cookie);

	}

	public void addCookie(String name, String value, Date expire, String path,
			String domain, boolean secure) {
		addCookie(new Cookie(name, value, expire, path, domain, secure));

	}

}
