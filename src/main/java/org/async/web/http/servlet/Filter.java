package org.async.web.http.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.async.web.http.core.ConnectionStatus;
import org.async.web.http.core.Request;
import org.async.web.http.core.Response;
import org.async.web.http.servlet.impl.WildCardPattern;

public abstract class Filter  {

	int idx;
	protected List<WildCardPattern> patterns = null;

	public abstract void filter(Request request, Response response,
			ConnectionStatus status, FilterChain chain) throws IOException;

	public boolean matches(Request request) {
		if (patterns != null) {
			for (WildCardPattern pattern : patterns) {
				if (pattern.matches(request.getUri())) {
					return true;
				}
			}
		}
		return false;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public List<WildCardPattern> getPatterns() {
		return patterns;
	}

	public void setPatterns(List<String> stringPatterns) {
		patterns = new ArrayList<WildCardPattern>(stringPatterns.size());
		for (String s : stringPatterns) {
			patterns.add(new WildCardPattern(s));
		}
	}
}
