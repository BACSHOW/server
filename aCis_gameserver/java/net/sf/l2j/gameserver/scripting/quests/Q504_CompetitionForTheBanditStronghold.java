package net.sf.l2j.gameserver.scripting.quests;

import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.clanhallsiege.BanditStrongholdSiege;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.scripting.QuestState;
import net.sf.l2j.gameserver.scripting.quests.audio.Sound;

public class Q504_CompetitionForTheBanditStronghold extends Quest
{
	// npcId
	private static final int Messenger = 35437;
	// itemId list
	private static final int TarlkAmulet = 4332;
	private static final int AlianceTrophey = 5009;
	// Quest mobs
	private static final int TarlkBugbear = 20570;
	private static final int TarlkBugbearWarrior = 20571;
	private static final int TarlkBugbearHighWarrior = 20572;
	private static final int TarlkBasilisk = 20573;
	private static final int ElderTarlkBasilisk = 20574;
	
	public Q504_CompetitionForTheBanditStronghold()
	{
		super(504, "Competition for the Bandit Stronghold");
		
		addStartNpc(Messenger);
		addTalkId(Messenger);
		addKillId(TarlkBugbear, TarlkBugbearWarrior, TarlkBugbearHighWarrior, TarlkBasilisk, ElderTarlkBasilisk);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(getName());
		if (st == null)
			return htmltext;
		
		switch (event)
		{
			case "35437-02.htm":
				st.set("cond", "1");
				st.setState(STATE_STARTED);
				st.playSound(Sound.SOUND_ACCEPT);
				break;
			
			case "35437-04.htm":
				if (st.getQuestItemsCount(TarlkAmulet) == 30)
				{
					st.takeItems(TarlkAmulet, -30);
					st.giveItems(AlianceTrophey, 1);
					st.playSound(Sound.SOUND_FINISH);
					st.exitQuest(true);
				}
				else
					htmltext = "35437-05.htm";
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestState(getName());
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case STATE_CREATED:
				htmltext = ((player.getClan() == null || player.getClan().getLevel() < 4 || !(player.getClan().getLeaderName() == player.getName()))) ? "35437-06.htm" : "35437-01.htm";
				break;
			
			case STATE_STARTED:
				final int cond = st.getInt("cond");
				if (BanditStrongholdSiege.getInstance().isRegistrationPeriod())
				{
					switch (npc.getNpcId())
					{
						case Messenger:
							if (cond == 0)
								htmltext = "35627-01.htm";
							else if (cond > 1)
								htmltext = "35627-03.htm";
							else
								npc.showChatWindow(player, 3);
							break;
					}
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		QuestState st = checkPlayerState(player, npc, STATE_STARTED);
		if (st == null || !st.isStarted())
			return null;
		
		if (st.getQuestItemsCount(TarlkAmulet) < 30)
		{
			st.giveItems(TarlkAmulet, 1);
			st.playSound(Sound.SOUND_ITEMGET);
		}
		if (st.getQuestItemsCount(TarlkAmulet) == 30)
		{
			st.set("cond", "2");
		}
		return null;
	}
	
}