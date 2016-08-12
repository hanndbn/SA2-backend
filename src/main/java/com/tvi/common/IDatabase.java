package com.tvi.common;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;

public interface IDatabase
{

	Statement createStatement();

	CallableStatement prepareCall(String sql);

	PreparedStatement prepareStatement(String sql);

	PreparedStatement preparedStatement(String sql, int k);

	String getDatabaseName();
}
