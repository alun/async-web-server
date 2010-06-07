package org.async.web.http.core;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class HTTPStatusCodes {
	private static final String CLRF = "\r\n";
	static Map<Integer, String> ANSWERS = new HashMap<Integer, String>();
	static {
		ANSWERS.put(200, "HTTP/1.1 200 OK");
		ANSWERS.put(206, "HTTP/1.x 206 Partial Content");
		ANSWERS.put(301, "HTTP/1.0 301 Moved Permanently"+CLRF+"Location: ");
		ANSWERS.put(400, "HTTP/1.0 400 Bad Request");
		ANSWERS.put(403, "HTTP/1.0 403 Forbidden");
		ANSWERS.put(404, "HTTP/1.0 404 Not Found");
		ANSWERS.put(405, "HTTP/1.0 405 Not Allowed");
		ANSWERS.put(500, "HTTP/1.0 500 Internal Server Error");
	}

	public static void sendError(Writer writer, int code, String info)
			throws IOException {
		writer.write(ANSWERS.get(code));
		if (info != null)
			writer.write(info);
		writer.write(CLRF);
		writer.write(CLRF);
		writeMessage(writer, code);
		writer.close();
	}

	public static void sendError(Writer writer, int code) throws IOException {
		sendError(writer, code, null);
	}

	public static void send(Writer writer, int code) throws IOException {
		writer.write(ANSWERS.get(code));
		writer.write(CLRF);
	}

	private static void writeMessage(Writer writer, int code)
			throws IOException {
		writer.write("<html><head><title>");
		writer.write(ANSWERS.get(code));
		writer.write("</title></head><body><h1>");
		writer.write(ANSWERS.get(code));
		writer.write("</h1></body></html>");
	}

}
