/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.events;

import net.sf.l2j.Config;
import net.sf.l2j.commons.concurrent.ThreadPool;
import net.sf.l2j.gameserver.util.Broadcast;

public class TvTEventManager implements Runnable
{
	/**
	 * The one and only instance of this class<br>
	 */
	private static TvTEventManager _instance = null;
	
	/**
	 * New instance only by getInstance()<br>
	 */
	private TvTEventManager()
	{
		if (Config.TVT_EVENT_ENABLED)
		{
			ThreadPool.schedule(this, 0);
			System.out.println("TvTEventEngine[TvTManager.TvTManager()]: Started.");
		}
		else
			System.out.println("TvTEventEngine[TvTManager.TvTManager()]: Engine is disabled.");
	}
	
	/**
	 * Initialize new/Returns the one and only instance<br>
	 * <br>
	 * @return TvTManager<br>
	 */
	public static TvTEventManager getInstance()
	{
		if (_instance == null)
			_instance = new TvTEventManager();
		
		return _instance;
	}
	
	/**
	 * The task method to handle cycles of the event
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		TvTEvent.init();
		
		for (;;)
		{
			waiter(Config.TVT_EVENT_INTERVAL * 60); // in config given as minutes
			
			if (!TvTEvent.startParticipation())
			{
				Broadcast.announceToOnlinePlayers("TvT Event: Event was canceled.", true);
				System.out.println("TvTEvent: Error spawning event npc for participation.");
				continue;
			}
			Broadcast.announceToOnlinePlayers("TvT Event: Registration opened for " + Config.TVT_EVENT_PARTICIPATION_TIME + " minute(s). Type .tvtjoin or .tvtleave", true);
			
			waiter(Config.TVT_EVENT_PARTICIPATION_TIME * 60); // in config given as minutes
			
			if (!TvTEvent.startFight())
			{
				Broadcast.announceToOnlinePlayers("TvT Event: Event canceled.", true);
				System.out.println("TvTEvent: Event canceled.");
				continue;
			}
			Broadcast.announceToOnlinePlayers("TvT Event: Registration closed!", true);
			TvTEvent.sysMsgToAllParticipants("TvT Event: Teleporting participants to an arena in " + Config.TVT_EVENT_START_LEAVE_TELEPORT_DELAY + " second(s).");
			
			waiter(Config.TVT_EVENT_RUNNING_TIME * 60); // in config given as minutes
			Broadcast.announceToOnlinePlayers(TvTEvent.calculateRewards(), true);
			TvTEvent.sysMsgToAllParticipants("TvT Event: Teleporting back to the registration npc in " + Config.TVT_EVENT_START_LEAVE_TELEPORT_DELAY + " second(s).");
			TvTEvent.stopFight();
		}
	}
	
	/**
	 * This method waits for a period time delay
	 * @param seconds
	 */
	void waiter(int seconds)
	{
		while (seconds > 1)
		{
			seconds--; // here because we don't want to see two time announce at the same time
			
			if (TvTEvent.isParticipating() || TvTEvent.isStarted())
			{
				switch (seconds)
				{
					case 3600: // 1 hour left
						if (TvTEvent.isParticipating())
							Broadcast.announceToOnlinePlayers("TvT Event: " + seconds / 60 / 60 + " hour(s) umtil registration is closed!", true);
						else if (TvTEvent.isStarted())
							TvTEvent.sysMsgToAllParticipants("TvT Event: " + seconds / 60 / 60 + " hour(s) until event is finished!");
						
						break;
					case 1800: // 30 minutes left
					case 900: // 15 minutes left
					case 600: // 10 minutes left
					case 300: // 5 minutes left
					case 240: // 4 minutes left
					case 180: // 3 minutes left
					case 120: // 2 minutes left
					case 60: // 1 minute left
						if (TvTEvent.isParticipating())
							Broadcast.announceToOnlinePlayers("TvT Event: " + seconds / 60 + " minute(s) until registration is closed!", true);
						else if (TvTEvent.isStarted())
							TvTEvent.sysMsgToAllParticipants("TvT Event: " + seconds / 60 + " minute(s) until the event is finished!");
						
						break;
					case 30: // 30 seconds left
						/**
						 * case 15: // 15 seconds left case 10: // 10 seconds left
						 */
					case 5: // 5 seconds left
						
						/**
						 * case 4: // 4 seconds left case 3: // 3 seconds left case 2: // 2 seconds left case 1: // 1 seconds left
						 */
						if (TvTEvent.isParticipating())
							Broadcast.announceToOnlinePlayers("TvT Event: " + seconds + " second(s) until registration is closed!", true);
						else if (TvTEvent.isStarted())
							TvTEvent.sysMsgToAllParticipants("TvT Event: " + seconds + " second(s) until the event is finished!");
						
						break;
				}
			}
			TvTEvent.waiter(1);
		}
	}
}