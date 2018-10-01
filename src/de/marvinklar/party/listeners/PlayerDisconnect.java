package de.marvinklar.party.listeners;

import de.marvinklar.party.utils.PartyUtil;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerDisconnect implements Listener
{
	@EventHandler
	public void onPlayerDisconnect(final PlayerDisconnectEvent e)
	{
		ProxiedPlayer player = e.getPlayer();
		if (PartyUtil.getPartyLeaders().contains(player.getName()) || PartyUtil.getInParty().containsKey(player.getName()))
		{
			PartyUtil.leaveParty(player);
		}
		player = null;
	}
}
