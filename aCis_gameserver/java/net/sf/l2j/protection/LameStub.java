package net.sf.l2j.protection;

import net.sf.l2j.gameserver.network.BlowFishKeygen;
import net.sf.l2j.gameserver.network.GameCrypt;

public class LameStub
{
	public static boolean ISLAME = false;
	
	public static void main(String[] args)
	{
		BlowFishKeygen._ISLAME = true;
		GameCrypt._ISLAME = true;
		ISLAME = true;
	}
}