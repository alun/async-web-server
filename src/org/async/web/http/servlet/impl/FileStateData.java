package org.async.web.http.servlet.impl;

import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

import org.async.web.http.servlet.StateData;

public class FileStateData implements StateData {

	public static int HEADERS = 0;
	public static int BODY = 1;
	public int state = HEADERS;
	public long from;
	public long to;
	public FileChannel channel;
	public WritableByteChannel out;
	public FileChannelProvider provider;
	public String path;

	@Override
	public int getStateIdx() {
		return state;
	}

	@Override
	public void close() {
		provider.closeFileChannel(path);
		
	}

	
}
