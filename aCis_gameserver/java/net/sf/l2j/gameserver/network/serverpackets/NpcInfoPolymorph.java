package net.sf.l2j.gameserver.network.serverpackets;

import net.sf.l2j.gameserver.datatables.CharTemplateTable;
import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.model.CharSelectInfoPackage;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.actor.PcPolymorph;
import net.sf.l2j.gameserver.model.actor.template.PlayerTemplate;
import net.sf.l2j.gameserver.network.serverpackets.L2GameServerPacket;

public final class NpcInfoPolymorph extends L2GameServerPacket {
   private final PcPolymorph _activeChar;
   private final CharSelectInfoPackage _morph;
   private final PlayerTemplate _template;
   private final L2Clan _clan;
   private final int _x;
   private final int _y;
   private final int _z;
   private final int _heading;
   private final int _mAtkSpd;
   private final int _pAtkSpd;
   private final int _runSpd;
   private final int _walkSpd;
   private final float _moveMultiplier;

   public NpcInfoPolymorph(PcPolymorph cha) {
      this._activeChar = cha;
      this._morph = cha.getPolymorphInfo();
      this._template = CharTemplateTable.getInstance().getTemplate(this._morph.getBaseClassId());
      this._clan = ClanTable.getInstance().getClan(this._morph.getClanId());
      this._x = this._activeChar.getX();
      this._y = this._activeChar.getY();
      this._z = this._activeChar.getZ();
      this._heading = this._activeChar.getHeading();
      this._mAtkSpd = this._activeChar.getMAtkSpd();
      this._pAtkSpd = this._activeChar.getPAtkSpd();
      this._moveMultiplier = this._activeChar.getStat().getMovementSpeedMultiplier();
      this._runSpd = (int) (_activeChar.getRunSpeed() / _moveMultiplier);
      this._walkSpd = (int) (_activeChar.getWalkSpeed() / _moveMultiplier);
   }

   @Override
   protected final void writeImpl() {
      this.writeC(3);
      this.writeD(this._x);
      this.writeD(this._y);
      this.writeD(this._z);
      this.writeD(this._heading);
      this.writeD(this._activeChar.getObjectId());
      this.writeS(this._morph.getName());
      this.writeD(this._morph.getRace());
      this.writeD(this._morph.getSex());
      this.writeD(this._morph.getBaseClassId());
      this.writeD(this._morph.getPaperdollItemId(16));
      this.writeD(this._morph.getPaperdollItemId(6));
      this.writeD(this._morph.getPaperdollItemId(7));
      this.writeD(this._morph.getPaperdollItemId(8));
      this.writeD(this._morph.getPaperdollItemId(9));
      this.writeD(this._morph.getPaperdollItemId(10));
      this.writeD(this._morph.getPaperdollItemId(11));
      this.writeD(this._morph.getPaperdollItemId(12));
      this.writeD(this._morph.getPaperdollItemId(13));
      this.writeD(this._morph.getPaperdollItemId(7));
      this.writeD(this._morph.getPaperdollItemId(15));
      this.writeD(this._morph.getPaperdollItemId(14));
      this.writeH(0);
      this.writeH(0);
      this.writeH(0);
      this.writeH(0);
      this.writeD(this._morph.getAugmentationId());
      this.writeH(0);
      this.writeH(0);
      this.writeH(0);
      this.writeH(0);
      this.writeH(0);
      this.writeH(0);
      this.writeH(0);
      this.writeH(0);
      this.writeH(0);
      this.writeH(0);
      this.writeH(0);
      this.writeH(0);
      this.writeD(0);
      this.writeH(0);
      this.writeH(0);
      this.writeH(0);
      this.writeH(0);
      this.writeD(0);
      this.writeD(0);
      this.writeD(this._mAtkSpd);
      this.writeD(this._pAtkSpd);
      this.writeD(0);
      this.writeD(0);
      this.writeD(this._runSpd);
      this.writeD(this._walkSpd);
      this.writeD(this._runSpd);
      this.writeD(this._walkSpd);
      this.writeD(this._runSpd);
      this.writeD(this._walkSpd);
      this.writeD(this._runSpd);
      this.writeD(this._walkSpd);
      this.writeF(_activeChar.getMovementSpeedMultiplier());
      this.writeF(_activeChar.getStat().getAttackSpeedMultiplier());
      this.writeF(_template.getCollisionRadius());
      this.writeF(_template.getCollisionHeight());
      this.writeD(this._morph.getHairStyle());
      this.writeD(this._morph.getHairColor());
      this.writeD(this._morph.getFace());
      this.writeS(this._activeChar.getVisibleTitle());
      if(this._clan != null) {
         this.writeD(this._clan.getClanId());
         this.writeD(this._clan.getCrestId());
         this.writeD(this._clan.getAllyId());
         this.writeD(this._clan.getAllyCrestId());
      } else {
         this.writeD(0);
         this.writeD(0);
         this.writeD(0);
         this.writeD(0);
      }

      this.writeD(0);
      this.writeC(1);
      this.writeC(this._activeChar.isRunning()?1:0);
      this.writeC(this._activeChar.isInCombat()?1:0);
      this.writeC(this._activeChar.isAlikeDead()?1:0);
      this.writeC(0);
      this.writeC(0);
      this.writeC(0);
      this.writeH(0);
      this.writeC(0);
      this.writeD(this._activeChar.getAbnormalEffect());
      this.writeC(0);
      this.writeH(0);
      this.writeD(this._morph.getClassId());
      this.writeD(this._activeChar.getMaxCp());
      this.writeD((int)this._activeChar.getCurrentCp());
      this.writeC(this._morph.getEnchantEffect() > 127?127:this._morph.getEnchantEffect());
      this.writeC(0);
      this.writeD(this._clan != null?this._clan.getCrestLargeId():0);
      this.writeC(0);
      this.writeC(0);
      this.writeC(0);
      this.writeD(0);
      this.writeD(0);
      this.writeD(0);
      this.writeD(this._activeChar.getNameColor());
      this.writeD(0);
      this.writeD(0);
      this.writeD(0);
      this.writeD(this._activeChar.getTitleColor());
      this.writeD(0);
   }
}