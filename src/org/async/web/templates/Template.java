package org.async.web.templates;

import java.io.IOException;
import java.io.Writer;

public interface Template {
	void render(TemplateExecutor executor,Writer writer,Object data) throws IOException;
}
