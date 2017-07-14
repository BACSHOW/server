package net.sf.l2j.gameserver.scripting.scripts.custom;

import java.util.HashMap;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Monster;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.scripting.QuestState;
import net.sf.l2j.gameserver.scripting.quests.audio.Sound;
import net.sf.l2j.gameserver.scripting.quests.audio.Voice;

public class NewbieHelper extends Quest
{
	private final static String qn = "NewbieHelper";
	private final static String qnTutorial = "Tutorial";
	
	// Quest Items
	// Human
	private final static int RECOMMENDATION_01 = 1067;
	private final static int RECOMMENDATION_02 = 1068;
	// Elf
	private final static int LEAF_OF_MOTHERTREE = 1069;
	// Dark Elf
	private final static int BLOOD_OF_JUNDIN = 1070;
	// Dwarf
	private final static int LICENSE_OF_MINER = 1498;
	// Orc
	private final static int VOUCHER_OF_FLAME = 1496;
	
	// Items Reward
	private final static int SOULSHOT_NOVICE = 5789;
	private final static int SPIRITSHOT_NOVICE = 5790;
	private final static int BLUE_GEM = 6353;
	private final static int TOKEN = 8542;
	private final static int SCROLL = 8594;
	
	private final HashMap<String, Event> _events = new HashMap<>();
	private final HashMap<Integer, Talk> _talks = new HashMap<>();
	
	public NewbieHelper()
	{
		super(-1, "custom");
		
		addStartNpc(30009, 30019, 30131, 30400, 30530, 30575);
		
		addTalkId(30009, 30019, 30131, 30400, 30530, 30575, 30008, 30017, 30129, 30370, 30528, 30573);
		
		addFirstTalkId(new int[]
		{
			30009, // Newbie Helper - Human
			30019, // Newbie Helper - Human
			30131, // Newbie Helper - Dark Elf
			30400, // Newbie Helper - Elf
			30530, // Newbie Helper - Dwarf
			30575, // Newbie Helper - Orc
			
			30598, // Newbie Guide
			30599, // Newbie Guide
			30600, // Newbie Guide
			30601, // Newbie Guide
			30602, // Newbie Guide
			
			30008, // Grand Master - Roien - Human
			30017, // Grand Master - Gallint - Human
			30129, // Hierarch - Dark Elf
			30370, // Nerupa - Elf
			30528, // Foreman - Dwarf
			30573
			// Flame Guardian - Orc
		});
		
		addKillId(18342);
		
		_events.put("30008_02", new Event("30008-03.htm", -84058, 243239, -3730, RECOMMENDATION_01, 0x00, SOULSHOT_NOVICE, 200, 0x00, 0, 0));
		_events.put("30017_02", new Event("30017-03.htm", -84058, 243239, -3730, RECOMMENDATION_02, 0x0a, SPIRITSHOT_NOVICE, 100, 0x00, 0, 0));
		_events.put("30129_02", new Event("30129-03.htm", 12116, 16666, -4610, BLOOD_OF_JUNDIN, 0x26, SPIRITSHOT_NOVICE, 100, 0x1f, SOULSHOT_NOVICE, 200));
		_events.put("30370_02", new Event("30370-03.htm", 45491, 48359, -3086, LEAF_OF_MOTHERTREE, 0x19, SPIRITSHOT_NOVICE, 100, 0x12, SOULSHOT_NOVICE, 200));
		_events.put("30528_02", new Event("30528-03.htm", 115642, -178046, -941, LICENSE_OF_MINER, 0x35, SOULSHOT_NOVICE, 200, 0x00, 0, 0));
		_events.put("30573_02", new Event("30573-03.htm", -45067, -113549, -235, VOUCHER_OF_FLAME, 0x31, SPIRITSHOT_NOVICE, 100, 0x2c, SOULSHOT_NOVICE, 200));
		
		// Grand Master - Roien - Human
		_talks.put(30008, new Talk(0, new String[]
		{
			"30008-01.htm",
			"30008-02.htm",
			"30008-04.htm"
		}, 0, 0));
		_talks.put(30009, new Talk(0, new String[]
		{
			"newbiehelper_fig_01.htm",
			"30009-03.htm",
			"",
			"30009-04.htm"
		}, 1, RECOMMENDATION_01));
		// Grand Master - Gallint - Human
		_talks.put(30017, new Talk(0, new String[]
		{
			"30017-01.htm",
			"30017-02.htm",
			"30017-04.htm"
		}, 0, 0));
		_talks.put(30019, new Talk(0, new String[]
		{
			"newbiehelper_fig_01.htm",
			"",
			"30019-03a.htm",
			"30019-04.htm"
		}, 1, RECOMMENDATION_02));
		// Hierarch - Dark Elf
		_talks.put(30129, new Talk(2, new String[]
		{
			"30129-01.htm",
			"30129-02.htm",
			"30129-04.htm"
		}, 0, 0));
		_talks.put(30131, new Talk(2, new String[]
		{
			"newbiehelper_fig_01.htm",
			"30131-03.htm",
			"30131-03a.htm",
			"30131-04.htm"
		}, 1, BLOOD_OF_JUNDIN));
		// Nerupa - Elf
		_talks.put(30370, new Talk(1, new String[]
		{
			"30370-01.htm",
			"30370-02.htm",
			"30370-04.htm"
		}, 0, 0));
		_talks.put(30400, new Talk(1, new String[]
		{
			"newbiehelper_fig_01.htm",
			"30400-03.htm",
			"30400-03a.htm",
			"30400-04.htm"
		}, 1, LEAF_OF_MOTHERTREE));
		// Foreman - Dwarf
		_talks.put(30528, new Talk(4, new String[]
		{
			"30528-01.htm",
			"30528-02.htm",
			"30528-04.htm"
		}, 0, 0));
		_talks.put(30530, new Talk(4, new String[]
		{
			"newbiehelper_fig_01.htm",
			"30530-03.htm",
			"",
			"30530-04.htm"
		}, 1, LICENSE_OF_MINER));
		// Flame Guardian - Orc
		_talks.put(30573, new Talk(3, new String[]
		{
			"30573-01.htm",
			"30573-02.htm",
			"30573-04.htm"
		}, 0, 0));
		_talks.put(30575, new Talk(3, new String[]
		{
			"newbiehelper_fig_01.htm",
			"30575-03.htm",
			"30575-03a.htm",
			"30575-04.htm"
		}, 1, VOUCHER_OF_FLAME));
	}
	
	class Talk
	{
		public int _raceId;
		public String[] _htmlfiles;
		public int _npcTyp;
		public int _item;
		
		public Talk(int raceId, String[] htmlfiles, int npcTyp, int item)
		{
			_raceId = raceId;
			_htmlfiles = htmlfiles;
			_npcTyp = npcTyp;
			_item = item;
		}
	}
	
	class Event
	{
		public String _htmlfile;
		public int _radarX;
		public int _radarY;
		public int _radarZ;
		public int _item;
		public int _classId1;
		public int _gift1;
		public int _count1;
		public int _classId2;
		public int _gift2;
		public int _count2;
		
		public Event(String htmlfile, int x, int y, int z, int item, int classId1, int gift1, int count1, int classId2, int gift2, int count2)
		{
			_htmlfile = htmlfile;
			_radarX = x;
			_radarY = y;
			_radarZ = z;
			_item = item;
			_classId1 = classId1;
			_gift1 = gift1;
			_count1 = count1;
			_classId2 = classId2;
			_gift2 = gift2;
			_count2 = count2;
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		QuestState st = player.getQuestState(qn);
		QuestState qs = player.getQuestState(qnTutorial);
		if (st == null || qs == null)
			return null;
		
		String htmltext = event;
		player = st.getPlayer();
		
		int ex = qs.getInt("Ex");
		int classId = st.getPlayer().getClassId().getId();
		
		if (event.equalsIgnoreCase("TimerEx_NewbieHelper"))
		{
			if (ex == 0)
			{
				st.playTutorialVoice(player.isMageClass() ? Voice.TUTORIAL_VOICE_009B.getSoundName() : Voice.TUTORIAL_VOICE_009A.getSoundName());
				qs.set("Ex", "1");
			}
			else if (ex == 3)
			{
				st.playTutorialVoice(Voice.TUTORIAL_VOICE_010A.getSoundName());
				qs.set("Ex", "4");
			}
			return null;
		}
		else if (event.equalsIgnoreCase("TimerEx_GrandMaster"))
		{
			if (ex >= 4)
			{
				st.showQuestionMark(7);
				st.playSound(Sound.SOUND_TUTORIAL);
				st.playTutorialVoice(Voice.TUTORIAL_VOICE_025_1000.getSoundName());
			}
			return null;
		}
		else
		{
			Event ev = _events.get(event);
			if (ev != null)
			{
				if (ev._radarX != 0)
					st.addRadar(ev._radarX, ev._radarY, ev._radarZ);
				htmltext = ev._htmlfile;
				if (st.getQuestItemsCount(ev._item) == 1 && st.getInt("onlyone") == 0)
				{
					st.rewardExpAndSp(0, 50);
					startQuestTimer("TimerEx_GrandMaster", 60000, null, player, false);
					st.takeItems(ev._item, 1);
					if (ex <= 3)
						qs.set("Ex", "4");
					if (classId == ev._classId1)
					{
						st.giveItems(ev._gift1, ev._count1);
						st.playTutorialVoice(ev._gift1 == SPIRITSHOT_NOVICE ? Voice.TUTORIAL_VOICE_027_1000.getSoundName() : Voice.TUTORIAL_VOICE_026_1000.getSoundName());
					}
					else if (classId == ev._classId2)
						if (ev._gift2 != 0)
						{
							st.giveItems(ev._gift2, ev._count2);
							st.playTutorialVoice(Voice.TUTORIAL_VOICE_026_1000.getSoundName());
						}
					st.unset("step");
					st.set("onlyone", "1");
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		String htmltext = "";
		QuestState qs = player.getQuestState(qnTutorial);
		
		QuestState st = player.getQuestState(qn);
		if (st == null)
			st = newQuestState(player);
		
		if (qs == null || Config.DISABLE_TUTORIAL)
		{
			npc.showChatWindow(player);
			return null;
		}
		
		int npcId = npc.getNpcId();
		
		int level = player.getLevel();
		boolean isMage = player.isMageClass();
		
		int npcTyp = 0, raceId = 0, item = 0;
		String[] htmlfiles = {};
		Talk talk = _talks.get(npcId);
		try
		{
			if (talk != null)
			{
				raceId = talk._raceId;
				htmlfiles = talk._htmlfiles;
				npcTyp = talk._npcTyp;
				item = talk._item;
			}
			if ((level >= 10 || st.getInt("onlyone") == 1) && npcTyp == 1)
				htmltext = "newbiehelper_03.htm";
			else if (st.getInt("onlyone") == 0 && level < 10)
			{
				if (player.getRace().ordinal() == raceId)
				{
					htmltext = htmlfiles[0];
					if (npcTyp == 1)
					{
						if (st.getInt("step") == 0 && qs.getInt("Ex") < 0)
						{
							qs.set("Ex", "0");
							st.set("step", "1");
							startQuestTimer("TimerEx_NewbieHelper", 30000, null, player, false);
							htmltext = isMage && player.getClassId().getId() != 49 ? "newbiehelper_mage_01.htm" : "newbiehelper_mage_01a.htm";
							st.setState(STATE_STARTED);
						}
						else if (st.getInt("step") == 1 && qs.getInt("Ex") <= 2 && st.getQuestItemsCount(item) == 0)
						{
							if (st.hasAtLeastOneQuestItem(BLUE_GEM))
							{
								st.takeItems(BLUE_GEM, -1);
								st.giveItems(item, 1);
								st.set("step", "2");
								qs.set("ucMemo", "3");
								qs.set("Ex", "3");
								startQuestTimer("TimerEx_NewbieHelper", 30000, null, player, false);
								if (isMage)
								{
									st.playTutorialVoice(Voice.TUTORIAL_VOICE_027_1000.getSoundName());
									st.giveItems(SPIRITSHOT_NOVICE, 100);
									htmltext = htmlfiles[2];
									if (htmltext.equalsIgnoreCase(""))
										htmltext = "<html><body>I am sorry.  I only help warriors.  Please go to another Newbie Helper who may assist you.</body></html>";
								}
								else
								{
									st.playTutorialVoice(Voice.TUTORIAL_VOICE_026_1000.getSoundName());
									st.giveItems(SOULSHOT_NOVICE, 200);
									htmltext = htmlfiles[1];
									if (htmltext.equalsIgnoreCase(""))
										htmltext = "<html><body>I am sorry.  I only help mystics.  Please go to another Newbie Helper who may assist you.</body></html>";
								}
							}
							else
								htmltext = isMage ? "newbiehelper_mage_02.htm" : player.getClassId().getId() == 49 ? "newbiehelper_mage_02a.htm" : "newbiehelper_fig_02.htm";
						}
						else if (st.getInt("step") == 2)
							htmltext = htmlfiles[3];
					}
					else if (npcTyp == 0)
					{
						int step = st.getInt("step");
						if (step == 1)
							htmltext = htmlfiles[0];
						else if (step == 2)
							htmltext = htmlfiles[1];
						else if (step == 3)
							htmltext = htmlfiles[2];
					}
				}
			}
			else if (npcId >= 30598 && npcId <= 30602)
			{
				if (qs.getInt("reward") == 0 && st.getInt("onlyone") == 1)
				{
					st.playTutorialVoice(isMage ? Voice.TUTORIAL_VOICE_027_1000.getSoundName() : Voice.TUTORIAL_VOICE_026_1000.getSoundName());
					st.giveItems(isMage ? SPIRITSHOT_NOVICE : SOULSHOT_NOVICE, isMage ? 100 : 200);
					st.giveItems(TOKEN, 12);
					st.giveItems(SCROLL, 2);
					qs.set("reward", "1");
					st.setState(Quest.STATE_COMPLETED);
				}
				npc.showChatWindow(player);
				return null;
			}
			else if (npcTyp == 0 && st.getState() == Quest.STATE_COMPLETED)
				htmltext = "" + npcId + "-04.htm";
			if (htmltext == null || htmltext.equalsIgnoreCase(""))
			{
				npc.showChatWindow(player);
				return null;
			}
		}
		catch (Exception ex)
		{
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		QuestState st = checkPlayerState(player, npc, STATE_STARTED);
		QuestState qs = player.getQuestState(qnTutorial);
		if (st == null || qs == null)
			return null;
		
		int ex = qs.getInt("Ex");
		
		if (ex <= 1)
		{
			qs.playTutorialVoice(Voice.TUTORIAL_VOICE_011_1000.getSoundName());
			qs.showQuestionMark(3);
			qs.set("Ex", "2");
		}
		if (ex <= 2 && st.getState() == Quest.STATE_STARTED && qs.getInt("Gemstone") == 0)
			if (Rnd.get(100) < 25)
			{
				((Monster) npc).dropItem(player, new IntIntHolder(BLUE_GEM, 1));
				st.playSound(Sound.SOUND_TUTORIAL);
			}
		return null;
	}
}