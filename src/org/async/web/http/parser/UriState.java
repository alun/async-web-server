package org.async.web.http.parser;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;

public class UriState implements State {
	protected int innerState;
	private static int MAX_URI_LENGTH = 4096;

	@Override
	public int run(Reader reader, char[] tail, CharArrayWriter builder,
			Callback callback, int innerState) throws IOException {
		// TODO GET
		while (reader.read(tail) > 0) {
			if ((tail[0] > 32 && tail[0] < 63)
					|| (tail[0] > 63 && tail[0] < 128)) {
				builder.append(tail[0]);
				if (builder.size() > MAX_URI_LENGTH) {
					throw new IllegalArgumentException();
				}
			} else if (tail[0] != ' ' && tail[0] != 63) {
				throw new IllegalArgumentException();
			} else {
				callback.uri(builder.toString());
				builder.reset();
				return tail[0] == 63 ? RootParser.QUERY_PARAM_NAME
						: RootParser.PROTOCOL_VERSION;
			}
		}
		return RootParser.RESUME;
	}

	@Override
	public int getInnerState() {
		return innerState;
	}

}
