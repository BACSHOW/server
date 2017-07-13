package net.sf.l2j.gameserver.taskmanager;

import java.util.Vector;

import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.instance.Player;

/**
 * @author Anarchy
 */
public class CustomCancelTaskManager implements Runnable
{
	private Player player = null;
	private Vector<L2Skill> buffsCanceled = null;
	
	public CustomCancelTaskManager(Player p, Vector<L2Skill> skill)
	{
		player = p;
		buffsCanceled = skill;
	}
	
	@Override
	public void run()
	{
		if (player == null || !player.isOnline())
			return;
		
		for (L2Skill s : buffsCanceled)
		{
			if (s == null)
				continue;
			
			s.getEffects(player, player);
		}
	}
}