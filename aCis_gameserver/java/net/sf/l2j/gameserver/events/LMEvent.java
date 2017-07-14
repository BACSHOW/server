package net.sf.l2j.gameserver.events;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Logger;

import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.datatables.DoorTable;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.datatables.SpawnTable;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.L2Spawn;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Summon;
import net.sf.l2j.gameserver.model.actor.instance.Door;
import net.sf.l2j.gameserver.model.actor.instance.Pet;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.itemcontainer.PcInventory;
import net.sf.l2j.gameserver.model.olympiad.OlympiadManager;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.clientpackets.Say2;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.StatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

import main.data.PlayerData;

public class LMEvent
{
	enum EventState
	{
		INACTIVE,
		INACTIVATING,
		PARTICIPATING,
		STARTING,
		STARTED,
		REWARDING
	}
	
	protected static final Logger _log = Logger.getLogger(LMEvent.class.getName());
	private static final String htmlPath = "data/html/mods/LMEvent/";
	private static EventState _state = EventState.INACTIVE;
	private static Npc _lastNpcSpawn = null;
	
	private static Map<Integer, LMPlayer> _lmPlayer = new HashMap<>();
	
	private static DecimalFormat f = new DecimalFormat(",##0,000");
	
	public LMEvent()
	{
	}
	
	/**
	 * Sets the LMEvent state<br>
	 * <br>
	 * @param state as EventState<br>
	 */
	private static void setState(EventState state)
	{
		synchronized (_state)
		{
			_state = state;
		}
	}
	
	/**
	 * Is LMEvent inactive?<br>
	 * <br>
	 * @return boolean: true if event is inactive(waiting for next event cycle), otherwise false<br>
	 */
	public static boolean isInactive()
	{
		boolean isInactive;
		
		synchronized (_state)
		{
			isInactive = _state == EventState.INACTIVE;
		}
		
		return isInactive;
	}
	
	/**
	 * Is LMEvent in inactivating?<br>
	 * <br>
	 * @return boolean: true if event is in inactivating progress, otherwise false<br>
	 */
	public static boolean isInactivating()
	{
		boolean isInactivating;
		
		synchronized (_state)
		{
			isInactivating = _state == EventState.INACTIVATING;
		}
		
		return isInactivating;
	}
	
	/**
	 * Is LMEvent in participation?<br>
	 * <br>
	 * @return boolean: true if event is in participation progress, otherwise false<br>
	 */
	public static boolean isParticipating()
	{
		boolean isParticipating;
		
		synchronized (_state)
		{
			isParticipating = _state == EventState.PARTICIPATING;
		}
		
		return isParticipating;
	}
	
	/**
	 * Is LMEvent starting?<br>
	 * <br>
	 * @return boolean: true if event is starting up(setting up fighting spot, teleport players etc.), otherwise false<br>
	 */
	public static boolean isStarting()
	{
		boolean isStarting;
		
		synchronized (_state)
		{
			isStarting = _state == EventState.STARTING;
		}
		
		return isStarting;
	}
	
	/**
	 * Is LMEvent started?<br>
	 * <br>
	 * @return boolean: true if event is started, otherwise false<br>
	 */
	public static boolean isStarted()
	{
		boolean isStarted;
		
		synchronized (_state)
		{
			isStarted = _state == EventState.STARTED;
		}
		
		return isStarted;
	}
	
	/**
	 * Is LMEvent rewadrding?<br>
	 * <br>
	 * @return boolean: true if event is currently rewarding, otherwise false<br>
	 */
	public static boolean isRewarding()
	{
		boolean isRewarding;
		
		synchronized (_state)
		{
			isRewarding = _state == EventState.REWARDING;
		}
		
		return isRewarding;
	}
	
	/**
	 * Close doors specified in configs
	 * @param doors
	 */
	private static void closeDoors(List<Integer> doors)
	{
		for (int doorId : doors)
		{
			Door doorInstance = DoorTable.getInstance().getDoor(doorId);
			
			if (doorInstance != null)
			{
				doorInstance.closeMe();
			}
		}
	}
	
	/**
	 * Open doors specified in configs
	 * @param doors
	 */
	private static void openDoors(List<Integer> doors)
	{
		for (int doorId : doors)
		{
			Door doorInstance = DoorTable.getInstance().getDoor(doorId);
			
			if (doorInstance != null)
			{
				doorInstance.openMe();
			}
		}
	}
	
	/**
	 * Starts the participation of the LMEvent<br>
	 * 1. Get L2NpcTemplate by Config.LM_EVENT_PARTICIPATION_NPC_ID<br>
	 * 2. Try to spawn a new npc of it<br>
	 * <br>
	 * @return boolean: true if success, otherwise false<br>
	 */
	public static boolean startParticipation()
	{
		try
		{
			final NpcTemplate template = NpcTable.getInstance().getTemplate(Config.LM_EVENT_PARTICIPATION_NPC_ID);
			final L2Spawn spawn = new L2Spawn(template);
			spawn.setLoc(Config.LM_EVENT_PARTICIPATION_NPC_COORDINATES[0], Config.LM_EVENT_PARTICIPATION_NPC_COORDINATES[1], Config.LM_EVENT_PARTICIPATION_NPC_COORDINATES[2], 0);
			
			SpawnTable.getInstance().addNewSpawn(spawn, false);
			final Npc npc = spawn.doSpawn(true);
			npc.scheduleDespawn(Config.LM_EVENT_PARTICIPATION_NPC_ID);
			npc.broadcastPacket(new MagicSkillUse(npc, npc, 1034, 1, 1, 1));
		}
		catch (Exception e)
		{
			System.out.println("LMEventEngine[LMEvent.startParticipation()]: exception: " + e);
			return false;
		}
		setState(EventState.PARTICIPATING);
		return true;
	}
	
	/**
	 * Starts the LMEvent fight<br>
	 * 1. Set state EventState.STARTING<br>
	 * 2. Close doors specified in configs<br>
	 * 3. Abort if not enought participants(return false)<br>
	 * 4. Set state EventState.STARTED<br>
	 * 5. Teleport all participants to team spot<br>
	 * <br>
	 * @return boolean: true if success, otherwise false<br>
	 */
	public static boolean startFight()
	{
		// Set state to STARTING
		setState(EventState.STARTING);
		
		// Randomize and balance team distribution
		Map<Integer, Player> allParticipants = new HashMap<>();
		
		Player player;
		Iterator<Player> iter;
		if (needParticipationFee())
		{
			iter = allParticipants.values().iterator();
			while (iter.hasNext())
			{
				player = iter.next();
				if (!hasParticipationFee(player))
					iter.remove();
			}
		}
		
		// Check the number of participants
		if (getPlayerCounts() < Config.LM_EVENT_MIN_PLAYERS)
		{
			// Set state INACTIVE
			setState(EventState.INACTIVE);
			
			// Cleanup of participants
			_lmPlayer.clear();
			
			return false;
		}
		
		// Opens all doors specified in configs for lm
		openDoors(Config.LM_DOORS_IDS_TO_OPEN);
		// Closes all doors specified in configs for lm
		closeDoors(Config.LM_DOORS_IDS_TO_CLOSE);
		// Set state STARTED
		setState(EventState.STARTED);
		
		for (LMPlayer LM : _lmPlayer.values())
		{
			if (LM != null)
			{
				// Teleporter implements Runnable and starts itself
				new LMEventTeleporter(LM.getPlayer(), false, false);
			}
			
		}
		
		return true;
	}
	
	public static TreeSet<LMPlayer> orderPosition(Collection<LMPlayer> listPlayer)
	{
		TreeSet<LMPlayer> players = new TreeSet<>(new Comparator<LMPlayer>()
		{
			@Override
			public int compare(LMPlayer p1, LMPlayer p2)
			{
				Integer c1 = Integer.valueOf(p2.getCredits() - p1.getCredits());
				Integer c2 = Integer.valueOf(p2.getPoints() - p1.getPoints());
				Integer c3 = p1.getHexCode().compareTo(p2.getHexCode());
				
				if (c1 == 0)
				{
					if (c2 == 0)
						return c3;
					return c2;
				}
				return c1;
			}
		});
		
		players.addAll(listPlayer);
		
		return players;
	}
	
	/**
	 * Calculates the LMEvent reward<br>
	 * 1. If both teams are at a tie(points equals), send it as system message to all participants, if one of the teams have 0 participants left online abort rewarding<br>
	 * 2. Wait till teams are not at a tie anymore<br>
	 * 3. Set state EvcentState.REWARDING<br>
	 * 4. Reward team with more points<br>
	 * 5. Show win html to wining team participants<br>
	 * <br>
	 * @return String: winning team name<br>
	 */
	public static String calculateRewards()
	{
		TreeSet<LMPlayer> players = orderPosition(_lmPlayer.values());
		String msg = "";
		if (!Config.LM_REWARD_PLAYERS_TIE && getPlayerCounts() > 1)
			return "LM Event ended, thanks to everyone who participated!\nHe did not have winners!";
		
		for (int i = 0; i < players.size(); i++)
		{
			if (players.isEmpty())
				break;
			
			LMPlayer player = players.first();
			
			if (player.getCredits() == 0 || player.getPoints() == 0)
				break;
			
			rewardPlayer(player);
			players.remove(player);
			msg += " Player: " + player.getPlayer().getName();
			msg += " Killed: " + player.getPoints();
			msg += " Died: " + String.valueOf(Config.LM_EVENT_PLAYER_CREDITS - player.getCredits());
			msg += "\n";
			if (!Config.LM_REWARD_PLAYERS_TIE)
				break;
		}
		
		// Set state REWARDING so nobody can point anymore
		setState(EventState.REWARDING);
		
		return "LM Event ended, thanks to everyone who participated!\nWinner(s):\n" + msg;
	}
	
	private static void rewardPlayer(LMPlayer p)
	{
		Player activeChar = p.getPlayer();
		
		// Check for nullpointer
		if (activeChar == null)
			return;
		
		SystemMessage systemMessage = null;
		String htmltext = "";
		for (int[] reward : Config.LM_EVENT_REWARDS)
		{
			PcInventory inv = activeChar.getInventory();
			
			// Check for stackable item, non stackabe items need to be added one by one
			if (ItemTable.getInstance().createDummyItem(reward[0]).isStackable())
			{
				inv.addItem("LM Event", reward[0], reward[1], activeChar, activeChar);
				
				if (reward[1] > 1)
				{
					systemMessage = SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S);
					systemMessage.addItemName(reward[0]);
					systemMessage.addItemNumber(reward[1]);
				}
				else
				{
					systemMessage = SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1);
					systemMessage.addItemName(reward[0]);
				}
				
				activeChar.sendPacket(systemMessage);
			}
			else
			{
				for (int i = 0; i < reward[1]; ++i)
				{
					inv.addItem("LM Event", reward[0], 1, activeChar, activeChar);
					systemMessage = SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1);
					systemMessage.addItemName(reward[0]);
					activeChar.sendPacket(systemMessage);
				}
			}
			htmltext += " - " + (reward[1] > 999 ? f.format(reward[1]) : reward[1]) + " " + ItemTable.getInstance().getTemplate(reward[0]).getName() + "<br1>";
		}
		
		StatusUpdate statusUpdate = new StatusUpdate(activeChar);
		NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);
		
		statusUpdate.addAttribute(StatusUpdate.CUR_LOAD, activeChar.getCurrentLoad());
		npcHtmlMessage.setHtml(HtmCache.getInstance().getHtm(htmlPath + "Reward.htm"));
		activeChar.sendPacket(statusUpdate);
		npcHtmlMessage.replace("%palyer%", activeChar.getName());
		npcHtmlMessage.replace("%killed%", String.valueOf(p.getPoints()));
		npcHtmlMessage.replace("%died%", String.valueOf(Config.LM_EVENT_PLAYER_CREDITS - p.getCredits()));
		npcHtmlMessage.replace("%reward%", htmltext);
		activeChar.sendPacket(npcHtmlMessage);
	}
	
	/**
	 * Stops the LMEvent fight<br>
	 * 1. Set state EventState.INACTIVATING<br>
	 * 2. Remove LM npc from world<br>
	 * 3. Open doors specified in configs<br>
	 * 4. Send Top Rank<br>
	 * 5. Teleport all participants back to participation npc location<br>
	 * 6. List players cleaning<br>
	 * 7. Set state EventState.INACTIVE<br>
	 */
	public static void stopFight()
	{
		// Set state INACTIVATING
		setState(EventState.INACTIVATING);
		// Opens all doors specified in configs for LM
		openDoors(Config.LM_DOORS_IDS_TO_CLOSE);
		// Closes all doors specified in Configs for LM
		closeDoors(Config.LM_DOORS_IDS_TO_OPEN);
		
		for (LMPlayer player : _lmPlayer.values())
		{
			if (player != null)
			{
				new LMEventTeleporter(player.getPlayer(), Config.LM_EVENT_PARTICIPATION_NPC_COORDINATES, false, false);
			}
		}
		
		// Cleanup list
		_lmPlayer = new HashMap<>();
		// Set state INACTIVE
		setState(EventState.INACTIVE);
	}
	
	public static boolean needParticipationFee()
	{
		return Config.LM_EVENT_PARTICIPATION_FEE[0] != 0 && Config.LM_EVENT_PARTICIPATION_FEE[1] != 0;
	}
	
	public static boolean hasParticipationFee(Player playerInstance)
	{
		return playerInstance.getInventory().getInventoryItemCount(Config.LM_EVENT_PARTICIPATION_FEE[0], -1) >= Config.LM_EVENT_PARTICIPATION_FEE[1];
	}
	
	/**
	 * Adds a player to a LMEvent<br>
	 * @param activeChar as Player<br>
	 * @return boolean: true if success, otherwise false<br>
	 */
	public static synchronized boolean addParticipant(Player activeChar)
	{
		// Check for nullpoitner
		if (activeChar == null)
			return false;
		
		if (isPlayerParticipant(activeChar))
			return false;
		
		String hexCode = EventConfig.hexToString(EventConfig.generateHex(16));
		_lmPlayer.put(activeChar.getObjectId(), new LMPlayer(activeChar, hexCode));
		return true;
	}
	
	public static boolean isPlayerParticipant(Player activeChar)
	{
		if (activeChar == null)
			return false;
		if (_lmPlayer.containsKey(activeChar.getObjectId()))
			return true;
		return false;
	}
	
	public static boolean isPlayerParticipant(int objectId)
	{
		Player activeChar = World.getInstance().getPlayer(objectId);
		if (activeChar == null)
			return false;
		return isPlayerParticipant(activeChar);
	}
	
	/**
	 * Removes a LMEvent player<br>
	 * @param activeChar as Player<br>
	 * @return boolean: true if success, otherwise false<br>
	 */
	public static boolean removeParticipant(Player activeChar)
	{
		if (activeChar == null)
			return false;
		if (!isPlayerParticipant(activeChar))
			return false;
		
		_lmPlayer.remove(activeChar.getObjectId());
		
		return true;
	}
	
	public static boolean payParticipationFee(Player activeChar)
	{
		return activeChar.destroyItemByItemId("LM Participation Fee", Config.LM_EVENT_PARTICIPATION_FEE[0], Config.LM_EVENT_PARTICIPATION_FEE[1], _lastNpcSpawn, true);
	}
	
	public static String getParticipationFee()
	{
		int itemId = Config.LM_EVENT_PARTICIPATION_FEE[0];
		int itemNum = Config.LM_EVENT_PARTICIPATION_FEE[1];
		
		if (itemId == 0 || itemNum == 0)
			return "-";
		
		return StringUtil.concat(String.valueOf(itemNum), " ", ItemTable.getInstance().getTemplate(itemId).getName());
	}
	
	/**
	 * Send a SystemMessage to all participated players<br>
	 * @param message as String<br>
	 */
	public static void sysMsgToAllParticipants(String message)
	{
		for (LMPlayer player : _lmPlayer.values())
			if (player != null)
				player.getPlayer().sendMessage(message);
	}
	
	/**
	 * Called when a player logs in<br>
	 * <br>
	 * @param activeChar as Player<br>
	 */
	public static void onLogin(Player activeChar)
	{
		if (activeChar == null || (!isStarting() && !isStarted()))
			return;
		if (!isPlayerParticipant(activeChar))
			return;
		
		new LMEventTeleporter(activeChar, false, false);
	}
	
	/**
	 * Called when a player logs out<br>
	 * <br>
	 * @param activeChar as Player<br>
	 */
	public static void onLogout(Player activeChar)
	{
		if (activeChar != null && (isStarting() || isStarted() || isParticipating()))
		{
			if (removeParticipant(activeChar))
				activeChar.setXYZInvisible(Config.LM_EVENT_PARTICIPATION_NPC_COORDINATES[0] + Rnd.get(101) - 50, Config.LM_EVENT_PARTICIPATION_NPC_COORDINATES[1] + Rnd.get(101) - 50, Config.LM_EVENT_PARTICIPATION_NPC_COORDINATES[2]);
		}
	}
	
	/**
	 * Called on every bypass by npc of type L2LMEventNpc<br>
	 * Needs synchronization cause of the max player check<br>
	 * <br>
	 * @param command as String<br>
	 * @param activeChar as Player<br>
	 */
	public static synchronized void onBypass(String command, Player activeChar)
	{
		if (activeChar == null || !isParticipating())
			return;
		
		final String htmContent;
		
		if (command.equals("lm_event_participation"))
		{
			NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);
			int playerLevel = activeChar.getLevel();
			
			if (activeChar.isCursedWeaponEquipped())
			{
				htmContent = HtmCache.getInstance().getHtm(htmlPath + "CursedWeaponEquipped.htm");
				if (htmContent != null)
					npcHtmlMessage.setHtml(htmContent);
			}
			else if (PlayerData.get(activeChar.getObjectId()).isAio())
			{
				htmContent = HtmCache.getInstance().getHtm(htmlPath + "Aio.htm");
				if (htmContent != null)
					npcHtmlMessage.setHtml(htmContent);
			}
			else if (OlympiadManager.getInstance().isRegistered(activeChar))
			{
				htmContent = HtmCache.getInstance().getHtm(htmlPath + "Olympiad.htm");
				if (htmContent != null)
					npcHtmlMessage.setHtml(htmContent);
			}
			else if (activeChar.getKarma() > 0)
			{
				htmContent = HtmCache.getInstance().getHtm(htmlPath + "Karma.htm");
				if (htmContent != null)
					npcHtmlMessage.setHtml(htmContent);
			}
			else if (playerLevel < Config.LM_EVENT_MIN_LVL || playerLevel > Config.LM_EVENT_MAX_LVL)
			{
				htmContent = HtmCache.getInstance().getHtm(htmlPath + "Level.htm");
				if (htmContent != null)
				{
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%min%", String.valueOf(Config.LM_EVENT_MIN_LVL));
					npcHtmlMessage.replace("%max%", String.valueOf(Config.LM_EVENT_MAX_LVL));
				}
			}
			else if (getPlayerCounts() == Config.LM_EVENT_MAX_PLAYERS)
			{
				htmContent = HtmCache.getInstance().getHtm(htmlPath + "Full.htm");
				if (htmContent != null)
				{
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%max%", String.valueOf(Config.LM_EVENT_MAX_PLAYERS));
				}
			}
			else if (needParticipationFee() && !hasParticipationFee(activeChar))
			{
				htmContent = HtmCache.getInstance().getHtm(htmlPath + "ParticipationFee.htm");
				if (htmContent != null)
				{
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%fee%", getParticipationFee());
				}
			}
			else if (isPlayerParticipant(activeChar))
				npcHtmlMessage.setHtml(HtmCache.getInstance().getHtm(htmlPath + "Registered.htm"));
			else if (addParticipant(activeChar))
				npcHtmlMessage.setHtml(HtmCache.getInstance().getHtm(htmlPath + "Registered.htm"));
			else
				return;
			
			activeChar.sendPacket(npcHtmlMessage);
		}
		else if (command.equals("lm_event_remove_participation"))
		{
			if (isPlayerParticipant(activeChar))
			{
				removeParticipant(activeChar);
				
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);
				
				npcHtmlMessage.setHtml(HtmCache.getInstance().getHtm(htmlPath + "Unregistered.htm"));
				activeChar.sendPacket(npcHtmlMessage);
			}
		}
	}
	
	/**
	 * Called on every onAction in L2PcIstance<br>
	 * <br>
	 * @param activeChar as Player<br>
	 * @param targetedPlayerObjectId as Integer<br>
	 * @return boolean: true if player is allowed to target, otherwise false<br>
	 */
	public static boolean onAction(Player activeChar, int targetedPlayerObjectId)
	{
		if (activeChar == null || !isStarted())
			return true;
		if (activeChar.isGM())
			return true;
		if (!isPlayerParticipant(activeChar) && isPlayerParticipant(targetedPlayerObjectId))
			return false;
		if (isPlayerParticipant(activeChar) && !isPlayerParticipant(targetedPlayerObjectId))
			return false;
		
		return true;
	}
	
	/**
	 * Called on every scroll use<br>
	 * <br>
	 * @param objectId as Integer<br>
	 * @return boolean: true if player is allowed to use scroll, otherwise false<br>
	 */
	public static boolean onScrollUse(int objectId)
	{
		if (!isStarted())
			return true;
		if (isPlayerParticipant(objectId) && !Config.LM_EVENT_SCROLL_ALLOWED)
			return false;
		return true;
	}
	
	/**
	 * Called on every potion use<br>
	 * <br>
	 * @param objectId as Integer<br>
	 * @return boolean: true if player is allowed to use potions, otherwise false<br>
	 */
	public static boolean onPotionUse(int objectId)
	{
		if (!isStarted())
			return true;
		if (isPlayerParticipant(objectId) && !Config.LM_EVENT_POTIONS_ALLOWED)
			return false;
		return true;
	}
	
	/**
	 * Called on every escape use<br>
	 * <br>
	 * @param objectId as Integer<br>
	 * @return boolean: true if player is not in LM Event, otherwise false<br>
	 */
	public static boolean onEscapeUse(int objectId)
	{
		if (!isStarted())
			return true;
		if (isPlayerParticipant(objectId))
			return false;
		return true;
	}
	
	/**
	 * Called on every summon item use<br>
	 * <br>
	 * @param objectId as Integer<br>
	 * @return boolean: true if player is allowed to summon by item, otherwise false<br>
	 */
	public static boolean onItemSummon(int objectId)
	{
		if (!isStarted())
			return true;
		if (isPlayerParticipant(objectId) && !Config.LM_EVENT_SUMMON_BY_ITEM_ALLOWED)
			return false;
		return true;
	}
	
	/**
	 * Is called when a player is killed<br>
	 * <br>
	 * @param killerCharacter as Character<br>
	 * @param killedPlayerInstance as Player<br>
	 */
	public static void onKill(Creature killerCharacter, Player killedPlayerInstance)
	{
		if (killedPlayerInstance == null || !isStarted())
			return;
		
		if (!isPlayerParticipant(killedPlayerInstance.getObjectId()))
			return;
		
		short killedCredits = _lmPlayer.get(killedPlayerInstance.getObjectId()).getCredits();
		if (killedCredits <= 1)
		{
			removeParticipant(killedPlayerInstance);
			new LMEventTeleporter(killedPlayerInstance, Config.LM_EVENT_PARTICIPATION_NPC_COORDINATES, true, true);
		}
		else
		{
			_lmPlayer.get(killedPlayerInstance.getObjectId()).decreaseCredits();
			new LMEventTeleporter(killedPlayerInstance, false, false);
		}
		
		if (killerCharacter == null)
			return;
		
		Player killerPlayerInstance = null;
		
		if (killerCharacter instanceof Pet || killerCharacter instanceof Summon)
		{
			killerPlayerInstance = ((Summon) killerCharacter).getOwner();
			if (killerPlayerInstance == null)
				return;
		}
		else if (killerCharacter instanceof Player)
			killerPlayerInstance = (Player) killerCharacter;
		else
			return;
		
		if (isPlayerParticipant(killerPlayerInstance))
		{
			_lmPlayer.get(killerPlayerInstance.getObjectId()).increasePoints();
			String msg = "";
			
			CreatureSay cs = new CreatureSay(killerPlayerInstance.getObjectId(), Say2.TELL, "LM Event", "You killed " + _lmPlayer.get(killerPlayerInstance.getObjectId()).getPoints() + " player(s)!");
			killerPlayerInstance.sendPacket(cs);
			if (killedCredits <= 1)
				msg = "You do not have credits, leaving the event!";
			else
				msg = "Now you have " + String.valueOf(killedCredits - 1) + " credit(s)!";
			cs = new CreatureSay(killedPlayerInstance.getObjectId(), Say2.TELL, "LM Event", msg);
			killedPlayerInstance.sendPacket(cs);
		}
		
		if (getPlayerCounts() == 1)
			LMManager.getInstance().skipDelay();
	}
	
	/**
	 * Called on Appearing packet received (player finished teleporting)<br>
	 * <br>
	 * @param activeChar
	 */
	public static void onTeleported(Player activeChar)
	{
		if (!isStarted() || activeChar == null || !isPlayerParticipant(activeChar.getObjectId()))
			return;
		
		if (activeChar.isMageClass())
		{
			if (Config.LM_EVENT_MAGE_BUFFS != null && !Config.LM_EVENT_MAGE_BUFFS.isEmpty())
			{
				for (int i : Config.LM_EVENT_MAGE_BUFFS.keySet())
				{
					L2Skill skill = SkillTable.getInstance().getInfo(i, Config.LM_EVENT_MAGE_BUFFS.get(i));
					if (skill != null)
						skill.getEffects(activeChar, activeChar);
				}
			}
		}
		else
		{
			if (Config.LM_EVENT_FIGHTER_BUFFS != null && !Config.LM_EVENT_FIGHTER_BUFFS.isEmpty())
			{
				for (int i : Config.LM_EVENT_FIGHTER_BUFFS.keySet())
				{
					L2Skill skill = SkillTable.getInstance().getInfo(i, Config.LM_EVENT_FIGHTER_BUFFS.get(i));
					if (skill != null)
						skill.getEffects(activeChar, activeChar);
				}
			}
		}
		EventConfig.removeParty(activeChar);
	}
	
	/*
	 * Return true if player valid for skill
	 */
	public static final boolean checkForLMSkill(Player source, Player target, L2Skill skill)
	{
		if (!isStarted())
			return true;
		
		// LM is started
		final boolean isSourceParticipant = isPlayerParticipant(source);
		final boolean isTargetParticipant = isPlayerParticipant(target);
		
		// both players not participating
		if (!isSourceParticipant && !isTargetParticipant)
			return true;
		// one player not participating
		if (!(isSourceParticipant && isTargetParticipant))
			return false;
		
		return true;
	}
	
	public static int getPlayerCounts()
	{
		return _lmPlayer.size();
	}
}