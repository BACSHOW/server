package net.sf.l2j.gameserver.scripting.quests;

import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcSay;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.scripting.QuestState;
import net.sf.l2j.gameserver.scripting.quests.audio.Sound;

/**
 * @author Demon
 */

public class Q115_TheOtherSideOfTruth extends Quest
{
	private static final int MISA = 32018;
	private static final int SUSPICIOUS = 32019;
	private static final int RAFFORTY = 32020;
	private static final int SCULPTURE1 = 32021;
	private static final int KIERRE = 32022;
	private static final int SCULPTURE2 = 32077;
	private static final int SCULPTURE3 = 32078;
	private static final int SCULPTURE4 = 32079;
	
	private static final int LETTER = 8079;
	private static final int LETTER2 = 8080;
	private static final int TABLET = 8081;
	private static final int REPORT = 8082;
	
	public Q115_TheOtherSideOfTruth()
	{
		super(115, "The Other Side Of Truth");
		
		addStartNpc(RAFFORTY);
		addTalkId(RAFFORTY, MISA, SCULPTURE1, SCULPTURE2, SCULPTURE3, SCULPTURE4, KIERRE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestState(getName());
		if (st == null)
			return htmltext;
		
		switch (event)
		{
			case "32020-02.htm":
				st.setState(Quest.STATE_STARTED);
				st.playSound(Sound.SOUND_ACCEPT);
				st.set("cond", "1");
				break;
			
			case "32018-04.htm":
				st.playSound(Sound.SOUND_MIDDLE);
				st.set("cond", "7");
				st.takeItems(LETTER2, 1);
				break;
			
			case "32020-05.htm":
				st.playSound(Sound.SOUND_MIDDLE);
				st.set("cond", "3");
				st.takeItems(LETTER, 1);
				break;
			
			case "32020-08.htm":
				st.playSound(Sound.SOUND_MIDDLE);
				st.set("cond", "4");
				break;
			
			case "32020-07a.htm":
				st.playSound(Sound.SOUND_MIDDLE);
				st.set("cond", "4");
				break;
			
			case "32020-12.htm":
				st.playSound(Sound.SOUND_MIDDLE);
				st.set("cond", "5");
				break;
			
			case "32020-16.htm":
				st.playSound(Sound.SOUND_MIDDLE);
				st.set("cond", "10");
				st.takeItems(REPORT, 1);
				break;
			
			case "32020-19.htm":
				st.playSound(Sound.SOUND_MIDDLE);
				st.set("cond", "11");
				break;
			
			case "32022-02.htm":
				st.playSound(Sound.SOUND_MIDDLE);
				st.set("cond", "9");
				Npc man = addSpawn(SUSPICIOUS, 104562, -107598, -3688, 0, false, 4000, false);
				man.broadcastPacket(new NpcSay(man.getObjectId(), 0, man.getNpcId(), "We meet again."));
				startQuestTimer("2", 3700, man, player, false);
				st.giveItems(REPORT, 1);
				break;
			
			case "Sculpture-04.htm":
				st.set("talk", "1");
				htmltext = "Sculpture-05.htm";
				st.set(String.valueOf(npc.getNpcId()), "1");
				break;
			
			case "Sculpture-04a.htm":
				st.playSound(Sound.SOUND_MIDDLE);
				st.set("cond", "8");
				Npc man2 = addSpawn(SUSPICIOUS, 117890, -126478, -2584, 0, false, 4000, false);
				man2.broadcastPacket(new NpcSay(man2.getObjectId(), 0, man2.getNpcId(), "This looks like the right place..."));
				startQuestTimer("1", 3700, man2, player, false);
				htmltext = "Sculpture-04.htm";
				st.giveItems(TABLET, 1);
				break;
			
			case "Sculpture-05.htm":
				st.set(String.valueOf(npc.getNpcId()), "1");
				break;
			
			case "1":
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), "I see someone. Is this fate?"));
				break;
			
			case "2":
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), "Don't bother trying to find out more about me. Follow your own destiny."));
				break;
			
			case "32020-06.htm":
				st.exitQuest(true);
				st.playSound(Sound.SOUND_FINISH);
				break;
			
			case "32020-08a.htm":
				st.exitQuest(true);
				st.playSound(Sound.SOUND_FINISH);
				break;
			
			case "32020-18.htm":
				if (st.getQuestItemsCount(TABLET) == 0)
				{
					st.playSound(Sound.SOUND_MIDDLE);
					st.set("cond", "11");
					htmltext = "32020-19.htm";
				}
				else
				{
					st.exitQuest(false);
					st.playSound(Sound.SOUND_FINISH);
					st.giveItems(57, 115673);
					st.rewardExpAndSp(493595, 40442);
				}
				break;
			
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
				if (player.getLevel() >= 53)
					htmltext = "32020-01.htm";
				else
					htmltext = "32020-00.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case RAFFORTY:
						if (cond == 1)
							htmltext = "32020-03.htm";
						else if (cond == 2)
							htmltext = "32020-04.htm";
						else if (cond == 3)
							htmltext = "32020-05.htm";
						else if (cond == 4)
							htmltext = "32020-11.htm";
						else if (cond == 5)
						{
							htmltext = "32020-13.htm";
							st.playSound(Sound.SOUND_MIDDLE);
							st.giveItems(LETTER2, 1);
							st.set("cond", "6");
						}
						else if (cond == 6)
							htmltext = "32020-14.htm";
						else if (cond == 9)
							htmltext = "32020-15.htm";
						else if (cond == 10)
							htmltext = "32020-17.htm";
						else if (cond == 11)
							htmltext = "32020-20.htm";
						else if (cond == 12)
						{
							htmltext = "32020-18.htm";
							st.exitQuest(false);
							st.playSound(Sound.SOUND_FINISH);
							st.giveItems(57, 60044);
						}
						break;
					
					case MISA:
						if (cond == 1)
						{
							htmltext = "32018-01.htm";
							st.giveItems(LETTER, 1);
							st.playSound(Sound.SOUND_MIDDLE);
							st.set("cond", "2");
						}
						else if (cond == 2)
							htmltext = "32018-02.htm";
						else if (cond == 6)
							htmltext = "32018-03.htm";
						else if (cond == 7)
							htmltext = "32018-05.htm";
						break;
					
					case SCULPTURE1:
						if (cond == 7)
						{
							if (npc.getNpcId() == 1)
								htmltext = "Sculpture-02.htm";
							else if (st.getInt("talk") == 1)
								htmltext = "Sculpture-06.htm";
							else
								htmltext = "Sculpture-03.htm";
						}
						else if (cond == 8)
							htmltext = "Sculpture-04.htm";
						else if (cond == 11)
						{
							st.giveItems(TABLET, 1);
							st.playSound(Sound.SOUND_MIDDLE);
							st.set("cond", "12");
							htmltext = "Sculpture-07.htm";
						}
						else if (cond == 12)
							htmltext = "Sculpture-08.htm";
						break;
					
					case SCULPTURE2:
						if (cond == 7)
						{
							if (npc.getNpcId() == 1)
								htmltext = "Sculpture-02.htm";
							else if (st.getInt("talk") == 1)
								htmltext = "Sculpture-06.htm";
							else
								htmltext = "Sculpture-03.htm";
						}
						else if (cond == 8)
							htmltext = "Sculpture-04.htm";
						else if (cond == 11)
						{
							st.giveItems(TABLET, 1);
							st.playSound(Sound.SOUND_MIDDLE);
							st.set("cond", "12");
							htmltext = "Sculpture-07.htm";
						}
						else if (cond == 12)
							htmltext = "Sculpture-08.htm";
						break;
					
					case SCULPTURE3:
						if (cond == 7)
						{
							if (npc.getNpcId() == 1)
								htmltext = "Sculpture-02.htm";
							else
								htmltext = "Sculpture-01.htm";
							st.set(String.valueOf(npc.getNpcId()), "1");
						}
						else if (cond == 8)
							htmltext = "Sculpture-04.htm";
						else if (cond == 11)
						{
							st.giveItems(TABLET, 1);
							st.playSound(Sound.SOUND_MIDDLE);
							st.set("cond", "12");
							htmltext = "Sculpture-07.htm";
						}
						else if (cond == 12)
							htmltext = "Sculpture-08.htm";
						break;
					
					case SCULPTURE4:
						if (cond == 7)
						{
							if (npc.getNpcId() == 1)
								htmltext = "Sculpture-02.htm";
							else
								htmltext = "Sculpture-01.htm";
							st.set(String.valueOf(npc.getNpcId()), "1");
						}
						else if (cond == 8)
							htmltext = "Sculpture-04.htm";
						else if (cond == 11)
						{
							st.giveItems(TABLET, 1);
							st.playSound(Sound.SOUND_MIDDLE);
							st.set("cond", "12");
							htmltext = "Sculpture-07.htm";
						}
						else if (cond == 12)
							htmltext = "Sculpture-08.htm";
						break;
					
					case KIERRE:
						if (cond == 8)
							htmltext = "32022-01.htm";
						else if (cond == 9)
							htmltext = "32022-03.htm";
						break;
				}
				break;
			case STATE_COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		return htmltext;
	}
}