/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.events.TvTEvent;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.olympiad.OlympiadManager;
import net.sf.l2j.gameserver.network.clientpackets.Say2;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import main.data.PlayerData;

/**
 * @author Baggos
 */
public class TvTEventCommand implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"tvtjoin",
		"tvtleave",
		"tvtstatus"
	};
	
	@Override
	public boolean useVoicedCommand(final String command, final Player activeChar, final String target)
	{
		if (command.startsWith("tvtjoin"))
			JoinTvT(target, activeChar);
		
		else if (command.startsWith("tvtleave"))
			LeaveTvT(activeChar);
		
		else if (command.startsWith("tvtstatus"))
			TvTStatus(activeChar);
		
		return true;
	}
	
	public static boolean JoinTvT(final String command, Player activeChar)
	{
		int playerLevel = activeChar.getLevel();
		if (!TvTEvent.isParticipating())
			activeChar.sendPacket(new CreatureSay(0, Say2.HERO_VOICE, "Hey " + activeChar.getName() + "", "There is no TvT Event in progress."));
		else if (TvTEvent.isPlayerParticipant(activeChar.getName()))
			activeChar.sendPacket(new CreatureSay(0, Say2.HERO_VOICE, "TvT Event", "You are already on the list."));
		else if (activeChar.isCursedWeaponEquipped())
			activeChar.sendPacket(new CreatureSay(0, Say2.HERO_VOICE, "TvT Event", "Cursed weapon owners are not allowed to participate."));
		else if (activeChar.isInJail())
			activeChar.sendPacket(new CreatureSay(0, Say2.HERO_VOICE, "TvT Event", "Nothing for you!."));
		else if (PlayerData.get(activeChar.getObjectId()).isAio())
			activeChar.sendPacket(new CreatureSay(0, Say2.HERO_VOICE, "TvT Event", "AIO player can not register!."));
		else if (OlympiadManager.getInstance().isRegisteredInComp(activeChar))
			activeChar.sendPacket(new CreatureSay(0, Say2.HERO_VOICE, "TvT Event", "Olympiad participants can't register."));
		else if (activeChar.getKarma() > 0)
			activeChar.sendPacket(new CreatureSay(0, Say2.HERO_VOICE, "TvT Event", "Chaotic players are not allowed to participate."));
		else if (TvTEvent._teams[0].getParticipatedPlayerCount() >= Config.TVT_EVENT_MAX_PLAYERS_IN_TEAMS && TvTEvent._teams[1].getParticipatedPlayerCount() >= Config.TVT_EVENT_MAX_PLAYERS_IN_TEAMS)
			activeChar.sendPacket(new CreatureSay(0, Say2.HERO_VOICE, "TvT Event", "Sorry the event is full!"));
		else if (playerLevel < Config.TVT_EVENT_MIN_LVL || playerLevel > Config.TVT_EVENT_MAX_LVL)
			activeChar.sendPacket(new CreatureSay(0, Say2.HERO_VOICE, "TvT Event", "Only players from level " + Config.TVT_EVENT_MIN_LVL + " until level " + Config.TVT_EVENT_MAX_LVL + " are allowed to participate."));
		else if (TvTEvent._teams[0].getParticipatedPlayerCount() > 19 && TvTEvent._teams[1].getParticipatedPlayerCount() > 19)
			activeChar.sendPacket(new CreatureSay(0, Say2.HERO_VOICE, "TvT Event", "The event is full! Maximum of " + Config.TVT_EVENT_MAX_PLAYERS_IN_TEAMS + "  player are allowed in one team."));
		else
		{
			TvTEvent.addParticipant(activeChar);
			NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);
			npcHtmlMessage.setHtml("<html><head><title>TvT Event</title></head><body>You are on the registration list now.</body></html>");
			activeChar.sendPacket(npcHtmlMessage);
		}
		return false;
	}
	
	public boolean LeaveTvT(final Player activeChar)
	{
		if (!TvTEvent.isParticipating())
			activeChar.sendPacket(new CreatureSay(0, Say2.HERO_VOICE, "Hey " + activeChar.getName() + "", "There is no TvT Event in progress."));
		else if (!TvTEvent.isInactive() && !TvTEvent.isPlayerParticipant(activeChar.getName()))
			activeChar.sendPacket(new CreatureSay(0, Say2.HERO_VOICE, "TvT Event", "You aren't registered in the TvT Event."));
		else
		{
			TvTEvent.removeParticipant(activeChar.getName());
			NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);
			npcHtmlMessage.setHtml("<html><head><title>TvT Event</title></head><body>You are not longer on the registration list.</body></html>");
			activeChar.sendPacket(npcHtmlMessage);
		}
		return false;
	}
	
	public boolean TvTStatus(final Player activeChar)
	{
		if (!TvTEvent.isStarted())
			activeChar.sendPacket(new CreatureSay(0, Say2.HERO_VOICE, "TvT Event", "TvT Event is not in progress yet."));
		else
		{
			String htmFile = "data/html/mods/tvt/TvTEventStatus.htm";
			String htmContent = HtmCache.getInstance().getHtm(htmFile);
			
			if (htmContent != null)
			{
				int[] teamsPlayerCounts = TvTEvent.getTeamsPlayerCounts();
				int[] teamsPointsCounts = TvTEvent.getTeamsPoints();
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
				
				npcHtmlMessage.setHtml(htmContent);
				// npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));
				npcHtmlMessage.replace("%team1name%", Config.TVT_EVENT_TEAM_1_NAME);
				npcHtmlMessage.replace("%team1playercount%", String.valueOf(teamsPlayerCounts[0]));
				npcHtmlMessage.replace("%team1points%", String.valueOf(teamsPointsCounts[0]));
				npcHtmlMessage.replace("%team2name%", Config.TVT_EVENT_TEAM_2_NAME);
				npcHtmlMessage.replace("%team2playercount%", String.valueOf(teamsPlayerCounts[1]));
				npcHtmlMessage.replace("%team2points%", String.valueOf(teamsPointsCounts[1])); // <---- array index from 0 to 1 thx DaRkRaGe
				activeChar.sendPacket(npcHtmlMessage);
			}
		}
		return false;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}