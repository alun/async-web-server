package org.async.web.http.servlet.impl;

import java.io.IOException;
import java.util.List;

import org.async.web.http.core.ConnectionStatus;
import org.async.web.http.core.Request;
import org.async.web.http.core.Response;
import org.async.web.http.servlet.Controller;
import org.async.web.http.servlet.State;
import org.async.web.http.servlet.StateData;

public class ControllerImpl<T extends StateData> implements Controller<T> {
	protected List<State<T>> states;

	public List<State<T>> getStates() {
		return states;
	}

	public void setStates(List<State<T>> states) {
		this.states = states;
	}

	@Override
	public T handle(Request request, Response response,
			ConnectionStatus status, T data) throws IOException {
		State<T> state = states.get(data == null ? 0 : data.getStateIdx());
		try {
			data = state.run(request, response, data, status);
		} catch (IOException e) {
			throw e;
		}
		return data;
	}

	@Override
	public T createStateData() {
		return null;
	}

}
