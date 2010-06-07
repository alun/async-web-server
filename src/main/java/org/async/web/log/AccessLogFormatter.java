package org.async.web.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class AccessLogFormatter extends Formatter {
	Date date = new Date();
	SimpleDateFormat format;

	public AccessLogFormatter(String dateFormat) {
		super();
		this.format = new SimpleDateFormat(dateFormat);
	}

	@Override
	public String format(LogRecord record) {
		StringBuffer buffer = new StringBuffer();
		date.setTime(record.getMillis());
		buffer.append(format.format(date));
		buffer.append(' ');
		buffer.append(record.getMessage());
		buffer.append('\n');
		return buffer.toString();
	}

}
