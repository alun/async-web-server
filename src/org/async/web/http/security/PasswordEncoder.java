package org.async.web.http.security;

public interface PasswordEncoder {
	boolean isMatches(String receivedPassword, String password, String salt);

	String encrypt(String receivedPassword, String salt);
}
