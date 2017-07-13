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

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.scripting.EventType;
import net.sf.l2j.gameserver.scripting.scripts.ai.L2AttackableAIScript;

public class FairyTrees extends L2AttackableAIScript
{
	private List<mobs> _mobs = new ArrayList<>();
	
	private static final int[] MOBS =
	{
		27185,
		27186,
		27187,
		27188
	};
	
	private static class mobs
	{
		private int _id;
		
		mobs(int id)
		{
			_id = id;
		}
		
		int getId()
		{
			return _id;
		}
	}
	
	public FairyTrees()
	{
		super("ai/group");
		
		_mobs.add(new mobs(27185));
		_mobs.add(new mobs(27186));
		_mobs.add(new mobs(27187));
		_mobs.add(new mobs(27188));
	}
	
	@Override
	protected void registerNpcs()
	{
		addEventIds(MOBS, EventType.ON_KILL);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isPet)
	{
		int npcId = npc.getNpcId();
		for (mobs monster : _mobs)
		{
			if (npcId == monster.getId())
			{
				for (int i = 0; i < 20; i++)
				{
					Attackable newNpc = (Attackable) addSpawn(27189, npc.getX(), npc.getY(), npc.getZ(), 0, false, 30000, false);
					Creature originalKiller = isPet ? killer.getPet() : killer;
					newNpc.setRunning();
					newNpc.addDamageHate(originalKiller, 0, 999);
					newNpc.getAI().setIntention(CtrlIntention.ATTACK, originalKiller);
					if (Rnd.get(1, 2) == 1)
					{
						L2Skill skill = SkillTable.getInstance().getInfo(4243, 1);
						if (skill != null && originalKiller != null)
						{
							skill.getEffects(newNpc, originalKiller);
						}
					}
				}
			}
		}
		
		return super.onKill(npc, killer, isPet);
	}
	
}