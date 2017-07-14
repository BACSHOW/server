package net.sf.l2j.gameserver.scripting.quests;

import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.scripting.QuestState;
import net.sf.l2j.gameserver.scripting.quests.audio.Sound;
import net.sf.l2j.gameserver.scripting.quests.audio.Voice;

public class Tutorial extends Quest
{
	// table for Quest Timer ( Ex == -2 ) [raceId, voice, html]
	public final String[][] QTEXMTWO =
	{
		{
			"0",
			Voice.TUTORIAL_VOICE_001A_2000.getSoundName(),
			"tutorial_human_fighter001.htm"
		},
		{
			"10",
			Voice.TUTORIAL_VOICE_001B_2000.getSoundName(),
			"tutorial_human_mage001.htm"
		},
		{
			"18",
			Voice.TUTORIAL_VOICE_001C_2000.getSoundName(),
			"tutorial_elven_fighter001.htm"
		},
		{
			"25",
			Voice.TUTORIAL_VOICE_001D_2000.getSoundName(),
			"tutorial_elven_mage001.htm"
		},
		{
			"31",
			Voice.TUTORIAL_VOICE_001E_2000.getSoundName(),
			"tutorial_delf_fighter001.htm"
		},
		{
			"38",
			Voice.TUTORIAL_VOICE_001F_2000.getSoundName(),
			"tutorial_delf_mage001.htm"
		},
		{
			"44",
			Voice.TUTORIAL_VOICE_001G_2000.getSoundName(),
			"tutorial_orc_fighter001.htm"
		},
		{
			"49",
			Voice.TUTORIAL_VOICE_001H_2000.getSoundName(),
			"tutorial_orc_mage001.htm"
		},
		{
			"53",
			Voice.TUTORIAL_VOICE_001I_2000.getSoundName(),
			"tutorial_dwarven_fighter001.htm"
		}
	};
	
	// table for Client Event Enable (8) [raceId, html, x, y, z]
	public final String[][] CEEa =
	{
		{
			"0",
			"tutorial_human_fighter007.htm",
			"-71424",
			"258336",
			"-3109"
		},
		{
			"10",
			"tutorial_human_mage007.htm",
			"-91036",
			"248044",
			"-3568"
		},
		{
			"18",
			"tutorial_elf007.htm",
			"46112",
			"41200",
			"-3504"
		},
		{
			"25",
			"tutorial_elf007.htm",
			"46112",
			"41200",
			"-3504"
		},
		{
			"31",
			"tutorial_delf007.htm",
			"28384",
			"11056",
			"-4233"
		},
		{
			"38",
			"tutorial_delf007.htm",
			"28384",
			"11056",
			"-4233"
		},
		{
			"44",
			"tutorial_orc007.htm",
			"-56736",
			"-113680",
			"-672"
		},
		{
			"49",
			"tutorial_orc007.htm",
			"-56736",
			"-113680",
			"-672"
		},
		{
			"53",
			"tutorial_dwarven_fighter007.htm",
			"108567",
			"-173994",
			"-406"
		}
	};
	
	// table for Question Mark Clicked (9 & 11) learning skills [raceId, html, x, y, z]
	public final String[][] QMCa =
	{
		{
			"0",
			"tutorial_fighter017.htm",
			"-83165",
			"242711",
			"-3720"
		},
		{
			"10",
			"tutorial_mage017.htm",
			"-85247",
			"244718",
			"-3720"
		},
		{
			"18",
			"tutorial_fighter017.htm",
			"45610",
			"52206",
			"-2792"
		},
		{
			"25",
			"tutorial_mage017.htm",
			"45610",
			"52206",
			"-2792"
		},
		{
			"31",
			"tutorial_fighter017.htm",
			"10344",
			"14445",
			"-4242"
		},
		{
			"38",
			"tutorial_mage017.htm",
			"10344",
			"14445",
			"-4242"
		},
		{
			"44",
			"tutorial_fighter017.htm",
			"-46324",
			"-114384",
			"-200"
		},
		{
			"49",
			"tutorial_fighter017.htm",
			"-46305",
			"-112763",
			"-200"
		},
		{
			"53",
			"tutorial_fighter017.htm",
			"115447",
			"-182672",
			"-1440"
		}
	};
	
	// table for Question Mark Clicked (24) newbie lvl [raceId, html]
	public final Map<Integer, String> QMCb = new HashMap<>();
	
	// table for Question Mark Clicked (35) 1st class transfer [raceId, html]
	public final Map<Integer, String> QMCc = new HashMap<>();
	
	// table for Tutorial Close Link (26) 2nd class transfer [raceId, html]
	public final Map<Integer, String> TCLa = new HashMap<>();
	
	// table for Tutorial Close Link (23) 2nd class transfer [raceId, html]
	public final Map<Integer, String> TCLb = new HashMap<>();
	
	// table for Tutorial Close Link (24) 2nd class transfer [raceId, html]
	public final Map<Integer, String> TCLc = new HashMap<>();
	
	public static final String qn = "Tutorial";
	
	public Tutorial()
	{
		super(-1, "Tutorial");
		
		QMCb.put(0, "tutorial_human009.htm");
		QMCb.put(10, "tutorial_human009.htm");
		QMCb.put(18, "tutorial_elf009.htm");
		QMCb.put(25, "tutorial_elf009.htm");
		QMCb.put(31, "tutorial_delf009.htm");
		QMCb.put(38, "tutorial_delf009.htm");
		QMCb.put(44, "tutorial_orc009.htm");
		QMCb.put(49, "tutorial_orc009.htm");
		QMCb.put(53, "tutorial_dwarven009.htm");
		
		QMCc.put(0, "tutorial_21.htm");
		QMCc.put(10, "tutorial_21a.htm");
		QMCc.put(18, "tutorial_21b.htm");
		QMCc.put(25, "tutorial_21c.htm");
		QMCc.put(31, "tutorial_21g.htm");
		QMCc.put(38, "tutorial_21h.htm");
		QMCc.put(44, "tutorial_21d.htm");
		QMCc.put(49, "tutorial_21e.htm");
		QMCc.put(53, "tutorial_21f.htm");
		
		TCLa.put(1, "tutorial_22w.htm");
		TCLa.put(4, "tutorial_22.htm");
		TCLa.put(7, "tutorial_22b.htm");
		TCLa.put(11, "tutorial_22c.htm");
		TCLa.put(15, "tutorial_22d.htm");
		TCLa.put(19, "tutorial_22e.htm");
		TCLa.put(22, "tutorial_22f.htm");
		TCLa.put(26, "tutorial_22g.htm");
		TCLa.put(29, "tutorial_22h.htm");
		TCLa.put(32, "tutorial_22n.htm");
		TCLa.put(35, "tutorial_22o.htm");
		TCLa.put(39, "tutorial_22p.htm");
		TCLa.put(42, "tutorial_22q.htm");
		TCLa.put(45, "tutorial_22i.htm");
		TCLa.put(47, "tutorial_22j.htm");
		TCLa.put(50, "tutorial_22k.htm");
		TCLa.put(54, "tutorial_22l.htm");
		TCLa.put(56, "tutorial_22m.htm");
		
		TCLb.put(4, "tutorial_22aa.htm");
		TCLb.put(7, "tutorial_22ba.htm");
		TCLb.put(11, "tutorial_22ca.htm");
		TCLb.put(15, "tutorial_22da.htm");
		TCLb.put(19, "tutorial_22ea.htm");
		TCLb.put(22, "tutorial_22fa.htm");
		TCLb.put(26, "tutorial_22ga.htm");
		TCLb.put(32, "tutorial_22na.htm");
		TCLb.put(35, "tutorial_22oa.htm");
		TCLb.put(39, "tutorial_22pa.htm");
		TCLb.put(50, "tutorial_22ka.htm");
		
		TCLc.put(4, "tutorial_22ab.htm");
		TCLc.put(7, "tutorial_22bb.htm");
		TCLc.put(11, "tutorial_22cb.htm");
		TCLc.put(15, "tutorial_22db.htm");
		TCLc.put(19, "tutorial_22eb.htm");
		TCLc.put(22, "tutorial_22fb.htm");
		TCLc.put(26, "tutorial_22gb.htm");
		TCLc.put(32, "tutorial_22nb.htm");
		TCLc.put(35, "tutorial_22ob.htm");
		TCLc.put(39, "tutorial_22pb.htm");
		TCLc.put(50, "tutorial_22kb.htm");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		QuestState st = player.getQuestState(qn);
		
		if (Config.DISABLE_TUTORIAL)
			return "";
		
		if (st == null)
			return null;
		
		String html = "";
		
		int classId = player.getClassId().getId();
		int Ex = st.getInt("Ex");
		
		if (event.startsWith("UC"))
		{
			if (player.getLevel() < 6 && st.getInt("onlyone") == 0)
			{
				switch (st.getInt("ucMemo"))
				{
					case 0:
						st.set("ucMemo", "0");
						startQuestTimer("QT", 10000, null, player, false);
						st.set("Ex", "-2");
						break;
					case 1:
						st.showQuestionMark(1);
						st.playTutorialVoice(Voice.TUTORIAL_VOICE_006_1000.getSoundName());
						st.playSound(Sound.SOUND_TUTORIAL);
						break;
					case 2:
						if (Ex == 2)
							st.showQuestionMark(3);
						else if (st.getQuestItemsCount(6353) > 0)
							st.showQuestionMark(5);
						st.playSound(Sound.SOUND_TUTORIAL);
						break;
					case 3:
						st.showQuestionMark(12);
						st.playSound(Sound.SOUND_TUTORIAL);
						st.onTutorialClientEvent(0);
						break;
				}
			}
		}
		else if (event.startsWith("QT"))
		{
			if (Ex == -2)
			{
				String voice = "";
				for (String[] element : QTEXMTWO)
					if (classId == Integer.valueOf(element[0]))
					{
						voice = element[1];
						html = element[2];
					}
				if (st.getQuestItemsCount(5588) == 0)
					st.giveItems(5588, 1);
				st.playTutorialVoice(voice);
				st.set("Ex", "-3");
				cancelQuestTimers("QT");
				startQuestTimer("QT", 30000, null, player, false);
			}
			else if (Ex == -3)
			{
				st.playTutorialVoice(Voice.TUTORIAL_VOICE_002_1000.getSoundName());
				st.set("Ex", "0");
			}
			else if (Ex == -4)
			{
				st.playTutorialVoice(Voice.TUTORIAL_VOICE_008_1000.getSoundName());
				st.set("Ex", "-5");
			}
		}
		// Tutorial close
		else if (event.startsWith("TE"))
		{
			cancelQuestTimers("TE");
			if (!event.equalsIgnoreCase("TE"))
				switch (Integer.valueOf(event.substring(2)))
				{
					case 0:
						st.closeTutorialHtml();
						break;
					case 1:
						st.closeTutorialHtml();
						st.playTutorialVoice(Voice.TUTORIAL_VOICE_006_1000.getSoundName());
						st.showQuestionMark(1);
						st.playSound(Sound.SOUND_TUTORIAL);
						startQuestTimer("QT", 30000, null, player, false);
						st.set("Ex", "-4");
						break;
					case 2:
						st.playTutorialVoice(Voice.TUTORIAL_VOICE_003_2000.getSoundName());
						html = "tutorial_02.htm";
						st.onTutorialClientEvent(1);
						st.set("Ex", "-5");
						break;
					case 3:
						html = "tutorial_03.htm";
						st.onTutorialClientEvent(2);
						break;
					case 5:
						html = "tutorial_05.htm";
						st.onTutorialClientEvent(8);
						break;
					case 7:
						html = "tutorial_100.htm";
						st.onTutorialClientEvent(0);
						break;
					case 8:
						html = "tutorial_101.htm";
						st.onTutorialClientEvent(0);
						break;
					case 10:
						html = "tutorial_103.htm";
						st.onTutorialClientEvent(0);
						break;
					case 12:
						st.closeTutorialHtml();
						break;
					case 23:
						if (TCLb.containsKey(classId))
							html = TCLb.get(classId);
						break;
					case 24:
						if (TCLc.containsKey(classId))
							html = TCLc.get(classId);
						break;
					case 25:
						html = "tutorial_22cc.htm";
						break;
					case 26:
						if (TCLa.containsKey(classId))
							html = TCLa.get(classId);
						break;
					case 27:
						html = "tutorial_29.htm";
						break;
					case 28:
						html = "tutorial_28.htm";
						break;
				}
		}
		// Client Event
		else if (event.startsWith("CE"))
		{
			int event_id = Integer.valueOf(event.substring(2));
			if (event_id == 1 && player.getLevel() < 6)
			{
				st.playTutorialVoice(Voice.TUTORIAL_VOICE_004_5000.getSoundName());
				html = "tutorial_03.htm";
				st.playSound(Sound.SOUND_TUTORIAL);
				st.onTutorialClientEvent(2);
			}
			else if (event_id == 2 && player.getLevel() < 6)
			{
				st.playTutorialVoice(Voice.TUTORIAL_VOICE_005_5000.getSoundName());
				html = "tutorial_05.htm";
				st.playSound(Sound.SOUND_TUTORIAL);
				st.onTutorialClientEvent(8);
			}
			else if (event_id == 8 && player.getLevel() < 6)
			{
				int x = 0;
				int y = 0;
				int z = 0;
				for (String[] element : CEEa)
					if (classId == Integer.valueOf(element[0]))
					{
						html = element[1];
						x = Integer.valueOf(element[2]);
						y = Integer.valueOf(element[3]);
						z = Integer.valueOf(element[4]);
					}
				if (x != 0)
				{
					st.playSound(Sound.SOUND_TUTORIAL);
					st.addRadar(x, y, z);
					st.playTutorialVoice(Voice.TUTORIAL_VOICE_007_3500.getSoundName());
					st.set("ucMemo", "1");
					st.set("Ex", "-5");
				}
			}
			else if (event_id == 30 && player.getLevel() < 10 && st.getInt("Die") == 0)
			{
				st.playTutorialVoice(Voice.TUTORIAL_VOICE_016_1000.getSoundName());
				st.playSound(Sound.SOUND_TUTORIAL);
				st.set("Die", "1");
				st.showQuestionMark(8);
				st.onTutorialClientEvent(0);
			}
			else if (event_id == 800000 && player.getLevel() < 6 && st.getInt("sit") == 0)
			{
				st.playTutorialVoice(Voice.TUTORIAL_VOICE_018_1000.getSoundName());
				st.playSound(Sound.SOUND_TUTORIAL);
				st.set("sit", "1");
				st.onTutorialClientEvent(0);
				html = "tutorial_21z.htm";
			}
			else if (event_id == 40)
			{
				switch (player.getLevel())
				{
					case 5:
						if (st.getInt("lvl") < 5 && !player.isMageClass() || classId == 49)
						{
							st.playTutorialVoice(Voice.TUTORIAL_VOICE_014_1000.getSoundName());
							st.showQuestionMark(9);
							st.playSound(Sound.SOUND_TUTORIAL);
							st.set("lvl", "5");
						}
						break;
					case 6:
						if (st.getInt("lvl") < 6 && player.getClassId().level() == 0)
						{
							st.playTutorialVoice(Voice.TUTORIAL_VOICE_020_1000.getSoundName());
							st.playSound(Sound.SOUND_TUTORIAL);
							st.showQuestionMark(24);
							st.set("lvl", "6");
						}
						break;
					case 7:
						if (st.getInt("lvl") < 7 && player.isMageClass() && classId != 49 && player.getClassId().level() == 0)
						{
							st.playTutorialVoice(Voice.TUTORIAL_VOICE_019_1000.getSoundName());
							st.playSound(Sound.SOUND_TUTORIAL);
							st.set("lvl", "7");
							st.showQuestionMark(11);
						}
						break;
					case 15:
						if (st.getInt("lvl") < 15)
						{
							st.playSound(Sound.SOUND_TUTORIAL);
							st.set("lvl", "15");
							st.showQuestionMark(33);
						}
						break;
					case 19:
						if (st.getInt("lvl") < 19 && player.getClassId().level() == 0)
							switch (classId)
							{
								case 0:
								case 10:
								case 18:
								case 25:
								case 31:
								case 38:
								case 44:
								case 49:
								case 52:
									st.playSound(Sound.SOUND_TUTORIAL);
									st.set("lvl", "19");
									st.showQuestionMark(35);
							}
						break;
					case 35:
						if (st.getInt("lvl") < 35 && player.getClassId().level() == 1)
							switch (classId)
							{
								case 1:
								case 4:
								case 7:
								case 11:
								case 15:
								case 19:
								case 22:
								case 26:
								case 29:
								case 32:
								case 35:
								case 39:
								case 42:
								case 45:
								case 47:
								case 50:
								case 54:
								case 56:
									st.playSound(Sound.SOUND_TUTORIAL);
									st.set("lvl", "35");
									st.showQuestionMark(34);
							}
						break;
				}
			}
			else if (event_id == 45 && player.getLevel() < 10 && st.getInt("HP") == 0)
			{
				st.playTutorialVoice(Voice.TUTORIAL_VOICE_017_1000.getSoundName());
				st.playSound(Sound.SOUND_TUTORIAL);
				st.set("HP", "1");
				st.showQuestionMark(10);
				st.onTutorialClientEvent(800000);
			}
			else if (event_id == 57 && player.getLevel() < 6 && st.getInt("Adena") == 0)
			{
				st.playTutorialVoice(Voice.TUTORIAL_VOICE_012_1000.getSoundName());
				st.playSound(Sound.SOUND_TUTORIAL);
				st.set("Adena", "1");
				st.showQuestionMark(23);
			}
			else if (event_id == 6353 && player.getLevel() < 6 && st.getInt("Gemstone") == 0)
			{
				st.playTutorialVoice(Voice.TUTORIAL_VOICE_013_1000.getSoundName());
				st.playSound(Sound.SOUND_TUTORIAL);
				st.set("Gemstone", "1");
				st.showQuestionMark(5);
			}
			else if (event_id == 1048576 && player.getLevel() < 6)
			{
				st.showQuestionMark(5);
				st.playTutorialVoice(Voice.TUTORIAL_VOICE_013_1000.getSoundName());
				st.playSound(Sound.SOUND_TUTORIAL);
			}
		}
		// Question mark clicked
		else if (event.startsWith("QM"))
		{
			int x = 0;
			int y = 0;
			int z = 0;
			switch (Integer.valueOf(event.substring(2)))
			{
				case 1:
					st.playTutorialVoice(Voice.TUTORIAL_VOICE_007_3500.getSoundName());
					st.set("Ex", "-5");
					for (String[] element : CEEa)
						if (classId == Integer.valueOf(element[0]))
						{
							html = element[1];
							x = Integer.valueOf(element[2]);
							y = Integer.valueOf(element[3]);
							z = Integer.valueOf(element[4]);
						}
					st.addRadar(x, y, z);
					break;
				case 3:
					html = "tutorial_09.htm";
					st.onTutorialClientEvent(1048576);
					break;
				case 5:
					for (String[] element : CEEa)
						if (classId == Integer.valueOf(element[0]))
						{
							html = element[1];
							x = Integer.valueOf(element[2]);
							y = Integer.valueOf(element[3]);
							z = Integer.valueOf(element[4]);
						}
					st.addRadar(x, y, z);
					html = "tutorial_11.htm";
					break;
				case 7:
					html = "tutorial_15.htm";
					st.set("ucMemo", "3");
					break;
				case 8:
					html = "tutorial_18.htm";
					break;
				case 9:
					for (String[] element : QMCa)
						if (classId == Integer.valueOf(element[0]))
						{
							html = element[1];
							x = Integer.valueOf(element[2]);
							y = Integer.valueOf(element[3]);
							z = Integer.valueOf(element[4]);
						}
					if (x != 0)
						st.addRadar(x, y, z);
					break;
				case 10:
					html = "tutorial_19.htm";
					break;
				case 11:
					for (String[] element : QMCa)
						if (classId == Integer.valueOf(element[0]))
						{
							html = element[1];
							x = Integer.valueOf(element[2]);
							y = Integer.valueOf(element[3]);
							z = Integer.valueOf(element[4]);
						}
					if (x != 0)
						st.addRadar(x, y, z);
					break;
				case 12:
					html = "tutorial_15.htm";
					st.set("ucMemo", "4");
					break;
				case 17:
					html = "tutorial_30.htm";
					break;
				case 23:
					html = "tutorial_24.htm";
					break;
				case 24:
					if (QMCb.containsKey(classId))
						html = QMCb.get(classId);
					break;
				case 26:
					if (player.isMageClass() && classId != 49)
						html = "tutorial_newbie004b.htm";
					else
						html = "tutorial_newbie004a.htm";
					break;
				case 33:
					html = "tutorial_27.htm";
					break;
				case 34:
					html = "tutorial_28.htm";
					break;
				case 35:
					if (QMCc.containsKey(classId))
						html = QMCc.get(classId);
					break;
			}
		}
		
		if (html.isEmpty())
			return null;
		st.showTutorialHTML(html);
		return null;
	}
}