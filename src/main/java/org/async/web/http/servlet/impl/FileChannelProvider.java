package org.async.web.http.servlet.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileChannelProvider {
	private static Logger logger = Logger
			.getLogger("org.async.web.http.servlet.impl.FileChannelProvider");

	private class ChannelInfo {
		FileChannel channel;
		int refs = 0;
	}

	private Map<String, ChannelInfo> channels = new HashMap<String, ChannelInfo>();

	public FileChannel getFileChannel(String uri) throws FileNotFoundException {
		ChannelInfo channelInfo = channels.get(uri);
		if (channelInfo == null) {
			channelInfo = new ChannelInfo();
			File f = new File(uri);
			channelInfo.channel = new FileInputStream(f).getChannel();
			channels.put(uri, channelInfo);
		}
		channelInfo.refs++;
		return channelInfo.channel;
	}

	public void closeFileChannel(String uri) {
		ChannelInfo channelInfo = channels.get(uri);
		if (channelInfo != null) {
			if (--channelInfo.refs == 0) {
				try {
					channelInfo.channel.close();
				} catch (IOException e) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.log(Level.WARNING, e.getMessage(), e);
					}
				}
				channels.remove(uri);
			}
		}
	}

}
