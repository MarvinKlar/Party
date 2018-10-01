package de.marvinklar.party.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import de.marvinklar.party.PartyMain;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PartyUtil
{
	private static HashMap<String, String>	inparty	= new HashMap<>();
	private static HashMap<String, String>	invite	= new HashMap<>();

	private static ArrayList<String> partyleader = new ArrayList<>();

	private static int	PremiumPlusPartySize	= 50;
	private static int	PremiumPartySize		= 10;
	private static int	PartySize				= 5;

	public static void createParty(final ProxiedPlayer player)
	{
		if (inparty.containsKey(player.getName()) || partyleader.contains(player.getName()))
		{
			player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Already_In_Party"));
		}
		else
		{
			partyleader.add(player.getName());
			player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Party_Created"));
		}
	}

	public static void listParty(final ProxiedPlayer player)
	{
		if (partyleader.contains(player.getName()))
		{
			String players = "";
			int count = 0;
			for (final ProxiedPlayer target : ProxyServer.getInstance().getPlayers())
			{
				if (inparty.containsKey(target.getName()) && inparty.get(target.getName()) == player.getName())
				{
					if (count != 0)
					{
						players = players + ", ";
					}
					players = players + target.getName();
					++count;
				}
			}
			player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Leader") + ": " + player.getName());
			player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Member") + ": " + players);
		}
		else if (inparty.containsKey(player.getName()))
		{
			String players = "";
			int count = 0;
			for (final ProxiedPlayer target : ProxyServer.getInstance().getPlayers())
			{
				if (inparty.containsKey(target.getName()) && inparty.get(target.getName()) == inparty.get(player.getName()))
				{
					if (count != 0)
					{
						players = players + ", ";
					}
					players = players + target.getName();
					++count;
				}
			}
			player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Leader") + ": " + inparty.get(player.getName()));
			player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Member") + ": " + players);
		}
		else
		{
			player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Not_In_Party"));
		}
	}

	public static void leaveParty(final ProxiedPlayer player)
	{
		if (inparty.containsKey(player.getName()))
		{
			player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Party_Left"));
			for (final ProxiedPlayer inParty : ProxyServer.getInstance().getPlayers())
			{
				if (inparty.containsKey(inParty.getName()) && inparty.get(inParty.getName()) == inparty.get(player.getName()))
				{
					inParty.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Player_Left_Party").replace("%player%", player.getName()));
				}
			}
			ProxyServer.getInstance().getPlayer(inparty.get(player.getName()))
					.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Player_Left_Party").replace("%player%", player.getName()));
			inparty.remove(player.getName());
		}
		else if (partyleader.contains(player.getName()))
		{
			player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Party_Deleted"));
			partyleader.remove(player.getName());
			for (final ProxiedPlayer target : ProxyServer.getInstance().getPlayers())
			{
				if (inparty.containsKey(target.getName()) && inparty.get(target.getName()) == player.getName())
				{
					target.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Player_Deleted_Party").replace("%player%", player.getName()));
					inparty.remove(target.getName());
				}
			}
		}
		else
		{
			player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Not_In_Party"));
		}
	}

	public static void chat(final ProxiedPlayer p, String message)
	{
		if (p.hasPermission("party.chat.color"))
		{
			message = ChatColor.translateAlternateColorCodes('&', message);
		}
		if (partyleader.contains(p.getName()))
		{
			p.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Party_Chat").replace("%player%", p.getName()).replace("%message%", message));
			for (final ProxiedPlayer target : ProxyServer.getInstance().getPlayers())
			{
				if (inparty.containsKey(target.getName()) && inparty.get(target.getName()) == p.getName())
				{
					target.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Party_Chat").replace("%player%", p.getName())
							.replace("%message%", message));
				}
			}
		}
		else if (inparty.containsKey(p.getName()))
		{
			ProxyServer.getInstance().getPlayer(inparty.get(p.getName()))
					.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Party_Chat").replace("%player%", p.getName()).replace("%message%", message));
			for (final ProxiedPlayer target : ProxyServer.getInstance().getPlayers())
			{
				if (inparty.containsKey(target.getName()) && inparty.get(target.getName()) == inparty.get(p.getName()))
				{
					target.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Party_Chat").replace("%player%", p.getName())
							.replace("%message%", message));
				}
			}
		}
		else
		{
			p.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Not_In_Party"));
		}
	}

	public static void invitePlayer(final ProxiedPlayer player, final ProxiedPlayer target)
	{
		if (partyleader.contains(player.getName()))
		{
			int i = 1;
			for (final ProxiedPlayer inParty : ProxyServer.getInstance().getPlayers())
			{
				if (inparty.containsKey(inParty.getName()) && inparty.get(inParty.getName()) == player.getName())
				{
					i = i + 1;
				}
			}
			if (i >= (player.hasPermission("party.premiumplus")
					? PremiumPlusPartySize
					: player.hasPermission("party.premium") ? PremiumPartySize : PartySize))
			{
				player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Party_Already_Full"));
			}
			else if (inparty.containsKey(target.getName()))
			{
				player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Player_Already_In_Party").replace("%player%", target.getName()));
			}
			else if (partyleader.contains(target.getName()))
			{
				player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Player_Already_Has_Party").replace("%player%", target.getName()));
			}
			else if (!inparty.containsKey(target.getName()) && !partyleader.contains(target.getName()))
			{
				invite.put(target.getName(), player.getName());
				player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Invitation").replace("%player%", target.getName()));
				target.sendMessage(ChatParser.parse(PartyMain.getPrefix() + PartyMain.getMessage("Invitation_Request").replace("%player%", player.getName())));
				ProxyServer.getInstance().getScheduler().schedule(PartyMain.getInstance(), () -> invite.remove(target.getName()), 5, TimeUnit.MINUTES);
			}
		}
		else if (inparty.containsKey(player.getName()))
		{
			player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Not_Party_Owner"));
		}
		else
		{
			createParty(player);
			invitePlayer(player, target);
		}
	}

	public static void acceptInvite(final ProxiedPlayer player)
	{
		if (partyleader.contains(player.getName()) || inparty.containsKey(player.getName()))
		{
			player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Already_In_Party"));
		}
		else if (invite.containsKey(player.getName()))
		{
			int i = 1;
			final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(invite.get(player.getName()));
			for (final ProxiedPlayer inParty : ProxyServer.getInstance().getPlayers())
			{
				if (inparty.containsKey(inParty.getName()) && inparty.get(inParty.getName()) == target.getName())
				{
					i = i + 1;
				}
			}
			if (i >= (player.hasPermission("party.premiumplus")
					? PremiumPlusPartySize
					: player.hasPermission("party.premium") ? PremiumPartySize : PartySize))
			{
				player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Party_Already_Full"));
			}
			else
			{
				for (final ProxiedPlayer inParty : ProxyServer.getInstance().getPlayers())
				{
					if (inparty.containsKey(inParty.getName()) && inparty.get(inParty.getName()) == target.getName())
					{
						inParty.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Player_Joined_Party").replace("%player%", player.getName()));
					}
				}
				target.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Player_Joined_Party").replace("%player%", player.getName()));
				invite.remove(player.getName());
				inparty.put(player.getName(), target.getName());
				player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Joined_Party").replace("%player%", target.getName()));
			}
		}
		else
		{
			player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("No_Invitation"));
		}
	}

	public static void kickPlayer(final ProxiedPlayer player, final ProxiedPlayer target)
	{
		if (partyleader.contains(target.getName()))
		{
			if (inparty.containsKey(player.getName()) && inparty.get(player.getName()) == target.getName())
			{
				inparty.remove(player.getName());
				player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Kicked").replace("%player%", target.getName()));
				target.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Player_Kicked").replace("%player%", player.getName()));
				for (final ProxiedPlayer inParty : ProxyServer.getInstance().getPlayers())
				{
					if (inparty.containsKey(inParty.getName()) && inparty.get(inParty.getName()) == target.getName())
					{
						inParty.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Player_Kicked").replace("%player%", player.getName()));
					}
				}
			}
			else
			{
				target.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Player_Not_In_Party").replace("%player%", player.getName()));
			}
		}
		else
		{
			target.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Not_Party_Owner"));
		}
	}

	public static void setPremiumPlusPartySize(final int size)
	{
		PremiumPlusPartySize = size;
	}

	public static void setPremiumPartySize(final int size)
	{
		PremiumPartySize = size;
	}

	public static void setPartySize(final int size)
	{
		PartySize = size;
	}

	public static HashMap<String, String> getInParty()
	{
		return inparty;
	}

	public static ArrayList<String> getPartyLeaders()
	{
		return partyleader;
	}
}
