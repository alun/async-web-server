package org.async.net.io;

import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;

public class ByteBufferUTF8Reader extends Reader {
	private ByteBuffer buffer;
	private int pos = 0;
	private int spins = 0;
	private char c;

	public ByteBufferUTF8Reader(ByteBuffer buffer) {
		super();
		this.buffer = buffer;
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		// http://www.j2meforums.com/wiki/index.php/UTF-8_Encoder/Decoder
		byte[] ar = buffer.array();
		len = (buffer.limit() - pos > len) ? len : buffer.limit() - pos;
		int e = pos + len;
		int i;
		if (len > 0) {
			for (i = 0; i < len && pos < e; pos++) {
				if(spins>0) {
					spins--;
					c |= ((ar[pos] & 0x3f) << (spins*6));
				} else	if ((ar[pos] & 0x80) == 0) {
					c = (char) ar[pos];
					spins=0;
				} else if ((ar[pos] & 0xe0) == 0xc0) {
					c |= ((ar[pos] & 0x1f) << 6);
					spins=1;
				} else if ((ar[pos] & 0xf0) == 0xe0) {
					c |= ((ar[pos] & 0x0f) << 12);
					spins=2;
				} else if ((ar[pos] & 0xf8) == 0xf0) {
					c |= ((ar[pos] & 0x07) << 18);
					spins=3;
				} else {
					spins=0;
					c = '?';
				}
				if(spins==0) {
					cbuf[i]=c;
					i++;
					c=0;
				}
			}
			return i;
		}
		return 0;

	}

	@Override
	public void reset() throws IOException {
		pos = 0;
		buffer.rewind();
	}

}
