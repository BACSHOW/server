package net.sf.l2j.gameserver.scripting.scripts.ai.group;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.scripting.scripts.ai.L2AttackableAIScript;

/**
 * @author Execution!
 */
public class Elpy extends L2AttackableAIScript
{
	
	private static final int ELPY = 20432;
	private static boolean _FirstAttacked;
	
	public Elpy()
	{
		super("ai/group");
		_FirstAttacked = false;
		addAttackActId(ELPY);
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isPet, L2Skill skill)
	{
		if (npc.getNpcId() == ELPY)
		{
			if (_FirstAttacked)
			{
				if (Rnd.get(100) < 100)
				{
					npc.isAfraid();
				}
			}
			else
			{
				_FirstAttacked = true;
			}
		}
		return super.onAttack(npc, attacker, damage, isPet, skill);
	}
}