package org.async.web.run;

import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.async.json.conf.JSONIoCContainer;
import org.async.net.Server;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class Starter {
	private static final String DEFAULT_CONF = "server.conf.json";
	private static final Logger logger = Logger
			.getLogger("org.async.web.run.Starter");

	public static void main(String[] args) {
		JSONIoCContainer container = null;
		try {
			container = new JSONIoCContainer(new FileReader(
					args.length > 0 ? args[0] : DEFAULT_CONF));
		} catch (Exception e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		final Thread rootThread = Thread.currentThread();
		try {
			final Server server=(Server) container.getBean(args.length > 1 ? args[1]: "$.server");
			Signal term = new Signal("TERM");
			Signal sint = new Signal("INT");
			Signal abrt = new Signal("ABRT");
			SignalHandler signalHandler = new SignalHandler() {

				@Override
				public void handle(Signal arg) {
					if (server != null) {
						server.shutdown();
						while (rootThread.isAlive()) {
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								if (logger.isLoggable(Level.SEVERE)) {
									logger.log(Level.SEVERE, e.getMessage(), e);
								}
							}
						}
					}
				}

			};
			Signal.handle(term, signalHandler);
			Signal.handle(sint, signalHandler);
			Signal.handle(abrt, signalHandler);

			if (logger.isLoggable(Level.INFO)) {
				logger.info("Listening on " + server.getBind().keySet());

			}
			server.run();
		} catch (Exception e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}

	}
}
