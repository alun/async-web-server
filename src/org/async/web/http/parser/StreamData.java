package org.async.web.http.parser;

import java.io.CharArrayWriter;

public class StreamData {
	char[] tail = new char[1];
	int stateIdx = 0;
	CharArrayWriter builder;

	public StreamData(int size) {
		super();
		builder = new CharArrayWriter(4096);
	}

	public char[] getTail() {
		return tail;
	}

	public void setTail(char[] tail) {
		this.tail = tail;
	}

	public int getStateIdx() {
		return stateIdx;
	}

	public void setStateIdx(int stateIdx) {
		this.stateIdx = stateIdx;
	}

	public CharArrayWriter getBuilder() {
		return builder;
	}

	public void setBuilder(CharArrayWriter builder) {
		this.builder = builder;
	}

}
