package net.sf.l2j.gameserver.model;

import net.sf.l2j.gameserver.templates.StatsSet;

public class HelperBuff
{
	private int _lowerLevel;
	private int _upperLevel;
	private int _skillId;
	private int _skillLevel;
	private boolean _isMagicClass;
	
	public HelperBuff(StatsSet set)
	{
		_lowerLevel = set.getInteger("lowerLevel");
		_upperLevel = set.getInteger("upperLevel");
		_skillId = set.getInteger("skillId");
		_skillLevel = set.getInteger("skillLevel");
		_isMagicClass = set.getBool("isMagicClass");
	}
	
	/**
	 * @return the lower level that the player must achieve in order to obtain this buff.
	 */
	public int getLowerLevel()
	{
		return _lowerLevel;
	}
	
	/**
	 * @return the upper level that the player mustn't exceed in order to obtain this buff.
	 */
	public int getUpperLevel()
	{
		return _upperLevel;
	}
	
	/**
	 * @return the skill id of the buff that the player will receive.
	 */
	public int getSkillId()
	{
		return _skillId;
	}
	
	/**
	 * @return the Level of the buff that the player will receive.
	 */
	public int getSkillLevel()
	{
		return _skillLevel;
	}
	
	/**
	 * @return false if it's a fighter buff, true if it's a magic buff.
	 */
	public boolean isMagicClassBuff()
	{
		return _isMagicClass;
	}
}