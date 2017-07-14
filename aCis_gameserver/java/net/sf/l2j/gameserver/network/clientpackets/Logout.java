package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.gameserver.events.TvTEvent;
import net.sf.l2j.gameserver.instancemanager.SevenSignsFestival;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.events.DMEvent;
import net.sf.l2j.gameserver.events.LMEvent;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.RestartResponse;
import net.sf.l2j.gameserver.taskmanager.AttackStanceTaskManager;

import main.EngineModsManager;

public final class Logout extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getActiveChar();
		if (player == null)
			return;
		
		if (player.getActiveEnchantItem() != null || player.isLocked())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (!TvTEvent.isInactive() && TvTEvent.isPlayerParticipant(player.getName()))
		{
			player.sendMessage("You can not restart when you registering in TvTEvent.");
			sendPacket(RestartResponse.valueOf(false));
			return;
		}
		
		if (!DMEvent.isInactive() && DMEvent.isPlayerParticipant(player.getObjectId()))
		{
			player.sendMessage("You can not restart when you registering in DMEvent.");
			sendPacket(RestartResponse.valueOf(false));
			return;
		}
		
		if (!LMEvent.isInactive() && LMEvent.isPlayerParticipant(player.getObjectId()))
		{
			player.sendMessage("You can not restart when you registering in LMEvent.");
			sendPacket(RestartResponse.valueOf(false));
			return;
		}
		
		if (player.isInsideZone(ZoneId.NO_RESTART))
		{
			player.sendPacket(SystemMessageId.NO_LOGOUT_HERE);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (AttackStanceTaskManager.getInstance().isInAttackStance(player))
		{
			player.sendPacket(SystemMessageId.CANT_LOGOUT_WHILE_FIGHTING);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isFestivalParticipant() && SevenSignsFestival.getInstance().isFestivalInitialized())
		{
			player.sendPacket(SystemMessageId.NO_LOGOUT_HERE);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// custom by fissban
		if (EngineModsManager.onExitWorld(player))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		player.removeFromBossZone();
		player.logout();
	}
}