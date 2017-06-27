package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.gameserver.datatables.RecipeTable;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.actor.instance.Player.StoreType;
import net.sf.l2j.gameserver.network.FloodProtectors;
import net.sf.l2j.gameserver.network.FloodProtectors.Action;

public final class RequestRecipeItemMakeSelf extends L2GameClientPacket
{
	private int _id;
	
	@Override
	protected void readImpl()
	{
		_id = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (!FloodProtectors.performAction(getClient(), Action.MANUFACTURE))
			return;
		
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		if (activeChar.getStoreType() == StoreType.MANUFACTURE || activeChar.isCrafting())
			return;
		
		RecipeTable.getInstance().requestMakeItem(activeChar, _id);
	}
}