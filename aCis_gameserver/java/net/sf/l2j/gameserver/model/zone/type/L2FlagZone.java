package net.sf.l2j.gameserver.model.zone.type;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import net.sf.l2j.commons.concurrent.ThreadPool;
import net.sf.l2j.commons.config.ExProperties;
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
		loadConfigs();
	}
	
	static int radius, respawn;
	static int[][] spawn_loc;
	
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
			activeChar.sendPacket(new ExShowScreenMessage(+ respawn + " seconds until auto respawn", 4000, 2, true));
			
			ThreadPool.schedule(new Runnable()
			{
				@Override
				public void run()
				{
					activeChar.doRevive();
					int[] loc = spawn_loc[Rnd.get(spawn_loc.length)];
					activeChar.teleToLocation(loc[0] + Rnd.get(-radius, radius), loc[1] + Rnd.get(-radius, radius), loc[2], 0);
				}
			}, 1000 * respawn);
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
	
	private static void loadConfigs()
	{
		try
		{
			Properties prop = new Properties();
			prop.load(new FileInputStream(new File("./config/mods/flagzone.properties")));
			spawn_loc = parseItemsList(prop.getProperty("SpawnLoc", "82273,148068,-3469"));
			radius = Integer.parseInt(prop.getProperty("RespawnRadius", "500"));
			respawn = Integer.parseInt(prop.getProperty("RespawnDelay", "5"));
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static int[][] parseItemsList(String line)
	{
		final String[] propertySplit = line.split(";");
		if (propertySplit.length == 0)
			return null;
		
		int i = 0;
		String[] valueSplit;
		final int[][] result = new int[propertySplit.length][];
		for (String value : propertySplit)
		{
			valueSplit = value.split(",");
			if (valueSplit.length != 3)
				return null;
			
			result[i] = new int[3];
			try
			{
				result[i][0] = Integer.parseInt(valueSplit[0]);
			}
			catch (NumberFormatException e)
			{
				return null;
			}
			
			try
			{
				result[i][1] = Integer.parseInt(valueSplit[1]);
			}
			catch (NumberFormatException e)
			{
				return null;
			}
			
			try
			{
				result[i][2] = Integer.parseInt(valueSplit[2]);
			}
			catch (NumberFormatException e)
			{
				return null;
			}
			i++;
		}
		return result;
	}
}