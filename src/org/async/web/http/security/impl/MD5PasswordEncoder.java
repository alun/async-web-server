package org.async.web.http.security.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.async.web.http.security.PasswordEncoder;

public class MD5PasswordEncoder implements PasswordEncoder {

	@Override
	public String encrypt(String receivedPassword, String salt) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte buf[] = (receivedPassword + salt).getBytes();
			byte[] md5buf = md5.digest(buf);
			StringBuilder result = new StringBuilder(2 * md5buf.length);
			for (int i = 0; i < md5buf.length; i++) {
				result.append("0" + Integer.toHexString((0xff & md5buf[i])));
			}
			return result.toString();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	@Override
	public boolean isMatches(String receivedPassword, String password,
			String salt) {
		return encrypt(receivedPassword, salt).equals(password);
	}
}
