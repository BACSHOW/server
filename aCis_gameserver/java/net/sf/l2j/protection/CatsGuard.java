package net.sf.l2j.protection;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.LoginServerThread;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.network.L2GameClient;
import net.sf.l2j.gameserver.network.L2GameClient.IExReader;
import net.sf.l2j.gameserver.network.serverpackets.GameGuardQuery;
import net.sf.l2j.gameserver.network.serverpackets.LeaveWorld;
import net.sf.l2j.gameserver.util.Broadcast;

public class CatsGuard
{
	protected static final Logger _log = Logger.getLogger(CatsGuard.class.getName());
	
	private class CatsGuardReader implements IExReader
	{
		private RC4 _crypt;
		private final L2GameClient _client;
		private int _prevcode = 0;
		private final byte[] buffer = new byte[4];
		private int _state;
		protected boolean _checkChar;
		
		protected CatsGuardReader(L2GameClient cl)
		{
			_state = 0;
			_client = cl;
		}
		
		public void setKey(int data[])
		{
			String key = "";
			for (int i = 0; i < 10; i++)
			{
				key += String.format("%X%X", data[1], Config.SERVER_KEY);
			}
			_crypt = new RC4(key, false);
			_state = 1;
		}
		
		@Override
		public int read(ByteBuffer buf)
		{
			int opcode = 0;
			if (_state == 0)
			{
				opcode = buf.get() & 0xff;
				if (opcode != 0xca)
				{
					illegalAction(_client, "Invalid opcode on pre-auth state");
					return 0;
				}
			}
			else
			{
				if (buf.remaining() < 4)
				{
					illegalAction(_client, "Invalid block size on authed state");
				}
				else
				{
					buf.get(buffer);
					opcode = decryptPacket(buffer) & 0xff;
				}
			}
			return opcode;
		}
		
		private int decryptPacket(byte[] packet)
		{
			packet = _crypt.rc4(packet);
			int crc = CRC16.calc(new byte[]
			{
				(byte) (_prevcode & 0xff),
				packet[1]
			});
			int read_crc = (((packet[3] & 0xff) << 8) & 0xff00) | (packet[2] & 0xff);
			if (crc != read_crc)
			{
				illegalAction(_client, "CRC error");
				return 0;
			}
			_prevcode = packet[1] & 0xff;
			return _prevcode;
		}
		
		@Override
		public void checkChar(Player cha)
		{
			if (!_checkChar || (cha == null))
			{
				return;
			}
			if (Config.ALLOW_GM_FROM_BANNED_HWID && cha.isGM())
			{
				return;
			}
			if (Config.LOG_OPTION.contains("BANNED"))
			{
				_log.info("CatsGuard: Client " + cha.getClient() + " try to log with banned hwid.");
			}
			cha.closeNetConnection(true);
		}
	}
	
	private Map<String, Integer> _connections;
	private final List<String> _premium = new ArrayList<>();
	private List<String> _bannedhwid;
	
	private CatsGuard()
	{
		if (Config.SERVER_KEY == 0)
			return;
		
		_connections = new HashMap<>();
		_bannedhwid = new ArrayList<>();
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement stm = con.prepareStatement("select * from banned_hwid"); ResultSet rs = stm.executeQuery())
		{
			while (rs.next())
			{
				_bannedhwid.add(rs.getString(1));
			}
		}
		catch (Exception e)
		{
			if (e.getClass().getSimpleName().equals("MySQLSyntaxErrorException"))
			{
				try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement stmt = con.prepareStatement("create table `banned_hwid` (`hwid` varchar(64) not null primary key)");)
				{
					stmt.execute();
				}
				catch (Exception ex)
				{
					_log.log(Level.WARNING, "", ex);
				}
			}
		}
		_log.info("CatsGuard: Loaded " + _bannedhwid.size() + " banned hwid(s)");
		_log.info("CatsGuard: Ready");
	}
	
	public boolean isEnabled()
	{
		return Config.ENABLED;
	}
	
	public void ban(Player player)
	{
		ban(player.getHWid());
	}
	
	public void ban(String hwid)
	{
		if (!Config.ENABLED)
		{
			return;
		}
		synchronized (_bannedhwid)
		{
			if (_bannedhwid.contains(hwid))
			{
				return;
			}
			_bannedhwid.add(hwid);
			try
			{
				Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement stm = con.prepareStatement("insert into banned_hwid values(?)");
				stm.setString(1, hwid);
				stm.execute();
				stm.close();
				con.close();
			}
			catch (SQLException e)
			{
				_log.warning("CatsGuard: Unable to store banned hwid");
			}
		}
	}
	
	public void unban(String hwid)
	{
		if (!Config.ENABLED)
		{
			return;
		}
		
		synchronized (_bannedhwid)
		{
			if (_bannedhwid.contains(hwid))
			{
				return;
			}
			
			_bannedhwid.add(hwid);
			try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement stm = con.prepareStatement("delete from banned_hwid where hwid=?"))
			{
				stm.setString(1, hwid);
				stm.execute();
			}
			catch (SQLException e)
			{
				_log.warning("CatsGuard: Unable to clear banned hwid");
			}
		}
	}
	
	protected void illegalAction(L2GameClient cl, String reason)
	{
		if ((cl.getActiveChar() != null) && Config.ANNOUNCE_HACK)
		{
			Broadcast.announceToOnlinePlayers("Player " + cl.getActiveChar().getName() + " used unlegal soft!");
		}
		if (Config.ON_HACK_ATTEMP.equals("hwidban") && (cl.getHWid() != null))
		{
			ban(cl.getHWid());
		}
		else if (Config.ON_HACK_ATTEMP.equals("jail") && (cl.getActiveChar() != null))
		{
			cl.getActiveChar().isInJail();
		}
		else if (Config.ON_HACK_ATTEMP.equals("ban") && (cl.getActiveChar() != null))
		{
			LoginServerThread.getInstance().sendAccessLevel(cl.getAccountName(), -100);
		}
		_log.info("CatsGuard: Client " + cl + " use illegal software and will " + Config.ON_HACK_ATTEMP + "ed. Reason: " + reason);
		cl.close(LeaveWorld.STATIC_PACKET);
	}
	
	public void initSession(L2GameClient cl)
	{
		if (!Config.ENABLED)
		{
			return;
		}
		cl.sendPacket(GameGuardQuery.STATIC_PACKET);
		cl._reader = new CatsGuardReader(cl);
	}
	
	public void doneSession(L2GameClient cl)
	{
		if (!Config.ENABLED)
		{
			return;
		}
		if (cl.getHWid() != null)
		{
			_premium.remove(cl.getHWid());
			if (_connections.containsKey(cl.getHWid()))
			{
				int nwnd = _connections.get(cl.getHWid());
				if (nwnd == 0)
				{
					_connections.remove(cl.getHWid());
				}
				else
				{
					_connections.put(cl.getHWid(), --nwnd);
				}
			}
		}
		cl._reader = null;
	}
	
	public void initSession(L2GameClient cl, int[] data)
	{
		if (!Config.ENABLED)
		{
			return;
		}
		if (data[0] != Config.SERVER_KEY)
		{
			if (Config.LOG_OPTION.contains("NOPROTECT"))
			{
				_log.info("CatsGuard: Client " + cl + " try to log with no CatsGuard");
			}
			cl.close(LeaveWorld.STATIC_PACKET);
			return;
		}
		String hwid = String.format("%x", data[3]);
		if (cl._reader == null)
		{
			if (Config.LOG_OPTION.contains("HACK"))
			{
				_log.info("CatsGuard: Client " + cl + " has no pre-authed state");
			}
			cl.close(LeaveWorld.STATIC_PACKET);
			return;
		}
		if (_bannedhwid.contains(hwid))
		{
			((CatsGuardReader) cl._reader)._checkChar = true;
		}
		if (!_connections.containsKey(hwid))
		{
			_connections.put(hwid, 0);
		}
		int nwindow = _connections.get(hwid);
		int max = Config.MAX_SESSIONS;
		if (_premium.contains(hwid))
		{
			max = Config.MAX_PREMIUM_SESSIONS;
		}
		if ((max > 0) && (++nwindow > max))
		{
			if (Config.LOG_OPTION.contains("SESSIONS"))
			{
				_log.info("CatsGuard: To many sessions from hwid " + hwid);
			}
			cl.close(LeaveWorld.STATIC_PACKET);
			return;
		}
		if (!_premium.contains(hwid))
		{
			_premium.add(hwid);
		}
		_connections.put(hwid, nwindow);
		cl.setHWID(hwid);
		((CatsGuardReader) cl._reader).setKey(data);
		if (Config.LOG_SESSIONS)
		{
			_log.info("Client " + cl.getAccountName() + " connected with hwid " + cl.getHWid());
		}
	}
	
	private static CatsGuard _instance;
	
	public static CatsGuard getInstance()
	{
		if (_instance == null)
		{
			_instance = new CatsGuard();
		}
		return _instance;
	}
}