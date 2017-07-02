/*
 * Copyright (C) 2004-2014 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.scripting.scripts.custom;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.scripting.Quest;

public class PcPoint extends Quest
{
    public PcPoint()
    {
        super(-1, "custom");
        for (NpcTemplate t : NpcTable.getInstance().getTemplates(t -> t.getNpcId() == Config.PC_BANG_POINTS_MOB_ID))
        {
            addKillId(t.getIdTemplate());
        }
    }
    
    @Override
    public String onKill(Npc npc, Player killer, boolean isPet)
    {
        if ((killer.getLevel() >= 75) && (npc.getLevel() < 60)) 
        {
            return "";
        }
		int score = 0;
		score = Rnd.get(Config.PC_BANG_POINTS_PER_MOB);
		killer.addPcBangScore(score);
		killer.updatePcBangWnd(score, true, false);
		killer.sendMessage("You have earned " + score + " PC Bang Points.");
        return super.onKill(npc, killer, isPet);
    }
    
    public static void main(String[] arg)
    {
        new PcPoint();
    }
    
}