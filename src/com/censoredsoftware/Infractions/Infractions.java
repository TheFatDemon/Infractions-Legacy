package com.censoredsoftware.Infractions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import com.censoredsoftware.Infractions.Handlers.CommandHandler;
import com.censoredsoftware.Infractions.Handlers.UpdateHandler;
import com.censoredsoftware.Infractions.Libraries.MetricsLite;
import com.censoredsoftware.Infractions.Listeners.PlayerListener;
import com.censoredsoftware.Infractions.Utilities.MiscUtil;
import com.censoredsoftware.Infractions.Utilities.SaveUtil;
import com.censoredsoftware.Infractions.Utilities.SettingUtil;

public class Infractions extends JavaPlugin implements Listener
{
	public static Logger log = Logger.getLogger("Minecraft");
	static String mainDirectory = "plugins/Infractions/";
	MiscUtil initialize;
	SaveUtil SAVE;

	public Infractions()
	{
		super();
	}

	private void initializeThreads()
	{
		int startdelay = (int) (SettingUtil.getSettingDouble("start_delay_seconds") * 20);
		int savefrequency = SettingUtil.getSettingInt("save_interval_seconds") * 20;
		if(startdelay <= 0) startdelay = 1;
		if(savefrequency <= 0) savefrequency = 300;
		// data save
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					SaveUtil.save(mainDirectory);
				}
				catch(FileNotFoundException e)
				{
					e.printStackTrace();
					log.severe("[Infractions] SaveUtil location error. Screenshot the stack trace and send to HmmmQuestionMark.");
				}
				catch(IOException e)
				{
					e.printStackTrace();
					log.severe("[Infractions] SaveUtil write error. Screenshot the stack trace and send to HmmmQuestionMark.");
				}
			}
		}, startdelay, savefrequency);
	}

	public void loadCommands()
	{
		CommandHandler ce = new CommandHandler(this);
		// info
		getCommand("infractions").setExecutor(ce);
		getCommand("virtues").setExecutor(ce);
		getCommand("history").setExecutor(ce);
		// actions
		getCommand("cite").setExecutor(ce);
		getCommand("uncite").setExecutor(ce);
	}

	public void loadListeners()
	{
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
	}

	public void loadMetrics()
	{
		try
		{
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		}
		catch(IOException e)
		{
			// Failed to submit the stats :-(
		}
	}

	@Override
	public void onDisable()
	{
		try
		{
			SaveUtil.save(mainDirectory);
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
			log.severe("[Infractions] SaveUtil location error. Screenshot the stack trace and send to HmmmQuestionMark.");
		}
		catch(IOException e)
		{
			e.printStackTrace();
			log.severe("[Infractions] SaveUtil write error. Screenshot the stack trace and send to HmmmQuestionMark.");
		}
		int c = 0;
		for(BukkitWorker bw : getServer().getScheduler().getActiveWorkers())
			if(bw.getOwner().equals(this)) c++;
		for(BukkitTask bt : getServer().getScheduler().getPendingTasks())
			if(bt.getOwner().equals(this)) c++;
		this.getServer().getScheduler().cancelTasks(this);
		log.info("[Infractions] SaveUtil completed and " + c + " tasks cancelled.");
	}

	@Override
	public void onEnable()
	{
		long firstTime = System.currentTimeMillis();
		log.info("[Infractions] Initializing.");
		new SettingUtil(this); // #1 (needed for MiscUtil to load)
		log.info("[Infractions] Updating configuration.");
		initialize = new MiscUtil(this); // #2 (needed for everything else to work)
		SAVE = new SaveUtil(mainDirectory); // #3 (needed to start save system)
		loadListeners(); // #5
		loadCommands(); // #6 (needed)
		loadMetrics(); // #7
		initializeThreads(); // #8 (regen and etc)

		if(SettingUtil.getSettingBoolean("update"))
		{
			if(UpdateHandler.shouldUpdate()) UpdateHandler.infractionsUpdate();
		}

		log.info("[Infractions] Preparation completed in " + ((double) (System.currentTimeMillis() - firstTime) / 1000) + " seconds.");
	}
}
