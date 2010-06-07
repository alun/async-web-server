package org.async.web.http.servlet.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.async.net.io.ByteBufferWriter;
import org.async.web.http.core.ConnectionStatus;
import org.async.web.http.core.HTTPException;
import org.async.web.http.core.Request;
import org.async.web.http.core.Response;
import org.async.web.http.servlet.State;

public class HeaderState implements State<FileStateData> {
	protected String documentRoot;
	protected Map<String, String> mimeTypes;
	private FileChannelProvider provider;

	@Override
	public FileStateData run(Request request, Response response,
			FileStateData data, ConnectionStatus status) throws IOException {

		if (data == null)
			data = new FileStateData();
		String path = documentRoot + request.getUri();
		try {
			data.channel = provider.getFileChannel(path);
		} catch (Exception e) {
			throw new HTTPException(404);
		}

		long size = data.channel.size();
		String v;
		long from = 0;
		long to = size - 1;

		if ((v = request.getHeaderValue("Range")) != null) {
			v = v.trim();
			int j = v.indexOf("-");
			if (v.startsWith("bytes=") && j >= 0) {
				from = parse(v.substring(6, j), 0);
				to = parse(v.substring(j + 1), size - 1);
			}
			if (to >= size) {
				to = size - 1;
			}
			if (to < from) {
				throw new HTTPException(400);
			}
			response.setStatusCode(206);
		}
		data.from = from;
		data.to = to;
		data.provider = provider;
		data.path = path;
		String mimeType = mimeTypes.get(path
				.substring(path.lastIndexOf('.') + 1));
		if (mimeType == null)
			mimeType = "application/octet-stream";
		response.setMimeType(mimeType);
		if (to - from + 1 == size) {
			response.addHeader("Accept-Ranges", "bytes");
		} else {
			String string = "bytes " + String.valueOf(from) + "-"
					+ String.valueOf(to) + "/" + String.valueOf(size);
			response.addHeader("Content-Range", string);
		}
		response.addHeader("Content-Length", String.valueOf(to - from + 1));
		Writer writer = response.startBody();
		data.out = ((ByteBufferWriter) writer).getChannel();
		data.state = 1;
		return data;
	}

	public String getDocumentRoot() {
		return documentRoot;
	}

	public void setDocumentRoot(String documentRoot) {
		this.documentRoot = documentRoot;
	}

	public Map<String, String> getMimeTypes() {
		return mimeTypes;
	}

	public void setMimeTypes(Map<String, String> mimeTypes) {
		this.mimeTypes = mimeTypes;
	}

	public FileChannelProvider getProvider() {
		return provider;
	}

	public void setProvider(FileChannelProvider provider) {
		this.provider = provider;
	}

	private long parse(String value, long def) {
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			return def;
		}
	}

}
