package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.network.SystemMessageId;

/**
 * This class handles following admin commands: - changelvl = change a character's access level Can be used for character ban (as opposed to regular //ban that affects accounts) or to grant mod/GM privileges ingame
 */
public class AdminChangeAccessLevel implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_changelvl"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		String[] parts = command.split(" ");
		if (parts.length == 2)
		{
			try
			{
				int lvl = Integer.parseInt(parts[1]);
				if (activeChar.getTarget() instanceof Player)
					onLineChange(activeChar, (Player) activeChar.getTarget(), lvl);
				else
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Usage: //changelvl <target_new_level> | <player_name> <new_level>");
			}
		}
		else if (parts.length == 3)
		{
			String name = parts[1];
			int lvl = Integer.parseInt(parts[2]);
			Player player = World.getInstance().getPlayer(name);
			if (player != null)
				onLineChange(activeChar, player, lvl);
			else
			{
				try (Connection con = L2DatabaseFactory.getInstance().getConnection())
				{
					PreparedStatement statement = con.prepareStatement("UPDATE characters SET accesslevel=? WHERE char_name=?");
					statement.setInt(1, lvl);
					statement.setString(2, name);
					statement.execute();
					int count = statement.getUpdateCount();
					statement.close();
					if (count == 0)
						activeChar.sendMessage("Character not found or access level unaltered.");
					else
						activeChar.sendMessage("Character's access level is now set to " + lvl);
				}
				catch (Exception e)
				{
				}
			}
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	/**
	 * @param activeChar
	 * @param player
	 * @param lvl
	 */
	private static void onLineChange(Player activeChar, Player player, int lvl)
	{
		player.setAccessLevel(lvl);
		if (lvl > 0)
			player.sendMessage("Your access level has been changed to " + lvl);
		else
		{
			player.sendMessage("Your character has been banned.");
			player.logout();
		}
		activeChar.sendMessage("Character's access level is now set to " + lvl + ". Effects won't be noticeable until next session.");
	}
}