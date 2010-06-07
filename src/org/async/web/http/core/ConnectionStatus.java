package org.async.web.http.core;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.async.net.ChannelProcessor;
import org.async.net.ClearableChannelProcessor;

public class ConnectionStatus {
	private SelectionKey key;

	public ConnectionStatus(SelectionKey key) {
		super();
		this.key = key;
	}

	public boolean isAlive() {
		SocketChannel channel = (SocketChannel) key.channel();
		return channel.isOpen();
	}

	public void resume() {
		key.interestOps(SelectionKey.OP_WRITE);

	}

	public void pause() {
		key.interestOps(SelectionKey.OP_READ);
	}

	public void stop() {
		ChannelProcessor channelProcessor = (ChannelProcessor) key.attachment();
		channelProcessor.close(key);
	}

	public void clear() {
		ChannelProcessor channelProcessor = (ChannelProcessor) key.attachment();
		if (channelProcessor instanceof ClearableChannelProcessor) {
			((ClearableChannelProcessor) channelProcessor).clear(key);
		}
	}

	public void change(ChannelProcessor processor) {
		clear();
		key.attach(processor);
	}

	public SelectionKey getKey() {
		return key;
	}
}
