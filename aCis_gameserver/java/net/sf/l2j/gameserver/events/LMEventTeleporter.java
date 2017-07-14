package net.sf.l2j.gameserver.events;

import net.sf.l2j.commons.concurrent.ThreadPool;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.Summon;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.Duel.DuelState;

public class LMEventTeleporter implements Runnable
{
	private Player _activeChar = null;
	private int[] _coordinates = new int[3];
	private boolean _adminRemove = false;
	
	/**
	 * Initialize the teleporter and start the delayed task<br>
	 * <br>
	 * @param activeChar as Player<br>
	 * @param coordinates as int[]<br>
	 * @param fastSchedule as boolean<br>
	 * @param adminRemove as boolean<br>
	 */
	public LMEventTeleporter(Player activeChar, int[] coordinates, boolean fastSchedule, boolean adminRemove)
	{
		_activeChar = activeChar;
		_coordinates = coordinates;
		_adminRemove = adminRemove;
		
		loadTeleport(fastSchedule);
	}
	
	/**
	 * Initialize the teleporter and start the delayed task<br>
	 * <br>
	 * @param activeChar as Player<br>
	 * @param fastSchedule as boolean<br>
	 * @param adminRemove as boolean<br>
	 */
	public LMEventTeleporter(Player activeChar, boolean fastSchedule, boolean adminRemove)
	{
		_activeChar = activeChar;
		_coordinates = Config.LM_EVENT_PLAYER_COORDINATES.get(Rnd.get(Config.LM_EVENT_PLAYER_COORDINATES.size()));
		_adminRemove = adminRemove;
		
		loadTeleport(fastSchedule);
	}
	
	private void loadTeleport(boolean fastSchedule)
	{
		long delay = (LMEvent.isStarted() ? Config.LM_EVENT_RESPAWN_TELEPORT_DELAY : Config.LM_EVENT_START_LEAVE_TELEPORT_DELAY) * 1000;
		ThreadPool.schedule(this, fastSchedule ? 0 : delay);
	}
	
	private void cryptChar(Player activeChar)
	{
		if (Config.LM_EVENT_HIDE_NAME)
		{
			String name = EventConfig.hexToString(EventConfig.generateHex(16));
			_activeChar.getAppearance().setVisibleName(name.substring(0, 16));
			_activeChar.getAppearance().setVisibleTitle("");
			
			_activeChar._originalColorName = _activeChar.getAppearance().getNameColor();
			_activeChar.getAppearance().setNameColor(Config.LM_COLOR_NAME);
			
			_activeChar._originalColorTitle = _activeChar.getAppearance().getTitleColor();
			_activeChar.getAppearance().setTitleColor(Config.LM_COLOR_TITLE);
		}
		_activeChar.setHideInfo(true);
	}
	
	private void decryptChar(Player activeChar)
	{
		if (Config.LM_EVENT_HIDE_NAME)
		{
			_activeChar.getAppearance().setVisibleName(_activeChar.getName());
			_activeChar.getAppearance().setVisibleTitle(_activeChar.getTitle());
			_activeChar.getAppearance().setNameColor(_activeChar._originalColorName);
			_activeChar.getAppearance().setTitleColor(_activeChar._originalColorTitle);
			
			_activeChar._originalColorName = null;
			_activeChar._originalColorTitle = null;
		}
		_activeChar.setHideInfo(false);
	}
	
	/**
	 * The task method to teleport the player<br>
	 * 1. Unsummon pet if there is one<br>
	 * 2. Remove all effects<br>
	 * 3. Revive and full heal the player<br>
	 * 4. Teleport the player<br>
	 * 5. Broadcast status and user info<br>
	 * <br>
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		if (_activeChar == null)
			return;
		
		Summon summon = _activeChar.getPet();
		
		if (summon != null)
			summon.unSummon(_activeChar);
		
		if (Config.LM_EVENT_EFFECTS_REMOVAL == 0 || (Config.LM_EVENT_EFFECTS_REMOVAL == 1 && (_activeChar.getTeam() == 0 || (_activeChar.isInDuel() && _activeChar.getDuelState() != DuelState.INTERRUPTED))))
			_activeChar.stopAllEffectsExceptThoseThatLastThroughDeath();
		
		if (_activeChar.isInDuel())
			_activeChar.setDuelState(DuelState.INTERRUPTED);
		
		_activeChar.doRevive();
		
		_activeChar.teleToLocation(_coordinates[0] + Rnd.get(101) - 50, _coordinates[1] + Rnd.get(101) - 50, _coordinates[2], 0);
		
		if (LMEvent.isStarted() && !_adminRemove)
		{
			_activeChar.setTeam(1);
			if (!_activeChar.isHideInfo())
				cryptChar(_activeChar);
		}
		else
		{
			_activeChar.setTeam(0);
			if (_activeChar.isHideInfo())
				decryptChar(_activeChar);
		}
		_activeChar.setCurrentCp(_activeChar.getMaxCp());
		_activeChar.setCurrentHp(_activeChar.getMaxHp());
		_activeChar.setCurrentMp(_activeChar.getMaxMp());
		
		_activeChar.broadcastStatusUpdate();
		_activeChar.broadcastTitleInfo();
		_activeChar.broadcastUserInfo();
		
	}
}