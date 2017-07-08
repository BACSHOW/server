package net.sf.l2j.gameserver.event;

import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Spawn;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.network.serverpackets.AbstractNpcInfo.NpcInfo;

import main.util.builders.html.HtmlBuilder;

import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class Battlefield extends Event
{
	protected EventState eventState;
	protected int winnerTeam;
	private Core task = new Core();
	private HashMap<Integer,L2Spawn> bases = new HashMap<>();
	private HashMap<Integer,Integer> owners = new HashMap<>();
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
						spawnBases();
						giveSkill();
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
						
						removeSkill();
						unspawnBases();
						setStatus(EventState.INACTIVE);
						
						if (winnerTeam == 0)
							EventManager.getInstance().end("The event ended in a tie! both teams had " + teams.get(1).getScore() + " points!");
						else
						{
							giveReward(getPlayersOfTeam(winnerTeam), getInt("rewardId"), getInt("rewardAmmount"));
							EventManager.getInstance().end("Congratulation! The " + teams.get(winnerTeam).getName() + " team won the event with " + teams.get(winnerTeam).getScore() + " points!");
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
	
	public Battlefield()
	{
		super();
		eventId = 14;
		createNewTeam(1, "Blue", getColor("Blue"), getPosition("Blue", 1));
		createNewTeam(2, "Red", getColor("Red"), getPosition("Red", 1));
	}
	
	@Override
	protected void endEvent()
	{
		winnerTeam = players.hashCode();
		
		setStatus(EventState.END);
		schedule(1);
	}
	
	@Override
	protected int getWinnerTeam()
	{
		if (teams.get(1).getScore() > teams.get(2).getScore())
			return 1;
		if (teams.get(2).getScore() > teams.get(1).getScore())
			return 2;
		
		return 0;
	}
	
	@Override
	public void onDie(Player victim, Creature killer)
	{
		super.onDie(victim, killer);
		addToResurrector(victim);
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
		sb.append("<html><body><table width=300><tr><td><center>Event phase</td></tr><tr><td><center>" + getString("eventName") + " - " + clock.getTime() + "</td></tr><tr><td><center><font color=" + teams.get(1).getHexaColor() + ">" + teams.get(1).getScore() + "</font> - <font color=" + teams.get(2).getHexaColor() + ">" + teams.get(2).getScore() + "</font></td></tr></table><br><table width=300>");
		
		int i = 0;
		for (EventTeam team : teams.values())
		{
			i++;
			sb.append("<tr><td><font color=" + team.getHexaColor() + ">" + team.getName() + "</font> team</td><td></td><td></td><td></td></tr>");
			for (Player p : getPlayersOfTeam(i))
				sb.append("<tr><td>" + p.getName() + "</td><td>lvl " + p.getLevel() + "</td><td>" + p.getTemplate().getClassName() + "</td><td>" + getScore(p) + "</td></tr>");
		}
		
		sb.append("</table><br></body></html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}

	@Override
	protected void start()
	{
		setStatus(EventState.START);
		schedule(1);
	}
	
	protected void spawnBases()
	{
		for (int i = 1;i <= getInt("numOfBases");i++)
		{
			bases.put(i, spawnNPC(getPosition("Base",i)[0],getPosition("Base",i)[1],getPosition("Base",i)[2],getInt("baseNpcId")));
			bases.get(i).getNpc().setTitle("- Neutral -");
			owners.put(i, 0);
		}
	}
	
	protected void unspawnBases()
	{
		for (L2Spawn base: bases.values())
			unspawnNPC(base);
	}
	
	@Override
	public void reset()
	{
		super.reset();
		bases.clear();
		owners.clear();
	}
	
	@Override
	protected void clockTick()
	{
		for (int owner : owners.values())
			if (owner != 0)
				teams.get(owner).increaseScore(1);
	}
	
	@Override
	public void useCapture(Player player, Npc base)
	{
		if (base.getNpcId() != getInt("baseNpcId"))
			return;
		
		for (Map.Entry<Integer, L2Spawn> baseSpawn : bases.entrySet())
		{
			if (baseSpawn.getValue().equals(base))
			{
				if (owners.get(baseSpawn.getKey()) == getTeam(player))
					return;
				
				owners.get(baseSpawn.getKey());
				Integer.valueOf(getTeam(player));
				baseSpawn.getValue().getNpc().setTitle("- " + teams.get(getTeam(player)).getName() + " -");
				for (Player p : getPlayerList())
					p.sendPacket(new NpcInfo(baseSpawn.getValue().getNpc(), p));
				
				announce(getPlayerList(), "The " + teams.get(getTeam(player)).getName() + " team captured a base!");
				increasePlayersScore(player);
			}
		}
	}
	
	@Override
	protected String getStartingMsg()
	{
		return "Capture the flags by using the Capture skill on them!";
	}
	
	@Override
	protected String getScorebar()
	{
		return teams.get(1).getName() + ": " + teams.get(1).getScore() + "  " + teams.get(2).getName() + ": " + teams.get(2).getScore() + "  Time: " + clock.getTime();
	}
	
	protected void removeSkill()
	{
		for (Player player : getPlayerList())
			player.removeSkill(SkillTable.getInstance().getInfo(getInt("captureSkillId"), 1), false);
	}
	
	protected void giveSkill()
	{
		for (Player player : getPlayerList())
		{
			player.addSkill(SkillTable.getInstance().getInfo(getInt("captureSkillId"), 1), false);
			player.sendSkillList();
		}
	}
}