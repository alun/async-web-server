package org.async.web.utils;

import java.util.HashMap;

public class IgnoreCaseHashMap<V> extends HashMap<String, V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6917989846069350056L;

	public IgnoreCaseHashMap() {
		super();
	}

	public V put(String key, V value) {
		return super.put(key.toUpperCase(), value);
	}

	@Override
	public V get(Object key) {
		if (key instanceof String) {
			return super.get(((String) key).toUpperCase());
		} else {
			return super.get(key);
		}
	}

}
