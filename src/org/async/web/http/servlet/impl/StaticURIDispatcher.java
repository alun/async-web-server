package org.async.web.http.servlet.impl;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import org.async.json.conf.Initializable;
import org.async.web.http.servlet.Controller;
import org.async.web.http.servlet.StateData;
import org.async.web.http.servlet.URIDistpatcher;

public class StaticURIDispatcher implements URIDistpatcher, Initializable {

	private Controller<StateData> fileController = null;
	private Controller<StateData> autoIndexController = null;
	private String documentRoot = "";
	private Map<String, String> mimeTypes;
	private boolean autoIndex = false;

	@Override
	public Controller<StateData> dispatch(String uri) {
		File f = new File(documentRoot + uri);
		if (f.exists()) {
			return f.isDirectory() ? autoIndexController : fileController;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		if (autoIndexController == null && autoIndex) {
			autoIndexController = new AutoIndexController();
			((AutoIndexController) autoIndexController)
					.setDocumentRoot(documentRoot);
		}
		if (fileController == null) {
			fileController = new ControllerImpl<StateData>();
			HeaderState headerState = new HeaderState();
			headerState.setDocumentRoot(documentRoot);
			headerState.setMimeTypes(mimeTypes);
			headerState.setProvider(new FileChannelProvider());
			((ControllerImpl) fileController).setStates(Arrays.asList(
					headerState, new BodyState()));
		}

	}

	public String getDocumentRoot() {
		return documentRoot;
	}

	public void setDocumentRoot(String documentRoot) {
		this.documentRoot = documentRoot;
	}

	public Controller<StateData> getAutoIndexController() {
		return autoIndexController;
	}

	public void setAutoIndexController(Controller<StateData> autoIndexController) {
		this.autoIndexController = autoIndexController;
	}

	public Controller<StateData> getFileController() {
		return fileController;
	}

	public void setFileController(Controller<StateData> fileController) {
		this.fileController = fileController;
	}

	public Map<String, String> getMimeTypes() {
		return mimeTypes;
	}

	public void setMimeTypes(Map<String, String> mimeTypes) {
		this.mimeTypes = mimeTypes;
	}

	public boolean isAutoIndex() {
		return autoIndex;
	}

	public void setAutoIndex(boolean autoIndex) {
		this.autoIndex = autoIndex;
	}

}
