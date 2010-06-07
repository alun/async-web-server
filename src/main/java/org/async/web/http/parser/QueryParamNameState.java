package org.async.web.http.parser;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;

public class QueryParamNameState implements State {
	protected int innerState;
	private static int MAX_QUERY_PARAM_NAME_LENGTH = 64;

	@Override
	public int run(Reader reader, char[] tail, CharArrayWriter builder,
			Callback callback, int innerState) throws IOException {
		while (reader.read(tail) > -1) {
			if (QueryStringChars.getAllowedChars().contains(tail[0])) {
				if (tail[0] == '=') {
					callback.queryParamName(builder.toString());
					builder.reset();
					return RootParser.QUERY_PARAM_VALUE;
				} else {
					if (tail[0] != '&') {
						builder.append(tail[0]);
						if (builder.size() > MAX_QUERY_PARAM_NAME_LENGTH) {
							throw new IllegalArgumentException();
						}
					}
				}
			} else {
				builder.reset();
				return RootParser.PROTOCOL_VERSION;
			}
		}
		return RootParser.RESUME;
	}

	@Override
	public int getInnerState() {
		return innerState;
	}

}
