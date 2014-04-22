package com.censoredsoftware.infractions.bukkit.legacy.util;

import com.censoredsoftware.infractions.bukkit.Infraction;
import com.censoredsoftware.infractions.bukkit.Infractions;
import com.censoredsoftware.infractions.bukkit.dossier.CompleteDossier;
import com.censoredsoftware.infractions.bukkit.dossier.Dossier;
import com.censoredsoftware.infractions.bukkit.evidence.Evidence;
import com.censoredsoftware.infractions.bukkit.evidence.EvidenceType;
import com.censoredsoftware.infractions.bukkit.issuer.Issuer;
import com.censoredsoftware.infractions.bukkit.issuer.IssuerType;
import com.censoredsoftware.infractions.bukkit.legacy.InfractionsPlugin;
import com.censoredsoftware.library.helper.MojangIdProvider;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;
import java.util.logging.Logger;

public class MiscUtil
{

	private static InfractionsPlugin plugin; // obviously needed
	static Logger log = Logger.getLogger("Minecraft");
	static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	static Calendar cal = Calendar.getInstance();
	static String date = dateFormat.format(cal.getTime());

	public static void consoleMSG(String level, String msg)
	{
		// Define variables
		Logger log = Logger.getLogger("Minecraft");

		// Strip color from console messages
		msg = ChatColor.stripColor(msg);

		if(level.equalsIgnoreCase("info")) log.info("[Demigods] " + msg);
		if(level.equalsIgnoreCase("warning")) log.warning("[Demigods] " + msg);
		if(level.equalsIgnoreCase("severe")) log.severe("[Demigods] " + msg);
	}

	public static int getMaxScore(Player p)
	{
		int maxScore = SettingUtil.getSettingInt("ban_at_score");
		if(maxScore < 1) maxScore = 1;
		else if(maxScore > 20) maxScore = 20;
		if(p.hasPermission("infractions.*"))
		{ // '*' Permission check.
			maxScore = SettingUtil.getSettingInt("ban_at_score");
		}
		else if(p.hasPermission("infractions.maxscore.1"))
		{
			maxScore = 1;
		}
		else if(p.hasPermission("infractions.maxscore.2"))
		{
			maxScore = 2;
		}
		else if(p.hasPermission("infractions.maxscore.3"))
		{
			maxScore = 3;
		}
		else if(p.hasPermission("infractions.maxscore.4"))
		{
			maxScore = 4;
		}
		else if(p.hasPermission("infractions.maxscore.5"))
		{
			maxScore = 5;
		}
		else if(p.hasPermission("infractions.maxscore.6"))
		{
			maxScore = 6;
		}
		else if(p.hasPermission("infractions.maxscore.7"))
		{
			maxScore = 7;
		}
		else if(p.hasPermission("infractions.maxscore.8"))
		{
			maxScore = 8;
		}
		else if(p.hasPermission("infractions.maxscore.9"))
		{
			maxScore = 9;
		}
		else if(p.hasPermission("infractions.maxscore.10"))
		{
			maxScore = 10;
		}
		else if(p.hasPermission("infractions.maxscore.11"))
		{
			maxScore = 11;
		}
		else if(p.hasPermission("infractions.maxscore.12"))
		{
			maxScore = 12;
		}
		else if(p.hasPermission("infractions.maxscore.13"))
		{
			maxScore = 13;
		}
		else if(p.hasPermission("infractions.maxscore.14"))
		{
			maxScore = 14;
		}
		else if(p.hasPermission("infractions.maxscore.15"))
		{
			maxScore = 15;
		}
		else if(p.hasPermission("infractions.maxscore.16"))
		{
			maxScore = 16;
		}
		else if(p.hasPermission("infractions.maxscore.17"))
		{
			maxScore = 17;
		}
		else if(p.hasPermission("infractions.maxscore.18"))
		{
			maxScore = 18;
		}
		else if(p.hasPermission("infractions.maxscore.19"))
		{
			maxScore = 19;
		}
		else if(p.hasPermission("infractions.maxscore.20"))
		{
			maxScore = 20;
		}
		return maxScore;
	}

	public static ChatColor chatColor(Player p, ChatColor color)
	{
		if(p == null) return ChatColor.RESET;
		return (color);
	}

	@SuppressWarnings("unchecked")
	public static boolean addInfraction(String target, CommandSender sender, int score, String reason, String proof)
	{
		try
		{
			UUID id = MojangIdProvider.getId(target);
			Issuer issuer = getIssuer(sender);
			Infractions.getCompleteDossier(target).cite(new Infraction(id, System.currentTimeMillis(), reason, score, issuer, createEvidence(issuer, proof)));
			return true;
		}
		catch(Exception ignored)
		{
		}
		return false;
	}

	public static String getInfractionsPlayer(final String guess)
	{
		Dossier dossier = Iterables.find(Infractions.allDossiers(), new Predicate<Dossier>()
		{
			@Override
			public boolean apply(Dossier dossier)
			{
				return dossier instanceof CompleteDossier && ((CompleteDossier) dossier).getLastKnownName().toLowerCase().startsWith(guess.toLowerCase());
			}
		}, null);
		if(dossier != null) return ((CompleteDossier) dossier).getLastKnownName();
		return null;
	}

	public static Issuer getIssuer(CommandSender sender)
	{
		if(sender instanceof Player) return new Issuer(IssuerType.STAFF, ((Player) sender).getUniqueId().toString());
		return new Issuer(IssuerType.UNKNOWN, sender.getName());
	}

	public static Evidence createEvidence(Issuer issuer, String proof)
	{
		boolean isImage = false;
		try
		{
			Image image = ImageIO.read(new URL(proof));
			isImage = image != null;
		}
		catch(Exception ignored)
		{
		}
		return new Evidence(issuer, isImage ? EvidenceType.IMAGE_URL : EvidenceType.OTHER_URL, System.currentTimeMillis(), proof);
	}

	public static void checkScore(String playerName)
	{
		checkScore(Bukkit.getOfflinePlayer(playerName));
	}

	public static void checkScore(OfflinePlayer offlinePlayer)
	{
		if(offlinePlayer == null || !offlinePlayer.isOnline()) return;
		Player p = offlinePlayer.getPlayer();
		if(!SettingUtil.getSettingBoolean("ban") || p.hasPermission("infractions.ignore")) return;
		if(getMaxScore(p) <= getScore(p) && (!p.hasPermission("infractions.banexempt")))
		{
			p.kickPlayer("You have been banned.");
			try
			{
				p.setBanned(true);
			}
			catch(NullPointerException e)
			{
				log.info("[Infractions] Unable to set " + p.toString() + " to banned.");
			}
		}
		else
		{
			try
			{
				p.setBanned(false);
			}
			catch(NullPointerException e)
			{
				log.info("[Infractions] Unable to set " + p.toString() + " to unbanned.");
			}
		}
	}

	public static void kickNotify(String p, String reason)
	{
		if(!VirtueUtil.getVirtues().contains(reason))
		{
			try
			{
				Player player = Bukkit.getPlayer(p);
				if(getMaxScore(player) <= getScore(player))
				{
					checkScore(player);
				}
				else if(SettingUtil.getSettingBoolean("kick_on_cite"))
				{
					player.kickPlayer("You've been cited for " + reason + "."); // Kick a
				}
				else
				{
					player.sendMessage(ChatColor.RED + "You've been cited for " + reason + ".");
					player.sendMessage("Use " + ChatColor.YELLOW + "/history" + ChatColor.WHITE + " for more information."); // Send
				}
			}
			catch(NullPointerException e)
			{
				SaveUtil.saveData(p, "NEWINFRACTION", true);
			}
		}
		else
		{
			VirtueUtil.rewardPlayer(p, reason);
		}
	}

	public static InfractionsPlugin getPlugin()
	{
		return plugin;
	}

	public static int getScore(Player p)
	{
		return getScore(p.getName());
	}

	public static int getScore(String p)
	{
		return Infractions.getCompleteDossier(p).getScore();
	}

	public static boolean hasPermissionOrOP(Player p, String pe)
	{ // convenience
		// method
		// for
		// permissions
		return p == null || p.isOp() || p.hasPermission(pe);
	}

	public static boolean isValidURL(String input)
	{
		if(!input.startsWith("http://") && !input.startsWith("https://"))
		{
			input = ("http://" + input);
		}
		try
		{
			URI uri = new URI(input);
			URL url = uri.toURL();
			java.net.URLConnection conn = url.openConnection();
			conn.connect();
			return true;
		}
		catch(MalformedURLException e)
		{
			return false;
		}
		catch(IOException e)
		{
			return false;
		}
		catch(URISyntaxException e)
		{
			return false;
		}
	}

	public static void messageSend(Player p, String str)
	{
		if(p == null)
		{
			str = "[Infractions] " + str;
			log.info(str);
		}
		else
		{
			p.sendMessage(str);
		}
	}

	public static boolean removeInfraction(String target, final String givenID)
	{
		CompleteDossier dossier = Infractions.getCompleteDossier(target);
		Infraction infraction = Iterables.find(dossier.getInfractions(), new Predicate<Infraction>()
		{
			@Override
			public boolean apply(Infraction infraction)
			{
				return givenID.equals(getId(infraction));
			}
		}, null);
		if(infraction != null)
		{
			dossier.acquit(infraction);
			return true;
		}
		return false;
	}

	public static String getId(Infraction infraction)
	{
		String s = infraction.getTimeCreated().toString();
		StringBuilder id = new StringBuilder();
		for(int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			id.append(getLetterForId(c));
		}
		return id.toString();
	}

	private static String getLetterForId(char num)
	{
		if(num == '1') return "F";
		if(num == '2') return "Z";
		if(num == '3') return "c";
		if(num == '4') return "A";
		if(num == '5') return "Q";
		if(num == '6') return "u";
		if(num == '7') return "p";
		if(num == '8') return "W";
		if(num == '9') return "j";
		return "b"; // 0
	}

	public MiscUtil(InfractionsPlugin i)
	{
		plugin = i;
	}
}
