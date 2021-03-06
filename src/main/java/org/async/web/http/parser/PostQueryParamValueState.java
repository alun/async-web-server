package org.async.web.http.parser;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;

public class PostQueryParamValueState implements State {

	protected int innerState;

	@Override
	public int run(Reader reader, char[] tail, CharArrayWriter builder,
			Callback callback, int st) throws IOException {
		this.innerState = st;
		while (reader.read(tail) > -1) {
			innerState--;
			if (QueryStringChars.getAllowedChars().contains(tail[0])) {
				if (tail[0] == '&') {
					callback.queryParamValue(builder.toString());
					builder.reset();
					if (innerState == 0)
						return RootParser.PARSED;
					else
						return RootParser.POST_QUERY_PARAM_NAME+(innerState<<8);
				} else {
					builder.append(tail[0]);
					if (innerState == 0) {
						callback.queryParamValue(builder.toString());
						builder.reset();
						return RootParser.PARSED;
					}
				}
			} else {
				callback.queryParamValue(builder.toString());
				builder.reset();
				throw new IllegalArgumentException();
			}
		}
		return RootParser.RESUME+(innerState<<8);
	}

	@Override
	public int getInnerState() {
		return innerState;
	}

}
