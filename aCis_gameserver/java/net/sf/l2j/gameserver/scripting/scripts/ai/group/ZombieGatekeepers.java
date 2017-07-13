package net.sf.l2j.gameserver.scripting.scripts.ai.group;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.scripting.scripts.ai.L2AttackableAIScript;

public class ZombieGatekeepers extends L2AttackableAIScript
{
	public ZombieGatekeepers()
	{
		super("ai/group");
		addAttackId(22136);
		addAggroRangeEnterId(22136);
	}
	
	private final Map<Integer, ArrayList<Creature>> _attackersList = new LinkedHashMap<>();
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isPet, L2Skill skill)
	{
		int npcObjId = npc.getObjectId();
		
		Creature target = isPet ? attacker.getPet() : attacker;
		
		if (_attackersList.get(npcObjId) == null)
		{
			ArrayList<Creature> player = new ArrayList<>();
			player.add(target);
			_attackersList.put(npcObjId, player);
		}
		else if (!_attackersList.get(npcObjId).contains(target))
		{
			_attackersList.get(npcObjId).add(target);
		}
		
		return super.onAttack(npc, attacker, damage, isPet, skill);
	}
	
	@Override
	public String onAggro(Npc npc, Player player, boolean isPet)
	{
		if ((player.getLevel() < 73) && !player.isGM())
		{
			((Attackable) npc).addDamageHate(player, 0, 999);
			npc.getAI().setIntention(CtrlIntention.ATTACK, player);
			return super.onAggro(npc, player, isPet);
		}
		
		int npcObjId = npc.getObjectId();
		
		Creature target = isPet ? player.getPet() : player;
		
		ItemInstance VisitorsMark = player.getInventory().getItemByItemId(8064);
		ItemInstance FadedVisitorsMark = player.getInventory().getItemByItemId(8065);
		ItemInstance PagansMark = player.getInventory().getItemByItemId(8067);
		
		long mark1 = VisitorsMark == null ? 0 : VisitorsMark.getCount();
		long mark2 = FadedVisitorsMark == null ? 0 : FadedVisitorsMark.getCount();
		long mark3 = PagansMark == null ? 0 : PagansMark.getCount();
		
		if ((mark1 == 0) && (mark2 == 0) && (mark3 == 0))
		{
			((Attackable) npc).addDamageHate(target, 0, 999);
			npc.getAI().setIntention(CtrlIntention.ATTACK, target);
		}
		else
		{
			if ((_attackersList.get(npcObjId) == null) || !_attackersList.get(npcObjId).contains(target))
			{
				((Attackable) npc).getAggroList().remove(target);
			}
			else
			{
				((Attackable) npc).addDamageHate(target, 0, 999);
				npc.getAI().setIntention(CtrlIntention.ATTACK, target);
			}
		}
		
		return super.onAggro(npc, player, isPet);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isPet)
	{
		int npcObjId = npc.getObjectId();
		if (_attackersList.get(npcObjId) != null)
		{
			_attackersList.get(npcObjId).clear();
		}
		
		return super.onKill(npc, killer, isPet);
	}
}