package org.async.web.http.parser;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;

public class LastRFState extends RFState {
	
	@Override
	public int run(Reader reader, char[] tail, CharArrayWriter builder,
			Callback callback, int innerState) throws IOException {
		int rs=super.run(reader, tail, builder, callback, innerState);
		return rs==RootParser.HEADER_NAME?RootParser.PARSED:rs;
	}

}
