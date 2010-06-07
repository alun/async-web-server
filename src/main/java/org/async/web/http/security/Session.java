package org.async.web.http.security;

import java.util.HashMap;
import java.util.Map;

public class Session {
	protected String sid;
	protected long lifetime;
	protected long expire;
	protected String path;

	private Map<String, Object> data;

	public void register(String variable, Object value) {
		if (data == null) {
			data = new HashMap<String, Object>();
		}
		data.put(variable, value);
	}

	public void unregister(String variable) {
		if (data != null) {
			data.remove(variable);
		}
	}

	public String getSid() {
		return sid;
	}

	public void prolongate(long msec) {
		expire = System.currentTimeMillis()
				+ (msec <= lifetime ? msec : lifetime);
	}

	public long getExpire() {
		return expire;
	}

	public boolean isExpired() {
		return expire < System.currentTimeMillis();
	}

	public Object get(String variable) {
		return data == null ? null : data.get(variable);
	}

	public long getLifetime() {
		return lifetime;
	}

	public void setLifetime(long lifetime) {
		this.lifetime = lifetime;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public void setExpire(long expire) {
		this.expire = expire;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	
}
