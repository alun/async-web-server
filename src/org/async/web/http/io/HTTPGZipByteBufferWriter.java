package org.async.web.http.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.GZIPOutputStream;

import org.async.net.io.ByteBufferWriter;

public class HTTPGZipByteBufferWriter extends ByteBufferWriter {
	private ByteArrayOutputStream baos;
	//TODO may be use ThreadLocal for baos buffer
	public HTTPGZipByteBufferWriter(ByteBufferWriter writer) {
		super(writer.getBuffer(), writer.getChannel());
		baos = new ByteArrayOutputStream(buffer
				.position());
	}

	private ByteBuffer size = ByteBuffer.allocate(8);

	@Override
	public void flush() throws IOException {
		baos.reset();
		buffer.flip();
		GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
		gzipOut.write(buffer.array(), 0, buffer.limit());
		gzipOut.finish();
		size.clear();
		size.put(Integer.toHexString(baos.size()).getBytes());
		size.put((byte) '\r');
		size.put((byte) '\n');
		size.flip();
		while (size.remaining() > 0) {
			channel.write(size);
		}
		buffer.clear();
		buffer.put(baos.toByteArray());
		baos.flush();
		buffer.flip();
		while (buffer.remaining() > 0) {
			channel.write(buffer);
		}
		size.clear();
		buffer.clear();
	}
}
