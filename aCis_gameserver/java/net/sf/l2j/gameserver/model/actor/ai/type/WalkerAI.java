package net.sf.l2j.gameserver.model.actor.ai.type;

import java.util.List;

import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.gameserver.datatables.NpcWalkerRoutesTable;
import net.sf.l2j.gameserver.model.L2NpcWalkerNode;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.SpawnLocation;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.actor.instance.Walker;

public class WalkerAI extends CreatureAI implements Runnable
{
	// The route used by this NPC, consisting of multiple nodes
	private final List<L2NpcWalkerNode> _route;
	
	// Flag allowing NPC to go to next point or no (allow to delay).
	private boolean _walkingToNextPoint = false;
	private long _nextMoveTime;
	
	// The currents node and position where the NPC is situated.
	private L2NpcWalkerNode _currentNode;
	private int _currentPos;
	
	public WalkerAI(Creature character)
	{
		super(character);
		
		_route = NpcWalkerRoutesTable.getInstance().getRouteForNpc(getActor().getNpcId());
		
		if (_route != null)
			ThreadPool.scheduleAtFixedRate(this, 1000, 1000);
		else
			_log.warning(getClass().getSimpleName() + ": Missing route data for NpcID: " + _actor);
	}
	
	@Override
	public void run()
	{
		onEvtThink();
	}
	
	@Override
	protected void onEvtThink()
	{
		if (_walkingToNextPoint)
		{
			checkArrived();
			return;
		}
		
		if (_nextMoveTime < System.currentTimeMillis())
			walkToLocation();
	}
	
	/**
	 * If npc can't walk to it's target then just teleport to next point
	 * @param loc ignoring it
	 */
	@Override
	protected void onEvtArrivedBlocked(SpawnLocation loc)
	{
		_log.warning("NpcWalker ID: " + getActor().getNpcId() + ": Blocked at coords: " + loc.toString() + ". Teleporting to next point.");
		
		getActor().teleToLocation(_currentNode.getMoveX(), _currentNode.getMoveY(), _currentNode.getMoveZ(), 0);
		super.onEvtArrivedBlocked(loc);
	}
	
	private void checkArrived()
	{
		if (getActor().isInsideRadius(_currentNode.getMoveX(), _currentNode.getMoveY(), _currentNode.getMoveZ(), 5, false, false))
		{
			String chat = _currentNode.getChatText();
			if (chat != null && !chat.isEmpty())
				getActor().broadcastNpcSay(chat);
			
			_nextMoveTime = System.currentTimeMillis() + Math.max(0, _currentNode.getDelay() * 1000);
			_walkingToNextPoint = false;
		}
	}
	
	private void walkToLocation()
	{
		if (_currentPos < (_route.size() - 1))
			_currentPos++;
		else
			_currentPos = 0;
		
		_currentNode = _route.get(_currentPos);
		
		if (_currentNode.getRunning())
			getActor().setRunning();
		else
			getActor().setWalking();
		
		_walkingToNextPoint = true;
		setIntention(CtrlIntention.MOVE_TO, new Location(_currentNode.getMoveX(), _currentNode.getMoveY(), _currentNode.getMoveZ()));
	}
	
	@Override
	public Walker getActor()
	{
		return (Walker) super.getActor();
	}
}