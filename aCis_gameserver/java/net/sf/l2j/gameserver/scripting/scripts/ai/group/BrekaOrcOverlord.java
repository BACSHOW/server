package net.sf.l2j.gameserver.scripting.scripts.ai.group;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.scripting.EventType;
import net.sf.l2j.gameserver.scripting.scripts.ai.L2AttackableAIScript;

public class BrekaOrcOverlord extends L2AttackableAIScript
{
	
	private static boolean _FirstAttacked;
	
	public BrekaOrcOverlord()
	{
		super("ai/group");
		_FirstAttacked = false;
	}
	
	@Override
	protected void registerNpcs()
	{
		addEventIds(20270, EventType.ON_ATTACK, EventType.ON_KILL);
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isPet, L2Skill skill)
	{
		if (npc.getNpcId() == 20270)
		{
			if (_FirstAttacked)
			{
				if (Rnd.get(100) < 50)
				{
					npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 0, npc.getName(), "Extreme strength! ! ! !"));
				}
				else if (Rnd.get(100) < 50)
				{
					npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 0, npc.getName(), "Humph, wanted to win me to be also in tender!"));
				}
				else if (Rnd.get(100) < 50)
				{
					npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 0, npc.getName(), "Haven't thought to use this unique skill for this small thing!"));
				}
			}
			else
			{
				_FirstAttacked = true;
			}
		}
		
		return super.onAttack(npc, attacker, damage, isPet, skill);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isPet)
	{
		if (npc.getNpcId() == 20270)
		{
			_FirstAttacked = false;
		}
		else if (_FirstAttacked)
		{
			addSpawn(npc.getNpcId(), npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, 0, true);
		}
		
		return super.onKill(npc, killer, isPet);
	}
}