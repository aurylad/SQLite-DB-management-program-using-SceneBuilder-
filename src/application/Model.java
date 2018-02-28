package application;

import java.sql.*;

public class Model {

	Connection connection;

	public Model() {
		connection = SQLiteConnection.connector();
	}

	public boolean isDbConnected(){
		try {
			return !connection.isClosed();
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

}
