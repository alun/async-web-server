package org.async.web.http.parser;

import java.util.HashSet;
import java.util.Set;

public class QueryStringChars {

	static Set<Character> allowedChars = new HashSet<Character>();
	static {
		for (char c = 'A'; c <= 'Z'; c++) {
			allowedChars.add(c);
		}
		for (char c = 'a'; c <= 'z'; c++) {
			allowedChars.add(c);
		}
		for (char c = '0'; c <= '9'; c++) {
			allowedChars.add(c);
		}
		allowedChars.add('?');
		allowedChars.add('/');
		allowedChars.add(':');
		allowedChars.add('@');
		allowedChars.add('!');
		allowedChars.add('$');
		allowedChars.add('&');
		allowedChars.add('\'');
		allowedChars.add('(');
		allowedChars.add(')');
		allowedChars.add('*');
		allowedChars.add('+');
		allowedChars.add(',');
		allowedChars.add(';');
		allowedChars.add('=');
		allowedChars.add('-');
		allowedChars.add('_');
		allowedChars.add('.');
		allowedChars.add('~');
		allowedChars.add('%');
	}

	public static Set<Character> getAllowedChars() {
		return allowedChars;
	}
}
