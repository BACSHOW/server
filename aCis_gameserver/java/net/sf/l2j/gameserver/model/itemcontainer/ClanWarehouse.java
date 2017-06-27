package net.sf.l2j.gameserver.model.itemcontainer;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance.ItemLocation;

public final class ClanWarehouse extends ItemContainer
{
	private final L2Clan _clan;
	
	public ClanWarehouse(L2Clan clan)
	{
		_clan = clan;
	}
	
	@Override
	public String getName()
	{
		return "ClanWarehouse";
	}
	
	@Override
	public int getOwnerId()
	{
		return _clan.getClanId();
	}
	
	@Override
	public Player getOwner()
	{
		return _clan.getLeader().getPlayerInstance();
	}
	
	@Override
	public ItemLocation getBaseLocation()
	{
		return ItemLocation.CLANWH;
	}
	
	@Override
	public boolean validateCapacity(int slots)
	{
		return _items.size() + slots <= Config.WAREHOUSE_SLOTS_CLAN;
	}
}