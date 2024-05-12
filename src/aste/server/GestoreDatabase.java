package aste.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

public class GestoreDatabase {
	private BasicDataSource dataSource;
	private Opzioni opzioni;

	public static class Opzioni {
		public boolean pool;
		public String nomeDriver;
		public String url;
		public String username;
		public String password;
		public int minIdle;
		public int maxIdle;
		public int maxOpenPreparedStatements;
		
		public Opzioni(
			boolean pool,	
			String nomeDriver,
			String url,
			String username,
			String password,
			int minIdle,
			int maxIdle,
			int maxOpenPreparedStatements
		) {
			this.pool = pool;
			this.nomeDriver = nomeDriver;
			this.url = url;
			this.username = username;
			this.password = password;
			this.minIdle = minIdle;
			this.maxIdle = maxIdle;
			this.maxOpenPreparedStatements = maxOpenPreparedStatements;
		}
	}

	public GestoreDatabase(Opzioni opzioni) throws RuntimeException {
		this.opzioni = opzioni;

		if (opzioni.pool)
		{
			dataSource = new BasicDataSource();
			dataSource.setDriverClassName(opzioni.nomeDriver);
			dataSource.setUrl(opzioni.url);
			dataSource.setUsername(opzioni.username);
			dataSource.setPassword(opzioni.password);
			dataSource.setMinIdle(opzioni.minIdle);
			dataSource.setMaxIdle(opzioni.maxIdle);
			dataSource.setMaxOpenPreparedStatements(opzioni.maxOpenPreparedStatements);
		}

		try (Connection test = getConnection();) {
			test.isValid(0);
		} catch (SQLException e) {
			throw new RuntimeException("Impossibile connettersi al database: " + e.getMessage());
		}
	}

	public Connection getConnection() throws SQLException {
		if (opzioni.pool) {
			return dataSource.getConnection();
		}

		return DriverManager.getConnection(opzioni.url, opzioni.username, opzioni.password);
	}
}