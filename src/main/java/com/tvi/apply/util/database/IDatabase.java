package com.tvi.apply.util.database;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;

public interface IDatabase
{

	Statement createStatement();

	CallableStatement prepareCall(String sql);

	PreparedStatement prepareStatement(String sql);

	PreparedStatement prepareStatement(String sql, int k);

	String getDatabaseName();
}
