package org.async.web.http.parser;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;

public class HeaderNameState implements State {
	protected int innerState;
	private static int MAX_HEADER_NAME_LENGTH = 64;

	@Override
	public int run(Reader reader, char[] tail, CharArrayWriter builder,
			Callback callback, int innerState) throws IOException {
		while (reader.read(tail) > -1) {
			if ((tail[0] >= 'A' && tail[0] <= 'Z')
					|| (tail[0] >= 'a' && tail[0] <= 'z') || (tail[0] == '-')
					|| (tail[0] == '_')) {
				builder.append(tail[0]);
				if (builder.size() > MAX_HEADER_NAME_LENGTH) {
					throw new IllegalArgumentException();
				}
			} else if (tail[0] == ':') {
				callback.headerName(builder.toString());
				builder.reset();
				return RootParser.HEADER_VALUE;
			} else if (tail[0] == '\r') {
				return RootParser.CLRFCLRF;
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
