package org.async.web.http.parser;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

public class ProtocolVersionState implements State{
	protected int innerState;
	private static final int PROTOCOL_VERSION_LENGHT = 8;
	protected Set<Character> allowedChars = new HashSet<Character>();
	{
		allowedChars.add('H');
		allowedChars.add('T');
		allowedChars.add('P');
		allowedChars.add('1');
		allowedChars.add('0');
		allowedChars.add('/');
		allowedChars.add('.');
	}

	@Override
	public int run(Reader reader, char[] tail, CharArrayWriter builder,
			Callback callback, int innerState) throws IOException {
		while (reader.read(tail) > 0) {
			if (allowedChars.contains(tail[0])) {
				builder.append(tail[0]);
				if (builder.size() > PROTOCOL_VERSION_LENGHT) {
					throw new IllegalArgumentException();
				}
			} else if (tail[0] != '\r') {
				throw new IllegalArgumentException();
			} else {
				callback.protocol(builder.toString());
				builder.reset();
				return RootParser.RF;
			}
		}
		return RootParser.RESUME;
	}

	@Override
	public int getInnerState() {
		return innerState;
	}

}
