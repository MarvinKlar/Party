package de.marvinklar.party.listeners;

import java.util.concurrent.TimeUnit;

import de.marvinklar.party.PartyMain;
import de.marvinklar.party.utils.PartyUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerSwitch implements Listener
{
	@EventHandler
	public void onServerSwitch(final ServerSwitchEvent e)
	{
		if (!PartyUtil.getPartyLeaders().contains(e.getPlayer().getName()))
		{
			return;
		}
		final ProxiedPlayer player = e.getPlayer();
		ProxyServer.getInstance().getScheduler().schedule(PartyMain.getInstance(), () ->
		{
			if (!player.isConnected())
			{
				return;
			}
			final ServerInfo serverinfo = player.getServer().getInfo();
			player.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Party_Changed_Server").replace("%server%", serverinfo.getName()));
			for (final ProxiedPlayer target : ProxyServer.getInstance().getPlayers())
			{
				if (PartyUtil.getInParty().containsKey(target.getName()) && PartyUtil.getInParty().get(target.getName()) == player.getName())
				{
					target.sendMessage(PartyMain.getPrefix() + PartyMain.getMessage("Party_Changed_Server").replace("%server%", serverinfo.getName()));
					if (!target.getServer().getInfo().equals(serverinfo))
					{
						target.connect(serverinfo);
					}
				}
			}
		}, 2, TimeUnit.SECONDS);
	}
}
