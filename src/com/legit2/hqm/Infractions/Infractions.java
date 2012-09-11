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

import com.legit2.hqm.Infractions.Util;

public class Infractions extends JavaPlugin implements Listener {
	public static Logger log = Logger.getLogger("Minecraft");
	static String mainDirectory = "plugins/Infractions/";
    Util initialize;
	Save SAVE;

	public Infractions(){
		super();
	}

	@Override
	public void onEnable() {
		long firstTime = System.currentTimeMillis();
		log.info("[Infractions] Initializing.");
		new Settings(this); // #1 (needed for Util to load)
		log.info("[Infractions] Updating configuration.");
		initialize = new Util(this); // #2 (needed for everything else to work)
		SAVE = new Save(mainDirectory); // #3 (needed to start save system)
		loadListeners(); // #4
		loadCommands(); // #5 (needed)
		initializeThreads(); // #6 (regen and etc)
		log.info("[Infractions] Preparation completed in "+((double)(System.currentTimeMillis()-firstTime)/1000)+" seconds.");
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
		log.info("[Infractions] Save completed and "+c+" tasuks cancelled.");
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

	public void loadCommands() {
		//for help files
		CommandManager ce = new CommandManager(this);
		//general
		getServer().getPluginManager().registerEvents(ce, this);
		getCommand("dg").setExecutor(ce);
		getCommand("check").setExecutor(ce);
		getCommand("claim").setExecutor(ce);
		getCommand("alliance").setExecutor(ce);
		getCommand("perks").setExecutor(ce);
		getCommand("checkplayer").setExecutor(ce);
		getCommand("removeplayer").setExecutor(ce);
		getCommand("value").setExecutor(ce);
		getCommand("bindings").setExecutor(ce);
		getCommand("debugplayer").setExecutor(ce);
	}

	public void loadListeners(){
		getServer().getPluginManager().registerEvents(new Manager(), this);
	}

	private void initializeThreads() {
		int startdelay = (int)(Settings.getSettingDouble("start_delay_seconds")*20);
		int savefrequency = Settings.getSettingInt("save_interval_seconds")*20;
		if (startdelay <= 0) startdelay = 1;
		if (savefrequency <= 0) savefrequency = 300;
		//data save
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				try {
					Save.save(mainDirectory);
					log.info("[Infractions] Saved data players. "+Save.getCompleteData().size()+" files total.");
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
}
