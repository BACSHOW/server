package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.ai.type.CreatureAI;
import net.sf.l2j.gameserver.model.actor.ai.type.WalkerAI;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;

/**
 * This class manages npcs who can walk using nodes.
 * @author Rayan RPG, JIV
 */
public class Walker extends Folk
{
	public Walker(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setAI(new WalkerAI(this));
	}
	
	@Override
	public void setAI(CreatureAI newAI)
	{
		// AI can't be detached, npc must move with the same AI instance.
		if (!(_ai instanceof WalkerAI))
			_ai = newAI;
	}
	
	@Override
	public void reduceCurrentHp(double i, Creature attacker, boolean awake, boolean isDOT, L2Skill skill)
	{
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		return false;
	}
	
	@Override
	public WalkerAI getAI()
	{
		return (WalkerAI) _ai;
	}
	
	@Override
	public void detachAI()
	{
		// AI can't be detached.
	}
}