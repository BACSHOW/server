package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.events.DMEvent;
import net.sf.l2j.gameserver.events.LMEvent;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class EventsVoicedInfo implements IVoicedCommandHandler
{
	private static final String[] _voicedCommands =
	{
		"lmjoin",
		"lmleave",
		"dminfo",
		"dmjoin",
		"dmleave",
	};
	private static final boolean USE_STATIC_HTML = true;
	private static final String HTML_DM = HtmCache.getInstance().getHtm("data/html/mods/DMEvent/Status.htm");
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (command.equalsIgnoreCase("lmjoin"))
		{
			LMEvent.onBypass("lm_event_participation", activeChar);
		}
		else if (command.equalsIgnoreCase("lmleave"))
		{
			LMEvent.onBypass("lm_event_remove_participation", activeChar);
		}
		else if (command.equalsIgnoreCase("dminfo"))
		{
			if (DMEvent.isStarting() || DMEvent.isStarted())
			{
				String htmContent = (USE_STATIC_HTML && !HTML_DM.isEmpty()) ? HTML_DM : HtmCache.getInstance().getHtm("data/html/mods/DMEvent/Status.htm");
				try
				{
					String[] firstPositions = DMEvent.getFirstPosition(Config.DM_REWARD_FIRST_PLAYERS);
					NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
					String htmltext = "";
					Boolean c = true;
					String c1 = "D9CC46";
					String c2 = "FFFFFF";
					if (firstPositions != null)
						for (int i = 0; i < firstPositions.length; i++)
						{
							String[] row = firstPositions[i].split("\\,");
							String color = (c ? c1 : c2);
							htmltext += "<tr>";
							htmltext += "<td width=\"35\" align=\"center\"><font color=\"" + color + "\">" + String.valueOf(i + 1) + "</font></td>";
							htmltext += "<td width=\"100\" align=\"left\"><font color=\"" + color + "\">" + row[0] + "</font></td>";
							htmltext += "<td width=\"125\" align=\"right\"><font color=\"" + color + "\">" + row[1] + "</font></td>";
							htmltext += "</tr>";
							c = !c;
						}
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%toprank%", htmltext);
					activeChar.sendPacket(npcHtmlMessage);
				}
				catch (Exception e)
				{
					_log.warning("wrong Events voiced: " + e);
				}
			}
			else
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			}
		}
		else if (command.equalsIgnoreCase("dmjoin"))
		{
			DMEvent.onBypass("dm_event_participation", activeChar);
		}
		else if (command.equalsIgnoreCase("dmleave"))
		{
			DMEvent.onBypass("dm_event_remove_participation", activeChar);
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
}