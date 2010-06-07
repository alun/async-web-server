package org.async.web.http.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

public class Cookie {
	private static final String COOKIE_DATE_FORMAT = "EEEE, d-MMM-y HH:m:s z";
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat(
			COOKIE_DATE_FORMAT, Locale.ENGLISH);

	private String name;
	private String value;
	private Date expire;
	private String path;
	private String domain;
	private boolean secure;
	static {
		FORMAT.setCalendar(Calendar.getInstance(new SimpleTimeZone(0, "GMT")));
	}

	public Cookie(String name, String value, Date expire, String path) {
		super();
		this.name = name;
		this.value = value;
		this.expire = expire;
		this.path = path;
	}

	public Cookie(String name, String value, Date expire, String path,
			String domain, boolean secure) {
		super();
		this.name = name;
		this.value = value;
		this.expire = expire;
		this.path = path;
		this.domain = domain;
		this.secure = secure;
	}

	public Cookie(String name, String value, Date expire, String path,
			String domain) {
		super();
		this.name = name;
		this.value = value;
		this.expire = expire;
		this.path = path;
		this.domain = domain;
	}

	public Cookie(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public Cookie(String name, String value, Date expire) {
		super();
		this.name = name;
		this.value = value;
		this.expire = expire;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getExpire() {
		return expire;
	}

	public void setExpire(Date expire) {
		this.expire = expire;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(128);
		builder.append(name + "=" + value + ";");
		if (expire != null) {
			builder.append("expires=");
			builder.append(FORMAT.format(expire));
			builder.append(";");
		}
		if (path != null) {
			builder.append("path=");
			builder.append(path);
			builder.append(";");
		}
		if (domain != null) {
			builder.append("domain=");
			builder.append(domain);
			builder.append(";");
		}
		if (secure) {
			builder.append("secure;");
		}
		return builder.toString();
	}

}
