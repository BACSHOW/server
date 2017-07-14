package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.protection.CatsGuard;

/**
 * @author zabbix
 */
public class GameGuardReply extends L2GameClientPacket
{
	private final int[] _reply;
	
	public GameGuardReply()
	{
		_reply = new int[4];
	}
	
	@Override
	protected void readImpl()
	{
		if (CatsGuard.getInstance().isEnabled() && (getClient().getHWid() == null))
		{
			_reply[0] = readD();
			_reply[1] = readD();
			_reply[2] = readD();
			_reply[3] = readD();
		}
		else
		{
			byte[] b = new byte[_buf.remaining()];
			readB(b);
		}
		
	}
	
	@Override
	protected void runImpl()
	{
		if (CatsGuard.getInstance().isEnabled())
		{
			CatsGuard.getInstance().initSession(getClient(), _reply);
		}
	}
}