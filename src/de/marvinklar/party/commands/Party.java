package de.marvinklar.party.commands;

import de.marvinklar.party.PartyMain;
import de.marvinklar.party.utils.PartyUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Party extends Command
{
	public Party()
	{
		super("Party");
	}

	@Override
	public void execute(final CommandSender s, final String[] args)
	{
		if (!(s instanceof ProxiedPlayer))
		{
			return;
		}
		final ProxiedPlayer player = (ProxiedPlayer) s;
		if (args.length > 0)
		{
			if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("liste"))
			{
				PartyUtil.listParty(player);
				return;
			}
			else if (args[0].equalsIgnoreCase("leave") || args[0].equalsIgnoreCase("verlassen"))
			{
				PartyUtil.leaveParty(player);
				return;
			}
			else if (args[0].equalsIgnoreCase("chat") || args[0].equalsIgnoreCase("msg"))
			{
				String msg = "";
				for (int i = 1; i < args.length; i++)
				{
					msg = msg + args[i] + " ";
				}
				if (msg.length() == 0)
				{
					player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Add_Message"));
					return;
				}
				msg = msg.substring(0, msg.length() - 1);
				PartyUtil.chat(player, msg);
				return;
			}
			else if (args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("einladen"))
			{
				if (args.length > 1)
				{
					for (final ProxiedPlayer target : ProxyServer.getInstance().getPlayers())
					{
						if (args[1].toLowerCase().equalsIgnoreCase(player.getName().toLowerCase()))
						{
							player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Error_Invitation"));
							return;
						}
						if (target.getName().toLowerCase().equalsIgnoreCase(args[1].toLowerCase()))
						{
							final ProxiedPlayer newtarget = ProxyServer.getInstance().getPlayer(args[1]);
							PartyUtil.invitePlayer(player, newtarget);
							return;
						}
					}
					player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Not_Online").replace("%player%", args[1]));
				}
				else
				{
					player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Usage_Party_Invite"));
				}
				return;
			}
			else if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("annehmen"))
			{
				PartyUtil.acceptInvite(player);
				return;
			}
			else if (args[0].equalsIgnoreCase("kick"))
			{
				if (args.length > 1)
				{
					for (final ProxiedPlayer target : ProxyServer.getInstance().getPlayers())
					{
						if (target.getName().equalsIgnoreCase(args[1]))
						{
							PartyUtil.kickPlayer(target, player);
							return;
						}
					}
					player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Not_Online").replace("%player%", args[1]));
				}
				else
				{
					player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Usage_Party_Kick"));
				}
				return;
			}
		}
		player.sendMessage(PartyMain.getMessage("Menu_Invite"));
		player.sendMessage(PartyMain.getMessage("Menu_Accept"));
		player.sendMessage(PartyMain.getMessage("Menu_Leave"));
		player.sendMessage(PartyMain.getMessage("Menu_Kick"));
		player.sendMessage(PartyMain.getMessage("Menu_List"));
		player.sendMessage(PartyMain.getMessage("Menu_Chat"));
	}
}
