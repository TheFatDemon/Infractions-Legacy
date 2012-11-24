package com.legit2.hqm.Infractions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Database {
	static Logger log = Logger.getLogger("Minecraft");
	@SuppressWarnings("unused")
	private Infractions plugin;

	static Boolean MySQL = Settings.getSettingBoolean("MySQL");
	static String dbURL = "jdbc:mysql://" + Settings.getSettingString("sqlHostname") + ":" + Settings.getSettingInt("sqlPort") + "/" + Settings.getSettingString("sqlDatabase");
	static String dbUser = Settings.getSettingString("sqlUsername");
	static String dbPass = Settings.getSettingString("sqlPassword");
	
	public Database(Infractions plugin) {
		this.plugin = plugin;

		if (testDBConnection()) {
			//setupMysql();
		} else {
			log.info("Failed to connect to database, disabling plugin...");
			plugin.getPluginLoader().disablePlugin(plugin);
		}
	}
	
	public static boolean testDBConnection() {
		try {
			Connection con = DriverManager.getConnection(dbURL, dbUser, dbPass);
			con.close();
			log.info("[Infractions] MYSQL WORKS! :D");
			return true;
		} catch (SQLException e) {
			log.info("[Infractions] MYSQL NO WORKY. :C");
			//do nothing
		}
		return false;
	}
}