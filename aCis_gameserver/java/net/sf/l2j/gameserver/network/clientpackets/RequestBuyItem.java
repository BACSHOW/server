package net.sf.l2j.gameserver.network.clientpackets;

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.BuyListTable;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Merchant;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.buylist.NpcBuyList;
import net.sf.l2j.gameserver.model.buylist.Product;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.gameserver.network.serverpackets.StatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

public final class RequestBuyItem extends L2GameClientPacket
{
	private static final int BATCH_LENGTH = 8; // length of the one item
	
	private int _listId;
	private List<IntIntHolder> _items = null;
	
	@Override
	protected void readImpl()
	{
		_listId = readD();
		int count = readD();
		if (count <= 0 || count > Config.MAX_ITEM_IN_PACKET || count * BATCH_LENGTH != _buf.remaining())
			return;
		
		_items = new ArrayList<>(count);
		for (int i = 0; i < count; i++)
		{
			int itemId = readD();
			int cnt = readD();
			
			if (itemId < 1 || cnt < 1)
				return;
			
			_items.add(new IntIntHolder(itemId, cnt));
		}
	}
	
	@Override
	protected void runImpl()
	{
		if (_items == null)
			return;
		
		final Player player = getClient().getActiveChar();
		if (player == null)
			return;
		
		if (!Config.KARMA_PLAYER_CAN_SHOP && player.getKarma() > 0)
			return;
		
		Npc merchant = null;
		if (!player.isGM())
		{
			merchant = (player.getTarget() instanceof Merchant) ? (Npc) player.getTarget() : null;
			if (merchant == null || !merchant.canInteract(player))
				return;
		}
		
		final NpcBuyList buyList = BuyListTable.getInstance().getBuyList(_listId);
		if (buyList == null)
			return;
		
		double castleTaxRate = 0;
		
		if (merchant != null)
		{
			if (!buyList.isNpcAllowed(merchant.getNpcId()))
				return;
			
			if (merchant instanceof Merchant && merchant.getCastle() != null)
				castleTaxRate = merchant.getCastle().getTaxRate();
		}
		
		int subTotal = 0;
		int slots = 0;
		int weight = 0;
		
		for (IntIntHolder i : _items)
		{
			int price = -1;
			
			final Product product = buyList.getProductByItemId(i.getId());
			if (product == null)
				return;
			
			if (!product.getItem().isStackable() && i.getValue() > 1)
			{
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED));
				return;
			}
			
			price = product.getPrice();
			if (i.getId() >= 3960 && i.getId() <= 4026)
				price *= Config.RATE_SIEGE_GUARDS_PRICE;
			
			if (price < 0)
				return;
			
			if (price == 0 && !player.isGM())
				return;
			
			if (product.hasLimitedStock())
			{
				// trying to buy more then available
				if (i.getValue() > product.getCount())
					return;
			}
			
			if ((Integer.MAX_VALUE / i.getValue()) < price)
				return;
			
			// first calculate price per item with tax, then multiply by count
			price = (int) (price * (1 + castleTaxRate));
			subTotal += i.getValue() * price;
			
			if (subTotal > Integer.MAX_VALUE)
				return;
			
			weight += i.getValue() * product.getItem().getWeight();
			if (!product.getItem().isStackable())
				slots += i.getValue();
			else if (player.getInventory().getItemByItemId(i.getId()) == null)
				slots++;
		}
		
		if (weight > Integer.MAX_VALUE || weight < 0 || !player.getInventory().validateWeight(weight))
		{
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.WEIGHT_LIMIT_EXCEEDED));
			return;
		}
		
		if (slots > Integer.MAX_VALUE || slots < 0 || !player.getInventory().validateCapacity(slots))
		{
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SLOTS_FULL));
			return;
		}
		
		// Charge buyer and add tax to castle treasury if not owned by npc clan
		if (subTotal < 0 || !player.reduceAdena("Buy", subTotal, player.getCurrentFolkNPC(), false))
		{
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
			return;
		}
		
		// Proceed the purchase
		for (IntIntHolder i : _items)
		{
			final Product product = buyList.getProductByItemId(i.getId());
			if (product == null)
				continue;
			
			if (product.hasLimitedStock())
			{
				if (product.decreaseCount(i.getValue()))
					player.getInventory().addItem("Buy", i.getId(), i.getValue(), player, merchant);
			}
			else
				player.getInventory().addItem("Buy", i.getId(), i.getValue(), player, merchant);
		}
		
		// add to castle treasury
		if (merchant instanceof Merchant && merchant.getCastle() != null)
			((Merchant) merchant).getCastle().addToTreasury((int) (subTotal * castleTaxRate));
		
		StatusUpdate su = new StatusUpdate(player);
		su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
		player.sendPacket(su);
		player.sendPacket(new ItemList(player, true));
	}
}