package net.sf.l2j.gameserver.datatables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.L2GameServerPacket;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * This class stores references to all online game masters. (access level > 100)
 */
public class GmListTable
{
	private final Map<Player, Boolean> _gmList = new ConcurrentHashMap<>();
	
	protected GmListTable()
	{
		
	}
	
	public static GmListTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	public List<Player> getAllGms(boolean includeHidden)
	{
		List<Player> tmpGmList = new ArrayList<>();
		
		for (Map.Entry<Player, Boolean> entry : _gmList.entrySet())
		{
			if (includeHidden || !entry.getValue())
				tmpGmList.add(entry.getKey());
		}
		
		return tmpGmList;
	}
	
	public List<String> getAllGmNames(boolean includeHidden)
	{
		List<String> tmpGmList = new ArrayList<>();
		
		for (Map.Entry<Player, Boolean> entry : _gmList.entrySet())
		{
			String name = entry.getKey().getName();
			if (!entry.getValue())
				tmpGmList.add(name);
			else if (includeHidden)
				tmpGmList.add(name + " (invis)");
		}
		
		return tmpGmList;
	}
	
	/**
	 * Add a Player player to the Set _gmList
	 * @param player
	 * @param hidden
	 */
	public void addGm(Player player, boolean hidden)
	{
		_gmList.put(player, hidden);
	}
	
	public void deleteGm(Player player)
	{
		_gmList.remove(player);
	}
	
	/**
	 * Refresh GM for GMlist.
	 * @param player : The GM to affect.
	 * @param showOrHide : The option to set.
	 */
	public void showOrHideGm(Player player, boolean showOrHide)
	{
		if (_gmList.containsKey(player))
			_gmList.put(player, showOrHide);
	}
	
	public boolean isGmVisible(Player player)
	{
		return _gmList.get(player);
	}
	
	public boolean isGmOnline(boolean includeHidden)
	{
		for (Map.Entry<Player, Boolean> entry : _gmList.entrySet())
		{
			if (includeHidden || !entry.getValue())
				return true;
		}
		
		return false;
	}
	
	public void sendListToPlayer(Player player)
	{
		if (isGmOnline(player.isGM()))
		{
			player.sendPacket(SystemMessageId.GM_LIST);
			
			for (String name : getAllGmNames(player.isGM()))
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.GM_S1).addString(name));
		}
		else
		{
			player.sendPacket(SystemMessageId.NO_GM_PROVIDING_SERVICE_NOW);
			player.sendPacket(new PlaySound("systemmsg_e.702"));
		}
	}
	
	public static void broadcastToGMs(L2GameServerPacket packet)
	{
		for (Player gm : getInstance().getAllGms(true))
			gm.sendPacket(packet);
	}
	
	public static void broadcastMessageToGMs(String message)
	{
		for (Player gm : getInstance().getAllGms(true))
			gm.sendMessage(message);
	}
	
	private static class SingletonHolder
	{
		protected static final GmListTable _instance = new GmListTable();
	}
}