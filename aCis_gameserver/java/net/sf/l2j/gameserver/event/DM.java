package net.sf.l2j.gameserver.event;

import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class DM extends Event
{
	protected EventState eventState;
	private Core task = new Core();
	private enum EventState
	{
		START, FIGHT, END, INACTIVE
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
						divideIntoTeams(1);
						preparePlayers();
						teleportToTeamPos();
						InvisAll();
						setStatus(EventState.FIGHT);
						schedule(20000);
						break;
						
					case FIGHT:
						unInvisAll();
						sendMsg();
						setStatus(EventState.END);
						clock.startClock(getInt("matchTime"));
						break;
						
					case END:
						clock.setTime(0);
						Creature winner = getPlayerWithMaxScore();
						giveReward(winner, getInt("rewardId"), getInt("rewardAmmount"));
						setStatus(EventState.INACTIVE);
						EventManager.getInstance().end("Congratulation! " + winner.getName() + " won the event with " + getScore(winner) + " kills!");
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
	
	public DM()
	{
		super();
		eventId = 1;
		createNewTeam(1, "All", getColor("All"), getPosition("All", 1));
	}
	
	@Override
	protected void endEvent()
	{
		setStatus(EventState.END);
		clock.setTime(0);
	}
	
	@Override
	public void onDie(Player victim, Creature killer)
	{
		super.onDie(victim, killer);
		addToResurrector(victim);
	}
	
	@Override
	public void onKill(Creature victim, Player killer)
	{
		super.onKill(victim, killer);
		increasePlayersScore(killer);
	}
	
	@Override
	protected void schedule(int time)
	{
		tpm.scheduleGeneral(task, time);
	}
	
	protected void setStatus(EventState s)
	{
		eventState = s;
	}
	
	@Override
	protected void showHtml(Player player, int obj)
	{
		if (players.size() > 0)
		{
			NpcHtmlMessage html = new NpcHtmlMessage(obj);
			StringBuilder sb = new StringBuilder();
			sb.append("<html><body><table width=270><tr><td width=200>Event Engine </td><td><a action=\"bypass -h eventstats 1\">Statistics</a></td></tr></table><br><center><table width=270 bgcolor=5A5A5A><tr><td width=70>Running</td><td width=130><center>" + getString("eventName") + "</td><td width=70>Time: " + clock.getTime() + "</td></tr></table><table width=270><tr><td><center>" + getPlayerWithMaxScore().getName() + " - " + getScore(getPlayerWithMaxScore()) + "</td></tr></table><br><table width=270>");
			
			for (Player p : getPlayersOfTeam(1))
				sb.append("<tr><td>" + p.getName() + "</td><td>lvl " + p.getLevel() + "</td><td>" + p.getTemplate().className + "</td><td>" + getScore(p) + "</td></tr>");
			
			sb.append("</table></body></html>");
			html.setHtml(sb.toString());
			player.sendPacket(html);
		}
	}
	
	@Override
	public boolean onSay(int type, Player player, String text)
	{
		return false;
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
		return "Free For All!";
	}
	
	@Override
	protected String getScorebar()
	{
		return "Max: " + getScore(getPlayerWithMaxScore()) + "  Time: " + clock.getTime() + "";
	}
}