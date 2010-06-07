package org.async.web.http.parser;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;

public class HeaderValueState implements State{
	protected int innerState;
	private static int MAX_HEADER_VALUE_LENGTH=4096;
	@Override
	public int run(Reader reader, char[] tail, CharArrayWriter builder, Callback callback, int innerState) throws IOException {
		while(reader.read(tail)>-1) {
			if(tail[0]!='\r') {
				builder.append(tail[0]);
				if(builder.size()>MAX_HEADER_VALUE_LENGTH) {
					throw new IllegalArgumentException();
				}
			} else {
				String str = builder.toString();
				if(str.charAt(0)!=' ') {
					throw new IllegalArgumentException();
				}
				callback.headerValue(str.substring(1,builder.size()));
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
