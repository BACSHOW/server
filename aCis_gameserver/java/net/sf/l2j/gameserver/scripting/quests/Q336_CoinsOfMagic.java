package net.sf.l2j.gameserver.scripting.quests;

import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.commons.math.MathUtil;

import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.scripting.QuestState;
import net.sf.l2j.gameserver.scripting.quests.audio.Sound;

public class Q336_CoinsOfMagic extends Quest
{
	private static final String qn = "Q336_CoinsOfMagic";
	
	// Items
	private static final int COIN_DIAGRAM = 3811;
	private static final int KALDIS_COIN = 3812;
	private static final int MEMBERSHIP_1 = 3813;
	private static final int MEMBERSHIP_2 = 3814;
	private static final int MEMBERSHIP_3 = 3815;
	
	// Coins
	private static final int BLOOD_MEDUSA = 3472;
	private static final int BLOOD_WEREWOLF = 3473;
	private static final int BLOOD_BASILISK = 3474;
	private static final int BLOOD_DREVANUL = 3475;
	private static final int BLOOD_SUCCUBUS = 3476;
	private static final int GOLD_WYVERN = 3482;
	private static final int GOLD_KNIGHT = 3483;
	private static final int GOLD_GIANT = 3484;
	private static final int GOLD_DRAKE = 3485;
	private static final int GOLD_WYRM = 3486;
	private static final int SILVER_UNICORN = 3490;
	private static final int SILVER_FAIRY = 3491;
	private static final int SILVER_DRYAD = 3492;
	private static final int SILVER_GOLEM = 3494;
	private static final int SILVER_UNDINE = 3495;
	
	// Exchange coins
	private static final int BELETHS_BLOOD = 0000;
	private static final int BELETHS_GOLD = 0000;
	private static final int BELETHS_SILVER = 0000;
	private static final int BLOOD_DRAGON = 0000;
	private static final int GOLD_DRAGON = 0000;
	private static final int SILVER_DRAGON = 0000;
	private static final int MANAKS_BLOOD_WEREWOLF = 0000;
	private static final int MANAKS_GOLD_GIANT = 0000;
	private static final int MANAKS_SILVER_DRYAD = 0000;
	private static final int NIAS_BLOOD_MEDUSA = 0000;
	private static final int NIAS_GOLD_WYVERN = 0000;
	private static final int NIAS_SILVER_FAIRY = 0000;
	
	// Talk NPC
	private static final int SORINT = 30232;
	private static final int BERNARD = 30702;
	private static final int PAGE = 30696;
	private static final int HAGGER = 30183;
	private static final int STAN = 30200;
	private static final int RALFORD = 30165;
	private static final int FERRIS = 30847;
	private static final int COLLOB = 30092;
	private static final int PANO = 30078;
	private static final int DUNING = 30688;
	private static final int LORAIN = 30673;
	
	// Kill NPC
	private static final int TIMAK_ORC_ARCHER = 20584;
	private static final int TIMAK_ORC_SOLDIER = 20585;
	private static final int TIMAK_ORC_SHAMAN = 20587;
	private static final int LAKIN = 20604;
	private static final int TORTURED_UNDEAD = 20678;
	private static final int HATAR_HANISHEE = 20663;
	private static final int SHACKLE = 20235;
	private static final int TIMAK_ORC = 20583;
	private static final int HEADLESS_KNIGHT = 20146;
	private static final int ROYAL_CAVE_SERVANT = 20240;
	private static final int MALRUK_SUCCUBUS_TUREN = 20245;
	private static final int FORMOR = 20568;
	private static final int FORMOR_ELDER = 20569;
	private static final int VANOR_SILENOS_SHAMAN = 20685;
	private static final int TARLK_BUGBEAR_HIGH_WARRIOR = 20572;
	private static final int OEL_MAHUM = 20161;
	private static final int OEL_MAHUM_WARRIOR = 20575;
	private static final int HARIT_LIZARDMAN_MATRIARCH = 20645;
	private static final int HARIT_LIZARDMAN_SHAMAN = 20644;
	
	// Trade list
	private static final Map<Integer, int[]> TRADE_LIST = new HashMap<>();
	{
		// Demon's Staff
		TRADE_LIST.put(206, new int[]
		{
			BELETHS_BLOOD,
			1
		});
		TRADE_LIST.put(206, new int[]
		{
			SILVER_DRAGON,
			1
		});
		TRADE_LIST.put(206, new int[]
		{
			GOLD_WYRM,
			13
		});
		// Dark Screamer
		TRADE_LIST.put(233, new int[]
		{
			BELETHS_GOLD,
			1
		});
		TRADE_LIST.put(233, new int[]
		{
			BLOOD_DRAGON,
			1
		});
		TRADE_LIST.put(233, new int[]
		{
			SILVER_DRYAD,
			1
		});
		TRADE_LIST.put(233, new int[]
		{
			GOLD_GIANT,
			1
		});
		// Widow Maker
		TRADE_LIST.put(303, new int[]
		{
			BELETHS_SILVER,
			1
		});
		TRADE_LIST.put(303, new int[]
		{
			GOLD_DRAGON,
			1
		});
		TRADE_LIST.put(303, new int[]
		{
			BLOOD_SUCCUBUS,
			1
		});
		TRADE_LIST.put(303, new int[]
		{
			BLOOD_BASILISK,
			2
		});
		// Sword of Limit
		TRADE_LIST.put(132, new int[]
		{
			GOLD_DRAGON,
			1
		});
		TRADE_LIST.put(132, new int[]
		{
			SILVER_DRAGON,
			1
		});
		TRADE_LIST.put(132, new int[]
		{
			BLOOD_DRAGON,
			1
		});
		TRADE_LIST.put(132, new int[]
		{
			SILVER_UNDINE,
			1
		});
		// Demon's Boots
		TRADE_LIST.put(2435, new int[]
		{
			MANAKS_GOLD_GIANT,
			1
		});
		// Demon's Stockings
		TRADE_LIST.put(472, new int[]
		{
			MANAKS_SILVER_DRYAD,
			1
		});
		TRADE_LIST.put(472, new int[]
		{
			SILVER_DRYAD,
			1
		});
		// Demon's Gloves
		TRADE_LIST.put(2459, new int[]
		{
			MANAKS_GOLD_GIANT,
			1
		});
		// Full Plate Helm
		TRADE_LIST.put(2414, new int[]
		{
			MANAKS_BLOOD_WEREWOLF,
			1
		});
		TRADE_LIST.put(2414, new int[]
		{
			GOLD_WYRM,
			1
		});
		TRADE_LIST.put(2414, new int[]
		{
			GOLD_GIANT,
			1
		});
		// Moonstone Earring
		TRADE_LIST.put(852, new int[]
		{
			NIAS_BLOOD_MEDUSA,
			2
		});
		TRADE_LIST.put(852, new int[]
		{
			BLOOD_DREVANUL,
			2
		});
		TRADE_LIST.put(852, new int[]
		{
			GOLD_DRAKE,
			2
		});
		TRADE_LIST.put(852, new int[]
		{
			GOLD_KNIGHT,
			3
		});
		// Nassens Earring
		TRADE_LIST.put(855, new int[]
		{
			NIAS_BLOOD_MEDUSA,
			7
		});
		TRADE_LIST.put(855, new int[]
		{
			BLOOD_DREVANUL,
			5
		});
		TRADE_LIST.put(855, new int[]
		{
			SILVER_GOLEM,
			5
		});
		TRADE_LIST.put(855, new int[]
		{
			GOLD_KNIGHT,
			5
		});
		// Ring of Binding
		TRADE_LIST.put(886, new int[]
		{
			NIAS_GOLD_WYVERN,
			5
		});
		TRADE_LIST.put(886, new int[]
		{
			GOLD_DRAKE,
			4
		});
		TRADE_LIST.put(886, new int[]
		{
			SILVER_GOLEM,
			4
		});
		TRADE_LIST.put(886, new int[]
		{
			BLOOD_DREVANUL,
			4
		});
		// Necklace of Protection
		TRADE_LIST.put(916, new int[]
		{
			NIAS_SILVER_FAIRY,
			5
		});
		TRADE_LIST.put(916, new int[]
		{
			SILVER_FAIRY,
			3
		});
		TRADE_LIST.put(916, new int[]
		{
			GOLD_KNIGHT,
			3
		});
		TRADE_LIST.put(916, new int[]
		{
			BLOOD_DREVANUL,
			3
		});
	}
	
	private static final int[][] PROMOTE =
	{
		{}, // Grade 0
		{}, // Grade 1
		{
			BLOOD_BASILISK,
			BLOOD_SUCCUBUS,
			GOLD_GIANT,
			GOLD_WYRM,
			SILVER_DRYAD,
			SILVER_UNDINE
		}, // Grade 2
		{
			BLOOD_WEREWOLF,
			GOLD_DRAKE,
			SILVER_FAIRY,
			BLOOD_DREVANUL,
			GOLD_KNIGHT,
			SILVER_GOLEM
		}
		// Grade 3
	};
	
	private static final int[][] EXCHANGE_LEVEL =
	{
		{
			PAGE,
			3
		},
		{
			LORAIN,
			3
		},
		{
			HAGGER,
			3
		},
		{
			RALFORD,
			2
		},
		{
			STAN,
			2
		},
		{
			DUNING,
			2
		},
		{
			FERRIS,
			1
		},
		{
			COLLOB,
			1
		},
		{
			PANO,
			1
		},
	};
	
	private static final int[][] DROPLIST =
	{
		{
			TIMAK_ORC_ARCHER,
			BLOOD_MEDUSA
		},
		{
			TIMAK_ORC_SOLDIER,
			BLOOD_MEDUSA
		},
		{
			TIMAK_ORC_SHAMAN,
			BLOOD_MEDUSA
		},
		{
			LAKIN,
			BLOOD_MEDUSA
		},
		{
			TORTURED_UNDEAD,
			BLOOD_MEDUSA
		},
		{
			HATAR_HANISHEE,
			BLOOD_MEDUSA
		},
		{
			SHACKLE,
			GOLD_WYVERN
		},
		{
			TIMAK_ORC,
			GOLD_WYVERN
		},
		{
			HEADLESS_KNIGHT,
			GOLD_WYVERN
		},
		{
			ROYAL_CAVE_SERVANT,
			GOLD_WYVERN
		},
		{
			MALRUK_SUCCUBUS_TUREN,
			GOLD_WYVERN
		},
		{
			FORMOR,
			SILVER_UNICORN
		},
		{
			FORMOR_ELDER,
			SILVER_UNICORN
		},
		{
			VANOR_SILENOS_SHAMAN,
			SILVER_UNICORN
		},
		{
			TARLK_BUGBEAR_HIGH_WARRIOR,
			SILVER_UNICORN
		},
		{
			OEL_MAHUM,
			SILVER_UNICORN
		},
		{
			OEL_MAHUM_WARRIOR,
			SILVER_UNICORN
		}
	};
	
	public Q336_CoinsOfMagic()
	{
		super(336, "Coins Of Magic");
		
		setItemsIds(COIN_DIAGRAM, KALDIS_COIN, MEMBERSHIP_1, MEMBERSHIP_2, MEMBERSHIP_3);
		
		addStartNpc(SORINT);
		addTalkId(SORINT, BERNARD, PAGE, HAGGER, STAN, RALFORD, FERRIS, COLLOB, PANO, DUNING, LORAIN);
		
		for (int mob[] : DROPLIST)
		{
			addKillId(mob[0]);
		}
		
		addKillId(HARIT_LIZARDMAN_MATRIARCH, HARIT_LIZARDMAN_SHAMAN);
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
		
		if (event.equalsIgnoreCase("30702-06.htm"))
		{
			if (st.getInt("cond") < 7)
			{
				st.set("cond", "7");
				st.playSound(Sound.SOUND_ACCEPT);
			}
		}
		else if (event.equalsIgnoreCase("30232-22.htm"))
		{
			if (st.getInt("cond") < 6)
			{
				st.set("cond", "6");
			}
			else if (event.equalsIgnoreCase("30232-23.htm"))
			{
				if (st.getInt("cond") < 5)
				{
					st.set("cond", "5");
				}
				else if (event.equalsIgnoreCase("30702-02.htm"))
				{
					st.set("cond", "2");
				}
				else if (event.equalsIgnoreCase("30232-05.htm"))
				{
					st.setState(STATE_STARTED);
					st.playSound(Sound.SOUND_ACCEPT);
					st.giveItems(COIN_DIAGRAM, 1);
					st.set("cond", "1");
				}
				else if (event.equalsIgnoreCase("30232-04.htm") || event.equalsIgnoreCase("30232-18a.htm"))
				{
					st.exitQuest(true);
					st.playSound(Sound.SOUND_GIVEUP);
				}
				else if (event.equalsIgnoreCase("raise"))
				{
					int _grade = st.getInt("grade");
					
					if (_grade == 1)
					{
						htmltext = "30232-15.htm";
					}
					else
					{
						int h = 0;
						for (int i : PROMOTE[_grade])
						{
							if (st.getQuestItemsCount(i) > 0)
							{
								h += 1;
							}
						}
						
						if (h == 6)
						{
							for (int j : PROMOTE[_grade])
							{
								st.takeItems(j, 1);
							}
							
							htmltext = "30232-" + (19 - _grade) + ".htm";
							st.takeItems(3812 + _grade, 1);
							st.giveItems(3811 + _grade, 1);
							
							if (_grade == 3)
							{
								st.set("cond", "9");
							}
							else if (_grade == 2)
							{
								st.set("cond", "11");
							}
							
							st.set("grade", Integer.toString(_grade - 1));
							st.playSound(Sound.SOUND_FANFARE);
						}
						else
						{
							htmltext = "30232-" + (16 - _grade) + ".htm";
							
							if (_grade == 3)
							{
								st.set("cond", "8");
							}
							else if (_grade == 2)
							{
								st.set("cond", "10");
							}
						}
					}
				}
				else if (MathUtil.isDigit(event))
				{
					int item = Integer.parseInt(event);
					
					if (TRADE_LIST.containsKey(item))
					{
						int j = 0;
						int k = 0;
						
						for (int[] i : TRADE_LIST.values())
						{
							if (i[0] == item)
							{
								k += 1;
								
								if (st.getQuestItemsCount(i[0]) >= i[1])
								{
									j += 1;
								}
							}
						}
						
						if (k == j)
						{
							for (int[] i : TRADE_LIST.values())
							{
								if ((st.getQuestItemsCount(i[0]) >= i[1]) && (i[0] == item))
								{
									st.takeItems(i[0], i[1]);
								}
							}
							
							st.giveItems(item, 1);
							htmltext = "30232-24a.htm";
						}
						else
						{
							htmltext = "30232-24.htm";
						}
					}
				}
				else if (event.startsWith("Li_"))
				{
					
				}
				else if (event.startsWith("Ex_"))
				{
					
				}
				else if (event.startsWith("Ga_"))
				{
					
				}
				else if (event.startsWith("_"))
				{
					
				}
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case STATE_CREATED:
				if (player.getLevel() < 40)
				{
					htmltext = "30232-01.htm";
					st.exitQuest(true);
				}
				else
				{
					htmltext = "30232-02.htm";
				}
				break;
			
			case STATE_STARTED:
				switch (npc.getNpcId())
				{
					case SORINT:
						if (st.getQuestItemsCount(COIN_DIAGRAM) > 0)
						{
							if (st.getQuestItemsCount(KALDIS_COIN) > 0)
							{
								st.takeItems(KALDIS_COIN, 1);
								st.takeItems(COIN_DIAGRAM, 1);
								st.giveItems(MEMBERSHIP_3, 1);
								st.set("grade", "3");
								st.set("cond", "4");
								st.playSound(Sound.SOUND_FANFARE);
								htmltext = "30232-07.htm";
							}
							else
								htmltext = "30232-06.htm";
						}
						else if (st.getInt("grade") == 3)
							htmltext = "30232-12.htm";
						else if (st.getInt("grade") == 2)
							htmltext = "30232-11.htm";
						else if (st.getInt("grade") == 1)
							htmltext = "30232-10.htm";
						break;
					
					case BERNARD:
						if ((st.getQuestItemsCount(COIN_DIAGRAM) > 0) && (st.getInt("grade") == 0))
							htmltext = "30702-01.htm";
						else if (st.getInt("grade") == 3)
							htmltext = "30702-05.htm";
						break;
					
					case PAGE:
					case HAGGER:
					case STAN:
					case RALFORD:
					case FERRIS:
					case COLLOB:
					case PANO:
					case DUNING:
					case LORAIN:
						for (int _npc[] : EXCHANGE_LEVEL)
						{
							if ((npc.getNpcId() == _npc[0]) && (st.getInt("grade") <= _npc[1]))
							{
								htmltext = npc.getNpcId() + "-01.htm";
								break;
							}
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
		QuestState st = checkPlayerState(player, npc, STATE_STARTED);
		if (st == null)
		{
			return null;
		}
		
		if ((npc.getNpcId() == HARIT_LIZARDMAN_MATRIARCH) || (npc.getNpcId() == HARIT_LIZARDMAN_SHAMAN))
		{
			if (st.getInt("cond") == 2)
			{
				if (st.dropItems(KALDIS_COIN, 1, 1, 100000 * (npc.getNpcId() == HARIT_LIZARDMAN_MATRIARCH ? 2 : 1)))
				{
					st.playSound(Sound.SOUND_MIDDLE);
					st.set("cond", "3");
				}
			}
		}
		
		for (int[] _drop : DROPLIST)
		{
			if (_drop[0] == npc.getNpcId())
			{
				st.dropItems(_drop[1], 1, 0, ((npc.getLevel() + (st.getInt("grade") * 3)) - 20));
				st.playSound(Sound.SOUND_ITEMGET);
				break;
			}
		}
		
		return null;
	}
}