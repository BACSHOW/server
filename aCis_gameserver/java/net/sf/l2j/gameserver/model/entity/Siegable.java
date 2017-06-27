package net.sf.l2j.gameserver.model.entity;

import java.util.Calendar;
import java.util.List;

import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.entity.Siege.SiegeSide;

public interface Siegable
{
	public void startSiege();
	
	public void endSiege();
	
	public List<L2Clan> getAttackerClans();
	
	public List<L2Clan> getDefenderClans();
	
	public boolean checkSide(L2Clan clan, SiegeSide type);
	
	public boolean checkSides(L2Clan clan, SiegeSide... types);
	
	public boolean checkSides(L2Clan clan);
	
	public Npc getFlag(L2Clan clan);
	
	public Calendar getSiegeDate();
}