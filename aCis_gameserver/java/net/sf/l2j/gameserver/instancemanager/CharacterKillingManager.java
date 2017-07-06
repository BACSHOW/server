package net.sf.l2j.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.commons.concurrent.ThreadPool;
import net.sf.l2j.gameserver.model.CharSelectInfoPackage;
import net.sf.l2j.gameserver.model.actor.PcPolymorph;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public final class CharacterKillingManager
{
       private static final Logger _log = Logger.getLogger(CharacterKillingManager.class.getName());
      
       private int _cycle = 0;
       private long _cycleStart = 0L;
       private int _winnerPvPKills;
       private int _winnerPvPKillsCount;
       private int _winnerPKKills;
       private int _winnerPKKillsCount;
      
       private volatile CharSelectInfoPackage _winnerPvPKillsInfo;
       private volatile CharSelectInfoPackage _winnerPKKillsInfo;
      
       private ScheduledFuture<?> _scheduledKillingCycleTask;
      
       private List<PcPolymorph> pvpMorphListeners = new CopyOnWriteArrayList<>();
       private List<PcPolymorph> pkMorphListeners = new CopyOnWriteArrayList<>();
      
       protected CharacterKillingManager()
       {
       }
      
       public synchronized void init()
       {
               try (Connection con = L2DatabaseFactory.getInstance().getConnection();
                       PreparedStatement st = con.prepareStatement("SELECT cycle, cycle_start, winner_pvpkills, winner_pvpkills_count, winner_pkkills, winner_pkkills_count FROM character_kills_info ORDER BY cycle_start DESC LIMIT 1");
                       ResultSet rs = st.executeQuery())
               {
                       if (rs.next())
                       {
                               _cycle = rs.getInt("cycle");
                               _cycleStart = rs.getLong("cycle_start");
                               _winnerPvPKills = rs.getInt("winner_pvpkills");
                               _winnerPvPKillsCount = rs.getInt("winner_pvpkills_count");
                               _winnerPKKills = rs.getInt("winner_pkkills");
                               _winnerPKKillsCount = rs.getInt("winner_pkkills_count");
                       }
               }
               catch (Exception e)
               {
                       _log.log(Level.WARNING, "Could not load characters killing cycle: " + e.getMessage(), e);
               }
              
               broadcastMorphUpdate();
              
               if (_scheduledKillingCycleTask != null)
               {
            	   _scheduledKillingCycleTask.cancel(true);
               }
               long millisToNextCycle = (_cycleStart + Config.CKM_CYCLE_LENGTH) - System.currentTimeMillis();
               _scheduledKillingCycleTask = ThreadPool.schedule(new CharacterKillingCycleTask(), millisToNextCycle);
            	   
               _log.info(getClass().getSimpleName() + ": Started! Cycle: " + _cycle + " - Next cycle in: " + _scheduledKillingCycleTask.getDelay(TimeUnit.SECONDS) + "s");
       }
      
       public synchronized void newKillingCycle()
       {
               _cycleStart = System.currentTimeMillis();
               computateCyclePvPWinner();
               computateCyclePKWinner();
               refreshKillingSnapshot();
              
               try (Connection con = L2DatabaseFactory.getInstance().getConnection();
                       PreparedStatement st = con.prepareStatement("INSERT INTO character_kills_info (cycle_start, winner_pvpkills, winner_pvpkills_count, winner_pkkills, winner_pkkills_count) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS))
               {
                       st.setLong(1, _cycleStart);
                       st.setInt(2, _winnerPvPKills);
                       st.setInt(3, _winnerPvPKillsCount);
                       st.setInt(4, _winnerPKKills);
                       st.setInt(5, _winnerPKKillsCount);
                       st.execute();
                      
                       try (ResultSet rs = st.getGeneratedKeys())
                       {
                               if (rs.next())
                               {
                                       _cycle = rs.getInt(1);
                               }
                       }
               }
               catch (Exception e)
               {
            	   _log.log(Level.WARNING, "Could not create characters killing cycle: " + e.getMessage(), e);
               }
               
               broadcastMorphUpdate();
               
               if (_scheduledKillingCycleTask != null)
               {
            	   _scheduledKillingCycleTask.cancel(true);
               }
               _scheduledKillingCycleTask = ThreadPool.schedule(new CharacterKillingCycleTask(), Config.CKM_CYCLE_LENGTH);
       }
      
       private void computateCyclePvPWinner()
       {
               _winnerPvPKills = 0;
               _winnerPvPKillsCount = 0;
               _winnerPvPKillsInfo = null;
              
               try (Connection con = L2DatabaseFactory.getInstance().getConnection();
                       PreparedStatement st = con.prepareStatement("SELECT c.obj_Id, (c.pvpkills - COALESCE(ck.pvpkills, 0)) pvpkills FROM characters c LEFT JOIN character_kills_snapshot ck ON ck.charId = c.obj_Id WHERE accesslevel = 0 ORDER BY pvpkills DESC LIMIT 1");
                       ResultSet rs = st.executeQuery();)
               {
                       if (rs.next())
                       {
                               int kills = rs.getInt(2);
                               if (kills > 0)
                               {
                                       _winnerPvPKills = rs.getInt(1);
                                       _winnerPvPKillsCount = kills;
                               }
                       }
               }
               catch (Exception e)
               {
                       _log.log(Level.WARNING, "Could not computate characters killing cycle winners: " + e.getMessage(), e);
               }
       }
      
       private void computateCyclePKWinner()
       {
               _winnerPKKills = 0;
               _winnerPKKillsCount = 0;
               _winnerPKKillsInfo = null;
              
               try (Connection con = L2DatabaseFactory.getInstance().getConnection();
                       PreparedStatement st = con.prepareStatement("SELECT c.obj_Id, (c.pkkills - COALESCE(ck.pkkills, 0)) pkkills FROM characters c LEFT JOIN character_kills_snapshot ck ON ck.charId = c.obj_Id WHERE accesslevel = 0 ORDER BY pkkills DESC LIMIT 1");
                       ResultSet rs = st.executeQuery();)
               {
                       if (rs.next())
                       {
                               int kills = rs.getInt(2);
                               if (kills > 0)
                               {
                                       _winnerPKKills = rs.getInt(1);
                                       _winnerPKKillsCount = kills;
                               }
                       }
               }
               catch (Exception e)
               {
                       _log.log(Level.WARNING, "Could not computate characters killing cycle winners: " + e.getMessage(), e);
               }
       }
      
       private static void refreshKillingSnapshot()
       {
               try (Connection con = L2DatabaseFactory.getInstance().getConnection();
                       PreparedStatement stTruncate = con.prepareStatement("TRUNCATE TABLE character_kills_snapshot");
                       PreparedStatement stRefresh = con.prepareStatement("INSERT INTO character_kills_snapshot (charId, pvpkills, pkkills) SELECT obj_Id, pvpkills, pkkills FROM characters WHERE (pvpkills > 0 OR pkkills > 0) AND accesslevel = 0"))
               {
                       stTruncate.executeUpdate();
                       stRefresh.executeUpdate();
               }
               catch (Exception e)
               {
                       _log.log(Level.WARNING, "Could not refresh characters killing snapshot: " + e.getMessage(), e);
               }
       }
      
       public void broadcastMorphUpdate()
       {
               final CharSelectInfoPackage winnerPvPKillsInfo = getWinnerPvPKillsInfo();
               for (PcPolymorph npc : pvpMorphListeners)
               {
                       broadcastPvPMorphUpdate(npc, winnerPvPKillsInfo);
               }
              
               final CharSelectInfoPackage winnerPKKillsInfo = getWinnerPKKillsInfo();
               for (PcPolymorph npc : pkMorphListeners)
               {
                       broadcastPKMorphUpdate(npc, winnerPKKillsInfo);
               }
       }
      
       private void broadcastPvPMorphUpdate(PcPolymorph npc, CharSelectInfoPackage winnerPvPKillsInfo)
       {
               if (winnerPvPKillsInfo == null)
               {
                       npc.setPolymorphInfo(null);
                       return;
               }
               npc.setVisibleTitle(Config.CKM_PVP_NPC_TITLE.replaceAll("%kills%", String.valueOf(_winnerPvPKillsCount)));
               npc.setTitleColor(Config.CKM_PVP_NPC_TITLE_COLOR);
               npc.setNameColor(Config.CKM_PVP_NPC_NAME_COLOR);
               npc.setPolymorphInfo(winnerPvPKillsInfo);
               npc.broadcastPacket(new SocialAction(npc, 16));
       }
      
       private void broadcastPKMorphUpdate(PcPolymorph npc, CharSelectInfoPackage winnerPKKillsInfo)
       {
               if (winnerPKKillsInfo == null)
               {
                       npc.setPolymorphInfo(null);
                       return;
               }
               npc.setVisibleTitle(Config.CKM_PK_NPC_TITLE.replaceAll("%kills%", String.valueOf(_winnerPKKillsCount)));
               npc.setTitleColor(Config.CKM_PK_NPC_TITLE_COLOR);
               npc.setNameColor(Config.CKM_PK_NPC_NAME_COLOR);
               npc.setPolymorphInfo(winnerPKKillsInfo);
               npc.broadcastPacket(new SocialAction(npc, 16));
       }
      
       public boolean addPvPMorphListener(PcPolymorph npc)
       {
               if (npc == null)
               {
                       return false;
               }
               broadcastPvPMorphUpdate(npc, getWinnerPvPKillsInfo());
               return pvpMorphListeners.add(npc);
       }
      
       public boolean removePvPMorphListener(PcPolymorph npc)
       {
               return pvpMorphListeners.remove(npc);
       }
      
       public boolean addPKMorphListener(PcPolymorph npc)
       {
               if (npc == null)
               {
                       return false;
               }
               broadcastPKMorphUpdate(npc, getWinnerPKKillsInfo());
               return pkMorphListeners.add(npc);
       }
      
       public boolean removePKMorphListener(PcPolymorph npc)
       {
               return pkMorphListeners.remove(npc);
       }
      
       private CharSelectInfoPackage getWinnerPvPKillsInfo()
       {
               if (_winnerPvPKills != 0 && _winnerPvPKillsInfo == null)
               {
                       synchronized (this)
                       {
                               if (_winnerPvPKillsInfo == null)
                               {
                                       _winnerPvPKillsInfo = PcPolymorph.loadCharInfo(_winnerPvPKills);
                               }
                       }
               }
               return _winnerPvPKillsInfo;
       }
      
       private CharSelectInfoPackage getWinnerPKKillsInfo()
       {
               if (_winnerPKKills != 0 && _winnerPKKillsInfo == null)
               {
                       synchronized (this)
                       {
                               if (_winnerPKKillsInfo == null)
                               {
                                       _winnerPKKillsInfo = PcPolymorph.loadCharInfo(_winnerPKKills);
                               }
                       }
               }
               return _winnerPKKillsInfo;
       }
     
       private class CharacterKillingCycleTask implements Runnable
       {
               /**
		 * 
		 */
		public CharacterKillingCycleTask()
		{
			// TODO Auto-generated constructor stub
		}

			@Override
               public void run()
               {
                       CharacterKillingManager.getInstance().newKillingCycle();
               }
       }
      
       public static CharacterKillingManager getInstance()
       {
               return SingletonHolder._instance;
       }
      
       private static class SingletonHolder
       {
               protected static final CharacterKillingManager _instance = new CharacterKillingManager();
       }
}