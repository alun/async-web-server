package org.async.web.http.security;

import java.util.List;

public interface User {
	String getUsername();

	String getEmail();

	String getPassword();

	List<String> getRoles();
	
	boolean is(List<String> roles);
	
}
