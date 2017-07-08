package net.sf.l2j.gameserver.event;

import java.util.HashMap;

import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import main.util.builders.html.HtmlBuilder;

public class VIPTvT extends Event
{
	protected EventState eventState;
	private Core task = new Core();
	private HashMap<Integer, Player> vips = new HashMap<>();
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
						selectNewVipOfTeam(1);
						selectNewVipOfTeam(2);
						forceSitAll();
						setStatus(EventState.FIGHT);
						schedule(20000);
						break;
						
					case FIGHT:
						forceStandAll();
						sendMsg();
						setStatus(EventState.END);
						clock.startClock(getInt("matchTime"));
						break;
						
					case END:
						clock.setTime(0);
						if (winnerTeam == 0)
							winnerTeam = getWinnerTeam();
						
						setStatus(EventState.INACTIVE);
						
						if (winnerTeam == 0)
							EventManager.getInstance().end("The event ended in a tie! both teams had " + teams.get(1).getScore() + " VIP kills!");
						else
						{
							giveReward(getPlayersOfTeam(winnerTeam), getInt("rewardId"), getInt("rewardAmmount"));
							EventManager.getInstance().end("Congratulation! The " + teams.get(winnerTeam).getName() + " team won the event with " + teams.get(winnerTeam).getScore() + " VIP kills!");
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
	
	public VIPTvT()
	{
		super();
		eventId = 8;
		createNewTeam(1, "Blue", getColor("Blue"), getPosition("Blue", 1));
		createNewTeam(2, "Red", getColor("Red"), getPosition("Red", 1));
	}
	
	@Override
	protected void endEvent()
	{
		winnerTeam = players.hashCode();
		
		setStatus(EventState.END);
		clock.setTime(0);
	}
	
	@Override
	public void onDie(Player victim, Creature killer)
	{
		super.onDie(victim, killer);
		if (vips.get(1) == victim)
		{
			teams.get(2).increaseScore();
			increasePlayersScore((Player) killer);
			selectNewVipOfTeam(1);
		}
		if (vips.get(2) == victim)
		{
			teams.get(1).increaseScore();
			increasePlayersScore((Player) killer);
			selectNewVipOfTeam(2);
		}
		
		addToResurrector(victim);
	}
	
	@Override
	protected void schedule(int time)
	{
		tpm.schedule(task, time);
	}
	
	protected void selectNewVipOfTeam(int team)
	{
		if (vips.get(team) != null)
		{
			int[] nameColor = teams.get(getTeam(vips.get(team))).getTeamColor();
			vips.get(team).getAppearance().setNameColor(nameColor[0], nameColor[1], nameColor[2]);
		}
		
		Player newvip = getRandomPlayerFromTeam(team);
		vips.put(team, newvip);
		
		if (team == 1)
		{
			int[] c = getColor("BlueVIP");
			newvip.getAppearance().setNameColor(c[0], c[1], c[2]);
		}
		else if (team == 2)
		{
			int[] c = getColor("RedVIP");
			newvip.getAppearance().setNameColor(c[0], c[1], c[2]);
		}
		
		if (!newvip.isDead())
		{
			newvip.setCurrentCp(newvip.getMaxCp());
			newvip.setCurrentMp(newvip.getMaxMp());
			newvip.setCurrentHp(newvip.getMaxHp());
		}
		
		newvip.broadcastUserInfo();
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
		sb.append("<html><body><table width=270><tr><td width=200>Event Engine </td><td><a action=\"bypass -h eventstats 1\">Statistics</a></td></tr></table><br><center><table width=270 bgcolor=5A5A5A><tr><td width=70>Running</td><td width=130><center>" + getString("eventName") + "</td><td width=70>Time: " + clock.getTime() + "</td></tr></table><table width=270><tr><td><center><font color=" + teams.get(1).getHexaColor() + ">" + teams.get(1).getScore() + "</font> - " + "<font color=" + teams.get(2).getHexaColor() + ">" + teams.get(2).getScore() + "</font></td></tr></table><br><table width=270>");
		
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
		vips.put(1, null);
		vips.put(2, null);
		setStatus(EventState.START);
		schedule(1);
	}
	
	@Override 
	public void onLogout(Player player) 
	{ 
		super.onLogout(player); 
		
		if (vips.get(1) == player) 
			selectNewVipOfTeam(1); 
		if (vips.get(2) == player) 
			selectNewVipOfTeam(2);
	}
	
	@Override
	protected String getStartingMsg()
	{
		return "Kill the enemy VIP while protecting yours!";
	}
	
	@Override
	protected String getScorebar()
	{
		return teams.get(1).getName() + ": " + teams.get(1).getScore() + "  " + teams.get(2).getName() + ": " + teams.get(2).getScore() + "  Time: " + clock.getTime();
	}
}