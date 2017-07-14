package net.sf.l2j.gameserver.handler.admincommandhandlers;

import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.protection.CatsGuard;

public class AdminHwidBan implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_hwid",
		"admin_hwidban",
		"admin_hwidunban"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.equals("admin_hwid"))
		{
			AdminHelpPage.showHelpPage(activeChar, "hwid-ban/hwid.htm");
		}
		else if (command.equals("admin_hwidban"))
		{
			String hwid = ((Player) activeChar.getTarget()).getHWid();
			if (hwid != null)
			{
				CatsGuard.getInstance().ban(hwid);
				activeChar.sendMessage("The player with HWID'om:" + hwid + "was banned.");
			}
			else
			{
				activeChar.sendMessage("This HWID'a does not exist.");
			}
		}
		else if (command.equals("admin_hwidunban"))
		{
			String hwid = ((Player) activeChar.getTarget()).getHWid();
			if (hwid != null)
			{
				CatsGuard.getInstance().unban(hwid);
				activeChar.sendMessage("The player with HWID'om:" + hwid + "was unban.");
			}
			else
			{
				activeChar.sendMessage("This HWID'a does not exist.");
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}