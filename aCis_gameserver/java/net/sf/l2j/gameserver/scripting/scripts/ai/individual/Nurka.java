package net.sf.l2j.gameserver.scripting.scripts.ai.individual;

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.clanhallsiege.FortressOfResistance;
import net.sf.l2j.gameserver.scripting.EventType;
import net.sf.l2j.gameserver.scripting.scripts.ai.L2AttackableAIScript;

/**
 * @author TerryXX
 */
public class Nurka extends L2AttackableAIScript
{
	private static final int NURKA = 35368;
	private static final int MESSENGER = 35382;
	private static List<String> CLAN_LEADERS = new ArrayList<>();
	
	public Nurka()
	{
		super("ai/individual");
	}
	
	@Override
	protected void registerNpcs()
	{
		addEventIds(MESSENGER, EventType.QUEST_START, EventType.ON_TALK);
		addEventIds(NURKA, EventType.ON_ATTACK, EventType.ON_KILL);
	}
	
	@SuppressWarnings("null")
	@Override
	public String onTalk(Npc npc, Player player)
	{
		if (npc.getNpcId() == MESSENGER)
		{
			if (player != null && CLAN_LEADERS.contains(player.getName()))
			{
				return "<html><body>Messenger:<br>You already registered!</body></html>";
			}
			else if (FortressOfResistance.Conditions(player))
			{
				CLAN_LEADERS.add(player.getName());
				return "<html><body>Messenger:<br>You have successful registered on a siege!</body></html>";
			}
			else
			{
				return "<html><body>Messenger:<br>Condition are not allow to do that!</body></html>";
			}
		}
		
		return super.onTalk(npc, player);
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isPet, L2Skill skill)
	{
		if (attacker != null && npc.getNpcId() == NURKA && CLAN_LEADERS.contains(attacker.getName()))
		{
			FortressOfResistance.getInstance().addSiegeDamage(attacker.getClan(), damage);
		}
		
		return super.onAttack(npc, attacker, damage, isPet, skill);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isPet)
	{
		FortressOfResistance.getInstance().CaptureFinish();
		return super.onKill(npc, killer, isPet);
	}
	
}