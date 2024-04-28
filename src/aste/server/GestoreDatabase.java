package aste.server;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

public class GestoreDatabase {
	private BasicDataSource dataSource;

	public static class Opzioni {
		private String nomeDriver;
		private String url;
		private String username;
		private String password;
		private int minIdle;
		private int maxIdle;
		private int maxOpenPreparedStatements;
		
		public Opzioni(String nomeDriver,
			String url,
			String username,
			String password,
			int minIdle,
			int maxIdle,
			int maxOpenPreparedStatements
		) {
			this.nomeDriver = nomeDriver;
			this.url = url;
			this.username = username;
			this.password = password;
			this.minIdle = minIdle;
			this.maxIdle = maxIdle;
			this.maxOpenPreparedStatements = maxOpenPreparedStatements;
		}
	}

	public GestoreDatabase(Opzioni opzioni) {
		dataSource = new BasicDataSource();

		dataSource.setDriverClassName(opzioni.nomeDriver);
		dataSource.setUrl(opzioni.url);
		dataSource.setUsername(opzioni.username);
		dataSource.setPassword(opzioni.password);
		dataSource.setMinIdle(opzioni.minIdle);
		dataSource.setMaxIdle(opzioni.maxIdle);
		dataSource.setMaxOpenPreparedStatements(opzioni.maxOpenPreparedStatements);
	}

	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
}