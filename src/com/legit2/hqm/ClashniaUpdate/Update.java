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
import org.bukkit.plugin.PluginDescriptionFile;

import com.legit2.hqm.Infractions.Util;

public class Update {
	static Logger log = Logger.getLogger("Minecraft");
	
	public static boolean shouldUpdate() {
		PluginDescriptionFile pdf = Util.getPlugin().getDescription();
		String latestVersion = pdf.getVersion();
		String onlineVersion = "";
		URL url = null;

		try {
			url = new URL(
					"http://www.clashnia.com/plugins/infractions/version.txt");
			BufferedReader in = null;
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			onlineVersion = in.readLine();
			if (latestVersion == (onlineVersion)) {
				log.info("[Infractions] Infractions is up to date. Version "
						+ latestVersion);
				in.close();
				return false;
			} else {
				log.info("[Infractions] Infractions is not up to date...");
				in.close();
				return true;
			}
		} catch (MalformedURLException ex) {
			log.warning("[Infractions] Error accessing version URL.");
		} catch (IOException ex) {
			log.warning("[Infractions] Error checking for update.");
		}
		return false;
	}

	public static void infractionsUpdate() {
		if ((shouldUpdate()))
			try {
				log.info("[Infractions] Attempting to update to latest version...");
				URL config = new URL(
						"http://www.clashnia.com/plugins/infractions/Infractions.jar");
				ReadableByteChannel rbc = Channels.newChannel(config
						.openStream());
				FileOutputStream fos = new FileOutputStream("plugins"
						+ File.separator + "Infractions.jar");
				fos.getChannel().transferFrom(rbc, 0L, 16777216L);
				log.info("[Infractions] Download complete!");
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
