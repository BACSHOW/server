package net.sf.l2j.gameserver.model;

import java.util.ArrayList;
import java.util.List;

public class TowerSpawn
{
	private final int _npcId;
	private final SpawnLocation _location;
	private List<Integer> _zoneList;
	private int _upgradeLevel;
	
	public TowerSpawn(int npcId, SpawnLocation location)
	{
		_location = location;
		_npcId = npcId;
	}
	
	public TowerSpawn(int npcId, SpawnLocation location, String[] zoneList)
	{
		_location = location;
		_npcId = npcId;
		
		_zoneList = new ArrayList<>();
		for (String zoneId : zoneList)
			_zoneList.add(Integer.parseInt(zoneId));
	}
	
	public int getId()
	{
		return _npcId;
	}
	
	public SpawnLocation getLocation()
	{
		return _location;
	}
	
	public List<Integer> getZoneList()
	{
		return _zoneList;
	}
	
	public void setUpgradeLevel(int level)
	{
		_upgradeLevel = level;
	}
	
	public int getUpgradeLevel()
	{
		return _upgradeLevel;
	}
}