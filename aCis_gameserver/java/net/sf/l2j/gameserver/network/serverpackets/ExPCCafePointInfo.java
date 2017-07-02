package net.sf.l2j.gameserver.network.serverpackets;

import net.sf.l2j.gameserver.model.actor.instance.Player;

public class ExPCCafePointInfo extends L2GameServerPacket
{
	private static final String _S__FE_31_EXPCCAFEPOINTINFO = "[S] FE:31 ExPCCafePointInfo";
	private Player _character;
	private int m_AddPoint;
	private int m_PeriodType;
	private int RemainTime;
	private int PointType;
	
	public ExPCCafePointInfo(Player user, int modify, boolean add, int hour, boolean _double)
	{
		_character = user;
		m_AddPoint = modify;
		
		if (add)
		{
			m_PeriodType = 1;
			PointType = 1;
		}
		else
		{
			if (add && _double)
			{
				m_PeriodType = 1;
				PointType = 0;
			}
			else
			{
				m_PeriodType = 2;
				PointType = 2;
			}
		}
		
		RemainTime = hour;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x31);
		writeD(_character.getPcBangScore());
		writeD(m_AddPoint);
		writeC(m_PeriodType);
		writeD(RemainTime);
		writeC(PointType);
	}
	
	@Override
	public String getType()
	{
		return _S__FE_31_EXPCCAFEPOINTINFO;
	}
}