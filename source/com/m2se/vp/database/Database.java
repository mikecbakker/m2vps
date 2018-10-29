
package com.m2se.vp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database
{
	public static Connection getConnection() throws SQLException
	{
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/autobv?user=vp_user&password=67utEdh5g667utrg6EWY&rewriteBatchedStatements=true");
	}
}
