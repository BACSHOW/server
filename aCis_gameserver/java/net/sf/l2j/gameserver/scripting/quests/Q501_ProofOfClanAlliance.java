package net.sf.l2j.gameserver.scripting.quests;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.scripting.QuestState;
import net.sf.l2j.gameserver.scripting.QuestTimer;
import net.sf.l2j.gameserver.scripting.quests.audio.Sound;

public class Q501_ProofOfClanAlliance extends Quest
{
	private static final String qn = "Q501_ProofOfClanAlliance";
	// Quest Npcs
	private static final int SIR_KRISTOF_RODEMAI = 30756;
	private static final int STATUE_OF_OFFERING = 30757;
	private static final int WITCH_ATHREA = 30758;
	private static final int WITCH_KALIS = 30759;
	
	// Quest Items
	private static final short HERB_OF_HARIT = 3832;
	private static final short HERB_OF_VANOR = 3833;
	private static final short HERB_OF_OEL_MAHUM = 3834;
	private static final short BLOOD_OF_EVA = 3835;
	private static final short SYMBOL_OF_LOYALTY = 3837;
	private static final short PROOF_OF_ALLIANCE = 3874;
	private static final short VOUCHER_OF_FAITH = 3873;
	private static final short ANTIDOTE_RECIPE = 3872;
	private static final short POTION_OF_RECOVERY = 3889;
	
	// Quest mobs, drop, rates and prices
	private static final int[] CHESTS =
	{
		27173,
		27174,
		27175,
		27176,
		27177
	};
	private static final int[][] MOBS =
	{
		{
			20685,
			HERB_OF_VANOR
		},
		{
			20644,
			HERB_OF_HARIT
		},
		{
			20576,
			HERB_OF_OEL_MAHUM
		}
	};
	
	private static final short RATE = 35;
	// stackable items paid to retry chest game: (default 10k adena)
	private static final short RETRY_PRICE = 10000;
	
	public Q501_ProofOfClanAlliance()
	{
		super(501, "Proof Of Clan Alliance");
		
		addStartNpc(SIR_KRISTOF_RODEMAI, STATUE_OF_OFFERING, WITCH_ATHREA);
		addTalkId(WITCH_KALIS);
		setItemsIds(SYMBOL_OF_LOYALTY, ANTIDOTE_RECIPE);
		for (int[] i : MOBS)
		{
			addKillId(i[0]);
			setItemsIds(i[1]);
		}
		for (int i : CHESTS)
		{
			addKillId(i);
		}
	}
	
	public QuestState getLeader(QuestState st)
	{
		L2Clan clan = st.getPlayer().getClan();
		QuestState leader = null;
		if ((clan != null) && (clan.getLeader() != null) && (clan.getLeader().getPlayerInstance() != null))
		{
			leader = clan.getLeader().getPlayerInstance().getQuestState(getName());
		}
		return leader;
	}
	
	public void removeQuestFromMembers(QuestState st, boolean leader)
	{
		removeQuestFromOfflineMembers(st);
		removeQuestFromOnlineMembers(st, leader);
	}
	
	public void removeQuestFromOfflineMembers(QuestState st)
	{
		if ((st.getPlayer() == null) || (st.getPlayer().getClan() == null))
		{
			st.exitQuest(true);
			return;
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement offline = con.prepareStatement("DELETE FROM character_quests WHERE name = ? AND charId IN (SELECT obj_Id FROM characters WHERE clanId = ? AND online = 0)");
			offline.setString(1, getName());
			offline.setInt(2, st.getPlayer().getClan().getClanId());
			offline.executeUpdate();
			offline.close();
		}
		catch (Exception e)
		{
		}
	}
	
	public void removeQuestFromOnlineMembers(QuestState st, boolean leader)
	{
		if ((st.getPlayer() == null) || (st.getPlayer().getClan() == null))
		{
			st.exitQuest(true);
			return;
		}
		
		QuestState l;
		Player pleader = null;
		
		if (leader)
		{
			l = getLeader(st);
			if (l != null)
			{
				pleader = l.getPlayer();
			}
		}
		
		if (pleader != null)
		{
			for (L2Effect eff : st.getPlayer().getAllEffects())
			{
				if (eff.getSkill().getId() == 4082)
				{
					st.getPlayer().stopImmobileUntilAttacked(eff);
				}
			}
		}
		for (Player pl : st.getPlayer().getClan().getOnlineMembers())
		{
			if ((pl != null) && (pl.getQuestState(getName()) != null))
			{
				pl.getQuestState(getName()).exitQuest(true);
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		QuestState st = player.getQuestState(getName());
		if ((st.getPlayer() == null) || (st.getPlayer().getClan() == null))
		{
			st.exitQuest(true);
			return getNoQuestMsg();
		}
		
		QuestState leader = getLeader(st);
		if (leader == null)
		{
			removeQuestFromMembers(st, true);
			return "Quest Failed";
		}
		
		String htmltext = event;
		
		if (st.getPlayer().isClanLeader())
		{
			if (event.equalsIgnoreCase("30756-03.htm"))
			{
				st.set("cond", "1");
				st.setState(STATE_STARTED);
				st.playSound(Sound.SOUND_ACCEPT);
			}
			else if (event.equalsIgnoreCase("30759-03.htm"))
			{
				st.set("cond", "2");
				st.set("dead_list", " ");
			}
			else if (event.equalsIgnoreCase("30759-07.htm"))
			{
				st.takeItems(SYMBOL_OF_LOYALTY, -1);
				st.giveItems(ANTIDOTE_RECIPE, 1);
				notifyDeath(npc, player, leader.getPlayer());
				st.set("cond", "3");
				st.set("chest_count", "0");
				st.set("chest_game", "0");
				st.set("chest_try", "0");
				startQuestTimer("poison_timer", 3600000, null, st.getPlayer(), false);
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(4082, 1), false, false);
				st.getPlayer().startImmobileUntilAttacked();
				htmltext = "30759-07.htm";
			}
		}
		
		if (event.equalsIgnoreCase("poison_timer"))
		{
			removeQuestFromMembers(st, true);
			htmltext = "30759-09.htm";
		}
		else if (event.equalsIgnoreCase("chest_timer"))
		{
			htmltext = "";
			if (leader.getInt("chest_game") < 2)
			{
				stop_chest_game(st);
			}
		}
		else if (event.equalsIgnoreCase("30757-04.htm"))
		{
			List<String> deadlist = new ArrayList<>();
			deadlist.addAll(Arrays.asList(leader.get("dead_list").split(" ")));
			deadlist.add(st.getPlayer().getName());
			String deadstr = "";
			for (String s : deadlist)
			{
				deadstr += s + " ";
			}
			leader.set("dead_list", deadstr);
			notifyDeath(npc, leader.getPlayer(), player);
			if (Rnd.chance(50))
			{
				st.getPlayer().reduceCurrentHp(st.getPlayer().getCurrentHp() * 8, st.getPlayer(), true, true, null);
			}
			st.giveItems(SYMBOL_OF_LOYALTY, 1);
			st.playSound(Sound.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30757-05.htm"))
		{
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("30758-03.htm"))
		{
			start_chest_game(st);
		}
		else if (event.equalsIgnoreCase("30758-07.htm"))
		{
			if (st.getQuestItemsCount(57) < RETRY_PRICE)
			{
				htmltext = "30758-06.htm";
			}
			else
			{
				st.takeItems(57, RETRY_PRICE);
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		QuestState st = player.getQuestState(qn);
		String htmltext = getNoQuestMsg();
		
		int cond = st.getInt("cond");
		
		if ((st.getPlayer() == null) || (st.getPlayer().getClan() == null))
		{
			st.exitQuest(true);
			return htmltext;
		}
		
		QuestState leader = getLeader(st);
		if (leader == null)
		{
			removeQuestFromMembers(st, true);
			return "Quest Failed";
		}
		
		int npcId = npc.getNpcId();
		if (npcId == SIR_KRISTOF_RODEMAI)
		{
			if (!st.getPlayer().isClanLeader())
			{
				st.exitQuest(true);
				return "30756-10.htm";
			}
			else if (st.getPlayer().getClan().getLevel() <= 2)
			{
				st.exitQuest(true);
				return "30756-08.htm";
			}
			else if (st.getPlayer().getClan().getLevel() >= 4)
			{
				st.exitQuest(true);
				return "30756-09.htm";
			}
			else if (st.getQuestItemsCount(VOUCHER_OF_FAITH) > 0)
			{
				st.playSound(Sound.SOUND_FANFARE);
				st.takeItems(VOUCHER_OF_FAITH, -1);
				st.giveItems(PROOF_OF_ALLIANCE, 1);
				st.getPlayer().addExpAndSp(0, 120000);
				htmltext = "30756-07.htm";
				st.exitQuest(true);
			}
			else if ((cond == 1) || (cond == 2))
			{
				return "30756-06.htm";
			}
			else if (st.getQuestItemsCount(PROOF_OF_ALLIANCE) == 0)
			{
				st.set("cond", "0");
				return "30756-01.htm";
			}
			else
			{
				st.exitQuest(true);
				return htmltext;
			}
		}
		else if (npcId == WITCH_KALIS)
		{
			if (st.getPlayer().isClanLeader())
			{
				if (cond == 1)
				{
					return "30759-01.htm";
				}
				else if (cond == 2)
				{
					htmltext = "30759-05.htm";
					if (st.getQuestItemsCount(SYMBOL_OF_LOYALTY) == 3)
					{
						int deads = 0;
						try
						{
							deads = st.get("dead_list").split(" ").length;
						}
						finally
						{
							if (deads == 3)
							{
								htmltext = "30759-06.htm";
							}
						}
					}
				}
				else if (cond == 3)
				{
					if ((st.getQuestItemsCount(HERB_OF_HARIT) > 0) && (st.getQuestItemsCount(HERB_OF_VANOR) > 0) && (st.getQuestItemsCount(HERB_OF_OEL_MAHUM) > 0) && (st.getQuestItemsCount(BLOOD_OF_EVA) > 0) && (st.getQuestItemsCount(ANTIDOTE_RECIPE) > 0))
					{
						st.takeItems(ANTIDOTE_RECIPE, 1);
						st.takeItems(HERB_OF_HARIT, 1);
						st.takeItems(HERB_OF_VANOR, 1);
						st.takeItems(HERB_OF_OEL_MAHUM, 1);
						st.takeItems(BLOOD_OF_EVA, 1);
						st.giveItems(POTION_OF_RECOVERY, 1);
						st.giveItems(VOUCHER_OF_FAITH, 1);
						QuestTimer timer = getQuestTimer("poison_timer", null, st.getPlayer());
						if (timer != null)
						{
							timer.cancel();
						}
						removeQuestFromMembers(st, false);
						for (L2Effect eff : st.getPlayer().getAllEffects())
						{
							if (eff.getSkill().getId() == 4082)
							{
								st.getPlayer().stopImmobileUntilAttacked(eff);
							}
						}
						st.set("cond", "4");
						st.playSound(Sound.SOUND_FINISH);
						return "30759-08.htm";
					}
					else if (st.getQuestItemsCount(VOUCHER_OF_FAITH) == 0)
					{
						return "30759-10.htm";
					}
				}
			}
			else if (leader.getInt("cond") == 3)
			{
				return "30759-11.htm";
			}
		}
		else if (npcId == STATUE_OF_OFFERING)
		{
			if (st.getPlayer().isClanLeader())
			{
				return "30757-03.htm";
			}
			else if (st.getPlayer().getLevel() <= 39)
			{
				st.exitQuest(true);
				return "30757-02.htm";
			}
			else
			{
				String[] dlist;
				int deads;
				try
				{
					dlist = leader.get("dead_list").split(" ");
					deads = dlist.length;
				}
				catch (Exception e)
				{
					removeQuestFromMembers(st, true);
					return "Who are you?";
				}
				if (deads < 3)
				{
					for (String str : dlist)
					{
						if (st.getPlayer().getName().equalsIgnoreCase(str))
						{
							return "you cannot die again!";
						}
					}
					return "30757-01.htm";
				}
			}
		}
		else if (npcId == WITCH_ATHREA)
		{
			if (st.getPlayer().isClanLeader())
			{
				return "30757-03.htm";
			}
			
			String[] dlist;
			try
			{
				dlist = leader.get("dead_list").split(" ");
			}
			catch (Exception e)
			{
				st.exitQuest(true);
				return "Who are you?";
			}
			Boolean flag = false;
			if (dlist != null)
			{
				for (String str : dlist)
				{
					if (st.getPlayer().getName().equalsIgnoreCase(str))
					{
						flag = true;
					}
				}
			}
			if (!flag)
			{
				st.exitQuest(true);
				return "Who are you?";
			}
			
			int game_state = leader.getInt("chest_game");
			if (game_state == 0)
			{
				if (leader.getInt("chest_try") == 0)
				{
					return "30758-01.htm";
				}
				return "30758-05.htm";
			}
			else if (game_state == 1)
			{
				return "30758-09.htm";
			}
			else if (game_state == 2)
			{
				st.playSound(Sound.SOUND_FINISH);
				st.giveItems(BLOOD_OF_EVA, 1);
				QuestTimer timer = getQuestTimer("chest_timer", null, st.getPlayer());
				if (timer != null)
				{
					timer.cancel();
				}
				stop_chest_game(st);
				leader.set("chest_game", "3");
				return "30758-08.htm";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isSunnon)
	{
		QuestState st = checkPlayerState(player, npc, STATE_STARTED);
		if (st == null)
		{
			return null;
		}
		if ((st.getPlayer() == null) || (st.getPlayer().getClan() == null))
		{
			st.exitQuest(true);
			return getNoQuestMsg();
		}
		
		QuestState leader = getLeader(st);
		if (leader == null)
		{
			removeQuestFromMembers(st, true);
			return "Quest Failed";
		}
		
		// first part, general checking
		int npcId = npc.getNpcId();
		
		QuestTimer timer = getQuestTimer("poison_timer", null, st.getPlayer());
		if (timer == null)
		{
			stop_chest_game(st);
			return "Quest Failed";
		}
		
		// second part, herbs gathering
		for (int[] m : MOBS)
		{
			if ((npcId == m[0]) && (st.getInt(String.valueOf(m[1])) == 0))
			{
				if (Rnd.chance(RATE))
				{
					st.giveItems(m[1], 1);
					leader.set(String.valueOf(m[1]), "1");
					st.playSound(Sound.SOUND_MIDDLE);
					return null;
				}
			}
		}
		
		// third part, chest game
		for (int i : CHESTS)
		{
			if (npcId == i)
			{
				timer = getQuestTimer("chest_timer", null, st.getPlayer());
				if (timer == null)
				{
					stop_chest_game(st);
					return "Time is up!";
				}
				if (Rnd.chance(25))
				{
					npc.broadcastNpcSay("###### BINGO! ######");
					int count = leader.getInt("chest_count");
					if (count < 4)
					{
						count += 1;
						leader.set("chest_count", String.valueOf(count));
					}
					if (count >= 4)
					{
						stop_chest_game(st);
						leader.set("chest_game", "2");
						timer.cancel();
						st.playSound(Sound.SOUND_MIDDLE);
					}
					else
					{
						st.playSound(Sound.SOUND_ITEMGET);
					}
				}
				return null;
			}
		}
		return null;
	}
	
	public void start_chest_game(QuestState st)
	{
		if ((st.getPlayer() == null) || (st.getPlayer().getClan() == null))
		{
			st.exitQuest(true);
			return;
		}
		QuestState leader = getLeader(st);
		if (leader == null)
		{
			removeQuestFromMembers(st, true);
			return;
		}
		leader.set("chest_game", "1");
		leader.set("chest_count", "0");
		int attempts = leader.getInt("chest_try");
		leader.set("chest_try", String.valueOf(attempts + 1));
		
		for (Npc npc : World.getInstance().getAllByNpcId(CHESTS, false))
		{
			npc.deleteMe();
		}
		
		for (int n = 1; n <= 5; n++)
		{
			for (int i : CHESTS)
			{
				addSpawn(i, 102100, 103450, -3400, 0, true, 60000, false);
			}
		}
		startQuestTimer("chest_timer", 60000, null, st.getPlayer(), false);
	}
	
	public void stop_chest_game(QuestState st)
	{
		QuestState leader = getLeader(st);
		if (leader == null)
		{
			removeQuestFromMembers(st, true);
			return;
		}
		
		for (Npc npc : World.getInstance().getAllByNpcId(CHESTS, false))
		{
			npc.deleteMe();
		}
		leader.set("chest_game", "0");
	}
	
	@Override
	public String onDeath(Creature npc, Creature pc, Player player)
	{
		QuestState st = checkPlayerState(player, (Npc) npc, STATE_STARTED);
		if ((st.getPlayer() == null) || (st.getPlayer().getClan() == null))
		{
			st.exitQuest(true);
			return null;
		}
		
		QuestState leader = getLeader(st);
		if (leader == null)
		{
			removeQuestFromMembers(st, true);
			return null;
		}
		
		if (st.getPlayer() == pc)
		{
			QuestTimer timer1 = getQuestTimer("poison_timer", null, st.getPlayer());
			QuestTimer timer2 = getQuestTimer("chest_timer", null, st.getPlayer());
			if (timer1 != null)
			{
				timer1.cancel();
			}
			if (timer2 != null)
			{
				timer2.cancel();
			}
			removeQuestFromMembers(st, true);
		}
		return null;
	}
}