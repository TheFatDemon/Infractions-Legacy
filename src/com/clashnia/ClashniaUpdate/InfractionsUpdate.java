package com.clashnia.ClashniaUpdate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;

import com.legit2.hqm.Infractions.Util;

public class InfractionsUpdate {
static Logger log = Logger.getLogger("Minecraft");
	
	/*
	 *  (String)OLD_DOWNLOAD_LINK : The download link for what should be this exact jar, or the last stable jar if this is a development build.
	 */
	static String OLD_DOWNLOAD_LINK = "http://dev.bukkit.org/media/files/649/676/Infractions.jar";
	
	public static boolean shouldUpdate()
	{
		PluginDescriptionFile pdf = Util.getPlugin().getDescription();
		String currentVersion = pdf.getVersion();
		
		if (currentVersion.startsWith("d")) return false; // development versions shouldn't downgrade

		try
		{
			String downloadLink = getDownloadLink();
			
			if (downloadLink.equals(OLD_DOWNLOAD_LINK))
			{
				log.info("[Infractions] Infractions is up to date.");
				return false;
			}
			else
			{
				log.info("[Infractions] Infractions is not up to date...");
				return true;
			}
		}
	catch (MalformedURLException ex)
	{
			log.severe("[Infractions] Error accessing version URL.");
		}
		catch (IOException ex)
		{
			log.severe("[Infractions] Error checking for update.");
		}
		return false;
	}
	
	public static void infractionsUpdate()
	{
		try
		{
			// Disable the plugin so it's all safe and sound while we update it
			Bukkit.getServer().getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("Infractions"));
			
			// Define variables
			byte[] buffer = new byte[1024];
			int read = 0;
			int bytesTransferred = 0;
			String downloadLink = getDownloadLink();

			log.info("[Infractions] Attempting to update to latest version...");
			
			// Set latest build URL
			URL plugin = new URL(downloadLink);
			
			// Open connection to latest build and set user-agent for download, also determine file size
			URLConnection pluginCon = plugin.openConnection();
			pluginCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2"); //FIXES 403 ERROR
            int contentLength = pluginCon.getContentLength();

            // Create new .jar file and add it to plugins directory
            File pluginUpdate = new File("plugins" + File.separator + "Infractions.jar");
			log.info("[Infractions] File has been written to: " + pluginUpdate.getCanonicalPath());
			
			InputStream is = pluginCon.getInputStream();
			OutputStream os = new FileOutputStream(pluginUpdate);
			
			while((read = is.read(buffer)) > 0)
			{
				os.write(buffer, 0, read);
				bytesTransferred += read;
				
				if(contentLength > 0)
				{
					// Determine percent of file and add it to variable
					int percentTransferred = (int) (((float) bytesTransferred / contentLength) * 100);
					
					if(percentTransferred != 100)
					{
						log.info("[Infractions] " + percentTransferred + "%");
					}
				}
			}
			
			is.close();
			os.flush();
			os.close();
			
			// Update complete! Reload the server now
			log.info("[Infractions] Download complete! Reloading server...");
			Bukkit.getServer().reload();
		}
		catch (MalformedURLException ex)
		{
			log.warning("[Infractions] Error accessing URL: " + ex);
		}
		catch (FileNotFoundException ex)
		{
			log.warning("[Infractions] Error accessing URL: " + ex);
		}
		catch (IOException ex)
		{
			log.warning("[Infractions] Error downloading file: " + ex);
		}
	}
	
	private static String getDownloadLink() throws IOException
	{
		String downloadLink;
		
		URL version = new URL("http://www.clashnia.com/plugins/infractions/dl.txt");
		URLConnection downloadCon = version.openConnection();
		downloadCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2"); //FIXES 403 ERROR
		BufferedReader in = new BufferedReader(new InputStreamReader(downloadCon.getInputStream()));
		downloadLink = in.readLine();
		in.close();
		
		return downloadLink;
	}
}
