/*
 * Copyright (C) 2004-2017 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.scripting.quests.audio;

import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;

public enum Sound implements IAudio
{
	SOUND_ACCEPT("ItemSound.quest_accept"),
	SOUND_ITEMGET("ItemSound.quest_itemget"),
	SOUND_MIDDLE("ItemSound.quest_middle"),
	SOUND_FINISH("ItemSound.quest_finish"),
	SOUND_GIVEUP("ItemSound.quest_giveup"),
	SOUND_JACKPOT("ItemSound.quest_jackpot"),
	SOUND_FANFARE("ItemSound.quest_fanfare_2"),
	SOUND_BEFORE_BATTLE("Itemsound.quest_before_battle"),
	SOUND_TUTORIAL("ItemSound.quest_tutorial"),
	SOUND_RACE_START("ItemSound2.race_start"),
	ITEMSOUND_ARMOR_WOOD("ItemSound.armor_wood_3"),
	AMBSOUND_DRONE("AmbSound.ed_drone_02"),
	ETCSOUND_ELROKI_SONG_FULL("EtcSound.elcroki_song_full"),
	AMDSOUND_WIND_LOOT("AmdSound.d_wind_loot_02"),
	INTERFACESOUND_CHARSTAT_OPEN("InterfaceSound.charstat_open_01"),
	ITEMSOUND_SHIP_ARRIVAL_DEPARTURE("itemsound.ship_arrival_departure"),
	ITEMSOUND_SHIP_5MIN("itemsound.ship_5min"),
	ITEMSOUND_SHIP_1MIN("itemsound.ship_1min"),
	// System Mgs
	SYSTEM_MSG_017("systemmsg_e.17"),
	SYSTEM_MSG_018("systemmsg_e.18"),
	SYSTEM_MSG_345("systemmsg_e.345"),
	SYSTEM_MSG_346("systemmsg_e.346"),
	SYSTEM_MSG_702("systemmsg_e.702"),
	SYSTEM_MSG_809("systemmsg_e.809"),
	SYSTEM_MSG_1209("systemmsg_e.1209"),
	SYSTEM_MSG_1233("systemmsg_e.1233");
	
	private final PlaySound _playSound;
	
	private Sound(String soundName)
	{
		_playSound = PlaySound.createSound(soundName);
	}
	
	public PlaySound withObject(WorldObject obj)
	{
		return PlaySound.createSound(getSoundName(), obj);
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