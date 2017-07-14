package net.sf.l2j.gameserver.scripting.quests.audio;

import net.sf.l2j.gameserver.network.serverpackets.PlaySound;

/**
 * @author Zealar
 */
public interface IAudio
{
	/**
	 * @return the name of the sound of this audio object
	 */
	public String getSoundName();
	
	/**
	 * @return the {@link PlaySound} packet of this audio object
	 */
	public PlaySound getPacket();
}