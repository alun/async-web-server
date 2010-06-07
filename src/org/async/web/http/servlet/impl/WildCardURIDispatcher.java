package org.async.web.http.servlet.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.async.json.conf.Initializable;
import org.async.web.http.servlet.Controller;
import org.async.web.http.servlet.StateData;

public class WildCardURIDispatcher extends URIDispatcherImpl implements
		Initializable {

	List<WildCardPattern> patterns;

	@Override
	public void init() {
		Set<String> keySet = getMapping().keySet();
		patterns = new ArrayList<WildCardPattern>(keySet.size());
		for (String key : keySet) {
			if (key.indexOf('*') >= 0) {
				patterns.add(new WildCardPattern(key));
			}
		}
		Collections.sort(patterns);
	}

	@Override
	public Controller<StateData> dispatch(String uri) {
		Controller<StateData> c = getMapping().get(uri);
		if (c != null) {
			return c;
		}
		for (WildCardPattern pattern : patterns) {
			if (pattern.matches(uri)) {
				return super.dispatch(pattern.getPattern());
			}
		}
		return null;
	}
}
