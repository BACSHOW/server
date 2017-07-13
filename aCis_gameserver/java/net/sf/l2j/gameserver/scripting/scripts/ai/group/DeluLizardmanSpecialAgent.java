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
public class DeluLizardmanSpecialAgent extends L2AttackableAIScript
{
	
	private static final int LIZARDMAN = 21105;
	
	private static boolean _FirstAttacked;
	
	public DeluLizardmanSpecialAgent()
	{
		super("ai/group");
		_FirstAttacked = false;
	}
	
	@Override
	protected void registerNpcs()
	{
		addEventIds(LIZARDMAN, EventType.ON_ATTACK, EventType.ON_KILL);
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isPet, L2Skill skill)
	{
		if (npc.getNpcId() == LIZARDMAN)
		{
			if (_FirstAttacked)
			{
				if (Rnd.get(100) == 40)
				{
					npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), "Hey! Were having a duel here!"));
				}
			}
			else
			{
				_FirstAttacked = true;
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), "How dare you interrupt our fight! Hey guys, help!"));
			}
		}
		return super.onAttack(npc, attacker, damage, isPet, skill);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isPet)
	{
		int npcId = npc.getNpcId();
		if (npcId == LIZARDMAN)
		{
			_FirstAttacked = false;
		}
		return super.onKill(npc, killer, isPet);
	}
}