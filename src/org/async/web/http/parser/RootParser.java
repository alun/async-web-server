package org.async.web.http.parser;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;

public class RootParser {

	protected static final int RESUME = 0xFFFF;
	public static final int PARSED = 0xEEEE;
	protected static final int METHOD = 0;
	protected static final int URI = 1;
	protected static final int PROTOCOL_VERSION = 2;
	protected static final int HEADER_NAME = 3;
	protected static final int HEADER_VALUE = 4;
	protected static final int RF = 5;
	protected static final int CLRFCLRF = 6;
	protected static final int QUERY_PARAM_NAME = 7;
	protected static final int QUERY_PARAM_VALUE = 8;
	public static final int POST_QUERY_PARAM_NAME = 9;
	public static final int POST_QUERY_PARAM_VALUE = 10;

	protected State[] states = { new MethodState(), new UriState(),
			new ProtocolVersionState(), new HeaderNameState(),
			new HeaderValueState(), new RFState(), new LastRFState(),
			new QueryParamNameState(), new QueryParamValueState(),
			new PostQueryParamNameState(), new PostQueryParamValueState() };

	public int parse(Reader reader, char[] tail, int stateIdx,
			CharArrayWriter builder, Callback callback) {
		while (true) {
			int innerState = stateIdx>>>16;
			State state = states[stateIdx & 0xFFFF];
			try {
				int next = callback.filter(state.run(reader, tail, builder,
						callback, innerState));
				innerState = next>>>16;
				next = next & 0xFFFF;
				if (next == RESUME) {
					return (stateIdx&0xFFFF)+(innerState<<16);
				} else if (next == PARSED) {
					return PARSED;
				} else {
					stateIdx = next+(innerState<<16);
				}
			} catch (Exception e) {
				callback.error(e);
			}
		}
	}

	public int parse(Reader reader, StreamData data, Callback callback)
			throws IOException {
		return data.stateIdx = parse(reader, data.tail, data.stateIdx,
				data.builder, callback);

	}



}
