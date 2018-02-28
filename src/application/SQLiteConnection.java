package application;

import java.sql.Connection;
import java.sql.*;

public class SQLiteConnection {

	public static Connection connector(){
		
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:chinook.db");
			return conn;
		} catch (Exception e) {
			return null;
			// TODO: handle exception
		}
	}
}
