package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.instance.Folk;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class CustomNobles extends Folk
{
   public CustomNobles(int objectId, NpcTemplate template)
   {
      super(objectId, template);
   }

   @Override
   public void onBypassFeedback(Player player, String command)
   {
      if(command.startsWith("becomeNoblesse"))
      {
         NpcHtmlMessage html;
         if(player.isNoble())
         {
            html = new NpcHtmlMessage(this.getObjectId());
            html.setFile("data/html/mods/nobless/noblesse-already.htm");
            player.sendPacket(html);
            return;
         }

         if(player.getPvpKills() <= Config.PVP_BECOME_NOBLES)
         {
            html = new NpcHtmlMessage(this.getObjectId());
            html.setFile("data/html/mods/nobless/noblesse-pvp.htm");
            player.sendPacket(html);
            return;
         }

         if(player.getInventory().getInventoryItemCount(Config.NOBLES_ITEM_ID1, 0) < Config.NOBLES_ITEM_COUNT1 || player.getInventory().getInventoryItemCount(Config.NOBLES_ITEM_ID2, 0) < Config.NOBLES_ITEM_COUNT2 || player.getInventory().getInventoryItemCount(Config.NOBLES_ITEM_ID3, 0) < Config.NOBLES_ITEM_COUNT3)
         {
            html = new NpcHtmlMessage(this.getObjectId());
            html.setFile("data/html/mods/nobless/noblesse-shards.htm");
            player.sendPacket(html);
            return;
         }

         html = new NpcHtmlMessage(this.getObjectId());
         html.setFile("data/html/mods/nobless/noblesse-successfully.htm");
         player.destroyItemByItemId("Consume", Config.NOBLES_ITEM_ID1, Config.NOBLES_ITEM_COUNT1, player, true);
         player.destroyItemByItemId("Consume", Config.NOBLES_ITEM_ID2, Config.NOBLES_ITEM_COUNT2, player, true);
         player.destroyItemByItemId("Consume", Config.NOBLES_ITEM_ID3, Config.NOBLES_ITEM_COUNT3, player, true);
         player.addItem("Loot", 7694, 1, player, true);
         player.setNoble(true, true);
         player.broadcastPacket(new SocialAction(player, 16));
         player.sendPacket(html);
      }
      else
      {
         super.onBypassFeedback(player, command);
      }

   }

   @Override
   public void showChatWindow(Player player)
   {
      NpcHtmlMessage html = new NpcHtmlMessage(this.getObjectId());
      html.setFile("data/html/mods/nobless/noblesse.htm");
      html.replace("%objectId%", String.valueOf(player.getTargetId()));
      player.sendPacket(html);
   }
}