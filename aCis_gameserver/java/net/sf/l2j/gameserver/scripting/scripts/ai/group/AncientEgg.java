package net.sf.l2j.gameserver.scripting.scripts.ai.group;

import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.scripting.scripts.ai.L2AttackableAIScript;

/**
 * @author Maxi to java Kidzor
 */
public class AncientEgg extends L2AttackableAIScript
{
	private final int EGG = 18344;
	
	public AncientEgg()
	{
		super("ai/group");
		addAttackId(EGG);
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isPet, L2Skill skill)
	{
		attacker.setTarget(attacker);
		attacker.doCast(SkillTable.getInstance().getInfo(5088, 1));
		return super.onAttack(npc, attacker, damage, isPet, skill);
	}
}