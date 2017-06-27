package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.commons.math.MathUtil;

import net.sf.l2j.gameserver.datatables.RecipeTable;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.actor.instance.Player.StoreType;
import net.sf.l2j.gameserver.network.FloodProtectors;
import net.sf.l2j.gameserver.network.FloodProtectors.Action;
import net.sf.l2j.gameserver.network.SystemMessageId;

public final class RequestRecipeShopMakeItem extends L2GameClientPacket
{
	private int _id;
	private int _recipeId;
	@SuppressWarnings("unused")
	private int _unknow;
	
	@Override
	protected void readImpl()
	{
		_id = readD();
		_recipeId = readD();
		_unknow = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (!FloodProtectors.performAction(getClient(), Action.MANUFACTURE))
			return;
		
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		final Player manufacturer = World.getInstance().getPlayer(_id);
		if (manufacturer == null)
			return;
		
		if (activeChar.isInStoreMode())
			return;
		
		if (manufacturer.getStoreType() != StoreType.MANUFACTURE)
			return;
		
		if (activeChar.isCrafting() || manufacturer.isCrafting())
			return;
		
		if (manufacturer.isInDuel() || activeChar.isInDuel())
		{
			activeChar.sendPacket(SystemMessageId.CANT_OPERATE_PRIVATE_STORE_DURING_COMBAT);
			return;
		}
		
		if (MathUtil.checkIfInRange(150, activeChar, manufacturer, true))
			RecipeTable.getInstance().requestManufactureItem(manufacturer, _recipeId, activeChar);
	}
}