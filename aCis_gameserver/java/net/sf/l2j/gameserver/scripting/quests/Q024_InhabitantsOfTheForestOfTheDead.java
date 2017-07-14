package net.sf.l2j.gameserver.scripting.quests;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.scripting.QuestState;
import net.sf.l2j.gameserver.scripting.quests.audio.Sound;

/**
 * @author Demon
 */

public class Q024_InhabitantsOfTheForestOfTheDead extends Quest
{
	private static final int DORIAN = 31389;
	private static final int WIZARD = 31522;
	private static final int TOMBSTONE = 31531;
	private static final int MAIDOFLIDIA = 31532;
	
	private static final int LETTER = 7065;
	private static final int HAIRPIN = 7148;
	private static final int TOTEM = 7151;
	private static final int FLOWER = 7152;
	private static final int SILVERCROSS = 7153;
	private static final int BROKENSILVERCROSS = 7154;
	private static final int SUSPICIOUSTOTEM = 7156;
	
	public Q024_InhabitantsOfTheForestOfTheDead()
	{
		super(24, "Inhabitants Of The Forrest Of The Dead");
		addStartNpc(DORIAN);
		addTalkId(DORIAN, TOMBSTONE, MAIDOFLIDIA, WIZARD);
		addKillId(21557, 21558, 21560, 21563, 21564, 21565, 21566, 21567);
		addAggroRangeEnterId(25332);
		setItemsIds(FLOWER, SILVERCROSS, BROKENSILVERCROSS, LETTER, HAIRPIN, TOTEM);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(getName());
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31389-02.htm"))
		{
			st.giveItems(FLOWER, 1);
			st.set("cond", "1");
			st.playSound(Sound.SOUND_ACCEPT);
			st.setState(Quest.STATE_STARTED);
		}
		else if (event.equalsIgnoreCase("31389-11.htm"))
		{
			st.set("cond", "3");
			st.playSound(Sound.SOUND_MIDDLE);
			st.giveItems(SILVERCROSS, 1);
		}
		else if (event.equalsIgnoreCase("31389-16.htm"))
		{
			st.playSound(Sound.INTERFACESOUND_CHARSTAT_OPEN);
		}
		else if (event.equalsIgnoreCase("31389-17.htm"))
		{
			st.takeItems(BROKENSILVERCROSS, -1);
			st.giveItems(HAIRPIN, 1);
			st.set("cond", "5");
		}
		else if (event.equalsIgnoreCase("31522-03.htm"))
		{
			st.takeItems(TOTEM, -1);
		}
		else if (event.equalsIgnoreCase("31522-07.htm"))
		{
			st.set("cond", "11");
		}
		else if (event.equalsIgnoreCase("31531-02.htm"))
		{
			st.playSound(Sound.SOUND_MIDDLE);
			st.set("cond", "2");
			st.takeItems(FLOWER, -1);
		}
		else if (event.equalsIgnoreCase("31532-04.htm"))
		{
			st.playSound(Sound.SOUND_MIDDLE);
			st.giveItems(LETTER, 1);
			st.set("cond", "6");
		}
		else if (event.equalsIgnoreCase("31532-06.htm"))
		{
			st.takeItems(HAIRPIN, -1);
			st.takeItems(LETTER, -1);
		}
		else if (event.equalsIgnoreCase("31532-16.htm"))
		{
			st.playSound(Sound.SOUND_MIDDLE);
			st.set("cond", "9");
		}
		else if (event.equalsIgnoreCase("31522-19.htm"))
		{
			st.giveItems(SUSPICIOUSTOTEM, 1);
			st.rewardExpAndSp(242105, 22529);
			st.exitQuest(false);
			st.unset("cond");
			st.playSound(Sound.SOUND_FINISH);
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg();
		final QuestState st = player.getQuestState(getName());
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case STATE_CREATED:
				QuestState st2 = player.getQuestState("Q023_LidiasHeart");
				if (st2 != null && st2.isCompleted())
				{
					if (player.getLevel() >= 65)
						htmltext = "31389-01.htm";
					else
						htmltext = "31389-00.htm";
				}
				else
					htmltext = "31328-00.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case DORIAN:
						if (cond == 1)
							htmltext = "31389-03.htm";
						else if (cond == 2)
							htmltext = "31389-04.htm";
						else if (cond == 3)
							htmltext = "31389-12.htm";
						else if (cond == 4)
							htmltext = "31389-13.htm";
						else if (cond == 5)
							htmltext = "31389-18.htm";
						break;
					
					case TOMBSTONE:
						if (cond == 1)
						{
							st.playSound(Sound.AMDSOUND_WIND_LOOT);
							htmltext = "31531-01.htm";
						}
						else if (cond == 2)
							htmltext = "31531-03.htm";
						break;
					
					case MAIDOFLIDIA:
						if (cond == 5)
							htmltext = "31532-01.htm";
						else if (cond == 6)
							htmltext = (st.getQuestItemsCount(LETTER) > 0) && (st.getQuestItemsCount(HAIRPIN) > 0) ? "31532-05.htm" : "31532-07.htm";
						else if (cond == 9)
							htmltext = "31532-16.htm";
						break;
					
					case WIZARD:
						if (cond == 10)
							htmltext = "31522-01.htm";
						else if (cond == 11)
							htmltext = "31522-08.htm";
						break;
				}
				break;
			case STATE_COMPLETED:
				if (npc.getNpcId() == WIZARD)
					htmltext = "31522-20.htm";
				else
					htmltext = getAlreadyCompletedMsg();
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		QuestState st = checkPlayerState(player, npc, STATE_STARTED);
		if (st == null)
			return null;
		
		final int npcId = npc.getNpcId();
		if ((st.getQuestItemsCount(TOTEM) < 0) && (st.getInt("cond") == 9))
		{
			if (((npcId == 21557) || (npcId == 21558) || (npcId == 21560) || (npcId == 21563) || (npcId == 21564) || (npcId == 21565) || (npcId == 21566) || (npcId == 21567)) && (Rnd.get(100) <= 30))
			{
				st.giveItems(TOTEM, 1);
				st.set("cond", "10");
				st.playSound(Sound.SOUND_ITEMGET);
			}
		}
		
		return null;
	}
}