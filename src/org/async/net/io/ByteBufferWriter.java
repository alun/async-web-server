package org.async.net.io;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.WritableByteChannel;

public class ByteBufferWriter extends Writer {
	protected ByteBuffer buffer;
	protected WritableByteChannel channel;

	public ByteBufferWriter(ByteBuffer buffer) {
		this(buffer, null);
	}

	public ByteBufferWriter(ByteBuffer buffer, WritableByteChannel channel) {
		super();
		this.buffer = buffer;
		this.channel = channel;
	}

	@Override
	public void close() throws IOException {
		flush();
		channel.close();
	}

	@Override
	public void flush() throws IOException {
		if (channel.isOpen()) {
				buffer.flip();
				while (buffer.remaining() > 0) {
					channel.write(buffer);
				}
		}
		buffer.clear();
	}

	public void clear() {
		buffer.clear();
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		if (channel.isOpen()) {
			for (int i = 0; i < len; i++) {
				char ch = cbuf[off + i];
				if (buffer.remaining() < 4) {
					flush();
				}
				// http://www.j2meforums.com/wiki/index.php/UTF-8_Encoder/Decoder
				int c = 0;
				c |= (ch & 0xffff);
				if (c >= 0 && c < 0x80) {
					buffer.put((byte) (c & 0xff));
				} else if (c > 0x7f && c < 0x800) {
					buffer.put((byte) (((c >>> 6) & 0x1f) | 0xc0));
					buffer.put((byte) (((c >>> 0) & 0x3f) | 0x80));
				} else if (c > 0x7ff && c < 0x10000) {
					buffer.put((byte) (((c >>> 12) & 0x0f) | 0xe0));
					buffer.put((byte) (((c >>> 6) & 0x3f) | 0x80));
					buffer.put((byte) (((c >>> 0) & 0x3f) | 0x80));
				} else if (c > 0x00ffff && c < 0xfffff) {
					buffer.put((byte) (((c >>> 18) & 0x07) | 0xf0));
					buffer.put((byte) (((c >>> 12) & 0x3f) | 0x80));
					buffer.put((byte) (((c >>> 6) & 0x3f) | 0x80));
					buffer.put((byte) (((c >>> 0) & 0x3f) | 0x80));
				}
			}
		} else {
			throw new ClosedChannelException();
		}
	}

	public void write(byte[] src) throws IOException {
		int rem=src.length;
		while(rem>0) {
			int c = rem>buffer.remaining()?buffer.remaining():rem;
			rem-=c;
			buffer.put(src,0,c);
			flush();
		}
	}

	public WritableByteChannel getChannel() {
		return channel;
	}

	public void setChannel(WritableByteChannel channel) {
		this.channel = channel;
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}


}
