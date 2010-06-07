package org.async.web.http.core;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.async.net.ClearableChannelProcessor;
import org.async.net.io.ByteBufferASCIIReader;
import org.async.net.io.ByteBufferWriter;
import org.async.web.http.servlet.Controller;
import org.async.web.http.servlet.FilterChain;
import org.async.web.http.servlet.StateData;

public class HTTPRequestProcessor implements ClearableChannelProcessor {
	private static Logger logger = Logger
			.getLogger("org.async.web.http.core.HTTPRequestProcessor");

	private FilterChain filterChain;

	private class Data {
		Request request;
		Response response;
		ConnectionStatus status;
		StateData stateData;

		public Data(Request request, Response response, ConnectionStatus status) {
			super();
			this.request = request;
			this.response = response;
			this.status = status;
		}
	}

	private ByteBufferASCIIReader reader;

	private Map<SelectionKey, Data> map = new HashMap<SelectionKey, Data>();

	private ByteBufferWriter writer;

	public HTTPRequestProcessor() {

	}

	public void register(SelectionKey key, Request request, Response response) {
		key.attach(this);
		ConnectionStatus status = new ConnectionStatus(key);
		map.put(key, new Data(request, response, status));
		if (filterChain != null) {
			request.setFilter(filterChain.initChain(request, response, status));
			request.setChain(filterChain);
		}
		key.interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
	}

	@Override
	public void accept(SelectionKey key) {
		throw new IllegalStateException();
	}

	@Override
	public void connect(SelectionKey key) {
		throw new IllegalStateException();
	}

	@Override
	public void close(SelectionKey key) {
		clear(key);
		try {
			key.channel().close();
			key.cancel();
		} catch (IOException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, e.getMessage(), e);
			}
		}
	}

	@Override
	public void read(SelectionKey key) {
		SocketChannel channel = (SocketChannel) key.channel();
		try {
			reader.reset();
			if (channel.read(reader.getBuffer()) == -1) {
				close(key);
			}
		} catch (IOException e) {
			close(key);
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, e.getMessage(), e);
			}
		}
	}

	@Override
	public void write(SelectionKey key) {
		Data data = map.get(key);
		writer.clear();
		Controller<StateData> controller = data.request.getController();
		if (data.stateData == null) {
			data.stateData = controller.createStateData();
		}
		try {
			writer.setChannel((WritableByteChannel) key.channel());
			if (data.request.getFilter() == null) {
				data.stateData = controller.handle(data.request, data.response,
						data.status, data.stateData);
			} else {
				data.request.getFilter().filter(data.request, data.response,
						data.status, filterChain);
			}
			writer.flush();
			// TODO ConnectionStatus.close !response.close
			if (!data.status.isAlive()) {
				close(key);
			}
		} catch (HTTPException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, e.getMessage(), e);
			}
			try {
				HTTPStatusCodes.sendError(writer, e.getErrCode());
				writer.close();
			} catch (IOException e1) {

			}
			close(key);
		} catch (Exception e) {
			if (!data.response.isHeadersSent()) {
				try {
					HTTPStatusCodes.sendError(writer, 500);
					writer.close();
				} catch (IOException e1) {

				}
			}
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, e.getMessage(), e);
			}
			close(key);
		}

	}

	public FilterChain getFilterChain() {
		return filterChain;
	}

	public void setFilterChain(FilterChain filterChain) {
		this.filterChain = filterChain;
	}

	public ByteBufferASCIIReader getReader() {
		return reader;
	}

	public void setReader(ByteBufferASCIIReader reader) {
		this.reader = reader;
	}

	public ByteBufferWriter getWriter() {
		return writer;
	}

	public void setWriter(ByteBufferWriter writer) {
		this.writer = writer;
	}

	@Override
	public void clear(SelectionKey key) {
		Data data = map.remove(key);
		if (data != null && data.stateData != null) {
			data.stateData.close();
		}

	}

	@Override
	public boolean isFree(SelectionKey key) {
		return false;
	}

	@Override
	public boolean isService(SelectionKey key) {
		return false;
	}

}
