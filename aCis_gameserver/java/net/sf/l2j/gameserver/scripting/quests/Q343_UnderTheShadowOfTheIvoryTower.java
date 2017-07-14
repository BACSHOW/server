package net.sf.l2j.gameserver.scripting.quests;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.base.ClassRace;
import net.sf.l2j.gameserver.model.base.ClassType;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.scripting.QuestState;
import net.sf.l2j.gameserver.scripting.quests.audio.Sound;

public class Q343_UnderTheShadowOfTheIvoryTower extends Quest
{
	private static final String qn = "Q343_UnderTheShadowOfTheIvoryTower";
	
	// NPCs
	private static final int CEMA = 30834;
	private static final int ICARUS = 30835;
	private static final int MARSHA = 30934;
	private static final int TRUMPIN = 30935;
	
	// Neutral items
	private static final int ADENA = 57;
	private static final int TOWER_SHIELD = 103;
	private static final int GHOST_STAFF = 188;
	private static final int BLOOD_OF_SAINTS = 316;
	private static final int SQUARE_SHIELD = 630;
	private static final int SOE = 736;
	private static final int RING_OF_AGES = 885;
	private static final int NECKLACE_OF_MERMAID = 917;
	private static final int EWC = 951;
	private static final int EWD = 955;
	private static final int SSD = 2510;
	private static final int SSC = 2511;
	
	// Quest items
	private static final int NEBULITE_ORB = 4364;
	private static final int ECTOPLASM_LIQUEUR = 4365;
	
	private static final String[] START_MSG =
	{
		"One~ Two~ Three~",
		"Go! One~ Two~ Three~",
		"Ready? Go! One~ Two~ Three~",
		"Here we go! One~ Two~ Three~"
	};
	
	private static final String[] TIE_MSG =
	{
		"Ah ha! A tie! Take back the orbs that you bet. Well, shall we play again?",
		"Ha! A tie! Take back the orbs that you bet. Shall we try again?"
	};
	
	private static final String[] WIN_MSG =
	{
		"Well, you certainly got lucky that time! Take all the orbs we put up as a bet. Come on! Let's play another round!",
		"Oh no! I lose! Go ahead. Take all the orbs we put up as a bet. Come on! Let's play again!",
		"Oh no! I lose! Go ahead. Take all the orbs we put up as a bet. Humph... Come on! Let's play again!"
	};
	
	private static final String[] LOSE_MSG =
	{
		"Oh, too bad. You lose! Shall we play another round?",
		"Oh...! You lose! Oh well, the orbs are mine. Shall we play another round?",
		"Oh, too bad, you lose! I'll take those orbs now... Hey now, shall we play another round?"
	};
	
	private static final String[] AGAIN_MSG =
	{
		"Play the game.",
		"Play the rock paper scissors game."
	};
	
	private static final String[][] TOSS_MSG =
	{
		{
			"You're right!",
			"You win!"
		},
		{
			"Hu wah! Right again!",
			"You won twice in a row!"
		},
		{
			"Hu wah! Right again!",
			"You won three times in a row!"
		},
		{
			"Ho ho! Right again!",
			"You won four times in a row!"
		}
	};
	
	private static final String[] OPTIONS =
	{
		"Scissors",
		"Rock",
		"Paper"
	};
	
	private static final int[] OUTCOME =
	{
		1,
		2,
		0
	};
	
	private static final String[] TOSS =
	{
		"Heads",
		"Tails"
	};
	
	private static final int[] ORBS =
	{
		10,
		30,
		70,
		150,
		310,
		0
	};
	
	public Q343_UnderTheShadowOfTheIvoryTower()
	{
		super(343, "Under The Shadow Of The Ivory Tower");
		
		setItemsIds(NEBULITE_ORB);
		
		addStartNpc(CEMA);
		addTalkId(CEMA, ICARUS, MARSHA, TRUMPIN);
		addKillId(20563, 20564, 20565, 20566);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("30834-02.htm"))
		{
			st.set("cond", "1");
			st.playSound(Sound.SOUND_ACCEPT);
			st.setState(STATE_STARTED);
		}
		else if (event.equalsIgnoreCase("30834-05.htm"))
		{
			int _orbs = st.getQuestItemsCount(NEBULITE_ORB);
			
			if (_orbs > 0)
			{
				st.takeItems(NEBULITE_ORB, _orbs);
				st.rewardItems(ADENA, _orbs * 125);
			}
			else
			{
				htmltext = "30834-09.htm";
			}
		}
		else if (event.equalsIgnoreCase("30835-02.htm"))
		{
			if (st.getQuestItemsCount(ECTOPLASM_LIQUEUR) > 0)
			{
				// Gracia Final reward system from AI.obj
				int _random = Rnd.get(1000);
				
				st.takeItems(ECTOPLASM_LIQUEUR, 1);
				
				if (_random <= 119)
				{
					st.giveItems(EWD, 1); // Scroll Enchant Weapon D
				}
				else if (_random <= 169)
				{
					st.giveItems(EWC, 1); // Scroll Enchant Weapon C
				}
				else if (_random <= 329)
				{
					st.giveItems(SSC, Rnd.get(200) + 401); // Spirit Shot: C
				}
				else if (_random <= 559)
				{
					st.giveItems(SSD, Rnd.get(200) + 401); // Spirit Shot: D
				}
				else if (_random <= 561)
				{
					st.giveItems(BLOOD_OF_SAINTS, 1); // Blood of Saints
				}
				else if (_random <= 578)
				{
					st.giveItems(SQUARE_SHIELD, 1); // Square Shield
				}
				else if (_random <= 579)
				{
					st.giveItems(GHOST_STAFF, 1); // Ghost Staff
				}
				else if (_random <= 581)
				{
					st.giveItems(RING_OF_AGES, 1); // Ring of Ages
				}
				else if (_random <= 582)
				{
					st.giveItems(TOWER_SHIELD, 1); // Tower Shield
				}
				else if (_random <= 584)
				{
					st.giveItems(NECKLACE_OF_MERMAID, 1); // Necklace of Mermaid
				}
				else
				{
					st.giveItems(SOE, 1); // Scroll of Escape
				}
				
				htmltext = "30835-02a.htm";
			}
		}
		else if (event.equalsIgnoreCase("30934-02.htm"))
		{
			if (st.getQuestItemsCount(NEBULITE_ORB) < 10)
			{
				htmltext = "30934-03a.htm";
			}
			else
			{
				st.set("rps_1sttime", "1");
			}
		}
		else if (event.equalsIgnoreCase("30934-03.htm"))
		{
			if (st.getQuestItemsCount(NEBULITE_ORB) >= 10)
			{
				st.takeItems(NEBULITE_ORB, 10);
				st.set("playing", "1");
				htmltext = getHtmlText(("30934-03.htm").replace("<msg>", START_MSG[Rnd.get(START_MSG.length)]));
			}
			else
			{
				htmltext = "30934-03a.htm";
			}
		}
		else if (event.equals("1") || event.equals("2") || event.equals("3"))
		{
			if (st.getInt("playing") == 1)
			{
				int _player = Integer.parseInt(event) - 1;
				int _marsha = Rnd.get(3);
				String[] msg;
				
				if (OUTCOME[_player] == _marsha)
				{
					msg = LOSE_MSG;
				}
				else if (OUTCOME[_marsha] == _player)
				{
					st.giveItems(NEBULITE_ORB, 20);
					msg = WIN_MSG;
				}
				else
				{
					st.giveItems(NEBULITE_ORB, 10);
					msg = TIE_MSG;
				}
				
				st.unset("playing");
				htmltext = getHtmlText(("30934-04.htm").replace("%player%", OPTIONS[_player]).replace("%marsha%", OPTIONS[_marsha]).replace("%msg%", msg[Rnd.get(msg.length)]).replace("%again%", AGAIN_MSG[Rnd.get(AGAIN_MSG.length)]));
			}
			else
			{
				htmltext = "<html><body>Player is cheating</body></html>";
				st.takeItems(NEBULITE_ORB, st.getQuestItemsCount(NEBULITE_ORB));
			}
		}
		else if (event.equalsIgnoreCase("30935-02.htm"))
		{
			if (st.getQuestItemsCount(NEBULITE_ORB) < 10)
			{
				htmltext = "30935-02a.htm";
			}
			else
			{
				st.set("ct_1sttime", "1");
			}
		}
		else if (event.equalsIgnoreCase("30935-03.htm"))
		{
			if (st.getQuestItemsCount(NEBULITE_ORB) >= 10)
			{
				st.set("toss", "1");
			}
			else
			{
				st.unset("row");
				htmltext = "30935-02a.htm";
			}
		}
		else if (event.equals("4") || event.equals("5"))
		{
			int _rnd = Rnd.get(2);
			int row;
			
			if (st.getInt("toss") == 1)
			{
				if (st.getQuestItemsCount(NEBULITE_ORB) >= 10)
				{
					if (_rnd == (Integer.parseInt(event) - 4))
					{
						row = st.getInt("row");
						
						if (row < 4)
						{
							row += 1;
							htmltext = "30935-06d.htm";
						}
						else
						{
							st.giveItems(NEBULITE_ORB, 310);
							row = 0;
							htmltext = "30935-06c.htm";
						}
					}
					else
					{
						row = 0;
						st.takeItems(NEBULITE_ORB, 10);
						htmltext = "30935-06b.htm";
					}
					
					st.set("row", Integer.toString(row));
					htmltext = getHtmlText((htmltext).replace("%toss%", TOSS[_rnd]).replace("%msg1%", TOSS_MSG[row][0]).replace("%msg2%", TOSS_MSG[row][1]).replace("%orbs%", Integer.toString(ORBS[row])).replace("%next%", Integer.toString(ORBS[row])));
				}
				else
				{
					st.unset("row");
					htmltext = "30935-02a.htm";
					st.unset("toss");
				}
			}
			else
			{
				htmltext = "<html><body>Player is cheating</body></html>";
				st.takeItems(NEBULITE_ORB, st.getQuestItemsCount(NEBULITE_ORB));
			}
		}
		else if (event.equalsIgnoreCase("quit"))
		{
			if (st.getInt("row") > 0)
			{
				int qty = st.getInt("row") - 1;
				st.giveItems(NEBULITE_ORB, ORBS[qty]);
				st.unset("row");
				htmltext = getHtmlText(("30935-06a.htm").replace("%nebulites%", Integer.toString(ORBS[qty])));
			}
			else
			{
				htmltext = "<html><body>Player is cheating</body></html>";
				st.takeItems(NEBULITE_ORB, st.getQuestItemsCount(NEBULITE_ORB));
			}
		}
		else if (event.equalsIgnoreCase("30834-06.htm") || event.equalsIgnoreCase("30834-02b.htm"))
		{
			st.playSound(Sound.SOUND_FINISH);
			st.exitQuest(true);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestState(qn);
		if (st == null)
		{
			return htmltext;
		}
		
		int _npc = npc.getNpcId();
		
		switch (st.getState())
		{
			case STATE_CREATED:
				if (isValidClass(player))
				{
					if (player.getLevel() >= 40)
					{
						htmltext = "30834-01.htm";
					}
					else
					{
						htmltext = "30834-01a.htm";
						st.exitQuest(true);
					}
				}
				else
				{
					htmltext = "30834-01b.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				switch (_npc)
				{
					case CEMA:
						if (st.hasQuestItems(NEBULITE_ORB))
						{
							htmltext = "30834-04.htm";
						}
						else
						{
							htmltext = "30834-03.htm";
						}
						break;
					
					case ICARUS:
						htmltext = "30835-01.htm";
						break;
					
					case MARSHA:
						if (st.getInt("rps_1sttime") == 1)
						{
							htmltext = "30934-01a.htm";
						}
						else
						{
							htmltext = "30934-01.htm";
						}
						break;
					
					case TRUMPIN:
						st.unset("row");
						if (st.getInt("ct_1sttime") == 1)
						{
							htmltext = "30935-01a.htm";
						}
						else
						{
							htmltext = "30935-01.htm";
						}
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		QuestState st = checkPlayerCondition(player, npc, "cond", "1");
		if (st == null)
		{
			return null;
		}
		
		st.dropItems(NEBULITE_ORB, 1, 0, 500000);
		
		return null;
	}
	
	public static boolean isValidClass(Player player)
	{
		if (player.getClassId().getType() != ClassType.FIGHTER && (player.getClassId().getRace() != ClassRace.ORC) && (player.getClassId().level() >= 1) && !player.isSubClassActive())
		{
			return true;
		}
		
		return false;
	}
	
	public static void onLoad()
	{
		new Q343_UnderTheShadowOfTheIvoryTower();
	}
}