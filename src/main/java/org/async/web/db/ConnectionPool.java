package org.async.web.db;

import org.async.jdbc.AsyncConnection;

public interface ConnectionPool {

	AsyncConnection getConnection();
}
