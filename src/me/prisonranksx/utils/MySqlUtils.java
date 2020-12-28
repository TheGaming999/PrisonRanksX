package me.prisonranksx.utils;

import java.sql.SQLException;
import java.sql.Statement;


public class MySqlUtils {

	private Statement statement;
	private String database;
	private String lampStatement;
	private String field;
	
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
		this.lampStatement += key + "->" + value + "||";
		this.field = uuid;
	}
	
	
	public void executeThenClose() {
		lampStatement = lampStatement.concat("!");
		lampStatement = lampStatement.replace("||!", "");
		String finalStatement = "UPDATE " + database + " set ";
		for(String entry : lampStatement.split("||")) {
			String[] entryFormat = entry.split("->");
			String key = entryFormat[0];
			String value = entryFormat[1];
			finalStatement += "`" + key + "`='" + value + "', ";
		}
		finalStatement = finalStatement.concat("!");
		finalStatement = finalStatement.replace(", !", " where " + "uuid" + "='" + field + "';");
		try {
			statement.executeUpdate(finalStatement);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
