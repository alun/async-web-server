package org.async.web.http.parser;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;

public class RFState implements State {
	protected int innerState;

	@Override
	public int run(Reader reader, char[] tail, CharArrayWriter builder,
			Callback callback, int innerState) throws IOException {
		while (reader.read(tail) > 0) {
			if (tail[0] == '\n') {
				return RootParser.HEADER_NAME;
			} else {
				throw new IllegalArgumentException();
			}
		}
		return RootParser.RESUME;
	}

	@Override
	public int getInnerState() {
		return innerState;
	}

}
