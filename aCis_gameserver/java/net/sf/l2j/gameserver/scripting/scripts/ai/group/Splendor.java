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

import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.scripting.EventType;
import net.sf.l2j.gameserver.scripting.scripts.ai.L2AttackableAIScript;

public class Splendor extends L2AttackableAIScript
{
	private static boolean AlwaysSpawn;
	
	private static Map<Integer, int[]> SplendorId = new HashMap<>();
	
	public Splendor()
	{
		super("ai/group");
		
		AlwaysSpawn = false;
	}
	
	@Override
	protected void registerNpcs()
	{
		SplendorId.put(21521, new int[]
		{
			21522,
			5,
			1
		});
		SplendorId.put(21524, new int[]
		{
			21525,
			5,
			1
		});
		SplendorId.put(21527, new int[]
		{
			21528,
			5,
			1
		});
		SplendorId.put(21537, new int[]
		{
			21538,
			5,
			1
		});
		SplendorId.put(21539, new int[]
		{
			21540,
			100,
			2
		});
		for (int NPC_ID : SplendorId.keySet())
		{
			addEventIds(NPC_ID, EventType.ON_ATTACK, EventType.ON_KILL);
		}
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isPet, L2Skill skill)
	{
		int npcId = npc.getNpcId();
		int NewMob = SplendorId.get(npcId)[0];
		int chance = SplendorId.get(npcId)[1];
		int ModeSpawn = SplendorId.get(npcId)[2];
		if (Rnd.get(100) <= chance * Config.RATE_QUEST_DROP)
		{
			if (SplendorId.containsKey(npcId))
			{
				if (ModeSpawn == 1)
				{
					npc.deleteMe();
					Attackable newNpc = (Attackable) addSpawn(NewMob, npc, isPet, ModeSpawn, isPet);
					newNpc.addDamageHate(attacker, 0, 999);
					newNpc.getAI().setIntention(CtrlIntention.ATTACK, attacker);
				}
				else if (AlwaysSpawn)
				{
					return super.onAttack(npc, attacker, damage, isPet, skill);
				}
				else if (ModeSpawn == 2)
				{
					AlwaysSpawn = true;
					Attackable newNpc1 = (Attackable) addSpawn(NewMob, npc, isPet, ModeSpawn, isPet);
					newNpc1.addDamageHate(attacker, 0, 999);
					newNpc1.getAI().setIntention(CtrlIntention.ATTACK, attacker);
				}
			}
		}
		
		return super.onAttack(npc, attacker, damage, isPet, skill);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isPet)
	{
		int npcId = npc.getNpcId();
		int ModeSpawn = SplendorId.get(npcId)[2];
		if (SplendorId.containsKey(npcId))
		{
			if (ModeSpawn == 2)
			{
				AlwaysSpawn = false;
			}
		}
		
		return super.onKill(npc, killer, isPet);
	}
	
}