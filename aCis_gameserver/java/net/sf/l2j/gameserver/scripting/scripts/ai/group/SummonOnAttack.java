/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.sf.l2j.gameserver.scripting.scripts.ai.group;

import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.scripting.scripts.ai.L2AttackableAIScript;

public class SummonOnAttack extends L2AttackableAIScript
{
	private static final int[] mobs =
	{
		20965,
		20966,
		20967,
		20968,
		20969,
		20970,
		20971,
		20972,
		20973
	};
	
	public SummonOnAttack()
	{
		super("ai/group");
		for (int id : mobs)
		{
			addAttackId(id);
		}
	}
	
	@Override
	public String onAttack(Npc npc, Player player, int damage, boolean isPet, L2Skill skill)
	{
		Creature attacker = isPet ? player.getPet().getOwner() : player;
		
		npc.setIsRunning(true);
		((Attackable) npc).addDamageHate(attacker, 0, 999);
		npc.getAI().setIntention(CtrlIntention.ATTACK, attacker);
		
		return super.onAttack(npc, player, damage, isPet, skill);
	}
}