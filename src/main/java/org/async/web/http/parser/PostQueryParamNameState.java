package org.async.web.http.parser;

import java.io.CharArrayWriter;
import java.io.Reader;

public class PostQueryParamNameState implements State {

	protected int innerState;

	@Override
	public int run(Reader reader, char[] tail, CharArrayWriter builder,
			Callback callback, int st) throws Exception {
		this.innerState = st;
		while (reader.read(tail) > -1) {
			innerState--;
			if (QueryStringChars.getAllowedChars().contains(tail[0])) {
				
				if (tail[0] == '=') {
					callback.queryParamName(builder.toString());
					builder.reset();
 					if (innerState == 0)
						return RootParser.PARSED;
 					else
						return RootParser.POST_QUERY_PARAM_VALUE+(innerState<<8);
				} else {
					if (tail[0] != '&')
						builder.append(tail[0]);
					if (innerState == 0 ) {
						callback.queryParamName(builder.toString());
						builder.reset();
						return RootParser.PARSED;
					}
				}
			} else {
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
