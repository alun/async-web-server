package org.async.web.http.core;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.async.json.conf.Initializable;
import org.async.net.ChannelProcessor;
import org.async.net.io.ByteBufferASCIIReader;
import org.async.net.io.ByteBufferWriter;
import org.async.web.http.parser.RootParser;

public class HTTPChannelProcessor implements ChannelProcessor, Initializable {
	private static Logger logger = Logger
			.getLogger("org.async.web.http.core.HttpChannelProcessor");
	private ByteBufferASCIIReader reader;
	private ByteBufferWriter writer;
	private RootParser parser = new RootParser();
	private Map<SelectionKey, RequestData> data = new HashMap<SelectionKey, RequestData>();
	private HTTPCallback callback;
	private HTTPRequestProcessor httpRequestProcessor;

	private Integer sendBufferSize = null; // SO_SNDBUF
	private Integer recvBufferSize = null; // SO_RCVBUF
	private Boolean tcpNoDelay = null; // TCP_NODELAY
	private Boolean soLinger = null; // // SO_LINGER on/off
	private Integer soLingerTime = null; // // SO_LINGER time in seconds
	private Integer soTimeout = null; // SO_TIMEOUT
	private Boolean keepAlive = null; // SO_KEEPALIVE
	private Integer requestBufferSize = 256;

	public HTTPChannelProcessor() {
		super();
	}

	@Override
	public void accept(SelectionKey key) {
		try {
			ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key
					.channel();
			SocketChannel socketChannel = serverSocketChannel.accept();
			socketChannel.configureBlocking(false);
			setSocketOptions(socketChannel.socket());
			socketChannel.register(key.selector(), SelectionKey.OP_READ, this);
		} catch (IOException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, e.getMessage(), e);
			}
			close(key);

		}
	}

	private void setSocketOptions(Socket socket) throws SocketException {
		if (sendBufferSize != null) {
			socket.setSendBufferSize(sendBufferSize);
		}
		if (recvBufferSize != null) {
			socket.setReceiveBufferSize(recvBufferSize);
		}
		if (tcpNoDelay != null) {
			socket.setTcpNoDelay(tcpNoDelay);
		}
		if (soLinger != null && soLingerTime != null) {
			socket.setSoLinger(soLinger, soLingerTime);
		}
		if (soTimeout != null) {
			socket.setSoTimeout(soTimeout);
		}
		if (keepAlive != null) {
			socket.setKeepAlive(keepAlive);
		}
	}

	@Override
	public void close(SelectionKey key) {
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
			if (channel.read(reader.getBuffer()) > -1) {
				reader.getBuffer().flip();
				RequestData requestData = data.get(key);
				if (requestData == null) {
					requestData = new RequestData(requestBufferSize);
					requestData.setRemoteAddr(channel.socket().getInetAddress()
							.getHostAddress());

					data.put(key, requestData);
				}
				callback.setData(requestData);
				if (RootParser.PARSED == parser.parse(reader, requestData,
						callback)) {
					Request request = new Request(requestData);
					writer.setChannel(channel);
					Response response = new Response(writer);
					data.remove(key);
					httpRequestProcessor.register(key, request, response);
				}
			} else {
				close(key);
			}
		} catch (HTTPException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, e.getMessage(), e);
			}
			writer.setChannel(channel);
			try {
				HTTPStatusCodes.sendError(writer, e.getErrCode());
				writer.close();
			} catch (IOException e1) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.log(Level.WARNING, e1.getMessage(), e1);
				}
			}
			close(key);
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, e.getMessage(), e);
			}
			close(key);
		} finally {

		}

	}

	@Override
	public void write(SelectionKey key) {

	}

	public HTTPCallback getCallback() {
		return callback;
	}

	public void setCallback(HTTPCallback callback) {
		this.callback = callback;
		callback.setHttpParser(parser);
	}

	@Override
	public void init() {
		reader = new ByteBufferASCIIReader(ByteBuffer
				.allocate(recvBufferSize == null ? 8192 : recvBufferSize));
		writer = new ByteBufferWriter(ByteBuffer
				.allocate(sendBufferSize == null ? 8192 : sendBufferSize));
		httpRequestProcessor.setReader(reader);
		httpRequestProcessor.setWriter(writer);
	}

	public Integer getSendBufferSize() {
		return sendBufferSize;
	}

	public void setSendBufferSize(Integer sendBufferSize) {
		this.sendBufferSize = sendBufferSize;
	}

	public Integer getRecvBufferSize() {
		return recvBufferSize;
	}

	public void setRecvBufferSize(Integer recvBufferSize) {
		this.recvBufferSize = recvBufferSize;
	}

	public Boolean getTcpNoDelay() {
		return tcpNoDelay;
	}

	public void setTcpNoDelay(Boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
	}

	public Boolean getSoLinger() {
		return soLinger;
	}

	public void setSoLinger(Boolean soLinger) {
		this.soLinger = soLinger;
	}

	public Integer getSoLingerTime() {
		return soLingerTime;
	}

	public void setSoLingerTime(Integer soLingerTime) {
		this.soLingerTime = soLingerTime;
	}

	public Integer getSoTimeout() {
		return soTimeout;
	}

	public void setSoTimeout(Integer soTimeout) {
		this.soTimeout = soTimeout;
	}

	public Boolean getKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(Boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	@Override
	public void connect(SelectionKey key) {
		throw new IllegalStateException();
	}

	public HTTPRequestProcessor getHttpRequestProcessor() {
		return httpRequestProcessor;
	}

	public void setHttpRequestProcessor(
			HTTPRequestProcessor httpRequestProcessor) {
		this.httpRequestProcessor = httpRequestProcessor;
	}

	@Override
	public boolean isFree(SelectionKey key) {
		return true;
	}

	@Override
	public boolean isService(SelectionKey key) {
		return true;
	}

	public Integer getRequestBufferSize() {
		return requestBufferSize;
	}

	public void setRequestBufferSize(Integer requestBufferSize) {
		this.requestBufferSize = requestBufferSize;
	}

}
