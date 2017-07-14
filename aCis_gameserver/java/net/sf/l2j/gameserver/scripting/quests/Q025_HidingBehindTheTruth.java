package net.sf.l2j.gameserver.scripting.quests;

import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.scripting.QuestState;
import net.sf.l2j.gameserver.scripting.quests.audio.Sound;

public class Q025_HidingBehindTheTruth extends Quest
{
	private static final int AGRIPEL = 31348;
	private static final int BENEDICT = 31349;
	private static final int BROKEN_BOOK_SHELF = 31534;
	private static final int COFFIN = 31536;
	private static final int MAID_OF_LIDIA = 31532;
	private static final int MYSTERIOUS_WIZARD = 31522;
	private static final int TOMBSTONE = 31531;
	
	private static final int CONTRACT = 7066;
	private static final int EARRING_OF_BLESSING = 874;
	private static final int GEMSTONE_KEY = 7157;
	private static final int LIDIAS_DRESS = 7155;
	private static final int MAP_FOREST_OF_DEADMAN = 7063;
	private static final int NECKLACE_OF_BLESSING = 936;
	private static final int RING_OF_BLESSING = 905;
	private static final int SUSPICIOUS_TOTEM_DOLL_1 = 7151;
	private static final int SUSPICIOUS_TOTEM_DOLL_2 = 7156;
	private static final int SUSPICIOUS_TOTEM_DOLL_3 = 7158;
	private static final int TRIOLS_PAWN = 27218;
	private Npc COFFIN_SPAWN = null;
	
	public Q025_HidingBehindTheTruth()
	{
		super(25, "Hiding Behind the Truth");
		
		addStartNpc(BENEDICT);
		addTalkId(AGRIPEL, BROKEN_BOOK_SHELF, COFFIN, MAID_OF_LIDIA, MYSTERIOUS_WIZARD, TOMBSTONE);
		addKillId(TRIOLS_PAWN);
		
		setItemsIds(SUSPICIOUS_TOTEM_DOLL_3);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(getName());
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("StartQuest"))
		{
			if (st.getInt("cond") == 0)
			{
				st.setState(Quest.STATE_STARTED);
			}
			QuestState qs_24 = st.getPlayer().getQuestState("Q024_InhabitantsOfTheForestOfTheDead");
			if ((qs_24 == null) || !qs_24.isCompleted())
			{
				st.set("cond", "1");
				st.playSound(Sound.SOUND_ACCEPT);
				return "31349-02.htm";
			}
			st.playSound(Sound.SOUND_ACCEPT);
			if (st.getQuestItemsCount(SUSPICIOUS_TOTEM_DOLL_1) == 0)
			{
				st.set("cond", "2");
				st.playSound(Sound.SOUND_MIDDLE);
				return "31349-03a.htm";
			}
			return "31349-03.htm";
		}
		else if (event.equalsIgnoreCase("31349-10.htm"))
		{
			st.set("cond", "4");
		}
		else if (event.equalsIgnoreCase("31348-08.htm"))
		{
			if (st.getInt("cond") == 4)
			{
				st.set("cond", "5");
				st.playSound(Sound.SOUND_MIDDLE);
				st.takeItems(SUSPICIOUS_TOTEM_DOLL_1, -1);
				st.takeItems(SUSPICIOUS_TOTEM_DOLL_2, -1);
				if (st.getQuestItemsCount(GEMSTONE_KEY) == 0)
				{
					st.giveItems(GEMSTONE_KEY, 1);
				}
			}
			else if (st.getInt("cond") == 5)
			{
				return "31348-08a.htm";
			}
		}
		else if (event.equalsIgnoreCase("31522-04.htm"))
		{
			st.set("cond", "6");
			st.playSound(Sound.SOUND_MIDDLE);
			if (st.getQuestItemsCount(MAP_FOREST_OF_DEADMAN) == 0)
			{
				st.giveItems(MAP_FOREST_OF_DEADMAN, 1);
			}
		}
		else if (event.equalsIgnoreCase("31534-07.htm"))
		{
			addSpawn(TRIOLS_PAWN, player.getX() + 50, player.getY() + 50, player.getZ(), player.getHeading(), false, 0, false);
			st.set("cond", "7");
			st.playSound(Sound.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("31534-11.htm"))
		{
			st.set("id", "8");
			st.giveItems(CONTRACT, 1);
		}
		else if (event.equalsIgnoreCase("31532-07.htm"))
		{
			st.set("cond", "11");
		}
		else if (event.equalsIgnoreCase("31531-02.htm"))
		{
			st.set("cond", "12");
			st.playSound(Sound.SOUND_MIDDLE);
			if (COFFIN_SPAWN != null)
			{
				COFFIN_SPAWN.deleteMe();
			}
			COFFIN_SPAWN = addSpawn(COFFIN, player.getX() + 50, player.getY() + 50, player.getZ(), player.getHeading(), false, 0, false);
			
			startQuestTimer("Coffin_Despawn", 120000, null, null, false);
		}
		else if (event.equalsIgnoreCase("Coffin_Despawn"))
		{
			if (COFFIN_SPAWN != null)
				COFFIN_SPAWN.deleteMe();
			
			if (st.getInt("cond") == 12)
			{
				st.set("cond", "11");
				st.playSound(Sound.SOUND_MIDDLE);
			}
			return null;
		}
		else if (event.equalsIgnoreCase("Lidia_wait"))
		{
			st.set("id", "14");
			return null;
		}
		else if (event.equalsIgnoreCase("31532-21.htm"))
		{
			st.set("cond", "15");
		}
		else if (event.equalsIgnoreCase("31522-13.htm"))
		{
			st.set("cond", "16");
		}
		else if (event.equalsIgnoreCase("31348-16.htm"))
		{
			st.set("cond", "16");
		}
		else if (event.equalsIgnoreCase("31348-17.htm"))
		{
			st.set("cond", "17");
		}
		else if (event.equalsIgnoreCase("31348-14.htm"))
		{
			st.set("id", "16");
		}
		else if (event.equalsIgnoreCase("End1"))
		{
			if (st.getInt("cond") != 17)
			{
				return "31532-24.htm";
			}
			st.giveItems(RING_OF_BLESSING, 2);
			st.giveItems(EARRING_OF_BLESSING, 1);
			st.rewardExpAndSp(572277, 53750);
			st.exitQuest(false);
			st.unset("cond");
			return "31532-25.htm";
		}
		else if (event.equalsIgnoreCase("End2"))
		{
			if (st.getInt("cond") != 18)
			{
				return "31522-15a.htm";
			}
			st.giveItems(NECKLACE_OF_BLESSING, 1);
			st.giveItems(EARRING_OF_BLESSING, 1);
			st.rewardExpAndSp(572277, 53750);
			st.playSound(Sound.SOUND_FINISH);
			st.exitQuest(false);
			st.unset("cond");
			return "31522-16.htm";
		}
		return event;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg();
		final QuestState st = player.getQuestState(getName());
		if (st == null)
			return htmltext;
		
		int IntId = st.getInt("id");
		int cond = st.getInt("cond");
		switch (npc.getNpcId())
		{
			case BENEDICT:
				if ((cond == 0) || (cond == 1))
					return "31349-01.htm";
				else if (cond == 2)
					return st.getQuestItemsCount(SUSPICIOUS_TOTEM_DOLL_1) == 0 ? "31349-03a.htm" : "31349-03.htm";
				else if (cond == 3)
					return "31349-03.htm";
				else if (cond == 4)
					return "31349-11.htm";
				break;
			
			case MYSTERIOUS_WIZARD:
				if (cond == 2)
				{
					st.set("cond", "3");
					st.giveItems(SUSPICIOUS_TOTEM_DOLL_2, 1);
					return "31522-01.htm";
				}
				else if (cond == 3)
					return "31522-02.htm";
				else if (cond == 5)
					return "31522-03.htm";
				else if (cond == 6)
					return "31522-05.htm";
				else if (cond == 8)
				{
					if (IntId != 8)
					{
						return "31522-05.htm";
					}
					st.set("cond", "9");
					st.playSound(Sound.SOUND_MIDDLE);
					return "31522-06.htm";
				}
				else if (cond == 15)
					return "31522-06a.htm";
				else if (cond == 16)
					return "31522-12.htm";
				else if (cond == 17)
					return "31522-15a.htm";
				else if (cond == 18)
				{
					st.set("id", "18");
					return "31522-15.htm";
				}
				break;
			
			case AGRIPEL:
				if (cond == 4)
					return "31348-01.htm";
				else if (cond == 5)
					return "31348-03.htm";
				else if (cond == 16)
					return IntId == 16 ? "31348-15.htm" : "31348-09.htm";
				else if ((cond == 17) || (cond == 18))
					return "31348-15.htm";
				
				break;
			
			case BROKEN_BOOK_SHELF:
				if (cond == 6)
					return "31534-01.htm";
				else if (cond == 7)
					return "31534-08.htm";
				else if (cond == 8)
					return IntId == 8 ? "31534-06.htm" : "31534-10.htm";
				break;
			
			case MAID_OF_LIDIA:
				if (cond == 9)
					return st.getQuestItemsCount(CONTRACT) > 0 ? "31532-01.htm" : "You have no Contract...";
				else if ((cond == 11) || (cond == 12))
					return "31532-08.htm";
				else if (cond == 13)
				{
					if (st.getQuestItemsCount(LIDIAS_DRESS) == 0)
					{
						return "31532-08.htm";
					}
					st.set("cond", "14");
					st.playSound(Sound.SOUND_MIDDLE);
					startQuestTimer("Lidia_wait", 60000, null, null, false);
					st.takeItems(LIDIAS_DRESS, 1);
					return "31532-09.htm";
				}
				else if (cond == 14)
					return IntId == 14 ? "31532-10.htm" : "31532-09.htm";
				else if (cond == 17)
				{
					st.set("id", "17");
					return "31532-23.htm";
				}
				else if (cond == 18)
					return "31532-24.htm";
				break;
			
			case TOMBSTONE:
				if (cond == 11)
					return "31531-01.htm";
				else if (cond == 12)
					return "31531-02.htm";
				else if (cond == 13)
					return "31531-03.htm";
				break;
			
			case COFFIN:
				if (cond == 12)
				{
					st.set("cond", "13");
					st.playSound(Sound.SOUND_MIDDLE);
					st.giveItems(LIDIAS_DRESS, 1);
					return "31536-01.htm";
				}
				else if (cond == 13)
					return "31531-03.htm";
				break;
		}
		return htmltext;
	}
	
	@Override
	public final String onKill(Npc npc, Player player, boolean isPet)
	{
		QuestState st = checkPlayerState(player, npc, STATE_STARTED);
		if (st == null)
			return null;
		
		int cond = st.getInt("cond");
		
		if ((npc.getNpcId() == TRIOLS_PAWN) && (cond == 7))
		{
			st.giveItems(SUSPICIOUS_TOTEM_DOLL_3, 1);
			st.playSound(Sound.SOUND_MIDDLE);
			st.set("cond", "7");
		}
		
		return null;
	}
	
}