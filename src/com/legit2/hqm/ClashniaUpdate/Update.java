package com.legit2.hqm.ClashniaUpdate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

import com.legit2.hqm.Infractions.Infractions;

public class Update {
	static Logger log = Logger.getLogger("Minecraft");
	private static Infractions plugin; // obviously needed

	public static void infractionsUpdate() {
		boolean shouldUpdate = false;
		boolean infractionsExists = true;
		String latestVersion = "";
		String onlineVersion = "";
		URL url = null;
		try {
			latestVersion = plugin.getServer().getPluginManager()
					.getPlugin("Infractions").getDescription().getVersion();
		} catch (Exception nullpointer) {
			infractionsExists = false;
			log.info("[Infractions] No instance of Infractions detected.");
			log.info("[Infractions] Beginning download...");
		}

		if (infractionsExists) {
			try {
				url = new URL(
						"http://www.clashnia.com/plugins/infractions/version.txt");
				BufferedReader in = null;
				in = new BufferedReader(new InputStreamReader(url.openStream()));
				onlineVersion = in.readLine();
				if (latestVersion.equals(onlineVersion)) {
					log.info("[Infractions] Infractions is up to date. Version "
							+ latestVersion);
				} else {
					log.info("[Infractions] Attempting to update to latest version...");
					shouldUpdate = true;
				}
				in.close();
			} catch (MalformedURLException ex) {
				log.warning("[Infractions] Error accessing version URL.");
			} catch (IOException ex) {
				log.warning("[Infractions] Error checking for update.");
			}
		}
		if ((shouldUpdate) || (!infractionsExists))
			try {
				URL config = new URL(
						"http://www.clashnia.com/plugins/infractions/Infractions.jar");
				ReadableByteChannel rbc = Channels.newChannel(config
						.openStream());
				FileOutputStream fos = new FileOutputStream("plugins"
						+ File.separator + "Infractions.jar");
				fos.getChannel().transferFrom(rbc, 0L, 16777216L);
				log.info("[Infractions] Download complete! You are on version "
						+ onlineVersion);
				Bukkit.getServer().reload();
			} catch (MalformedURLException ex) {
				log.warning("[Infractions] Error accessing URL: " + ex);
			} catch (FileNotFoundException ex) {
				log.warning("[Infractions] Error accessing URL: " + ex);
			} catch (IOException ex) {
				log.warning("[Infractions] Error downloading file: " + ex);
			}
	}
}
