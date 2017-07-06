package net.sf.l2j.gameserver.model.zone.type;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.zone.L2SpawnZone;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class L2TownZone extends L2SpawnZone
{
	private int _townId;
	private int _castleId;
	private boolean _isPeaceZone;
	
	public L2TownZone(int id)
	{
		super(id);
		
		// Default peace zone
		_isPeaceZone = true;
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("townId"))
			_townId = Integer.parseInt(value);
		else if (name.equals("castleId"))
			_castleId = Integer.parseInt(value);
		else if (name.equals("isPeaceZone"))
			_isPeaceZone = Boolean.parseBoolean(value);
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		if (character instanceof Player)
		{
			if(((Player) character).isFlying() && Config.ALLOW_WYVERN_RESTRITION_CITY)
			{
				((Player) character).teleToLocation(Config.WYVERN_RESTRITION_LOC[0], Config.WYVERN_RESTRITION_LOC[1], Config.WYVERN_RESTRITION_LOC[2], 0);
				showHtmlWyvern((Player) character);
			}
			
			// PVP possible during siege, now for siege participants only
			// Could also check if this town is in siege, or if any siege is going on
			if (((Player) character).getSiegeState() != 0 && Config.ZONE_TOWN == 1)
				return;
		}
		
		if (_isPeaceZone && Config.ZONE_TOWN != 2)
			character.setInsideZone(ZoneId.PEACE, true);
		
		character.setInsideZone(ZoneId.TOWN, true);
	}
	
	@Override
	protected void onExit(Creature character)
	{
		if (_isPeaceZone)
			character.setInsideZone(ZoneId.PEACE, false);
		
		character.setInsideZone(ZoneId.TOWN, false);
	}
	
	@Override
	public void onDieInside(Creature character)
	{
	}
	
	@Override
	public void onReviveInside(Creature character)
	{
	}
	
	/**
	 * @return the zone town id (if any)
	 */
	public int getTownId()
	{
		return _townId;
	}
	
	/**
	 * @return the castle id (used to retrieve taxes).
	 */
	public final int getCastleId()
	{
		return _castleId;
	}
	
	public final boolean isPeaceZone()
	{
		return _isPeaceZone;
	}
	
	private static void showHtmlWyvern(Player activeChar)
	{
		String filename = "data/html/mods/WyvernTownProtection.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		html.setFile(filename);
		html.replace("%objectId%", String.valueOf(activeChar.getObjectId()));
		html.replace("%wyvernX%", Config.WYVERN_RESTRITION_LOC[0]);
		html.replace("%wyvernY%", Config.WYVERN_RESTRITION_LOC[1]);
		html.replace("%wyvernZ%", Config.WYVERN_RESTRITION_LOC[2]);
		activeChar.sendPacket(html);
	}
}