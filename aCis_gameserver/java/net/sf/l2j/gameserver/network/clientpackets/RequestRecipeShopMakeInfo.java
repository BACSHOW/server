package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.actor.instance.Player.StoreType;
import net.sf.l2j.gameserver.network.serverpackets.RecipeShopItemInfo;

public final class RequestRecipeShopMakeInfo extends L2GameClientPacket
{
	private int _playerObjectId, _recipeId;
	
	@Override
	protected void readImpl()
	{
		_playerObjectId = readD();
		_recipeId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getActiveChar();
		if (player == null)
			return;
		
		final Player shop = World.getInstance().getPlayer(_playerObjectId);
		if (shop == null || shop.getStoreType() != StoreType.MANUFACTURE)
			return;
		
		player.sendPacket(new RecipeShopItemInfo(shop, _recipeId));
	}
}