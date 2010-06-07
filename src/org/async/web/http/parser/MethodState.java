package org.async.web.http.parser;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;

public class MethodState implements State {
	protected int innerState;
	private static int MAX_METHOD_LENGTH = 16;

	@Override
	public int run(Reader reader, char[] tail, CharArrayWriter builder,
			Callback callback, int innerState) throws IOException {
		while (reader.read(tail) > 0) {
			if (tail[0] >= 'A' && tail[0] <= 'Z') {
				builder.append(tail[0]);
				if (builder.size() > MAX_METHOD_LENGTH) {
					throw new IllegalArgumentException();
				}

			} else if (tail[0] != ' ') {
				throw new IllegalArgumentException();
			} else {
				callback.method(builder.toString());
				builder.reset();
				return RootParser.URI;
			}

		}
		return RootParser.RESUME;
	}

	@Override
	public int getInnerState() {
		return innerState;
	}

}
