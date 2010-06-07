package org.async.web.http.parser;

import java.io.CharArrayWriter;
import java.io.Reader;

public interface State {
	int run(Reader reader,char[] tail,CharArrayWriter builder,Callback callback, int innerState) throws Exception;
//	void setInnerState(int state);
	int getInnerState();
}
