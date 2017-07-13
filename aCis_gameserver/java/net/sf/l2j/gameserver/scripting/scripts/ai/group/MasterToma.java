package net.sf.l2j.gameserver.scripting.scripts.ai.group;

import net.sf.l2j.commons.concurrent.ThreadPool;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.SpawnTable;
import net.sf.l2j.gameserver.model.L2Spawn;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.scripting.scripts.ai.L2AttackableAIScript;

public class MasterToma extends L2AttackableAIScript
{
	
	private static int MASTER_TOMA = 30556;
	private static Location[] spawnLoc =
	{
		new Location(151680, -174891, -1807),
		new Location(154153, -220105, -3402),
		new Location(178834, -184336, -352)
	};
	private static long TELEPORT_PERIOD = 30 * 60 * 1000; // 30 min
	private static Npc masterToma = null;
	protected static boolean isSpawned = false;
	
	public MasterToma()
	{
		super("ai/group");
		ThreadPool.scheduleAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					if (isSpawned)
					{
						onSpawn(false);
					}
					else
					{
						onSpawn(true);
					}
				}
				catch (Exception e)
				{
				}
			}
		}, 1000L, TELEPORT_PERIOD);
	}
	
	protected static void onSpawn(boolean start)
	{
		if ((masterToma != null) && !start)
		{
			masterToma.deleteMe();
		}
		Location loc = spawnLoc[Rnd.get(spawnLoc.length)];
		try
		{
			final NpcTemplate template = NpcTable.getInstance().getTemplate(MASTER_TOMA);
			final L2Spawn spawn = new L2Spawn(template);
			spawn.setLoc(loc.getX(), loc.getY(), loc.getZ() + 20, 0);
			
			SpawnTable.getInstance().addNewSpawn(spawn, false);
			final Npc npc = spawn.doSpawn(true);
			npc.scheduleDespawn(MASTER_TOMA);
			isSpawned = true;
		}
		catch (SecurityException | ClassNotFoundException | NoSuchMethodException e)
		{
			_log.warning("Could not spawn Npc " + MASTER_TOMA);
		}
	}
}