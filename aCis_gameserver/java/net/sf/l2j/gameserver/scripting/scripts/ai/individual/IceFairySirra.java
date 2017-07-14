package net.sf.l2j.gameserver.scripting.scripts.ai.individual;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.datatables.DoorTable;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.SpawnTable;
import net.sf.l2j.gameserver.instancemanager.ZoneManager;
import net.sf.l2j.gameserver.model.L2Spawn;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Door;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.zone.type.L2BossZone;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.scripting.EventType;
import net.sf.l2j.gameserver.scripting.scripts.ai.L2AttackableAIScript;

public class IceFairySirra extends L2AttackableAIScript
{
	private static final Logger _log = Logger.getLogger(IceFairySirra.class.getName());
	private static final int STEWARD = 32029;
	private static final int SILVER_HEMOCYTE = 8057;
	
	private static final L2BossZone ICEFAIRY_ZONE = ZoneManager.getInstance().getZoneById(110016, L2BossZone.class);
	
	private static Player _player = null;
	protected List<Npc> _allMobs = new ArrayList<>();
	protected Future<?> _onDeadEventTask = null;
	
	public IceFairySirra()
	{
		super("ai/individual");
		init();
	}
	
	@Override
	protected void registerNpcs()
	{
		addEventIds(STEWARD, EventType.QUEST_START, EventType.ON_TALK, EventType.ON_FIRST_TALK);
		addKillId(22100, 22102, 22104);
	}
	
	public void init()
	{
		Npc steward = findSpawn(STEWARD);
		if (steward != null)
			steward.setBusy(false);
		openGates();
	}
	
	public void cleanUp()
	{
		init();
		cancelQuestTimer("30MinutesRemaining", null, _player);
		cancelQuestTimer("20MinutesRemaining", null, _player);
		cancelQuestTimer("10MinutesRemaining", null, _player);
		cancelQuestTimer("End", null, _player);
		for (Npc mob : _allMobs)
		{
			try
			{
				mob.deleteMe();
			}
			catch (Exception e)
			{
				_log.severe(IceFairySirra.class.getName() + ": Failed deleting mob.");
				if (Config.DEVELOPER)
					e.printStackTrace();
			}
		}
		_allMobs.clear();
	}
	
	private static Npc findSpawn(int npcId)
	{
		for (L2Spawn spawn : SpawnTable.getInstance().getSpawnTable())
		{
			if (spawn.getNpcId() == npcId)
				return spawn.getNpc();
		}
		return null;
	}
	
	protected void openGates()
	{
		for (int i = 23140001; i < 23140003; i++)
		{
			try
			{
				Door door = DoorTable.getInstance().getDoor(i);
				if (door != null)
				{
					door.openMe();
				}
				else
				{
					_log.warning(IceFairySirra.class.getName() + ": Attempted to open undefined door. doorId: " + i);
				}
			}
			catch (Exception e)
			{
				_log.warning(IceFairySirra.class.getName() + ": Failed closing door");
			}
		}
	}
	
	protected void closeGates()
	{
		for (int i = 23140001; i < 23140003; i++)
		{
			try
			{
				Door door = DoorTable.getInstance().getDoor(i);
				if (door != null)
				{
					door.closeMe();
				}
				else
				{
					_log.warning(IceFairySirra.class.getName() + ": Attempted to close undefined door. doorId: " + i);
				}
			}
			catch (Exception e)
			{
				_log.warning(IceFairySirra.class.getName() + ": Failed closing door");
			}
		}
	}
	
	public boolean checkItems(Player player)
	{
		if (player.getParty() != null)
		{
			for (Player pc : player.getParty().getMembers())
			{
				ItemInstance i = pc.getInventory().getItemByItemId(SILVER_HEMOCYTE);
				if (i == null || i.getCount() < 10)
				{
					return false;
				}
			}
		}
		else
			return false;
		return true;
	}
	
	public void destroyItems(Player player)
	{
		if (player.getParty() != null)
		{
			for (Player pc : player.getParty().getMembers())
			{
				ItemInstance i = pc.getInventory().getItemByItemId(SILVER_HEMOCYTE);
				pc.destroyItem("Hemocytes", i.getObjectId(), 10, null, false);
			}
		}
		else
			cleanUp();
	}
	
	public void teleportInside(Player player)
	{
		if (player.getParty() != null)
		{
			for (Player pc : player.getParty().getMembers())
			{
				pc.teleToLocation(113533, -126159, -3488, 0);
				
				ICEFAIRY_ZONE.allowPlayerEntry(pc, 2103);
			}
		}
		else
			cleanUp();
	}
	
	public void screenMessage(Player player, String text, int time)
	{
		if (player.getParty() != null)
		{
			for (Player pc : player.getParty().getMembers())
			{
				pc.sendPacket(new ExShowScreenMessage(text, time));
			}
		}
		else
			cleanUp();
	}
	
	public void doSpawns()
	{
		int[][] mobs =
		{
			{
				29060,
				102722 - 50,
				-127892 - 50,
				-2768 + 30
			},
			{
				29056,
				102722,
				-127892,
				-2768
			},
			{
				22100,
				102722 + 50,
				-127892 + 50,
				-2768 + 30
			},
			{
				22102,
				102722 - 100,
				-127892 - 100,
				-2768 + 30
			},
			{
				22104,
				102722 + 100,
				-127892 + 100,
				-2768 + 30
			}
		};
		
		try
		{
			for (int i = 0; i < 5; i++)
			{
				final NpcTemplate template = NpcTable.getInstance().getTemplate(mobs[i][0]);
				final L2Spawn spawnDat = new L2Spawn(template);
				if (template != null)
				{
					spawnDat.setLoc(mobs[i][1], mobs[i][2], mobs[i][3], 0);
					spawnDat.setRespawnDelay(60);
					SpawnTable.getInstance().addNewSpawn(spawnDat, false);
					final Npc npc = spawnDat.doSpawn(true);
					_allMobs.add(spawnDat.doSpawn(false));
					npc.scheduleDespawn(mobs[i][0]);
				}
				else
				{
					_log.warning(IceFairySirra.class.getName() + ": Data missing in NPC table for ID: " + mobs[i][0]);
				}
			}
		}
		catch (Exception e)
		{
			_log.warning(IceFairySirra.class.getName() + ": Spawns could not be initialized: ");
			if (Config.DEVELOPER)
				e.printStackTrace();
		}
	}
	
	public String getHtmlPath(int val)
	{
		String pom = "";
		pom = "32029-" + val;
		if (val == 0)
			pom = "32029";
		String temp = "data/html/default/" + pom + ".htm";
		if (HtmCache.getInstance().isLoadable(temp))
			return temp;
		// If the file is not found, the standard message
		// "I have nothing to say to you" is returned
		return "data/html/npcdefault.htm";
	}
	
	public void sendHtml(Npc npc, Player player, String filename)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", String.valueOf(npc.getObjectId()));
		player.sendPacket(html);
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		if (player.getQuestState("IceFairySirra") == null)
			newQuestState(player);
		player.setLastQuestNpcObject(npc.getObjectId());
		String filename = "";
		if (npc.isBusy())
			filename = getHtmlPath(10);
		else
			filename = getHtmlPath(0);
		sendHtml(npc, player, filename);
		return null;
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		if (event.equalsIgnoreCase("check_condition"))
		{
			if (npc.isBusy())// should never happen
				return super.onAdvEvent(event, npc, player);
			String filename = "";
			if (player.isInParty() && player.getParty().getLeaderObjectId() == player.getObjectId())
			{
				if (checkItems(player) == true)
				{
					startQuestTimer("start", 100000, null, player, false);
					_player = player;
					destroyItems(player);
					player.getInventory().addItem("Scroll", 8379, 3, player, null);
					npc.setBusy(true);
					screenMessage(player, "Steward: Please wait a moment.", 100000);
					filename = getHtmlPath(3);
				}
				else
				{
					filename = getHtmlPath(2);
				}
			}
			else
			{
				filename = getHtmlPath(1);
			}
			sendHtml(npc, player, filename);
		}
		else if (event.equalsIgnoreCase("start"))
		{
			if (ICEFAIRY_ZONE == null)
			{
				_log.warning(IceFairySirra.class.getName() + ": Failed to load zone");
				cleanUp();
				return super.onAdvEvent(event, npc, player);
			}
			closeGates();
			doSpawns();
			startQuestTimer("Party_Port", 2000, null, player, false);
			startQuestTimer("End", 1802000, null, player, false);
		}
		else if (event.equalsIgnoreCase("Party_Port"))
		{
			teleportInside(player);
			screenMessage(player, "Steward: Please restore the Queen's appearance!", 10000);
			startQuestTimer("30MinutesRemaining", 300000, null, player, false);
		}
		else if (event.equalsIgnoreCase("30MinutesRemaining"))
		{
			screenMessage(player, "30 minute(s) are remaining.", 10000);
			startQuestTimer("20minutesremaining", 600000, null, player, false);
		}
		else if (event.equalsIgnoreCase("20MinutesRemaining"))
		{
			screenMessage(player, "20 minute(s) are remaining.", 10000);
			startQuestTimer("10minutesremaining", 600000, null, player, false);
		}
		else if (event.equalsIgnoreCase("10MinutesRemaining"))
		{
			screenMessage(player, "Steward: Waste no time! Please hurry!", 10000);
		}
		else if (event.equalsIgnoreCase("End"))
		{
			screenMessage(player, "Steward: Was it indeed too much to ask.", 10000);
			cleanUp();
		}
		return super.onAdvEvent(event, npc, player);
	}
}