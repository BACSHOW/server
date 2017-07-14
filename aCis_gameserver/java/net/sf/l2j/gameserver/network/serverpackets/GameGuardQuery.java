package net.sf.l2j.gameserver.network.serverpackets;

/**
 * @author zabbix Lets drink to code!
 */
public class GameGuardQuery extends L2GameServerPacket
{
	public static final GameGuardQuery STATIC_PACKET = new GameGuardQuery();
	
	@Override
	public void runImpl()
	{
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xf9);
	}
}