package net.sf.l2j.gameserver.handler.admincommandhandlers;

import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.instancemanager.games.MonsterRace;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.DeleteObject;
import net.sf.l2j.gameserver.network.serverpackets.MonRaceInfo;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.scripting.quests.audio.Music;
import net.sf.l2j.gameserver.scripting.quests.audio.Sound;

public class AdminMonsterRace implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_mons"
	};
	
	protected static int state = -1;
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.equalsIgnoreCase("admin_mons"))
		{
			/*
			 * -1 0 to initialize the race 0 15322 to start race 13765 -1 in middle of race -1 0 to end the race 8003 to 8027
			 */
			int[][] codes =
			{
				{
					-1,
					0
				},
				{
					0,
					15322
				},
				{
					13765,
					-1
				},
				{
					-1,
					0
				}
			};
			MonsterRace race = MonsterRace.getInstance();
			
			if (state == -1)
			{
				state++;
				race.newRace();
				race.newSpeeds();
				activeChar.broadcastPacket(new MonRaceInfo(codes[state][0], codes[state][1], race.getMonsters(), race.getSpeeds()));
			}
			else if (state == 0)
			{
				state++;
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.MONSRACE_RACE_START);
				activeChar.sendPacket(sm);
				PlaySound SRace = Music.S_RACE.getPacket();
				activeChar.sendPacket(SRace);
				activeChar.broadcastPacket(SRace);
				PlaySound SRace2 = Sound.SOUND_RACE_START.getPacket();
				activeChar.sendPacket(SRace2);
				activeChar.broadcastPacket(SRace2);
				MonRaceInfo spk = new MonRaceInfo(codes[state][0], codes[state][1], race.getMonsters(), race.getSpeeds());
				activeChar.sendPacket(spk);
				activeChar.broadcastPacket(spk);
				
				ThreadPool.schedule(new RunRace(codes, activeChar), 5000);
			}
		}
		return true;
	}
	
	class RunRace implements Runnable
	{
		
		private final int[][] codes;
		private final Player activeChar;
		
		public RunRace(int[][] pCodes, Player pActiveChar)
		{
			codes = pCodes;
			activeChar = pActiveChar;
		}
		
		@Override
		public void run()
		{
			activeChar.broadcastPacket(new MonRaceInfo(codes[2][0], codes[2][1], MonsterRace.getInstance().getMonsters(), MonsterRace.getInstance().getSpeeds()));
			ThreadPool.schedule(new RunEnd(activeChar), 30000);
		}
	}
	
	class RunEnd implements Runnable
	{
		private final Player activeChar;
		
		public RunEnd(Player pActiveChar)
		{
			activeChar = pActiveChar;
		}
		
		@Override
		public void run()
		{
			for (int i = 0; i < 8; i++)
				activeChar.broadcastPacket(new DeleteObject(MonsterRace.getInstance().getMonsters()[i]));
			
			state = -1;
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}