package net.sf.l2j.gameserver.network.clientpackets;

import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.AdminCommandAccessRights;
import net.sf.l2j.gameserver.handler.AdminCommandHandler;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.Player;

import main.EngineModsManager;

/**
 * This class handles all GM commands triggered by //command
 */
public final class SendBypassBuildCmd extends L2GameClientPacket
{
	private static final Logger GMAUDIT_LOG = Logger.getLogger("gmaudit");
	
	private String _command;
	
	@Override
	protected void readImpl()
	{
		_command = readS();
		if (_command != null)
			_command = _command.trim();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		if (EngineModsManager.onVoiced(activeChar, "admin_" + _command))
			return;
		
		String command = "admin_" + _command.split(" ")[0];
		
		final IAdminCommandHandler ach = AdminCommandHandler.getInstance().getAdminCommandHandler(command);
		if (ach == null)
		{
			if (activeChar.isGM())
				activeChar.sendMessage("The command " + command.substring(6) + " doesn't exist.");
			
			_log.warning("No handler registered for admin command '" + command + "'");
			return;
		}
		
		if (!AdminCommandAccessRights.getInstance().hasAccess(command, activeChar.getAccessLevel()))
		{
			activeChar.sendMessage("You don't have the access right to use this command.");
			_log.warning(activeChar.getName() + " tried to use admin command " + command + ", but have no access to use it.");
			return;
		}
		
		if (Config.GMAUDIT)
			GMAUDIT_LOG.info(activeChar.getName() + " [" + activeChar.getObjectId() + "] used '" + _command + "' command on: " + ((activeChar.getTarget() != null) ? activeChar.getTarget().getName() : "none"));
		
		ach.useAdminCommand("admin_" + _command, activeChar);
	}
}