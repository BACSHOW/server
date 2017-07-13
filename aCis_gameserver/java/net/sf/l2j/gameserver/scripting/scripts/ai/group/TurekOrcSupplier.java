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

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.scripting.EventType;
import net.sf.l2j.gameserver.scripting.scripts.ai.L2AttackableAIScript;

public class TurekOrcSupplier extends L2AttackableAIScript
{
	private static final int NPC = 20498;
	private static boolean _FirstAttacked;
	
	public TurekOrcSupplier()
	{
		super("ai/group");
		
		_FirstAttacked = false;
	}
	
	@Override
	protected void registerNpcs()
	{
		addEventIds(NPC, EventType.ON_ATTACK, EventType.ON_KILL);
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isPet, L2Skill skill)
	{
		if (npc.getNpcId() == NPC)
		{
			if (_FirstAttacked)
			{
				if (Rnd.get(40) != 0)
				{
					return null;
				}
				npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 0, npc.getName(), "You wont take me down easily."));
			}
			else
			{
				_FirstAttacked = true;
				npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 0, npc.getName(), "We shall see about that!"));
			}
		}
		return super.onAttack(npc, attacker, damage, isPet, skill);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isPet)
	{
		if (npc.getNpcId() == NPC)
		{
			_FirstAttacked = false;
		}
		else if (_FirstAttacked)
		{
			addSpawn(npc.getNpcId(), npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, 0, false);
		}
		return super.onKill(npc, killer, isPet);
	}
	
}