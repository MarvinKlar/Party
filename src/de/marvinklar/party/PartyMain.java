package de.marvinklar.party;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;

import de.marvinklar.party.commands.Party;
import de.marvinklar.party.listeners.PlayerDisconnect;
import de.marvinklar.party.listeners.ServerSwitch;
import de.marvinklar.party.utils.PartyUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class PartyMain extends Plugin
{
	private static Plugin plugin;

	private static File				file;
	private static Configuration	config;

	private static String	lang;
	private static String	prefix;

	@Override
	public void onEnable()
	{
		plugin = this;

		loadConfig();

		prefix = getMessage("Prefix") + " ";

		PartyUtil.setPremiumPlusPartySize(config.getInt("Partysize.PremiumPlus"));
		PartyUtil.setPremiumPartySize(config.getInt("Partysize.Premium"));
		PartyUtil.setPartySize(config.getInt("Partysize.Normal"));

		ProxyServer.getInstance().getPluginManager().registerCommand(this, new Party());
		ProxyServer.getInstance().getPluginManager().registerListener(this, new ServerSwitch());
		ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerDisconnect());
	}

	public static Plugin getInstance()
	{
		return plugin;
	}

	public static void loadConfig()
	{
		try
		{
			file = new File(PartyMain.plugin.getDataFolder(), "config.yml");
			if (!PartyMain.plugin.getDataFolder().exists())
			{
				PartyMain.plugin.getDataFolder().mkdir();
				if (!file.exists())
				{
					Files.copy(PartyMain.plugin.getResourceAsStream("config.yml"), file.toPath(), new CopyOption[0]);
				}
			}
			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		lang = config.getString("Language");
	}

	public static void save()
	{
		try
		{
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(PartyMain.config, PartyMain.file);
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

	public static String getMessage(final String message)
	{
		return replaceCodeToUmlaut(config.getString("Lang." + lang + "." + message));
	}

	public static String getPrefix()
	{
		return prefix;
	}

	public static String replaceCodeToUmlaut(final String string)
	{
		return ChatColor.translateAlternateColorCodes('&', string.replace("&suml", "ß").replace("&ouml", "ö").replace("&uuml", "ü").replace("&auml", "ä").replace("&Ouml", "Ö")
				.replace("&Uuml", "Ü").replace("&Auml", "Ä").replace(">>", "»")
				.replace("<<", "«").replace("[newline]", "\n"));
	}
}