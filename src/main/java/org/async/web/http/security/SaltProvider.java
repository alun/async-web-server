package org.async.web.http.security;

public interface SaltProvider {

	String getSalt(User user);
}
