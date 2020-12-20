package me.prisonranksx.utils;

import java.sql.SQLException;
import java.sql.Statement;


public class MySqlUtils {

	private Statement statement;
	private String database;
	
	public MySqlUtils(Statement statement, String database) {
		this.statement = statement;
		this.database = database;
	}
	
	/**
	 * 
	 * @param uuid player uuid
	 * @param key (name, rank, prestige, rebirth, path)
	 * @param value (new string value)
	 */
	public void set(String uuid, String key, String value) {
		try {
			statement.executeUpdate("UPDATE " + database + " set `" + key + "`='" + value + "' where uuid='" + uuid + "';");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
