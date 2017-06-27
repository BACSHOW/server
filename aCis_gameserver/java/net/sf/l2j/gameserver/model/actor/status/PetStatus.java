package net.sf.l2j.gameserver.model.actor.status;

import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.ai.CtrlEvent;
import net.sf.l2j.gameserver.model.actor.instance.Pet;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

public class PetStatus extends SummonStatus
{
	private int _currentFed = 0; // Current Fed of the L2PetInstance
	
	public PetStatus(Pet activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public final void reduceHp(double value, Creature attacker)
	{
		reduceHp(value, attacker, true, false, false);
	}
	
	@Override
	public final void reduceHp(double value, Creature attacker, boolean awake, boolean isDOT, boolean isHpConsumption)
	{
		if (getActiveChar().isDead())
			return;
		
		super.reduceHp(value, attacker, awake, isDOT, isHpConsumption);
		
		if (attacker != null)
		{
			if (!isDOT && getActiveChar().getOwner() != null)
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.PET_RECEIVED_S2_DAMAGE_BY_S1);
				sm.addCharName(attacker);
				sm.addNumber((int) value);
				getActiveChar().getOwner().sendPacket(sm);
			}
			getActiveChar().getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, attacker);
		}
	}
	
	public int getCurrentFed()
	{
		return _currentFed;
	}
	
	public void setCurrentFed(int value)
	{
		_currentFed = value;
	}
	
	@Override
	public Pet getActiveChar()
	{
		return (Pet) super.getActiveChar();
	}
}