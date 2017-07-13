package net.sf.l2j.gameserver.scripting.scripts.ai.group;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.scripting.scripts.ai.L2AttackableAIScript;

public class MagmaDrake extends L2AttackableAIScript
{
	
	private static final int NPC[] =
	{
		21393,
		21657
	};
	
	public MagmaDrake()
	{
		super("ai/group");
		addKillId(21393);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isPet)
	{
		if (npc.getNpcId() == NPC[0])
		{
			if (Rnd.get(100) <= 30)
			{
				for (int i = 0; i < 5; i++)
				{
					Attackable newNpc = (Attackable) addSpawn(NPC[1], npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, 0, false);
					Creature originalAttacker = isPet ? killer.getPet() : killer;
					newNpc.setRunning();
					newNpc.addDamageHate(originalAttacker, 0, 999);
					newNpc.getAI().setIntention(CtrlIntention.ATTACK, originalAttacker);
				}
				return super.onKill(npc, killer, isPet);
			}
		}
		return null;
	}
}