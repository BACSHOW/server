package net.sf.l2j.gameserver.scripting.scripts.ai.group;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcSay;
import net.sf.l2j.gameserver.scripting.EventType;
import net.sf.l2j.gameserver.scripting.scripts.ai.L2AttackableAIScript;

/**
 * @author Maxi
 */
public class CatsEyeBandit extends L2AttackableAIScript
{
	
	private static final int BANDIT = 27038;
	
	private static boolean _FirstAttacked;
	
	public CatsEyeBandit()
	{
		super("ai/group");
		_FirstAttacked = false;
	}
	
	@Override
	protected void registerNpcs()
	{
		addEventIds(BANDIT, EventType.ON_ATTACK, EventType.ON_KILL);
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isPet, L2Skill skill)
	{
		if (npc.getNpcId() == BANDIT)
		{
			if (_FirstAttacked)
			{
				if (Rnd.get(100) == 40)
				{
					npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), "You childish fool, do you think you can catch me?"));
				}
			}
			else
			{
				_FirstAttacked = true;
			}
		}
		return super.onAttack(npc, attacker, damage, isPet, skill);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isPet)
	{
		int npcId = npc.getNpcId();
		if (npcId == BANDIT)
		{
			int objId = npc.getObjectId();
			if (Rnd.get(100) == 80)
			{
				npc.broadcastPacket(new NpcSay(objId, 0, npcId, "I must do something about this shameful incident..."));
			}
			_FirstAttacked = false;
		}
		return super.onKill(npc, killer, isPet);
	}
}