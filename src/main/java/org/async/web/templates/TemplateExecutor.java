package org.async.web.templates;

import java.io.IOException;
import java.io.Writer;

public interface TemplateExecutor {
	public void render(String name, Writer writer, Object data) throws IOException ;
}
