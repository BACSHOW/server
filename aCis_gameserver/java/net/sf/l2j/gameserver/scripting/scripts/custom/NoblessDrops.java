/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.scripting.scripts.custom;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.scripting.Quest;

public class NoblessDrops extends Quest
{
	private static final String qn = "NoblessDrops";
	
	private static final int[] CAVE_MOBS =
	{
		20329,
		20340,
		20341,
		20368,
		20375,
		20374,
		20415,
		20416,
		20427,
		20429,
		20487,
		20488,
		20489,
		20491,
		20542,
		20543
	};
	
	// Count
	public static int ItemCountMin1 = Config.QUEST_NOBLES_ITEM_COUNT_MIN1;
	public static int ItemCountMax1 = Config.QUEST_NOBLES_ITEM_COUNT_MAX1;
	public static int ItemCountMin2 = Config.QUEST_NOBLES_ITEM_COUNT_MIN2;
	public static int ItemCountMax2 = Config.QUEST_NOBLES_ITEM_COUNT_MAX2;
	
	// Chance
	private static final int SILVER_CHANCE = Config.QUEST_NOBLES_ITEM_CHANCE1;
	private static final int GOLDEN_CHANCE = Config.QUEST_NOBLES_ITEM_CHANCE2;

	public NoblessDrops()
	{
		super(-1, "custom");

		addKillId(CAVE_MOBS);
	}

	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		player.getQuestState(qn);
		
		if (Rnd.get(100) < SILVER_CHANCE)
		{
			player.addItem(Config.QUEST_ITEM_NAME2, Config.QUEST_NOBLES_ITEM_ID1, Rnd.get(ItemCountMin1, ItemCountMax1), player, true);
		}
		if (Rnd.get(100) < GOLDEN_CHANCE)
		{
			player.addItem(Config.QUEST_ITEM_NAME2, Config.QUEST_NOBLES_ITEM_ID2, Rnd.get(ItemCountMin2, ItemCountMax2), player, true);
		}

		return null;
	}

	public static void main(String[] args)
	{
		new NoblessDrops();
	}
}