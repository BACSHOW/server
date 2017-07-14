package net.sf.l2j.gameserver.scripting.quests.audio;

import net.sf.l2j.gameserver.network.serverpackets.PlaySound;

/**
 * @author Zealar
 */
public enum Music implements IAudio
{
	B03_D_10000("B03_D", 10000),
	B04_S01("B04_S01"),
	BS01_A_10000("BS01_A", 10000),
	BS01_A_7000("BS01_A", 7000),
	BS01_D_10000("BS01_D", 10000),
	BS02_D_10000("BS02_D", 10000),
	BS02_D_7000("BS02_D", 7000),
	BS03_A_10000("BS03_A", 10000),
	SF_S_01("SF_S_01"),
	NS22_F("NS22_F"),
	S_RACE("S_Race"),
	SF_P_01("SF_P_01"),
	SIEGE_VICTORY("Siege_Victory");
	
	private final PlaySound _playSound;
	
	Music(String soundName)
	{
		_playSound = PlaySound.createMusic(soundName, 0);
	}
	
	Music(String soundName, int delay)
	{
		_playSound = PlaySound.createMusic(soundName, delay);
	}
	
	@Override
	public String getSoundName()
	{
		return _playSound.getSoundName();
	}
	
	@Override
	public PlaySound getPacket()
	{
		return _playSound;
	}
}