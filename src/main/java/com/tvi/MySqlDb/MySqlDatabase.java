package com.tvi.MySqlDb;

import com.tvi.common.IDatabase;

import java.sql.CallableStatement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySqlDatabase implements IDatabase
{

	public String database = "";
	private Connection connection;
	private String connectionstring;
	public String username;
	public String password;

	public MySqlDatabase()
	{
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException("cannot find mysql library:" + ex.toString());
		}
	}

	private void keepAlive()
	{
		try {

			int itry = 0;
			while (connection.isClosed()) {
				itry++;
				if (itry == 10) {
					throw new RuntimeException("cannot connect to the database\n");
				}
				this.connection = DriverManager.getConnection(connectionstring, username, password);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void connect(String host, String username, String password, String database)
	{
		this.database = database;
		connectionstring = "jdbc:mysql://" + host + "/" + database + "?useUnicode=true&characterEncoding=UTF-8";
		this.username = username;
		this.password = password;
		try {
			this.connection = DriverManager.getConnection(connectionstring, username, password);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Statement createStatement()
	{
		try {
			keepAlive();
			return this.connection.createStatement();
		} catch (SQLException ex) {
			throw new RuntimeException(ex.toString());
		}
	}

	@Override
	public CallableStatement prepareCall(String sql)
	{
		try {
			keepAlive();
			return connection.prepareCall(sql);
		} catch (SQLException ex) {
			throw new RuntimeException(ex.toString());
		}
	}

	@Override
	public PreparedStatement prepareStatement(String sql)
	{
		try {
			keepAlive();
			return this.connection.prepareStatement(sql);
		} catch (SQLException ex) {
			throw new RuntimeException(ex.toString());
		}
	}

	@Override
	public PreparedStatement preparedStatement(String sql, int k)
	{
		try {
			keepAlive();
			return this.connection.prepareStatement(sql, k);
		} catch (SQLException ex) {
			throw new RuntimeException(ex.toString());
		}
	}

	@Override
	public String getDatabaseName()
	{
		return database;
	}

}
