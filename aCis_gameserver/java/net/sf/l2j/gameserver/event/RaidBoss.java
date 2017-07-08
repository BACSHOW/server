package net.sf.l2j.gameserver.event;

import net.sf.l2j.gameserver.datatables.SpawnTable;
import net.sf.l2j.gameserver.model.L2Spawn;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import main.util.builders.html.HtmlBuilder;

public class RaidBoss extends Event
{
	protected EventState eventState;
	private Core task = new Core();
	private L2Spawn raid;
	private enum EventState
	{
		START, FIGHT, END, TELEPORT, INACTIVE
	}
	
	protected class Core implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				switch (eventState)
				{
					case START:
						divideIntoTeams(2);
						preparePlayers();
						teleportToTeamPos();
						createPartyOfTeam(1);
						createPartyOfTeam(2);
						forceSitAll();
						setStatus(EventState.FIGHT);
						schedule(20000);
						break;
						
					case FIGHT:
						forceStandAll();
						spawnRaid();
						sendMsg();
						setStatus(EventState.END);
						clock.startClock(getInt("matchTime"));
						break;
						
					case END:
						clock.setTime(0);
						if (winnerTeam == 0)
							winnerTeam = getWinnerTeam();
						
						setStatus(EventState.INACTIVE);
						removeRaid();
						
						if (winnerTeam == 0)
							EventManager.getInstance().end("The event ended in a tie! no-one killed the Raid Boss.");
						else
						{
							giveReward(getPlayersOfTeam(winnerTeam), getInt("rewardId"), getInt("rewardAmmount"));
							EventManager.getInstance().end("Congratulation! The " + teams.get(winnerTeam).getName() + " team has killed the Raid Boss!");
						}
						break;
				}
			}
			catch (Throwable e)
			{
				e.printStackTrace();
				EventManager.getInstance().end("Error! Event ended.");
			}
		}
	}
	
	public RaidBoss()
	{
		super();
		eventId = 16;
		createNewTeam(1, "Blue", getColor("Blue"), getPosition("Blue", 1));
		createNewTeam(2, "Red", getColor("Red"), getPosition("Red", 1));
	}
	
	@Override
	protected void endEvent()
	{
		winnerTeam = players.head().getNext().getValue()[0];
		
		setStatus(EventState.END);
		clock.setTime(0);
	}
	
	@Override
	public void onDie(Player victim, Creature killer)
	{
		if (killer.isRaid() || killer.isRaidMinion())
			addToResurrector(victim);
		else
			super.onDie(victim, killer);
	}
	
	@Override
	public void onKill(Creature victim, Player killer)
	{
		if (victim.isRaid() || victim.isRaidMinion())
		{
			getPlayersTeam(killer).increaseScore();
			increasePlayersScore(killer);
			setStatus(EventState.END);
			clock.setTime(0);
		}
		else
			super.onKill(victim, killer);
	}
	
	@Override
	protected void schedule(int time)
	{
		tpm.schedule(task, time);
	}
	
	protected void setStatus(EventState s)
	{
		eventState = s;
	}
	
	@Override
	protected void showHtml(Player player, int obj)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(obj);
		HtmlBuilder sb = new HtmlBuilder();
		sb.append("<html><body><table width=270><tr><td width=200>Event Engine </td><td><a action=\"bypass -h eventstats 1\">Statistics</a></td></tr></table><br><center><table width=270 bgcolor=5A5A5A><tr><td width=70>Running</td><td width=130><center>" + getString("eventName") + "</td><td width=70>Time: " + clock.getTime() + "</td></tr></table><center><table width=270><tr><td><center><font color=" + teams.get(1).getHexaColor() + ">" + teams.get(1).getScore() + "</font> - <font color=" + teams.get(2).getHexaColor() + ">" + teams.get(2).getScore() + "</font></td></tr></table><br><table width=270>");
		
		int i = 0;
		for (EventTeam team : teams.values())
		{
			i++;
			sb.append("<tr><td><font color=" + team.getHexaColor() + ">" + team.getName() + "</font> team</td><td></td><td></td><td></td></tr>");
			for (Player p : getPlayersOfTeam(i))
				sb.append("<tr><td>" + p.getName() + "</td><td>lvl " + p.getLevel() + "</td><td>" + p.getTemplate().getClassName() + "</td><td>" + getScore(p) + "</td></tr>");
		}
		
		sb.append("</table></body></html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}
	
	@Override
	protected void start()
	{
		setStatus(EventState.START);
		schedule(1);
	}
	
	@Override
	protected String getStartingMsg()
	{
		return "Kill the Raid Boss!";
	}
	
	@Override
	protected String getScorebar()
	{
		return "Time: " + clock.getTime();
	}
	
	protected void spawnRaid()
	{
		raid = spawnNPC(106037, 114816, -1587, getInt("raidId"));
	}
	
	protected void removeRaid()
	{
		raid.getNpc().deleteMe();
		raid.doRespawn();
		SpawnTable.getInstance().deleteSpawn(raid, true);
	}
}