package org.async.web.http.security.impl;

import org.async.web.http.security.SaltProvider;
import org.async.web.http.security.User;

public class DummySaltProvider implements SaltProvider {
	private String secret;

	@Override
	public String getSalt(User user) {
		return secret;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

}
