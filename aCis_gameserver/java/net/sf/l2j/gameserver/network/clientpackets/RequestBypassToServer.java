package net.sf.l2j.gameserver.network.clientpackets;

import java.text.DecimalFormat;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.communitybbs.CommunityBoard;
import net.sf.l2j.gameserver.datatables.AdminCommandAccessRights;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.handler.AdminCommandHandler;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.handler.VoicedCommandHandler;
import net.sf.l2j.gameserver.instancemanager.NewbiesSystemManager;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.OlympiadManagerNpc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.Hero;
import net.sf.l2j.gameserver.model.item.DropCategory;
import net.sf.l2j.gameserver.model.item.DropData;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.olympiad.OlympiadManager;
import net.sf.l2j.gameserver.network.FloodProtectors;
import net.sf.l2j.gameserver.network.FloodProtectors.Action;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import main.EngineModsManager;
import main.data.IconData;

public final class RequestBypassToServer extends L2GameClientPacket
{
	private static final Logger GMAUDIT_LOG = Logger.getLogger("gmaudit");
	
	private String _command;
	
	@Override
	protected void readImpl()
	{
		_command = readS();
	}
	
	@Override
	protected void runImpl()
	{
		if (!FloodProtectors.performAction(getClient(), Action.SERVER_BYPASS))
			return;
		
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		if (_command.isEmpty())
		{
			_log.info(activeChar.getName() + " sent an empty requestBypass packet.");
			activeChar.logout();
			return;
		}
		
		try
		{
			if (_command.startsWith("admin_"))
			{
				if (EngineModsManager.onVoiced(activeChar, _command))
					return;
				
				String command = _command.split(" ")[0];
				
				IAdminCommandHandler ach = AdminCommandHandler.getInstance().getAdminCommandHandler(command);
				if (ach == null)
				{
					if (activeChar.isGM())
						activeChar.sendMessage("The command " + command.substring(6) + " doesn't exist.");
					
					_log.warning("No handler registered for admin command '" + command + "'");
					return;
				}
				
				if (!AdminCommandAccessRights.getInstance().hasAccess(command, activeChar.getAccessLevel()))
				{
					activeChar.sendMessage("You don't have the access rights to use this command.");
					_log.warning(activeChar.getName() + " tried to use admin command " + command + " without proper Access Level.");
					return;
				}
				
				if (Config.GMAUDIT)
					GMAUDIT_LOG.info(activeChar.getName() + " [" + activeChar.getObjectId() + "] used '" + _command + "' command on: " + ((activeChar.getTarget() != null) ? activeChar.getTarget().getName() : "none"));
				
				ach.useAdminCommand(_command, activeChar);
			}
			if (_command.startsWith("voiced_"))
			{
				String command = _command.split(" ")[0];
				
				IVoicedCommandHandler ach = VoicedCommandHandler.getInstance().getHandler(_command.substring(7));
				
				if (ach == null)
				{
					activeChar.sendMessage("The command " + command.substring(7) + " does not exist!");
					_log.warning("No handler registered for command '" + _command + "'");
					return;
				}
				
				ach.useVoicedCommand(_command.substring(7), activeChar, null);
			}
			else if (_command.startsWith("player_help "))
			{
				playerHelp(activeChar, _command.substring(12));
			}
			else if (_command.startsWith("npc_"))
			{
				if (!activeChar.validateBypass(_command))
					return;
				
				int endOfId = _command.indexOf('_', 5);
				String id;
				if (endOfId > 0)
					id = _command.substring(4, endOfId);
				else
					id = _command.substring(4);
				
				try
				{
					final WorldObject object = World.getInstance().getObject(Integer.parseInt(id));
					
					if (object != null && object instanceof Npc && endOfId > 0 && ((Npc) object).canInteract(activeChar))
						((Npc) object).onBypassFeedback(activeChar, _command.substring(endOfId + 1));
					
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				}
				catch (NumberFormatException nfe)
				{
				}
			}
			// Navigate throught Manor windows
			else if (_command.startsWith("manor_menu_select?"))
			{
				WorldObject object = activeChar.getTarget();
				if (object instanceof Npc)
					((Npc) object).onBypassFeedback(activeChar, _command);
			}
			else if (_command.startsWith("bbs_") || _command.startsWith("_bbs") || _command.startsWith("_friend") || _command.startsWith("_mail") || _command.startsWith("_block"))
			{
				CommunityBoard.getInstance().handleCommands(getClient(), _command);
			}
			else if (_command.startsWith("Quest "))
			{
				if (!activeChar.validateBypass(_command))
					return;
				
				String[] str = _command.substring(6).trim().split(" ", 2);
				if (str.length == 1)
					activeChar.processQuestEvent(str[0], "");
				else
					activeChar.processQuestEvent(str[0], str[1]);
			}
			else if (_command.startsWith("_match"))
			{
				String params = _command.substring(_command.indexOf("?") + 1);
				StringTokenizer st = new StringTokenizer(params, "&");
				int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
				int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
				int heroid = Hero.getInstance().getHeroByClass(heroclass);
				if (heroid > 0)
					Hero.getInstance().showHeroFights(activeChar, heroclass, heroid, heropage);
			}
			else if (_command.startsWith("_diary"))
			{
				String params = _command.substring(_command.indexOf("?") + 1);
				StringTokenizer st = new StringTokenizer(params, "&");
				int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
				int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
				int heroid = Hero.getInstance().getHeroByClass(heroclass);
				if (heroid > 0)
					Hero.getInstance().showHeroDiary(activeChar, heroclass, heroid, heropage);
			}
			else if (_command.startsWith("class"))
			{
				NewbiesSystemManager.getInstance().Classes(_command, activeChar);
			}
			else if (_command.startsWith("arenachange")) // change
			{
				final boolean isManager = activeChar.getCurrentFolkNPC() instanceof OlympiadManagerNpc;
				if (!isManager)
				{
					// Without npc, command can be used only in observer mode on arena
					if (!activeChar.isInObserverMode() || activeChar.isInOlympiadMode() || activeChar.getOlympiadGameId() < 0)
						return;
				}
				
				if (OlympiadManager.getInstance().isRegisteredInComp(activeChar))
				{
					activeChar.sendPacket(SystemMessageId.WHILE_YOU_ARE_ON_THE_WAITING_LIST_YOU_ARE_NOT_ALLOWED_TO_WATCH_THE_GAME);
					return;
				}
				
				final int arenaId = Integer.parseInt(_command.substring(12).trim());
				activeChar.enterOlympiadObserverMode(arenaId);
			}
			else if (_command.startsWith("Engine"))
				EngineModsManager.onEvent(activeChar, activeChar.getLastNpcTalk(),_command.replace("Engine ", ""));
			else if (_command.startsWith("DropListNpc"))
			{
				final WorldObject object = activeChar.getTarget();
				if (object instanceof Npc)
				{
					NpcHtmlMessage html = new NpcHtmlMessage(0);
					StringBuilder html1 = new StringBuilder("<html>");
					
					html1.append("<title>NPC Name: " + object.getName() + "</title>");
					html1.append("<body>");
					html1.append("<br>");
					html1.append("<table cellspacing=2 cellpadding=1 width=\"280\">");
					
					if (((Npc) object).getTemplate().getDropData() != null)
					{
						for (DropCategory cat : ((Npc) object).getTemplate().getDropData())
						{
							for (DropData drop : cat.getAllDrops())
							{
								final Item item = ItemTable.getInstance().getTemplate(drop.getItemId());
								
								if (item == null)
									continue;
								int mind = 0, maxd = 0;
								String smind = null, smaxd = null;
								String name = item.getName();
								if (cat.isSweep())
								{
									mind = (int) (Config.RATE_DROP_SPOIL * drop.getMinDrop());
									maxd = (int) (Config.RATE_DROP_SPOIL * drop.getMaxDrop());
								}
								else if (drop.getItemId() == 57)
								{
									mind = 300 * drop.getMinDrop();
									maxd = 300 * drop.getMaxDrop();
								}
								else
								{
									mind = (int) (Config.RATE_DROP_ITEMS * drop.getMinDrop());
									maxd = (int) (Config.RATE_DROP_ITEMS * drop.getMaxDrop());
								}
								if (mind > 999999)
								{
									DecimalFormat df = new DecimalFormat("###.#");
									smind = df.format(((double) (mind)) / 1000000) + " KK";
									smaxd = df.format(((double) (maxd)) / 1000000) + " KK";
								}
								else if (mind > 999)
								{
									smind = ((mind / 1000)) + " K";
									smaxd = ((maxd / 1000)) + " K";
								}
								
								else
								{
									smind = Integer.toString(mind);
									smaxd = Integer.toString(maxd);
								}
								if (name.startsWith("Common Item - "))
								{
									name = "(CI)" + name.substring(14);
								}
								if (name.length() >= 34)
								{
									name = name.substring(0, 30) + "...";
								}
								html1.append("<tr>");
								html1.append("<td valign=top align=center height=38 width=40><img src=\"" + IconData.getIconByItemId(item.getItemId()) + "\" height=32 width=32></td>");
								html1.append("<td>");
								html1.append("<table cellpadding=0 cellspacing=1 width=237>");
								html1.append("<tr>");
								html1.append("<td>" + (drop.getChance() >= 10000 ? (double) drop.getChance() / 10000 : drop.getChance() < 10000 ? (double) drop.getChance() / 10000 : "N/A") + "</td>");
								html1.append("</tr>");
								html1.append("<tr>");
								html1.append("<td>Name: <font color=fff600>" + name + "</font> " + (maxd == 1 ? "[1]" : "[" + smind + " - " + smaxd + "]") + "</td>");
								html1.append("</tr>");
								html1.append("</table>");
								html1.append("</td>");
							}
						}
					}
					
					html1.append("</tr></table>");
					html1.append("</body>");
					html1.append("</html>");
					
					html.setHtml(html1.toString());
					activeChar.sendPacket(html);
					
					html1 = null;
					html = null;
				}
				
			}
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Bad RequestBypassToServer: " + e, e);
		}
	}
	
	private static void playerHelp(Player activeChar, String path)
	{
		if (path.indexOf("..") != -1)
			return;
		
		final StringTokenizer st = new StringTokenizer(path);
		final String[] cmd = st.nextToken().split("#");
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/help/" + cmd[0]);
		if (cmd.length > 1)
			html.setItemId(Integer.parseInt(cmd[1]));
		html.disableValidation();
		activeChar.sendPacket(html);
	}
}