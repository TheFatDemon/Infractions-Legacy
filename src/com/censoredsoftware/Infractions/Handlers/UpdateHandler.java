package com.censoredsoftware.Infractions.Handlers;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.Bukkit;

import com.censoredsoftware.Infractions.Infractions;
import com.censoredsoftware.Infractions.Libraries.UpdateChecker;
import com.censoredsoftware.Infractions.Utilities.MiscUtil;

public class UpdateHandler
{
	static Infractions plugin;

	/*
	 * (String)OLD_DOWNLOAD_LINK : The download link for what should be this exact jar, or the last stable jar if this is a development build.
	 */

	public static UpdateChecker checker = new UpdateChecker("http://dev.bukkit.org/server-mods/infractions/files.rss");

	public UpdateHandler(Infractions infractions)
	{
		plugin = infractions;
	}

	public static boolean shouldUpdate()
	{
		if(checker.updateNeeded())
		{
			MiscUtil.consoleMSG("info", "A new version is available: " + checker.getVersion());
			return true;
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
			int read;
			int bytesTransferred = 0;
			String downloadLink = getDownloadLink();

			MiscUtil.consoleMSG("info", "Attempting to update to latest version...");

			// Set latest build URL
			URL plugin = new URL(downloadLink);

			// Open connection to latest build and set user-agent for download, also determine file size
			URLConnection pluginCon = plugin.openConnection();
			pluginCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2"); // FIXES 403 ERROR
			int contentLength = pluginCon.getContentLength();

			// Create new .jar file and add it to plugins directory
			File pluginUpdate = new File("plugins" + File.separator + "Infractions.jar");
			MiscUtil.consoleMSG("info", "File has been written to: " + pluginUpdate.getCanonicalPath());

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
						MiscUtil.consoleMSG("info", percentTransferred + "%");
					}
				}
			}

			is.close();
			os.flush();
			os.close();

			// Update complete! Reload the server now
			MiscUtil.consoleMSG("info", "Download complete! Reloading server...");
			Bukkit.getServer().reload();
		}
		catch(MalformedURLException ex)
		{
			MiscUtil.consoleMSG("warning", "Error accessing URL: " + ex);
		}
		catch(FileNotFoundException ex)
		{
			MiscUtil.consoleMSG("warning", "Error accessing URL: " + ex);
		}
		catch(IOException ex)
		{
			MiscUtil.consoleMSG("warning", "Error downloading file: " + ex);
		}
	}

	private static String getDownloadLink() throws IOException
	{
		String downloadLink = checker.getJarLink();

		return downloadLink;
	}
}
