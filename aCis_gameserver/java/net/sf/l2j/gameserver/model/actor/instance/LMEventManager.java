package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.events.LMEvent;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class LMEventManager extends Folk
{
	private static final String htmlPath = "data/html/mods/LMEvent/";
	
	/**
	 * @param objectId
	 * @param template
	 */
	public LMEventManager(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player playerInstance, String command)
	{
		LMEvent.onBypass(command, playerInstance);
	}
	
	@Override
	public void showChatWindow(Player activeChar)
	{
		if (activeChar == null)
			return;
		
		if (LMEvent.isParticipating())
		{
			final boolean isParticipant = LMEvent.isPlayerParticipant(activeChar.getObjectId());
			final String htmContent;
			
			if (!isParticipant)
				htmContent = HtmCache.getInstance().getHtm(htmlPath + "Participation.htm");
			else
				htmContent = HtmCache.getInstance().getHtm(htmlPath + "RemoveParticipation.htm");
			
			if (htmContent != null)
			{
				int PlayerCounts = LMEvent.getPlayerCounts();
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));
				npcHtmlMessage.replace("%playercount%", String.valueOf(PlayerCounts));
				if (!isParticipant)
					npcHtmlMessage.replace("%fee%", LMEvent.getParticipationFee());
				
				activeChar.sendPacket(npcHtmlMessage);
			}
		}
		else if (LMEvent.isStarting() || LMEvent.isStarted())
		{
			final String htmContent = HtmCache.getInstance().getHtm(htmlPath + "Status.htm");
			
			if (htmContent != null)
			{
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				String htmltext = "";
				htmltext = String.valueOf(LMEvent.getPlayerCounts());
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace("%countplayer%", htmltext);
				activeChar.sendPacket(npcHtmlMessage);
			}
		}
		
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
	}
}
