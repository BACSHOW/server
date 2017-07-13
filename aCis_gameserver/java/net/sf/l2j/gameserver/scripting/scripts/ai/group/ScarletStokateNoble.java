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

import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.scripting.EventType;
import net.sf.l2j.gameserver.scripting.scripts.ai.L2AttackableAIScript;

public class ScarletStokateNoble extends L2AttackableAIScript
{
	private static final int NPC[] =
	{
		21378,
		21652
	};
	
	public ScarletStokateNoble()
	{
		super("ai/group");
	}
	
	@Override
	protected void registerNpcs()
	{
		addEventIds(NPC, EventType.ON_KILL);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isPet)
	{
		if (npc.getNpcId() == NPC[0])
		{
			if (Rnd.get(100) <= 20)
			{
				addSpawn(NPC[1], npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, 0, false);
				addSpawn(NPC[1], npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, 0, false);
				addSpawn(NPC[1], npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, 0, false);
				addSpawn(NPC[1], npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, 0, false);
				addSpawn(NPC[1], npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, 0, false);
			}
		}
		return super.onKill(npc, killer, isPet);
	}
	
}