package org.async.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.async.json.conf.Initializable;
import org.async.web.http.core.IdleHandler;

public class Server  implements Runnable,Initializable {
	private static final Logger logger = Logger
			.getLogger("org.async.net.Server");
	protected Selector selector;
	protected Map<String, ChannelProcessor> bind;
	protected int timeout = 0;
	protected int shutdownTimeout = 60000;
	protected List<IdleHandler> idleHandlers;
	protected long shutdownStart = 0;

	@Override
	public void init() {
		for (Entry<String, ChannelProcessor> entry : bind.entrySet()) {
			try {
				ServerSocketChannel serverChannel = ServerSocketChannel.open();
				serverChannel.configureBlocking(false);
				String key = entry.getKey();
				int i = key.indexOf(':');
				String host = key.substring(0, i);
				int port = Integer.parseInt(key.substring(i + 1));
				InetSocketAddress isa = new InetSocketAddress(host, port);
				serverChannel.socket().bind(isa);
				SelectionKey serverKey = serverChannel.register(selector,
						serverChannel.validOps());
				serverKey.attach(entry.getValue());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public Server() {
		super();
		try {
			this.selector = SelectorProvider.provider().openSelector();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void run() {
		try {
			while (true) {
				if ((selector.select(timeout)) > 0) {
					Set<SelectionKey> keys = selector.selectedKeys();
					Iterator<SelectionKey> i = keys.iterator();
					while (i.hasNext()) {
						SelectionKey key = i.next();
						i.remove();
						if (!key.isValid()) {
							continue;
						}
						ChannelProcessor channelProcessor = (ChannelProcessor) key
								.attachment();
						if (key.isAcceptable()) {
							channelProcessor.accept(key);
						} else if (key.isReadable()) {
							channelProcessor.read(key);
						} else if (key.isWritable()) {
							channelProcessor.write(key);
						} else if (key.isConnectable()) {
							channelProcessor.connect(key);
						}
					}
				}
				if (shutdownStart > 0) {
					if (doShutdown()
							|| System.currentTimeMillis() - shutdownStart > shutdownTimeout) {
						if (logger.isLoggable(Level.INFO)) {
							logger.info("Bye ;)");
							return;
						}
					}
				} else {
					if (idleHandlers != null) {
						for (IdleHandler handler : idleHandlers) {
							handler.onIdle();
						}
					}
				}

			}
		} catch (Exception e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}

	private boolean doShutdown() {
		boolean onlyServices = true;
		Iterator<SelectionKey> iterator = selector.keys().iterator();

		while (iterator.hasNext()) {
			try {
				SelectionKey key = iterator.next();
				SelectableChannel channel = key.channel();
				ChannelProcessor cp = (ChannelProcessor) key.attachment();
				if (channel instanceof ServerSocketChannel) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Stopping "+cp.getClass()+" channel");
					}
					channel.close();
					key.cancel();
				} else if (cp.isFree(key) && !cp.isService(key)) {
					try {
						if (logger.isLoggable(Level.INFO)) {
							logger.info("Stopping service "+cp.getClass());
						}
						cp.close(key);
					} catch (Exception e) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.log(Level.WARNING, e.getMessage(), e);
						}
					}
				} else if (!cp.isService(key)) {
					onlyServices = false;
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Not finished connection found "+cp.getClass()+", hold services active");
					}
				}
				cp.close(key);
			} catch (Exception e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}

		if (onlyServices) {
			iterator = selector.keys().iterator();
			while (iterator.hasNext()) {
				SelectionKey key = iterator.next();
				ChannelProcessor cp = (ChannelProcessor) key.attachment();
				if (cp.isFree(key)) {
					try {
						cp.close(key);
					} catch (Exception e) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.log(Level.WARNING, e.getMessage(), e);
						}
					}
				}
			}
		}

		return selector.keys().isEmpty();
	}

	public Selector getSelector() {
		return selector;
	}

	public Map<String, ChannelProcessor> getBind() {
		return bind;
	}

	public void setBind(Map<String, ChannelProcessor> bind) {
		this.bind = bind;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public List<IdleHandler> getIdleHandlers() {
		return idleHandlers;
	}

	public void setIdleHandlers(List<IdleHandler> idleHandlers) {
		this.idleHandlers = idleHandlers;
	}

	public void shutdown() {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Shutdown Signal");
		}
		setTimeout(10);
		this.shutdownStart = System.currentTimeMillis();
		selector.wakeup();
	}

	public int getShutdownTimeout() {
		return shutdownTimeout;
	}

	public void setShutdownTimeout(int shutdownTimeout) {
		this.shutdownTimeout = shutdownTimeout;
	}

	public long getShutdownStart() {
		return shutdownStart;
	}

	public void setShutdownStart(long shutdownStart) {
		this.shutdownStart = shutdownStart;
	}

}
