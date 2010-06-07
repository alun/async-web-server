package org.async.web.http.servlet.impl;

import java.util.List;

import org.async.json.conf.Initializable;
import org.async.web.http.core.ConnectionStatus;
import org.async.web.http.core.Request;
import org.async.web.http.core.Response;
import org.async.web.http.servlet.Filter;
import org.async.web.http.servlet.FilterChain;

public class FilterChainImpl implements FilterChain, Initializable {

	List<Filter> filters = null;

	@Override
	public Filter initChain(Request request, Response response,
			ConnectionStatus status) {
		return next(request, response, status);
	}

	@Override
	public Filter next(Request request, Response response,
			ConnectionStatus status) {
		Filter filter = request.getFilter();
		request.setFilter(null);
		for (int i = filter == null ? 0 : filter.getIdx()+1, e = filters.size(); i < e; i++) {
			Filter f = filters.get(i);
			if (f.matches(request)) {
				request.setFilter(f);
				return f;
			}
		}
		return null;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	@Override
	public void init() {
		for (int i = 0, e = filters.size(); i < e; i++) {
			filters.get(i).setIdx(i);
		}
	}
}
