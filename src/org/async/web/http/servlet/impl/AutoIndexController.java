package org.async.web.http.servlet.impl;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;

import org.async.web.http.core.ConnectionStatus;
import org.async.web.http.core.Request;
import org.async.web.http.core.Response;
import org.async.web.http.servlet.Controller;
import org.async.web.http.servlet.StateData;

public class AutoIndexController implements Controller<StateData> {

	private String documentRoot = "";

	public String getDocumentRoot() {
		return documentRoot;
	}

	public void setDocumentRoot(String documentRoot) {
		this.documentRoot = documentRoot;
	}

	@Override
	public StateData handle(Request request, Response response,
			ConnectionStatus status, StateData data) throws IOException {
		String uri = request.getUri();
		File f = new File(documentRoot + uri);
		if (!f.exists()) {
			response.sendError(404);
			return data;
		}
		// TODO template engine
		Writer writer = response.startGZippedBody();

		writer.write("<html>");
		writer.write("<head>");
		writer.write("<title>Index of " + uri + "</title>");
		writer.write("</head>");
		writer.write("<body>");
		writer.write("<h1>Index of " + uri + "</h1>");
		writer.write("<ul>");
		ArrayList<String> files = new ArrayList<String>();
		ArrayList<String> dirs = new ArrayList<String>();
		if (f.listFiles() != null) {
			for (File file : f.listFiles()) {
				if (file.isDirectory()) {
					dirs.add(file.getName());
				} else {
					files.add(file.getName());
				}
			}
		}
		Collections.sort(files);
		Collections.sort(dirs);
		for (String s : dirs) {
			writer.write("<li><a href=\"" + uri + s + "/\">" + s + "/</a>");
		}
		for (String s : files) {
			writer.write("<li><a href=\"" + uri + s + "\">" + s + "</a>");
		}
		writer.write("</ul>");
		writer.write("</body>");
		writer.write("</html>");
		writer.flush();
		response.close();
		return data;
	}

	@Override
	public StateData createStateData() {
		return null;
	}

}
