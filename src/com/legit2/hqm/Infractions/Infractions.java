package com.legit2.hqm.Infractions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

public class Infractions extends JavaPlugin implements Listener {
	public static Logger log = Logger.getLogger("Minecraft");
	static String mainDirectory = "plugins/Infractions/";
	Util initialize;
	Save SAVE;
	public boolean MySQL = false;
	public String dbHost = null;
	public String dbPort = null;
	public String dbUser = null;
	public String dbPass = null;
	public String dbDatabase = null;

	public Infractions() {
		super();
	}

	private void initializeThreads() {
		int startdelay = (int) (Settings
				.getSettingDouble("start_delay_seconds") * 20);
		int savefrequency = Settings.getSettingInt("save_interval_seconds") * 20;
		if (startdelay <= 0)
			startdelay = 1;
		if (savefrequency <= 0)
			savefrequency = 300;
		// data save
		getServer().getScheduler().scheduleSyncRepeatingTask(this,
				new Runnable() {
					@Override
					public void run() {
						try {
							Save.save(mainDirectory);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
							log.severe("[Infractions] Save location error. Screenshot the stack trace and send to marinating.");
						} catch (IOException e) {
							e.printStackTrace();
							log.severe("[Infractions] Save write error. Screenshot the stack trace and send to marinating.");
						}
					}
				}, startdelay, savefrequency);
	}

	public void loadCommands() {
		CommandManager ce = new CommandManager(this);
		// info
		getServer().getPluginManager().registerEvents(ce, this);
		getCommand("infractions").setExecutor(ce);
		getCommand("history").setExecutor(ce);
		// actions
		getCommand("cite").setExecutor(ce);
		getCommand("uncite").setExecutor(ce);
	}

	public void loadListeners() {
		getServer().getPluginManager().registerEvents(new Manager(), this);
	}
	
	public void loadMetrics() {
		try {
		    MetricsLite metrics = new MetricsLite(this);
		    metrics.start();
		} catch (IOException e) {
		    // Failed to submit the stats :-(
		}
	}

	@Override
	public void onDisable() {
		try {
			Save.save(mainDirectory);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.severe("[Infractions] Save location error. Screenshot the stack trace and send to marinating.");
		} catch (IOException e) {
			e.printStackTrace();
			log.severe("[Infractions] Save write error. Screenshot the stack trace and send to marinating.");
		}
		int c = 0;
		for (BukkitWorker bw : getServer().getScheduler().getActiveWorkers())
			if (bw.getOwner().equals(this))
				c++;
		for (BukkitTask bt : getServer().getScheduler().getPendingTasks())
			if (bt.getOwner().equals(this))
				c++;
		this.getServer().getScheduler().cancelTasks(this);
		log.info("[Infractions] Save completed and " + c + " tasks cancelled.");
	}

	@Override
	public void onEnable() {
		long firstTime = System.currentTimeMillis();
		log.info("[Infractions] Initializing.");
		new Settings(this); // #1 (needed for Util to load)
		log.info("[Infractions] Updating configuration.");
		initialize = new Util(this); // #2 (needed for everything else to work)
		SAVE = new Save(mainDirectory); // #3 (needed to start save system)
		// checkMySQL(); // #4
		loadListeners(); // #5
		loadCommands(); // #6 (needed)
		loadMetrics(); // #7
		initializeThreads(); // #8 (regen and etc)
		log.info("[Infractions] Preparation completed in "
				+ ((double) (System.currentTimeMillis() - firstTime) / 1000)
				+ " seconds.");
	}

	@EventHandler
	public void saveOnExit(PlayerQuitEvent e) {
		try {
			Save.save(mainDirectory);
		} catch (FileNotFoundException er) {
			er.printStackTrace();
			log.severe("[Infractions] Save location error. Screenshot the stack trace and send to marinating.");
		} catch (IOException er) {
			er.printStackTrace();
			log.severe("[Infractions] Save write error. Screenshot the stack trace and send to marinating.");
		}
	}
	
	/**
	public void checkMySQL() {
		Boolean MySQL = Settings.getSettingBoolean("MySQL");
		String dbHost = Settings.getSettingString("host");
		String dbPort = Settings.getSettingString("port");
		String dbUser = Settings.getSettingString("username");
		String dbPass = Settings.getSettingString("password");
		String dbDatabase = Settings.getSettingString("database");
		
		if (dbHost.equals(null)) {
			MySQL = false;
			log.severe("[Infractions] MySQL is on, but host is not defined, disabling.");
		}
		if (dbUser.equals(null)) {
			MySQL = false;
			log.severe("[Infractions] MySQL is on, but username is not defined, disabling.");
		}
		if (dbPass.equals(null)) {
			MySQL = false;
			log.severe("[Infractions] MySQL is on, but password is not defined, disabling.");
		}
		if (dbDatabase.equals(null)) {
			MySQL = false;
			log.severe("[Infractions] MySQL is on, but database is not defined, disabling.");
		} 
		if (MySQL) {
			mysql = new MySQL(log, "[Infractions] ", dbHost, dbPort, dbDatabase, dbUser, dbPass);
			
			log.info("[Infractions] MySQL Initializing");
			try {
				mysql.open();
			} catch (Exception e) {
				out.println(e.getMessage());
			}
			if (mysql.checkConnection()) { // Check if the Connection was successful
				log.info("[Infractions] MySQL connection successful");
				if (!mysql.checkTable("infractions")) { // Check if the table exists in the database if not create it
					log.info("[Infractions] Creating table infractions");
					String query = "CREATE TABLE infractions (key INT, infractions VARCHAR(255), x INT, y INT, z INT);";
					mysql.createTable(query);
				}
			} else {
				log.severe("[Infractions] MySQL connection failed");
				MySQL = false;
			}
		}
	}
	**/
}
