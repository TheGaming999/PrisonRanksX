package me.prisonranksx.utils;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import me.prisonranksx.PrisonRanksX;


public class MySqlUtils {

	private Statement statement;
	private String database;
	private String field;
	private Map<String, String> temp;
	
	public MySqlUtils(Statement statement, String database) {
		this.statement = statement;
		this.database = database;
		this.temp = new LinkedHashMap<>();
	}
	
	/**
	 * 
	 * @param uuid player uuid
	 * @param key (name, rank, prestige, rebirth, path)
	 * @param value (new string value)
	 */
	public void set(String uuid, String key, String value) {
		this.temp.put(key, value);
		this.field = uuid;
	}
	
	
	public void executeThenClose() {
		String finalStatement = "UPDATE " + database + " set ";
		for (String _key : temp.keySet()) {
			String _value = temp.get(_key);
			finalStatement += "`" + _key + "`='" + _value + "', ";
		}
		finalStatement = finalStatement.substring(0, finalStatement.length() - 2) + " where uuid ='" + field + "';";
		try {
			PrisonRanksX.getInstance().debug(finalStatement);
			if(statement != null) {
			statement.executeUpdate(finalStatement);
			statement.close();
			} else {
				System.out.println("Failed to execute mysql command. The statement is null.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
