package org.async.net.io;

import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;

public class ByteBufferASCIIReader extends Reader {
	private ByteBuffer buffer;
	private int pos = 0;


	public ByteBufferASCIIReader(ByteBuffer buffer) {
		super();
		this.buffer = buffer;
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		len = (buffer.limit() - pos > len) ? len : buffer.limit() - pos;
		if (len > 0) {
			for (int i = 0; i < len; i++,pos++) {
				if(buffer.get(pos)>=0x0001&&(buffer.get(pos)<=0x007F)) {
					cbuf[off + i] = (char) buffer.get(pos);
				}
			}
		}
		return len < 1 ? -1 : len;
	}

	@Override
	public void reset() throws IOException {
		pos=0;
		buffer.clear();
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}




}
