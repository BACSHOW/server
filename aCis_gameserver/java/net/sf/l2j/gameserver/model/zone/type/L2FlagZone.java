package net.sf.l2j.gameserver.model.zone.type;

import net.sf.l2j.commons.concurrent.ThreadPool;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.zone.L2ZoneType;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.taskmanager.PvpFlagTaskManager;

public class L2FlagZone extends L2ZoneType
{
	L2Skill noblesse = SkillTable.getInstance().getInfo(1323, 1);
	
	public L2FlagZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		character.setInsideZone(ZoneId.PVP, true);
		if (character instanceof Player)
		{
			Player player = character.getActingPlayer();
			PvpFlagTaskManager.getInstance().remove(player);
			noblesse.getEffects(player, player);
			player.updatePvPFlag(1);
			((Player) character).sendPacket(SystemMessageId.ENTERED_COMBAT_ZONE);
			character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true);
		}
	}
	
	@Override
	protected void onExit(Creature character)
	{
		character.setInsideZone(ZoneId.PVP, false);
		if (character instanceof Player)
		{
			Player player = character.getActingPlayer();
			PvpFlagTaskManager.getInstance().remove(player);
			player.updatePvPFlag(0);
			((Player) character).sendPacket(SystemMessageId.LEFT_COMBAT_ZONE);
			character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);
		}
	}
	
	@Override
	public void onDieInside(Creature character)
	{
		if (character instanceof Player)
		{
			final Player activeChar = ((Player) character);
			activeChar.sendPacket(new ExShowScreenMessage(+ Config.FLAGZONE_RESPAWN + " seconds until auto respawn", 4000, 2, true));
			
			ThreadPool.schedule(new Runnable()
			{
				@Override
				public void run()
				{
					activeChar.doRevive();
					activeChar.teleToLocation(Config.FLAGZONE_SPAWN_LOC[0] + Rnd.get(-Config.FLAGZONE_RADIUS, Config.FLAGZONE_RADIUS), Config.FLAGZONE_SPAWN_LOC[1] + Rnd.get(-Config.FLAGZONE_RADIUS, Config.FLAGZONE_RADIUS), Config.FLAGZONE_SPAWN_LOC[2], 0);
				}
			}, 1000 * Config.FLAGZONE_RESPAWN);
		}
	}
	
	@Override
	public void onReviveInside(Creature character)
	{
		if (character instanceof Player)
		{
			final Player player = (Player) character;
			noblesse.getEffects(player, player);
			
			player.setCurrentHp(player.getMaxHp());
			player.setCurrentCp(player.getMaxCp());
			player.setCurrentMp(player.getMaxMp());
		}
	}
}