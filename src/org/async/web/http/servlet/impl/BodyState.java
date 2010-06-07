package org.async.web.http.servlet.impl;

import java.io.IOException;

import org.async.web.http.core.ConnectionStatus;
import org.async.web.http.core.Request;
import org.async.web.http.core.Response;
import org.async.web.http.servlet.State;

public class BodyState implements State<FileStateData> {
	private static final int BLOCK_SIZE = 65536;

	@Override
	public FileStateData run(Request request, Response response,
			FileStateData data, ConnectionStatus status) throws IOException {
		long delta = (data.to - data.from) + 1;
		long count = delta > BLOCK_SIZE ? BLOCK_SIZE : delta;
		long c = 0;
		c = data.channel.transferTo(data.from, count, data.out);
		data.from += c;
		if (data.from == data.to + 1) {
			response.close();
		}
		return data;
	}

}
