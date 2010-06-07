package org.async.web.templates;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.async.json.conf.Initializable;

public class TemplateProcessor implements TemplateExecutor, Initializable {
	private static Logger logger=Logger.getLogger("org.async.web.templates.TemplateProcessor");
	private Map<String, Template> templates = new HashMap<String, Template>();
	private static int NOT_SET = -1;
	private static int WRITE_STATIC_DATA = 0;
	private static int TAG = 1;
	private static int SCRIPT = 2;
	private static int EXP = 3;
	private static int INCLUDE = 4;
	private String templatesDir;
	private String resultDir;
	private String ext;

	public void render(String name, Writer writer, Object data)
			throws IOException {
		Template template = templates.get(name);
		template.render(this, writer, data);
	}

	private Class<?> compile(File src, File dest) throws FileNotFoundException,
			IOException, MalformedURLException, ClassNotFoundException {
		if(logger.isLoggable(Level.CONFIG)) {
			logger.config("Compiling Template "+src.getAbsolutePath()+" to "+dest.getAbsolutePath());
		}
		Reader reader = new FileReader(src);
		Writer writer = new FileWriter(dest);
		StringBuilder include = new StringBuilder();
		char[] t = new char[2];
		int state = NOT_SET;
		reader.read(t);
		if (t[0] == '<' && t[1] == '%') {
			t[0] = t[1];
			reader.read(t, 1, 1);
			if (t[1] == '@') {
				t[0] = t[1] = ' ';
				while (!(t[0] == '%' && t[1] == '>')) {
					writer.write(t[0]);
					t[0] = t[1];
					reader.read(t, 1, 1);
				}
				t[0] = t[1] = ' ';
			} else {
				state = TAG;
				t[1] = ' ';
			}
		}
		writer.write("import java.io.Writer;\n");
		writer.write("import org.async.web.templates.Template;\n");
		writer.write("import org.async.web.templates.TemplateExecutor;\n");
		writer.write("import java.io.IOException;\n\n");

		String className = dest.getName().substring(
				0,
				dest.getName().indexOf('.') > 0 ? dest.getName()
						.indexOf('.') : dest.getName().length() - 1);
		writer.write("public class "
				+ className
				+ " implements Template {\n\n");
		writer
				.write("public void render(TemplateExecutor executor,Writer writer,Object data) throws IOException {\n");
		do {
			if (t[0] == '<' && t[1] == '%') {
				if (state == 0)
					writer.write("\");\n");
				state = TAG;
				t[1] = ' ';
			} else if (t[0] == '<' && t[1] == '#') {
				if (state == 0)
					writer.write("\");\n");
				t[0] = t[1] = ' ';
				while (!(t[0] == '#' && t[1] == '>')) {
					include.append(t[0]);
					t[0] = t[1];
					reader.read(t, 1, 1);
				}
				t[0] = t[1] = ' ';
				String inc = include.toString().trim();
				writer.write("executor.render("
						+ inc.substring(0, inc.indexOf(".") > 0 ? inc
								.indexOf(".") : inc.length() - 1)
						+ ",writer,data);\n");
				state = NOT_SET;
			} else if (state > 0 && t[0] == '%' && t[1] == '>') {
				if (state == EXP) {
					writer.write(".toString());");
				}
				t[1] = ' ';
				state = NOT_SET;
				writer.write('\n');
			} else if (state == TAG) {
				state = (t[1] == '=') ? EXP : SCRIPT;
				if (state == EXP) {
					writer.write("writer.write(");
				}
				t[1] = ' ';
			} else if (state == INCLUDE) {
				include.append(t[0]);
			} else if (state > TAG) {
				writer.write(t[0]);
			} else {
				if (state == NOT_SET) {
					state = WRITE_STATIC_DATA;
					writer.write("writer.write(\"");
				}
				if (t[0] == '"' || t[0] == '\n')
					writer.write('\\');
				if (t[0] == '\n')
					t[0] = 'n';
				writer.write(t[0]);
			}
			t[0] = t[1];
		} while (reader.read(t, 1, 1) != -1);
		if (state == 0) {
			writer.write("\");\n");
		}
		writer.write("}\n\n}");
		writer.flush();
		writer.close();
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		ByteArrayOutputStream err = new ByteArrayOutputStream(8192);
		ByteArrayOutputStream out = new ByteArrayOutputStream(8192);
		compiler.run(new ByteArrayInputStream(new byte[]{}), out,err, dest.getAbsolutePath());
		if(logger.isLoggable(Level.INFO)) {
			logger.info(new String(out.toByteArray()));
		}
		if(logger.isLoggable(Level.SEVERE)) {
			logger.severe(new String(err.toByteArray()));
		}
		URL url = new URL("file", "", dest.getParent() + "/");
		URL[] urls = new URL[] { url };
		ClassLoader cl = new URLClassLoader(urls);
		Class<?> cls = cl.loadClass(dest.getName().substring(0,
				dest.getName().lastIndexOf('.')));
		return cls;
	}

	@Override
	public void init() {
		File[] files = new File(templatesDir).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(ext);
			}
		});
		if (files != null) {
			for (File file : files) {
				try {
					String name = file.getName().substring(0,
							file.getName().lastIndexOf('.'));
					String templateName = "Template"
							+ name.substring(0, 1).toUpperCase()
							+ name.substring(1);
					Class<?> cls = compile(file, new File(new File(resultDir)
							.getAbsolutePath()
							+ "/" + templateName + ".java"));
					Template template = (Template) cls.newInstance();
					templates.put(name, template);
				} catch (Exception e) {
					if(logger.isLoggable(Level.SEVERE)) {
						logger.log(Level.SEVERE,e.getMessage(),e);
					}
				}
			}
		}

	}

	public String getTemplatesDir() {
		return templatesDir;
	}

	public void setTemplatesDir(String templatesDir) {
		this.templatesDir = templatesDir;
	}

	public String getResultDir() {
		return resultDir;
	}

	public void setResultDir(String resultDir) {
		this.resultDir = resultDir;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

}
