package org.async.web.log;

import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LogConfHelper {
	public static Level getLevelProperty(String name, Level defaultValue) {
		LogManager manager = LogManager.getLogManager();
		String val = manager.getProperty(name);
		if (val == null) {
			return defaultValue;
		}
		try {
			return Level.parse(val.trim());
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	public static String getStringProperty(String name, String defaultValue) {
		LogManager manager = LogManager.getLogManager();
		String val = manager.getProperty(name);
		if (val == null) {
			return defaultValue;
		}
		return val.trim();
	}

	public static int getIntProperty(String name, int defaultValue) {
		LogManager manager = LogManager.getLogManager();
		String val = manager.getProperty(name);
		if (val == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(val.trim());
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	public static boolean getBooleanProperty(String name, boolean defaultValue) {
		LogManager manager = LogManager.getLogManager();
		String val = manager.getProperty(name);
		if (val == null) {
			return defaultValue;
		}
		val = val.toLowerCase();
		if (val.equals("true") || val.equals("1")) {
			return true;
		} else if (val.equals("false") || val.equals("0")) {
			return false;
		}
		return defaultValue;
	}

	public static Logger getCustomFileLogger(String name,String def,Formatter formatter) {
		Logger customLogger=Logger.getLogger(name);
		String accessLog=LogConfHelper.getStringProperty(name+".pattern",def);
		try {
			FileHandler handler = new FileHandler(accessLog, true);
			handler.setFormatter(formatter);
			handler.setLevel(LogConfHelper.getLevelProperty(name+".level", Level.INFO));
			customLogger.addHandler(handler);
		} catch (Exception e) {
			if (Logger.getAnonymousLogger().isLoggable(Level.SEVERE)) {
				Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return customLogger;
	}
}
