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
package net.sf.l2j.gameserver.model.zone.type;

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.commons.concurrent.ThreadPool;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.datatables.MapRegionTable.TeleportType;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.actor.instance.Servitor;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.zone.L2ZoneType;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;

/**
 * @author rapfersan92
 */
public class L2MultiZone extends L2ZoneType
{
   private static boolean _isNoRestart;
   private static boolean _isNoStore;
   private static boolean _isNoSummonFriend;
   private static boolean _isFlagEnabled;
   private static boolean _isPTZone;
   private static boolean _isReviveEnabled;
   private static boolean _isHealEnabled;
   private static int _revivePower;
   private static int _reviveDelay;
   private static List<Location> _reviveLocations = new ArrayList<>();
   private static List<Integer> _restrictedItems = new ArrayList<>();
   private static List<Integer> _restrictedSkills = new ArrayList<>();
  
   public L2MultiZone(int id)
   {
       super(id);
      
       _isNoRestart = false;
       _isNoStore = false;
       _isNoSummonFriend = false;
       _isFlagEnabled = false;
       _isPTZone = false;
       _isReviveEnabled = false;
       _isHealEnabled = false;
       _revivePower = 0;
       _reviveDelay = 0;
   }
  
   @Override
   public void setParameter(String name, String value)
   {
       if (name.equals("isNoRestart"))
           _isNoRestart = Boolean.parseBoolean(value);
       else if (name.equals("isNoStore"))
           _isNoStore = Boolean.parseBoolean(value);
       else if (name.equals("isNoSummonFriend"))
           _isNoSummonFriend = Boolean.parseBoolean(value);
       else if (name.equals("isFlagEnabled"))
           _isFlagEnabled = Boolean.parseBoolean(value);
       else if (name.equals("isPTZone"))
    	   _isPTZone = Boolean.parseBoolean(value);
       else if (name.equals("isReviveEnabled"))
           _isReviveEnabled = Boolean.parseBoolean(value);
       else if (name.equals("isHealEnabled"))
           _isHealEnabled = Boolean.parseBoolean(value);
       else if (name.equals("revivePower"))
           _revivePower = Integer.parseInt(value);
       else if (name.equals("reviveDelay"))
           _reviveDelay = Integer.parseInt(value);
       else if (name.equals("reviveLocations"))
       {
           String[] property = value.split(";");
           for (String data : property)
           {
               String[] coordinates = data.split(",");
               _reviveLocations.add(new Location(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), Integer.parseInt(coordinates[2])));
           }
       }
       else if (name.equals("restrictedItems"))
       {
           String[] property = value.split(",");
           for (String itemId : property)
               _restrictedItems.add(Integer.parseInt(itemId));
       }
       else if (name.equals("restrictedSkills"))
       {
           String[] property = value.split(",");
           for (String skillId : property)
               _restrictedSkills.add(Integer.parseInt(skillId));
       }
       else
           super.setParameter(name, value);
   }
  
   @Override
   protected void onEnter(Creature character)
   {
	   if (character instanceof Player)
	   {
		   character.setInsideZone(ZoneId.MULTI, true);
		   
		   if (_isNoRestart)
			   character.setInsideZone(ZoneId.NO_RESTART, true);
		   
		   if (_isNoStore)
			   character.setInsideZone(ZoneId.NO_STORE, true);
		   
		   if (_isNoSummonFriend)
			   character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true);
		   
		   if (character instanceof Player || character instanceof Servitor)
		   {
			   final Player player = (Player) character;
			   player.sendPacket(new ExShowScreenMessage("You have entered a multi zone.", 5000));
			   
			   if (_isFlagEnabled)
				   player.updatePvPFlag(1);
			   
			   if (_isPTZone)
			   {
				   if (player.getParty() != null)
				   {
					   character.isInsideZone(ZoneId.MULTI);
					   player.sendMessage("You have entered a party zone.");
				   }
				   else
				   {
					   player.sendMessage("This is strict area for party ONLY. You will be teleported at the nearest town.");
					   player.teleToLocation(TeleportType.TOWN);
				   }
			   }
		   }
          
           checkItemRestriction(character);
           checkSkillRestriction(character);
       }
   }
  
   @Override
   protected void onExit(Creature character)
   {
       character.setInsideZone(ZoneId.MULTI, false);
      
       if (_isNoRestart)
           character.setInsideZone(ZoneId.NO_RESTART, false);
      
       if (_isNoStore)
           character.setInsideZone(ZoneId.NO_STORE, false);
      
       if (_isNoSummonFriend)
           character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);
      
       if (character instanceof Player)
       {
           final Player player = (Player) character;
           player.sendPacket(new ExShowScreenMessage("You have left a multi zone.", 5000));
          
           if (_isFlagEnabled)
               player.updatePvPFlag(0);
       }
   }
  
   @Override
   public void onDieInside(Creature character)
   {
       if (character instanceof Player && _isReviveEnabled)
       {
           ThreadPool.schedule(() -> respawnCharacter(character), _reviveDelay * 1000);
           character.sendPacket(new ExShowScreenMessage("You will be revived in " + _reviveDelay + " second(s).", 5000));
       }
   }
  
   @Override
   public void onReviveInside(Creature character)
   {
       if (character instanceof Player && _isHealEnabled)
       {
           character.setCurrentCp(character.getMaxCp());
           character.setCurrentHp(character.getMaxHp());
           character.setCurrentMp(character.getMaxMp());
           character.sendPacket(new ExShowScreenMessage("Your CP, HP and MP have been restored.", 5000));
       }
   }
  
   private static void respawnCharacter(Creature character)
   {
       if (character.isDead())
       {
           character.doRevive(_revivePower);
           character.teleToLocation(Rnd.get(_reviveLocations), 20);
       }
   }
  
   private static void checkItemRestriction(Creature character)
   {
       for (ItemInstance item : character.getInventory().getPaperdollItems())
       {
           if (item == null || !isRestrictedItem(item.getItemId()))
               continue;
          
           character.getInventory().unEquipItemInSlot(item.getLocationSlot());
           InventoryUpdate iu = new InventoryUpdate();
           iu.addModifiedItem(item);
           character.sendPacket(iu);
       }
   }
  
   private static void checkSkillRestriction(Creature character)
   {
       for (L2Effect effect : character.getAllEffects())
       {
           if (effect == null || !isRestrictedSkill(effect.getSkill().getId()))
               continue;
          
           effect.exit(true);
       }
   }
  
   public static boolean isFlagEnabled()
   {
       return _isFlagEnabled;
   }
   
   public static boolean isPTZone()
   {
       return _isPTZone;
   }
   
   public static boolean isReviveEnabled()
   {
       return _isReviveEnabled;
   }
  
   public static boolean isRestrictedItem(int itemId)
   {
       return _restrictedItems.contains(itemId);
   }
  
   public static boolean isRestrictedSkill(int skillId)
   {
       return _restrictedSkills.contains(skillId);
   }
}