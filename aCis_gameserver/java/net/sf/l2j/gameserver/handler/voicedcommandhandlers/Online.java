package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.instance.Player;

public class Online implements IVoicedCommandHandler
{
	private static final String[] _voicedCommands =
	{
		"online"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		
		if (command.equals("online") && Config.ENABLE_ONLINE)
		{
			activeChar.sendMessage("====[Online Players]====");
			activeChar.sendMessage("There are " + World.getInstance().getPlayers().size() + " players online");
			activeChar.sendMessage("=======================");
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
}