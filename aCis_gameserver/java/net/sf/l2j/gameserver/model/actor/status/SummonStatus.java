package net.sf.l2j.gameserver.model.actor.status;

import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Summon;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.Duel.DuelState;

public class SummonStatus extends PlayableStatus
{
	public SummonStatus(Summon activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public void reduceHp(double value, Creature attacker)
	{
		reduceHp(value, attacker, true, false, false);
	}
	
	@Override
	public void reduceHp(double value, Creature attacker, boolean awake, boolean isDOT, boolean isHPConsumption)
	{
		if (getActiveChar().isDead())
			return;
		
		if (attacker != null)
		{
			final Player attackerPlayer = attacker.getActingPlayer();
			if (attackerPlayer != null && (getActiveChar().getOwner() == null || getActiveChar().getOwner().getDuelId() != attackerPlayer.getDuelId()))
				attackerPlayer.setDuelState(DuelState.INTERRUPTED);
		}
		super.reduceHp(value, attacker, awake, isDOT, isHPConsumption);
	}
	
	@Override
	public Summon getActiveChar()
	{
		return (Summon) super.getActiveChar();
	}
}