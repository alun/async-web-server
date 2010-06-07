package org.async.web.http.core;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HTTPUtils {
	private static final String DOMAIN = "domain=";
	private static final String PATH = "path=";
	private static final String EXPIRES = "expires=";
	private static final char SEMICOLON = ';';
	private static final char EQ = '=';
	private static final String SET_COOKIE = "Set-Cookie: ";
	private static final String CLRF = "\r\n";
	private static final DateFormat expiresFormatter = new SimpleDateFormat("E, dd-MMM-yyyy HH:mm:ss z",Locale.ENGLISH);//TODO create more faster class for this operation
	public static void setCookie(Writer writer,String name,String value,Date expires,String path,String domain) throws IOException {
		//TODO encode strings
		writer.write(SET_COOKIE);
		writer.write(name);
		writer.write(EQ);
		writer.write(value);
		if(expires!=null) {
			writer.write(SEMICOLON);
			writer.write(EXPIRES);
			writer.write(expiresFormatter.format(expires));
		}
		if(path!=null) {
			writer.write(SEMICOLON);
			writer.write(PATH);
			writer.write(path);
		}
		if(domain!=null) {
			writer.write(SEMICOLON);
			writer.write(DOMAIN);
			writer.write(domain);
		}
		writer.write(CLRF);
	}
}

