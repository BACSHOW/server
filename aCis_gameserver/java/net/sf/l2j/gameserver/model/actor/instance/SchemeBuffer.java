package net.sf.l2j.gameserver.model.actor.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.math.MathUtil;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.BufferTable;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Summon;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

public class SchemeBuffer extends Folk
{
	private static final int PAGE_LIMIT = 6;
	
	public SchemeBuffer(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String currentCommand = st.nextToken();
		
		if (currentCommand.startsWith("menu"))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile(getHtmlPath(getNpcId(), 0));
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else if (currentCommand.startsWith("cleanup"))
		{
			player.stopAllEffectsExceptThoseThatLastThroughDeath();
			
			final Summon summon = player.getPet();
			if (summon != null)
				summon.stopAllEffectsExceptThoseThatLastThroughDeath();
			
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile(getHtmlPath(getNpcId(), 0));
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else if (currentCommand.startsWith("heal"))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile(getHtmlPath(getNpcId(), 0));
			html.replace("%objectId%", getObjectId());
			
			if (player.getPvpFlag() != 0 || player.getKarma() != 0 || player.isInCombat())
			{
				player.sendMessage("You cannot be healed while in combat mode.");
				player.sendPacket(html);
			}
			else
			{
			player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
			player.setCurrentCp(player.getMaxCp());
			
			final Summon summon = player.getPet();
			if (summon != null)
				summon.setCurrentHpMp(summon.getMaxHp(), summon.getMaxMp());
			
			player.sendPacket(html);
			}
		}
		else if (currentCommand.startsWith("support"))
		{
			showGiveBuffsWindow(player);
		}
		else if (currentCommand.startsWith("givebuffs"))
		{
			final String schemeName = st.nextToken();
			final int cost = Integer.parseInt(st.nextToken());
			
			Creature target = null;
			if (st.hasMoreTokens())
			{
				final String targetType = st.nextToken();
				if (targetType != null && targetType.equalsIgnoreCase("pet"))
					target = player.getPet();
			}
			else
				target = player;
			
			if (target == null)
				player.sendMessage("You don't have a pet.");
			else if (cost == 0 || player.reduceAdena("NPC Buffer", cost, this, true))
			{
				for (int skillId : BufferTable.getInstance().getScheme(player.getObjectId(), schemeName))
					SkillTable.getInstance().getInfo(skillId, SkillTable.getInstance().getMaxLevel(skillId)).getEffects(this, target);
			}
		}
		else if (currentCommand.startsWith("editschemes"))
		{
			showEditSchemeWindow(player, st.nextToken(), st.nextToken(), Integer.parseInt(st.nextToken()));
		}
		else if (currentCommand.startsWith("skill"))
		{
			final String groupType = st.nextToken();
			final String schemeName = st.nextToken();
			
			final int skillId = Integer.parseInt(st.nextToken());
			final int page = Integer.parseInt(st.nextToken());
			
			final List<Integer> skills = BufferTable.getInstance().getScheme(player.getObjectId(), schemeName);
			
			if (currentCommand.startsWith("skillselect") && !schemeName.equalsIgnoreCase("none"))
			{
				if (skills.size() < player.getMaxBuffCount())
					skills.add(skillId);
				else
					player.sendMessage("This scheme has reached the maximum amount of buffs.");
			}
			else if (currentCommand.startsWith("skillunselect"))
				skills.remove(Integer.valueOf(skillId));
			
			showEditSchemeWindow(player, groupType, schemeName, page);
		}
		else if (currentCommand.startsWith("createscheme"))
		{
			try
			{
				final String schemeName = st.nextToken();
				if (schemeName.length() > 14)
				{
					player.sendMessage("Scheme's name must contain up to 14 chars. Spaces are trimmed.");
					return;
				}
				
				final Map<String, ArrayList<Integer>> schemes = BufferTable.getInstance().getPlayerSchemes(player.getObjectId());
				if (schemes != null)
				{
					if (schemes.size() == Config.BUFFER_MAX_SCHEMES)
					{
						player.sendMessage("Maximum schemes amount is already reached.");
						return;
					}
					
					if (schemes.containsKey(schemeName))
					{
						player.sendMessage("The scheme name already exists.");
						return;
					}
				}
				
				BufferTable.getInstance().setScheme(player.getObjectId(), schemeName.trim(), new ArrayList<Integer>());
				showGiveBuffsWindow(player);
			}
			catch (Exception e)
			{
				player.sendMessage("Scheme's name must contain up to 14 chars. Spaces are trimmed.");
			}
		}
		else if (currentCommand.startsWith("deletescheme"))
		{
			try
			{
				final String schemeName = st.nextToken();
				final Map<String, ArrayList<Integer>> schemes = BufferTable.getInstance().getPlayerSchemes(player.getObjectId());
				
				if (schemes != null && schemes.containsKey(schemeName))
					schemes.remove(schemeName);
			}
			catch (Exception e)
			{
				player.sendMessage("This scheme name is invalid.");
			}
			showGiveBuffsWindow(player);
		}
		else if (currentCommand.equalsIgnoreCase("getbuff"))
		{
			int buffid = 0;
			int bufflevel = 1;
			String nextWindow = null;
			if (st.countTokens() == 3)
			{
				buffid = Integer.valueOf(st.nextToken()).intValue();
				bufflevel = Integer.valueOf(st.nextToken()).intValue();
				nextWindow = st.nextToken();
			}
			else if (st.countTokens() == 1)
			{
				buffid = Integer.valueOf(st.nextToken()).intValue();
			}
			if (buffid != 0)
			{
				player.broadcastPacket(new MagicSkillUse(this, player, buffid, bufflevel, 5, 0));
				
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(buffid, bufflevel));
				SkillTable.getInstance().getInfo(buffid, bufflevel).getEffects(this, player);
				showChatWindow(player, nextWindow);
			}
		}
		else if (currentCommand.equalsIgnoreCase("warrior_w_zerk"))
		{
			player.stopAllEffects();
			SkillTable.getInstance().getInfo(1040, 3).getEffects(this, player); //shield
			SkillTable.getInstance().getInfo(1068, 3).getEffects(this, player); //might
			SkillTable.getInstance().getInfo(1077, 3).getEffects(this, player); //focus
			SkillTable.getInstance().getInfo(1204, 2).getEffects(this, player); //wind walk
			SkillTable.getInstance().getInfo(1036, 2).getEffects(this, player); // Magic Barrier
			SkillTable.getInstance().getInfo(1045, 6).getEffects(this, player); // Blessed Body
			SkillTable.getInstance().getInfo(1048, 6).getEffects(this, player); // Blessed Soul
			SkillTable.getInstance().getInfo(1086, 2).getEffects(this, player); // Haste
			SkillTable.getInstance().getInfo(1240, 3).getEffects(this, player); // Guidance
			SkillTable.getInstance().getInfo(1062, 2).getEffects(this, player); // Berserker Spirit
			SkillTable.getInstance().getInfo(1242, 3).getEffects(this, player); // Death Whisper
			SkillTable.getInstance().getInfo(1388, 3).getEffects(this, player); // Greater Might
			SkillTable.getInstance().getInfo(1035, 4).getEffects(this, player); // Mental Shield
			SkillTable.getInstance().getInfo(4699, 13).getEffects(this, player); // Blessing of Queen
			SkillTable.getInstance().getInfo(1363, 1).getEffects(this, player); // Victory Chant
			SkillTable.getInstance().getInfo(271, 1).getEffects(this, player); // Dance of the Warrior
			SkillTable.getInstance().getInfo(272, 1).getEffects(this, player); // Dance of Inspiration
			SkillTable.getInstance().getInfo(274, 1).getEffects(this, player); // Dance of Fire
			SkillTable.getInstance().getInfo(275, 1).getEffects(this, player); // Dance of Fury
			SkillTable.getInstance().getInfo(264, 1).getEffects(this, player); // Song of Earth
			SkillTable.getInstance().getInfo(267, 1).getEffects(this, player); // Song of Warding
			SkillTable.getInstance().getInfo(268, 1).getEffects(this, player); // Song of Wind
			SkillTable.getInstance().getInfo(269, 1).getEffects(this, player); // Song of Hunter
			SkillTable.getInstance().getInfo(304, 1).getEffects(this, player); // Song of Vitality
			SkillTable.getInstance().getInfo(349, 1).getEffects(this, player); // Song of Renewal
			SkillTable.getInstance().getInfo(364, 1).getEffects(this, player); // Song of Champion
			
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile(getHtmlPath(getNpcId(), 0));
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else if (currentCommand.equalsIgnoreCase("warrior_w/o_zerk"))
		{
			player.stopAllEffects();
			SkillTable.getInstance().getInfo(1040, 3).getEffects(this, player); //shield
			SkillTable.getInstance().getInfo(1068, 3).getEffects(this, player); //might
			SkillTable.getInstance().getInfo(1077, 3).getEffects(this, player); //focus
			SkillTable.getInstance().getInfo(1204, 2).getEffects(this, player); //wind walk
			SkillTable.getInstance().getInfo(1036, 2).getEffects(this, player); // Magic Barrier
			SkillTable.getInstance().getInfo(1045, 6).getEffects(this, player); // Blessed Body
			SkillTable.getInstance().getInfo(1048, 6).getEffects(this, player); // Blessed Soul
			SkillTable.getInstance().getInfo(1086, 2).getEffects(this, player); // Haste
			SkillTable.getInstance().getInfo(1240, 3).getEffects(this, player); // Guidance
			SkillTable.getInstance().getInfo(1242, 3).getEffects(this, player); // Death Whisper
			SkillTable.getInstance().getInfo(1388, 3).getEffects(this, player); // Greater Might
			SkillTable.getInstance().getInfo(1035, 4).getEffects(this, player); // Mental Shield
			SkillTable.getInstance().getInfo(4699, 13).getEffects(this, player); // Blessing of Queen
			SkillTable.getInstance().getInfo(1363, 1).getEffects(this, player); // Victory Chant
			SkillTable.getInstance().getInfo(271, 1).getEffects(this, player); // Dance of the Warrior
			SkillTable.getInstance().getInfo(272, 1).getEffects(this, player); // Dance of Inspiration
			SkillTable.getInstance().getInfo(274, 1).getEffects(this, player); // Dance of Fire
			SkillTable.getInstance().getInfo(275, 1).getEffects(this, player); // Dance of Fury
			SkillTable.getInstance().getInfo(264, 1).getEffects(this, player); // Song of Earth
			SkillTable.getInstance().getInfo(267, 1).getEffects(this, player); // Song of Warding
			SkillTable.getInstance().getInfo(268, 1).getEffects(this, player); // Song of Wind
			SkillTable.getInstance().getInfo(269, 1).getEffects(this, player); // Song of Hunter
			SkillTable.getInstance().getInfo(304, 1).getEffects(this, player); // Song of Vitality
			SkillTable.getInstance().getInfo(349, 1).getEffects(this, player); // Song of Renewal
			SkillTable.getInstance().getInfo(364, 1).getEffects(this, player); // Song of Champion
			
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile(getHtmlPath(getNpcId(), 0));
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			
		}
		else if (currentCommand.equalsIgnoreCase("mage_w_zerk"))
		{
			player.stopAllEffects();
			SkillTable.getInstance().getInfo(1040, 3).getEffects(this, player); // Shield
			SkillTable.getInstance().getInfo(1078, 6).getEffects(this, player); // Concentration
			SkillTable.getInstance().getInfo(1085, 3).getEffects(this, player); // Acumen
			SkillTable.getInstance().getInfo(1204, 2).getEffects(this, player); // Wind Walk
			SkillTable.getInstance().getInfo(1036, 2).getEffects(this, player); // Magic Barrier
			SkillTable.getInstance().getInfo(1045, 6).getEffects(this, player); // Blessed Body
			SkillTable.getInstance().getInfo(1048, 6).getEffects(this, player); // Blessed Soul
			SkillTable.getInstance().getInfo(1062, 2).getEffects(this, player); // Berserker Spirit
			SkillTable.getInstance().getInfo(1059, 3).getEffects(this, player); // Empower
			SkillTable.getInstance().getInfo(1303, 2).getEffects(this, player); // Wild Magic
			SkillTable.getInstance().getInfo(1389, 3).getEffects(this, player); // Greater Shield
			SkillTable.getInstance().getInfo(1035, 4).getEffects(this, player); // Mental Shield 
			SkillTable.getInstance().getInfo(4703, 13).getEffects(this, player); // Gift of Seraphim
			SkillTable.getInstance().getInfo(1363, 1).getEffects(this, player); // Victory Chant
			SkillTable.getInstance().getInfo(273, 1).getEffects(this, player); // Dance of the Mystic
			SkillTable.getInstance().getInfo(276, 1).getEffects(this, player); // Dance of Concentration
			SkillTable.getInstance().getInfo(365, 1).getEffects(this, player); // Siren's Dance 
			SkillTable.getInstance().getInfo(264, 1).getEffects(this, player); // Song of Earth
			SkillTable.getInstance().getInfo(267, 1).getEffects(this, player); // Song of Warding
			SkillTable.getInstance().getInfo(268, 1).getEffects(this, player); // Song of Wind
			SkillTable.getInstance().getInfo(304, 1).getEffects(this, player); // Song of Vitality
			SkillTable.getInstance().getInfo(349, 1).getEffects(this, player); // Song of Renewal
			
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile(getHtmlPath(getNpcId(), 0));
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else if (currentCommand.equalsIgnoreCase("mage_w/o_zerk"))
		{
			player.stopAllEffects();
			SkillTable.getInstance().getInfo(1040, 3).getEffects(this, player); // Shield
			SkillTable.getInstance().getInfo(1078, 6).getEffects(this, player); // Concentration
			SkillTable.getInstance().getInfo(1085, 3).getEffects(this, player); // Acumen
			SkillTable.getInstance().getInfo(1204, 2).getEffects(this, player); // Wind Walk
			SkillTable.getInstance().getInfo(1036, 2).getEffects(this, player); // Magic Barrier
			SkillTable.getInstance().getInfo(1045, 6).getEffects(this, player); // Blessed Body
			SkillTable.getInstance().getInfo(1048, 6).getEffects(this, player); // Blessed Soul
			SkillTable.getInstance().getInfo(1059, 3).getEffects(this, player); // Empower
			SkillTable.getInstance().getInfo(1303, 2).getEffects(this, player); // Wild Magic
			SkillTable.getInstance().getInfo(1389, 3).getEffects(this, player); // Greater Shield
			SkillTable.getInstance().getInfo(1035, 4).getEffects(this, player); // Mental Shield 
			SkillTable.getInstance().getInfo(4703, 13).getEffects(this, player); // Gift of Seraphim
			SkillTable.getInstance().getInfo(1363, 1).getEffects(this, player); // Victory Chant
			SkillTable.getInstance().getInfo(273, 1).getEffects(this, player); // Dance of the Mystic
			SkillTable.getInstance().getInfo(276, 1).getEffects(this, player); // Dance of Concentration
			SkillTable.getInstance().getInfo(365, 1).getEffects(this, player); // Siren's Dance 
			SkillTable.getInstance().getInfo(264, 1).getEffects(this, player); // Song of Earth
			SkillTable.getInstance().getInfo(267, 1).getEffects(this, player); // Song of Warding
			SkillTable.getInstance().getInfo(268, 1).getEffects(this, player); // Song of Wind
			SkillTable.getInstance().getInfo(304, 1).getEffects(this, player); // Song of Vitality
			SkillTable.getInstance().getInfo(349, 1).getEffects(this, player); // Song of Renewal
			
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile(getHtmlPath(getNpcId(), 0));
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		
		super.onBypassFeedback(player, command);
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String filename = "";
		if (val == 0)
			filename = "" + npcId;
		else
			filename = npcId + "-" + val;
		
		return "data/html/mods/buffer/" + filename + ".htm";
	}
	
	/**
	 * Sends an html packet to player with Give Buffs menu info for player and pet, depending on targetType parameter {player, pet}
	 * @param player : The player to make checks on.
	 */
	private void showGiveBuffsWindow(Player player)
	{
		final StringBuilder sb = new StringBuilder(200);
		
		final Map<String, ArrayList<Integer>> schemes = BufferTable.getInstance().getPlayerSchemes(player.getObjectId());
		if (schemes == null || schemes.isEmpty())
			sb.append("<font color=\"LEVEL\">You haven't defined any scheme.</font>");
		else
		{
			for (Map.Entry<String, ArrayList<Integer>> scheme : schemes.entrySet())
			{
				final int cost = getFee(scheme.getValue());
				StringUtil.append(sb, "<font color=\"LEVEL\">", scheme.getKey(), " [", scheme.getValue().size(), " / ", player.getMaxBuffCount(), "]", ((cost > 0) ? " - cost: " + StringUtil.formatNumber(cost) : ""), "</font><br1>");
				StringUtil.append(sb, "<a action=\"bypass npc_%objectId%_givebuffs ", scheme.getKey(), " ", cost, "\">Use on Me</a>&nbsp;|&nbsp;");
				StringUtil.append(sb, "<a action=\"bypass npc_%objectId%_givebuffs ", scheme.getKey(), " ", cost, " pet\">Use on Pet</a>&nbsp;|&nbsp;");
				StringUtil.append(sb, "<a action=\"bypass npc_%objectId%_editschemes Buffs ", scheme.getKey(), " 1\">Edit</a>&nbsp;|&nbsp;");
				StringUtil.append(sb, "<a action=\"bypass npc_%objectId%_deletescheme ", scheme.getKey(), "\">Delete</a><br>");
			}
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(getHtmlPath(getNpcId(), 1));
		html.replace("%schemes%", sb.toString());
		html.replace("%max_schemes%", Config.BUFFER_MAX_SCHEMES);
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
	
	/**
	 * This sends an html packet to player with Edit Scheme Menu info. This allows player to edit each created scheme (add/delete skills)
	 * @param player : The player to make checks on.
	 * @param groupType : The group of skills to select.
	 * @param schemeName : The scheme to make check.
	 * @param page The page.
	 */
	private void showEditSchemeWindow(Player player, String groupType, String schemeName, int page)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		final List<Integer> schemeSkills = BufferTable.getInstance().getScheme(player.getObjectId(), schemeName);
		
		html.setFile(getHtmlPath(getNpcId(), 2));
		html.replace("%schemename%", schemeName);
		html.replace("%count%", schemeSkills.size() + " / " + player.getMaxBuffCount());
		html.replace("%typesframe%", getTypesFrame(groupType, schemeName));
		html.replace("%skilllistframe%", getGroupSkillList(player, groupType, schemeName, page));
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
	
	/**
	 * @param player : The player to make checks on.
	 * @param groupType : The group of skills to select.
	 * @param schemeName : The scheme to make check.
	 * @param page The page.
	 * @return a String representing skills available to selection for a given groupType.
	 */
	private String getGroupSkillList(Player player, String groupType, String schemeName, int page)
	{
		// Retrieve the entire skills list based on group type.
		List<Integer> skills = BufferTable.getInstance().getSkillsIdsByType(groupType);
		if (skills.isEmpty())
			return "That group doesn't contain any skills.";
		
		// Calculate page number.
		final int max = MathUtil.countPagesNumber(skills.size(), PAGE_LIMIT);
		if (page > max)
			page = max;
		
		// Cut skills list up to page number.
		skills = skills.subList((page - 1) * PAGE_LIMIT, Math.min(page * PAGE_LIMIT, skills.size()));
		
		final List<Integer> schemeSkills = BufferTable.getInstance().getScheme(player.getObjectId(), schemeName);
		final StringBuilder sb = new StringBuilder(skills.size() * 150);
		
		int row = 0;
		for (int skillId : skills)
		{
			sb.append(((row % 2) == 0 ? "<table width=\"280\" bgcolor=\"000000\"><tr>" : "<table width=\"280\"><tr>"));
			
			if (skillId < 100)
			{
				if (schemeSkills.contains(skillId))
					StringUtil.append(sb, "<td height=40 width=40><img src=\"icon.skill00", skillId, "\" width=32 height=32></td><td width=190>", SkillTable.getInstance().getInfo(skillId, 1).getName(), "<br1><font color=\"B09878\">", BufferTable.getInstance().getAvailableBuff(skillId).getDescription(), "</font></td><td><button action=\"bypass npc_%objectId%_skillunselect ", groupType, " ", schemeName, " ", skillId, " ", page, "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomout1\"></td>");
				else
					StringUtil.append(sb, "<td height=40 width=40><img src=\"icon.skill00", skillId, "\" width=32 height=32></td><td width=190>", SkillTable.getInstance().getInfo(skillId, 1).getName(), "<br1><font color=\"B09878\">", BufferTable.getInstance().getAvailableBuff(skillId).getDescription(), "</font></td><td><button action=\"bypass npc_%objectId%_skillselect ", groupType, " ", schemeName, " ", skillId, " ", page, "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomin2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
			}
			else if (skillId < 1000)
			{
				if (schemeSkills.contains(skillId))
					StringUtil.append(sb, "<td height=40 width=40><img src=\"icon.skill0", skillId, "\" width=32 height=32></td><td width=190>", SkillTable.getInstance().getInfo(skillId, 1).getName(), "<br1><font color=\"B09878\">", BufferTable.getInstance().getAvailableBuff(skillId).getDescription(), "</font></td><td><button action=\"bypass npc_%objectId%_skillunselect ", groupType, " ", schemeName, " ", skillId, " ", page, "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomout1\"></td>");
				else
					StringUtil.append(sb, "<td height=40 width=40><img src=\"icon.skill0", skillId, "\" width=32 height=32></td><td width=190>", SkillTable.getInstance().getInfo(skillId, 1).getName(), "<br1><font color=\"B09878\">", BufferTable.getInstance().getAvailableBuff(skillId).getDescription(), "</font></td><td><button action=\"bypass npc_%objectId%_skillselect ", groupType, " ", schemeName, " ", skillId, " ", page, "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomin2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
			}
			else
			{
				if (schemeSkills.contains(skillId))
					StringUtil.append(sb, "<td height=40 width=40><img src=\"icon.skill", skillId, "\" width=32 height=32></td><td width=190>", SkillTable.getInstance().getInfo(skillId, 1).getName(), "<br1><font color=\"B09878\">", BufferTable.getInstance().getAvailableBuff(skillId).getDescription(), "</font></td><td><button action=\"bypass npc_%objectId%_skillunselect ", groupType, " ", schemeName, " ", skillId, " ", page, "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomout1\"></td>");
				else
					StringUtil.append(sb, "<td height=40 width=40><img src=\"icon.skill", skillId, "\" width=32 height=32></td><td width=190>", SkillTable.getInstance().getInfo(skillId, 1).getName(), "<br1><font color=\"B09878\">", BufferTable.getInstance().getAvailableBuff(skillId).getDescription(), "</font></td><td><button action=\"bypass npc_%objectId%_skillselect ", groupType, " ", schemeName, " ", skillId, " ", page, "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomin2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
			}
			
			sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=277 height=1>");
			row++;
		}
		
		// Build page footer.
		sb.append("<br><img src=\"L2UI.SquareGray\" width=277 height=1><table width=\"100%\" bgcolor=000000><tr>");
		
		if (page > 1)
			StringUtil.append(sb, "<td align=left width=70><a action=\"bypass npc_" + getObjectId() + "_editschemes ", groupType, " ", schemeName, " ", page - 1, "\">Previous</a></td>");
		else
			StringUtil.append(sb, "<td align=left width=70>Previous</td>");
		
		StringUtil.append(sb, "<td align=center width=100>Page ", page, "</td>");
		
		if (page < max)
			StringUtil.append(sb, "<td align=right width=70><a action=\"bypass npc_" + getObjectId() + "_editschemes ", groupType, " ", schemeName, " ", page + 1, "\">Next</a></td>");
		else
			StringUtil.append(sb, "<td align=right width=70>Next</td>");
		
		sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=277 height=1>");
		
		return sb.toString();
	}
	
	/**
	 * @param groupType : The group of skills to select.
	 * @param schemeName : The scheme to make check.
	 * @return a string representing all groupTypes available. The group currently on selection isn't linkable.
	 */
	private static String getTypesFrame(String groupType, String schemeName)
	{
		final StringBuilder sb = new StringBuilder(500);
		sb.append("<table>");
		
		int count = 0;
		for (String type : BufferTable.getInstance().getSkillTypes())
		{
			if (count == 0)
				sb.append("<tr>");
			
			if (groupType.equalsIgnoreCase(type))
				StringUtil.append(sb, "<td width=65>", type, "</td>");
			else
				StringUtil.append(sb, "<td width=65><a action=\"bypass npc_%objectId%_editschemes ", type, " ", schemeName, " 1\">", type, "</a></td>");
			
			count++;
			if (count == 4)
			{
				sb.append("</tr>");
				count = 0;
			}
		}
		
		if (!sb.toString().endsWith("</tr>"))
			sb.append("</tr>");
		
		sb.append("</table>");
		
		return sb.toString();
	}
	
	/**
	 * @param list : A list of skill ids.
	 * @return a global fee for all skills contained in list.
	 */
	private static int getFee(ArrayList<Integer> list)
	{
		if (Config.BUFFER_STATIC_BUFF_COST > 0)
			return list.size() * Config.BUFFER_STATIC_BUFF_COST;
		
		int fee = 0;
		for (int sk : list)
			fee += BufferTable.getInstance().getAvailableBuff(sk).getValue();
		
		return fee;
	}
}