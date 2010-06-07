package org.async.web.http.servlet.impl;

import java.util.Map;

import org.async.web.http.servlet.Controller;
import org.async.web.http.servlet.StateData;
import org.async.web.http.servlet.URIDistpatcher;

public class URIDispatcherImpl implements URIDistpatcher {

	// TODO add Host support
	private Map<String, Controller<StateData>> mapping = null;

	@Override
	public Controller<StateData> dispatch(String uri) {
		return mapping.get(uri);
	}

	public Map<String, Controller<StateData>> getMapping() {
		return mapping;
	}

	public void setMapping(Map<String, Controller<StateData>> mapping) {
		this.mapping = mapping;

	}
	
	
}
