package net.sf.l2j.gameserver.scripting.quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.l2j.commons.math.MathUtil;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.network.clientpackets.Say2;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.scripting.QuestState;
import net.sf.l2j.gameserver.scripting.quests.audio.Sound;

public class Q335_SongOfTheHunter extends Quest
{
	private static final String qn = "Q335_SongOfTheHunter";
	
	// Npc
	private static final int GREY = 30744;
	private static final int TOR = 30745;
	private static final int CYBELLIN = 30746;
	
	// Items
	private static final int CYB_DAGGER = 3471;
	private static final int LICENSE_1 = 3692;
	private static final int LICENSE_2 = 3693;
	private static final int LEAF_PIN = 3694;
	private static final int TEST_INSTRUCTIONS_1 = 3695;
	private static final int TEST_INSTRUCTIONS_2 = 3696;
	private static final int CYB_REQ = 3697;
	
	// Mob
	private static final int BREKA_ORC_WARRIOR = 20271;
	private static final int WINDSUS = 20553;
	private static final int TARLK_BUGBEAR_WARRIOR = 20571;
	private static final int GREMLIN_FILCHER = 27149;
	
	private static final int[] LIZARDMAN =
	{
		20578,
		20579,
		20581,
		20582,
		20641,
		20642,
		20643
	};
	
	// @formatter:off
	// Droplist - Format: Npc Id : {itemId, itemAmount, chance}
	private static final Map<Integer, int[]> LEVEL_1 = new HashMap<>();
	{
		LEVEL_1.put(20550, new int[] {3709, 40, 75}); // Gaurdian Basilisk
		LEVEL_1.put(20581, new int[] {3710, 20, 50}); // Leto Lizardman Shaman
		LEVEL_1.put(27140, new int[] {3711, 1, 100}); // Breka Overlord Haka
		LEVEL_1.put(27141, new int[] {3712, 1, 100}); // Breka Overlord Jaka
		LEVEL_1.put(27142, new int[] {3713, 1, 100}); // Breka Overlord Marka
		LEVEL_1.put(27143, new int[] {3714, 1, 100}); // Windsus Aleph
		LEVEL_1.put(20563, new int[] {3715, 20, 50}); // Manashen Gargoyle
		LEVEL_1.put(20565, new int[] {3715, 20, 50}); // Enchanted Stone Golemn
		LEVEL_1.put(20555, new int[] {3716, 30, 70}); // Giant Fungus
	}
	
	// Droplist - Format: Npc Id : {itemId, itemAmount, chance}
	private static final Map<Integer, int[]> LEVEL_2 = new HashMap<>();
	{
		LEVEL_2.put(20586, new int[] {3717, 20, 50}); // Timak Orc Warrior
		LEVEL_2.put(20560, new int[] {3718, 20, 50}); // Trisalim Spider
		LEVEL_2.put(20561, new int[] {3718, 20, 50}); // Trisalim Tarantula
		LEVEL_2.put(20591, new int[] {3719, 30, 100}); // Valley Treant
		LEVEL_2.put(20597, new int[] {3719, 30, 100}); // Valley Treant Elder
		LEVEL_2.put(20675, new int[] {3720, 20, 50}); // Tairim
		LEVEL_2.put(20660, new int[] {3721, 20, 50}); // Archer of Greed
		LEVEL_2.put(27144, new int[] {3722, 1, 100}); // Tarlk Raider Athu
		LEVEL_2.put(27145, new int[] {3723, 1, 100}); // Tarlk Raider Lanka
		LEVEL_2.put(27146, new int[] {3724, 1, 100}); // Tarlk Raider Triska
		LEVEL_2.put(27147, new int[] {3725, 1, 100}); // Tarlk Raider Motura
		LEVEL_2.put(27148, new int[] {3726, 1, 100}); // Tarlk Raider Kalath
	}
	
	// Droplist - Format: Npc Id : {itemRequired, itemGive, itemToGiveAmount, itemAmount, chance}
	private static final Map<Integer, int[]> TOR_REQUESTS_1 = new HashMap<>();
	{
		TOR_REQUESTS_1.put(20578, new int[] {3727, 3769, 1, 40, 80}); // Leto Lizardman Archer
		TOR_REQUESTS_1.put(20579, new int[] {3727, 3769, 1, 40, 83}); // Leto Lizardman Soldier
		TOR_REQUESTS_1.put(20586, new int[] {3728, 3770, 1, 50, 89}); // Timak Orc Warrior
		TOR_REQUESTS_1.put(20588, new int[] {3728, 3770, 1, 50, 100}); // Timak Orc Overlord
		TOR_REQUESTS_1.put(20565, new int[] {3729, 3771, 1, 50, 100}); // Enchanted Stone Golem
		TOR_REQUESTS_1.put(20556, new int[] {3730, 3772, 1, 30, 50}); // Giant Monster Eye
		TOR_REQUESTS_1.put(20557, new int[] {3731, 3773, 1, 40, 80}); // Dire Wyrm
		TOR_REQUESTS_1.put(20550, new int[] {3732, 3774, Rnd.get(2) + 1, 100, 100}); // Guardian Basilisk
		TOR_REQUESTS_1.put(20552, new int[] {3733, 3775, 1, 50, 100}); // Fettered Soul
		TOR_REQUESTS_1.put(20553, new int[] {3734, 3776, 1, 30, 50}); // Windsus
		TOR_REQUESTS_1.put(20554, new int[] {3735, 3777, 2, 100, 100}); // Grandis
		TOR_REQUESTS_1.put(20631, new int[] {3736, 3778, 1, 50, 100}); // Taik Orc Archer
		TOR_REQUESTS_1.put(20632, new int[] {3736, 3778, 1, 50, 93}); // Taik Orc Warrior
		TOR_REQUESTS_1.put(20600, new int[] {3737, 3779, 1, 30, 50}); // Karul Bugbear
		TOR_REQUESTS_1.put(20601, new int[] {3738, 3780, 1, 40, 62}); // Tamlin Orc
		TOR_REQUESTS_1.put(20602, new int[] {3738, 3780, 1, 40, 80}); // Tamlin Orc Archer
		TOR_REQUESTS_1.put(27157, new int[] {3739, 3781, 1, 1, 100}); // Leto Chief Narak
		TOR_REQUESTS_1.put(20567, new int[] {3740, 3782, 1, 50, 50}); // Enchanted Gargoyle
		TOR_REQUESTS_1.put(20269, new int[] {3741, 3783, 1, 50, 93}); // Breka Orc Shaman
		TOR_REQUESTS_1.put(20271, new int[] {3741, 3783, 1, 50, 100}); // Breka Orc Warrior
		TOR_REQUESTS_1.put(27156, new int[] {3742, 3784, 1, 1, 100}); // Leto Shaman Ketz
		TOR_REQUESTS_1.put(27158, new int[] {3743, 3785, 1, 1, 100}); // Timak Raider Kaikee
		TOR_REQUESTS_1.put(20603, new int[] {3744, 3786, 1, 30, 50}); // Kronbe Spider
		TOR_REQUESTS_1.put(27160, new int[] {3746, 3788, 1, 1, 100}); // Gok Magok
		TOR_REQUESTS_1.put(27164, new int[] {3747, 3789, 1, 1, 100}); // Karul Chief Orooto
	}
	
	// Droplist - Format: Npc Id : {itemRequired, itemGive, itemToGiveAmount, itemAmount, chance}
	private static final Map<Integer, int[]> TOR_REQUESTS_2 = new HashMap<>();
	{
		TOR_REQUESTS_2.put(20560, new int[] {3749, 3791, 40, 66}); // Trisalim Spider
		TOR_REQUESTS_2.put(20561, new int[] {3749, 3791, 40, 75}); // Trisalim Tarantula
		TOR_REQUESTS_2.put(20633, new int[] {3750, 3792, 50, 53}); // Taik Orc Shaman
		TOR_REQUESTS_2.put(20634, new int[] {3750, 3792, 50, 99}); // Taik Orc Captain
		TOR_REQUESTS_2.put(20641, new int[] {3751, 3793, 40, 88}); // Harit Lizardman Grunt
		TOR_REQUESTS_2.put(20642, new int[] {3751, 3793, 40, 88}); // Harit Lizardman Archer
		TOR_REQUESTS_2.put(20643, new int[] {3751, 3793, 40, 91}); // Harit Lizardman Warrior
		TOR_REQUESTS_2.put(20661, new int[] {3752, 3794, 20, 50}); // Hatar Ratman Thief
		TOR_REQUESTS_2.put(20662, new int[] {3752, 3794, 20, 52}); // Hatar Ratman Boss
		TOR_REQUESTS_2.put(20667, new int[] {3753, 3795, 30, 90}); // Farcran
		TOR_REQUESTS_2.put(20589, new int[] {3754, 3796, 40, 49}); // Fline
		TOR_REQUESTS_2.put(20590, new int[] {3755, 3797, 40, 51}); // Liele
		TOR_REQUESTS_2.put(20592, new int[] {3756, 3798, 40, 80}); // Satyr
		TOR_REQUESTS_2.put(20598, new int[] {3756, 3798, 40, 100}); // Satyr Elder
		TOR_REQUESTS_2.put(20682, new int[] {3758, 3800, 30, 70}); // Vanor Silenos Grunt
		TOR_REQUESTS_2.put(20683, new int[] {3758, 3800, 30, 85}); // Vanor Silenos Scout
		TOR_REQUESTS_2.put(20684, new int[] {3758, 3800, 30, 90}); // Vanor Silenos Warrior
		TOR_REQUESTS_2.put(20571, new int[] {3759, 3801, 30, 63}); // Tarlk Bugbear Warrior
		TOR_REQUESTS_2.put(27159, new int[] {3760, 3802, 1, 100}); // Timak Overlord Okun
		TOR_REQUESTS_2.put(27161, new int[] {3761, 3803, 1, 100}); // Taik Overlord Kakran
		TOR_REQUESTS_2.put(20639, new int[] {3762, 3804, 40, 86}); // Mirror
		TOR_REQUESTS_2.put(20664, new int[] {3763, 3805, 20, 77}); // Deprive
		TOR_REQUESTS_2.put(20593, new int[] {3764, 3806, 20, 68}); // Unicorn
		TOR_REQUESTS_2.put(20599, new int[] {3764, 3806, 20, 86}); // Unicorn Elder
		TOR_REQUESTS_2.put(27163, new int[] {3765, 3807, 1, 100}); // Vanor Elder Kerunos
		TOR_REQUESTS_2.put(20659, new int[] {3766, 3808, 20, 73}); // Grave Wanderer
		TOR_REQUESTS_2.put(27162, new int[] {3767, 3809, 1, 100}); // Hatar Chieftain Kubel
		TOR_REQUESTS_2.put(20676, new int[] {3768, 3810, 10, 64}); // Judge of Marsh
	}
	
	// SpawnList - Format: Npc Id : {item1, item2, npcToSpawn}
	private static final Map<Integer, int[]> TOR_REQUESTS_SPAWN = new HashMap<>();
	{
		// Level 1
		TOR_REQUESTS_SPAWN.put(20582, new int[] {3739, 3781, 27157}); // Leto Lizardman Overlord
		TOR_REQUESTS_SPAWN.put(20581, new int[] {3742, 3784, 27156}); // Leto Lizardman Shaman
		TOR_REQUESTS_SPAWN.put(20586, new int[] {3743, 3785, 27158}); // Timak Orc Warrior
		TOR_REQUESTS_SPAWN.put(20554, new int[] {3746, 3788, 27160}); // Grandis
		
		// Level 2
		TOR_REQUESTS_SPAWN.put(20588, new int[] {3760, 3802, 27159}); // Timak Orc Overlord
		TOR_REQUESTS_SPAWN.put(20634, new int[] {3761, 3803, 27161}); // Tiak Orc Captain
		TOR_REQUESTS_SPAWN.put(20686, new int[] {3765, 3807, 27163}); // Vanor Silenos Chieftan
		TOR_REQUESTS_SPAWN.put(20662, new int[] {3767, 3809, 27162}); // Hatar Ratman Boss
	}
	
	// Filcher DropList - Format: reqId : {item, amount, bonus}
	private static final Map<Integer, int[]> FLICHER = new HashMap<>();
	{
		FLICHER.put(3752, new int[] {3794, 20, 3});
		FLICHER.put(3754, new int[] {3796, 40, 5});
		FLICHER.put(3755, new int[] {3797, 40, 5});
		FLICHER.put(3762, new int[] {3804, 40, 5});
	}
	
	// Drop List #1 - Format: Npc Id, {Item Id, Item Amount, Chance}
	private static final Map<Integer, int[]> DROPLIST_1 = new HashMap<>();
	{
		DROPLIST_1.put(20550, new int[] {3709, 40, 75}); // Gaurdian Basilisk
		DROPLIST_1.put(20581, new int[] {3710, 20, 50}); // Leto Lizardman Shaman
		DROPLIST_1.put(27140, new int[] {3711, 1, 100}); // Breka Overlord Haka
		DROPLIST_1.put(27141, new int[] {3712, 1, 100}); // Breka Overlord Jaka
		DROPLIST_1.put(27142, new int[] {3713, 1, 100}); // Breka Overlord Marka
		DROPLIST_1.put(27143, new int[] {3714, 1, 100}); // Windsus Aleph
		DROPLIST_1.put(20563, new int[] {3715, 20, 50}); // Manashen Gargoyle
		DROPLIST_1.put(20565, new int[] {3715, 20, 50}); // Enchanted Stone Golemn
		DROPLIST_1.put(20555, new int[] {3716, 30, 70}); // Giant Fungus
	}
	
	// Drop List #2 - Format: Npc Id, {Item Id, Item Amount, Chance}
	private static final Map<Integer, int[]> DROPLIST_2 = new HashMap<>();
	{
		DROPLIST_2.put(20586, new int[] {3717, 20, 50}); // Timak Orc Warrior
		DROPLIST_2.put(20560, new int[] {3718, 20, 50}); // Trisalim Spider
		DROPLIST_2.put(20561, new int[] {3718, 20, 50}); // Trisalim Tarantula
		DROPLIST_2.put(20591, new int[] {3719, 30, 100}); // Valley Treant
		DROPLIST_2.put(20597, new int[] {3719, 30, 100}); // Valley Treant Elder
		DROPLIST_2.put(20675, new int[] {3720, 20, 50}); // Tairim
		DROPLIST_2.put(20660, new int[] {3721, 20, 50}); // Archer of Greed
		DROPLIST_2.put(27144, new int[] {3722, 1, 100}); // Tarlk Raider Athu
		DROPLIST_2.put(27145, new int[] {3723, 1, 100}); // Tarlk Raider Lanka
		DROPLIST_2.put(27146, new int[] {3724, 1, 100}); // Tarlk Raider Triska
		DROPLIST_2.put(27147, new int[] {3725, 1, 100}); // Tarlk Raider Motura
		DROPLIST_2.put(27148, new int[] {3726, 1, 100}); // Tarlk Raider Kalath
	}
	
	private static final int[][][] GREY_ADVANCE = 
	{
		{{3709}, {40}}, // Level 1
		{{3710}, {20}},
		{{3711, 3712, 3713}, {1}},
		{{3714}, {1}},
		{{3715}, {20}},
		{{3716}, {30}},
		{{3717}, {20}}, // Level 2
		{{3718}, {20}},
		{{3719}, {30}},
		{{3720}, {20}},
		{{3721}, {20}},
		{{3722, 3723, 3724, 3725, 3726}, {1}}
	};
	
	// Rewards List #1 - Format: Request Id, {Item Id, Quantity, Reward Amount}
	private static final Map<Integer, int[]> TOR_REWARDS_1 = new HashMap<>();
	{
		TOR_REWARDS_1.put(3727, new int[] {3769, 40, 2090});
		TOR_REWARDS_1.put(3728, new int[] {3770, 50, 6340});
		TOR_REWARDS_1.put(3729, new int[] {3771, 50, 9480});
		TOR_REWARDS_1.put(3730, new int[] {3772, 30, 9110});
		TOR_REWARDS_1.put(3731, new int[] {3773, 40, 8690});
		TOR_REWARDS_1.put(3732, new int[] {3774, 100, 9480});
		TOR_REWARDS_1.put(3733, new int[] {3775, 50, 11280});
		TOR_REWARDS_1.put(3734, new int[] {3776, 30, 9640});
		TOR_REWARDS_1.put(3735, new int[] {3777, 100, 9180});
		TOR_REWARDS_1.put(3736, new int[] {3778, 50, 5160});
		TOR_REWARDS_1.put(3737, new int[] {3779, 30, 3140});
		TOR_REWARDS_1.put(3738, new int[] {3780, 40, 3160});
		TOR_REWARDS_1.put(3739, new int[] {3781, 1, 6370});
		TOR_REWARDS_1.put(3740, new int[] {3782, 50, 19080});
		TOR_REWARDS_1.put(3741, new int[] {3783, 50, 17730});
		TOR_REWARDS_1.put(3742, new int[] {3784, 1, 5790});
		TOR_REWARDS_1.put(3743, new int[] {3785, 1, 8560});
		TOR_REWARDS_1.put(3744, new int[] {3786, 30, 8320});
		TOR_REWARDS_1.put(3746, new int[] {3788, 1, 27540});
		TOR_REWARDS_1.put(3747, new int[] {3789, 1, 20560});
	}
	
	// Rewards List #2 - Format: Request Id, {Item Id, Quantity, Reward Amount}
	private static final Map<Integer, int[]> TOR_REWARDS_2 = new HashMap<>();
	{
		TOR_REWARDS_2.put(3749, new int[] {3791, 40, 7250});
		TOR_REWARDS_2.put(3750, new int[] {3792, 50, 7160});
		TOR_REWARDS_2.put(3751, new int[] {3793, 40, 6580});
		TOR_REWARDS_2.put(3752, new int[] {3794, 20, 10100});
		TOR_REWARDS_2.put(3753, new int[] {3795, 30, 13000});
		TOR_REWARDS_2.put(3754, new int[] {3796, 40, 7660});
		TOR_REWARDS_2.put(3755, new int[] {3797, 40, 7660});
		TOR_REWARDS_2.put(3756, new int[] {3798, 40, 11260});
		TOR_REWARDS_2.put(3758, new int[] {3800, 30, 8810});
		TOR_REWARDS_2.put(3759, new int[] {3801, 30, 7350});
		TOR_REWARDS_2.put(3760, new int[] {3802, 1, 8760});
		TOR_REWARDS_2.put(3761, new int[] {3803, 1, 9380});
		TOR_REWARDS_2.put(3762, new int[] {3804, 40, 17820});
		TOR_REWARDS_2.put(3763, new int[] {3805, 20, 17540});
		TOR_REWARDS_2.put(3764, new int[] {3806, 20, 14160});
		TOR_REWARDS_2.put(3765, new int[] {3807, 1, 15960});
		TOR_REWARDS_2.put(3766, new int[] {3808, 20, 39100});
		TOR_REWARDS_2.put(3767, new int[] {3809, 1, 39550});
		TOR_REWARDS_2.put(3768, new int[] {3810, 10, 41200});
	}
	// @formatter:on
	
	// Format: Item Id, Adena Amount
	private static final Map<Integer, Integer> CYB_REWARDS = new HashMap<>();
	{
		CYB_REWARDS.put(3699, 3400);
		CYB_REWARDS.put(3700, 6800);
		CYB_REWARDS.put(3701, 13600);
		CYB_REWARDS.put(3702, 37200);
		CYB_REWARDS.put(3703, 54400);
		CYB_REWARDS.put(3704, 108800);
		CYB_REWARDS.put(3705, 217600);
		CYB_REWARDS.put(3706, 435200);
		CYB_REWARDS.put(3707, 870400);
	}
	
	private static final String[] TOR_MENU =
	{
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3727\">C: Obtain 40 charms of Kadesh</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3728\">C: Collect 50 Timak Jade Necklaces</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3729\">C: Gather 50 Enchanted Golem Shards</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3730\">C: Collect and bring back 30 pieces of Giant Monster Eye Meat</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3731\">C: Collect and bring back 40 Dire Wyrm Eggs</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3732\">C: Collect and bring back 100 guardian basilisk talons</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3733\">C: Collect and bring back 50 revenants chains</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3734\">C: Collect and bring back 30 Windsus Tusks</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3735\">C: Collect and bring back 100 Grandis Skulls</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3736\">C: Collect and bring back 50 Taik Obsidian Amulets</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3737\">C: Bring me 30 heads of karul bugbears</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3738\">C: Collect 40 Tamlin Ivory Charms</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3739\">B: Bring me the head of Elder Narak of the leto lizardmen</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3740\">B: Collect and bring back 50 Enchanted Gargoyle Horns</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3741\">B: Collect and bring back 50 Coiled Serpent Totems</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3742\">B: Bring me the totem of the Serpent Demon Kadesh</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3743\">B: Bring me the head of Kaikis</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3744\">B: Collect and bring back 30 Kronbe Venom Sacs</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3746\">A: Recover the precious stone tablet that was stolen from a Dwarven cargo wagon by grandis</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3747\">A: Recover the precious Book of Shunaiman</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3749\">C: Collect and bring back 40 Trisalim Venom Sacs</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3750\">C: Collect and bring back 50 Taik Orc Totems</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3751\">C: Collect and bring back 40 Harit Lizardman barbed necklaces</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3752\">C: Collect and bring back 20 coins of the old empire</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3753\">C: Kill 30 farcrans and bring back their skins</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3754\">C: Collect and bring back 40 Tempest Shards</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3755\">C: Collect and bring back 40 Tsunami Shards</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3756\">C: Collect and bring back 40 Satyr Manes</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3758\">C: Collect and bring back 30 Shillien Manes</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3759\">C: Collect and bring back 30 tarlk bugbear totems</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3760\">B: Bring me the head of Okun</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3761\">B: Bring me the head of Kakran</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3762\">B: Collect and bring back 40 narcissus soulstones</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3763\">B: Collect and bring back 20 Deprive Eyes</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3764\">B: Collect and bring back 20 horns of summon unicorn</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3765\">B: Bring me the golden mane of Kerunos</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3766\">A: Bring back 20 skulls of undead executed criminals</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3767\">A: Recover the stolen bust of the late King Travis</a><br>",
		"<a action=\"bypass -h Quest Q335_SongOfTheHunter 3768\">A: Recover 10 swords of Cadmus</a><br>"
	};
	
	private final List<Integer> NPCS = new ArrayList<>();
	
	public Q335_SongOfTheHunter()
	{
		super(335, "Song Of The Hunter");
		
		setItemsIds(CYB_DAGGER, LICENSE_1, LICENSE_2, LEAF_PIN, TEST_INSTRUCTIONS_1, TEST_INSTRUCTIONS_2, CYB_REQ);
		
		addStartNpc(GREY);
		addTalkId(GREY, TOR, CYBELLIN);
		
		addKill(LEVEL_1);
		addKill(LEVEL_2);
		addKill(TOR_REQUESTS_1);
		addKill(TOR_REQUESTS_2);
		addKill(TOR_REQUESTS_SPAWN);
		addKillId(GREMLIN_FILCHER);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("30744-03.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.giveItems(TEST_INSTRUCTIONS_1, 1);
			st.playSound(Sound.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30744-32.htm"))
		{
			if (st.getQuestItemsCount(LEAF_PIN) >= 20)
			{
				htmltext = "30744-33.htm";
				st.giveItems(57, 20000);
			}
			
			st.playSound(Sound.SOUND_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("30744-19.htm"))
		{
			if (!hasItem(st, 1))
			{
				htmltext = "30744-18.htm";
				st.giveItems(TEST_INSTRUCTIONS_2, 1);
			}
		}
		else if (event.equalsIgnoreCase("30745-03.htm"))
		{
			if (st.getQuestItemsCount(TEST_INSTRUCTIONS_2) > 0)
			{
				htmltext = "30745-04.htm";
			}
			else if (event.equals("Tor_list_1"))
			{
				if (st.getInt("hasTask") == 0)
				{
					htmltext = "<html><body>Guild Member Tor:<br>";
					
					int _pins = st.getQuestItemsCount(LEAF_PIN);
					int _reply_0 = Rnd.get(12);
					int _reply_1 = Rnd.get(12);
					int _reply_2 = Rnd.get(12);
					int _reply_3 = Rnd.get(12);
					int _reply_4 = Rnd.get(12);
					
					if (Rnd.get(100) < 20)
					{
						if ((_pins > 0) && (_pins < 4))
						{
							_reply_0 = Rnd.get(6) + 12;
							_reply_2 = Rnd.get(6);
							_reply_3 = Rnd.get(6) + 6;
						}
						else if (_pins >= 4)
						{
							_reply_0 = Rnd.get(6) + 6;
							
							if (Rnd.get(20) == 0)
							{
								_reply_1 = Rnd.get(2) + 18;
							}
							
							_reply_2 = Rnd.get(6);
							_reply_3 = Rnd.get(6) + 6;
						}
					}
					else if (_pins >= 4)
					{
						if (Rnd.get(20) == 0)
						{
							_reply_1 = Rnd.get(2) + 18;
						}
						
						_reply_2 = Rnd.get(6);
						_reply_3 = Rnd.get(6) + 6;
					}
					
					htmltext += TOR_MENU[_reply_0] + TOR_MENU[_reply_1] + TOR_MENU[_reply_2] + TOR_MENU[_reply_3] + TOR_MENU[_reply_4];
					htmltext += "</body></html>";
				}
			}
			else if (event.equals("Tor_list_2"))
			{
				if (st.getInt("hasTask") == 0)
				{
					htmltext = "<html><body>Guild Member Tor:<br>";
					
					int _pins = st.getQuestItemsCount(LEAF_PIN);
					int _reply_0 = Rnd.get(10);
					int _reply_1 = Rnd.get(10);
					int _reply_2 = Rnd.get(5);
					int _reply_3 = Rnd.get(5) + 5;
					int _reply_4 = Rnd.get(10);
					
					if (Rnd.get(100) < 20)
					{
						if ((_pins > 0) && (_pins < 4))
						{
							_reply_0 = Rnd.get(6) + 10;
						}
						else if (_pins >= 4)
						{
							_reply_0 = Rnd.get(6) + 10;
							
							if (Rnd.get(20) == 0)
							{
								_reply_1 = Rnd.get(3) + 16;
							}
						}
					}
					else if (_pins >= 4)
					{
						if (Rnd.get(20) == 0)
						{
							_reply_1 = Rnd.get(3) + 16;
						}
						
						htmltext += TOR_MENU[_reply_0 + 20] + TOR_MENU[_reply_1 + 20] + TOR_MENU[_reply_2 + 20] + TOR_MENU[_reply_3 + 20] + TOR_MENU[_reply_4 + 20];
						htmltext += "</body></html>";
					}
				}
			}
			else if (event.equalsIgnoreCase("30745-10.htm"))
			{
				st.takeItems(LEAF_PIN, 1);
				
				for (int i = 3727; i < 3811; i++)
				{
					st.takeItems(i, -1);
				}
				
				st.set("hasTask", "0");
			}
			else if (event.equalsIgnoreCase("30746-03.htm"))
			{
				if (st.getQuestItemsCount(CYB_REQ) == 0)
				{
					st.giveItems(CYB_REQ, 1);
				}
				
				if (st.getQuestItemsCount(3471) == 0)
				{
					st.giveItems(3471, 1);
				}
				
				if (st.getQuestItemsCount(3698) == 0)
				{
					st.giveItems(3698, 1);
				}
				
				st.takeItems(6708, -1);
			}
			else if (event.equalsIgnoreCase("30746-08.htm"))
			{
				for (int i : CYB_REWARDS.values())
				{
					if (st.getQuestItemsCount(i) > 0)
					{
						st.takeItems(i, -1);
						st.giveItems(57, CYB_REWARDS.get(i));
						break;
					}
				}
			}
			else if (event.equalsIgnoreCase("30746-12.htm"))
			{
				st.takeItems(3698, -1);
				st.takeItems(3697, -1);
				st.takeItems(3471, -1);
			}
			else if (MathUtil.isDigit(event))
			{
				int _item = Integer.parseInt(event);
				
				st.set("hasTask", "1");
				st.giveItems(_item, 1);
				
				_item = _item - 3712;
				
				htmltext = "30745-" + _item + ".htm";
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestState(qn);
		if (st == null)
		{
			return htmltext;
		}
		
		int _npc = npc.getNpcId();
		int _cond = st.getInt("cond");
		int _bracelet_1 = st.getQuestItemsCount(LICENSE_1);
		int _bracelet_2 = st.getQuestItemsCount(LICENSE_2);
		
		switch (st.getState())
		{
			case STATE_CREATED:
				if (player.getLevel() >= 35)
				{
					htmltext = "02";
				}
				else
				{
					htmltext = "01";
				}
				break;
			
			case STATE_STARTED:
				switch (_npc)
				{
					case GREY:
						if (_cond == 1)
						{
							if (hasItem(st, 3))
							{
								htmltext = "12";
								st.set("cond", "2");
								
								for (int i = 3709; i < 3717; i++)
								{
									st.takeItems(i, -1);
								}
								
								st.takeItems(TEST_INSTRUCTIONS_1, -1);
								st.giveItems(LICENSE_1, 1);
							}
							else
							{
								htmltext = "11";
							}
						}
						else if (_cond == 2)
						{
							int _instructions = st.getQuestItemsCount(TEST_INSTRUCTIONS_2);
							
							if ((player.getLevel() < 45) && (_bracelet_1 > 0))
							{
								htmltext = "13";
							}
							else if ((player.getLevel() >= 45) && (_bracelet_1 > 0) && (_instructions == 0))
							{
								htmltext = "16";
							}
							else if (_instructions > 0)
							{
								if (hasItem(st, 3))
								{
									htmltext = "28";
									st.set("cond", "3");
									
									for (int i = 3718; i < 3727; i++)
									{
										st.takeItems(i, -1);
									}
									
									st.takeItems(TEST_INSTRUCTIONS_2, -1);
									st.takeItems(LICENSE_1, -1);
									st.giveItems(LICENSE_2, 1);
								}
								else
								{
									htmltext = "27";
								}
							}
						}
						else if (_cond == 3)
						{
							htmltext = "29";
						}
						break;
					
					case TOR:
						if ((_bracelet_1 == 0) && (_bracelet_2 == 0))
						{
							htmltext = "01";
						}
						else if (_bracelet_1 > 0)
						{
							int _request = hasRequest(player, 1);
							
							if (st.getInt("hasTask") == 0)
							{
								if (player.getLevel() >= 45)
								{
									if (st.getQuestItemsCount(TEST_INSTRUCTIONS_2) > 0)
									{
										htmltext = "04";
									}
									else
									{
										htmltext = "05";
									}
								}
							}
							else if (_request > 0)
							{
								htmltext = "12";
								
								int _item = TOR_REWARDS_1.get(_request)[0];
								int _reward = TOR_REWARDS_1.get(_request)[2];
								
								st.set("hasTask", "0");
								st.takeItems(_request, -1);
								st.takeItems(_item, -1);
								st.giveItems(LEAF_PIN, 1);
								st.giveItems(57, _reward);
								st.playSound(Sound.SOUND_MIDDLE);
							}
							else
							{
								htmltext = "08";
							}
						}
						else if (_bracelet_2 > 0)
						{
							int _request = hasRequest(player, 2);
							
							if (st.getInt("hasTask") == 0)
							{
								htmltext = "06";
							}
							else if (_request > 0)
							{
								htmltext = "13";
								
								int _item = TOR_REWARDS_2.get(_request)[0];
								int _reward = TOR_REWARDS_2.get(_request)[2];
								
								st.set("hasTask", "0");
								st.takeItems(_request, -1);
								st.takeItems(_item, -1);
								st.giveItems(LEAF_PIN, 1);
								st.giveItems(57, _reward);
								st.playSound(Sound.SOUND_MIDDLE);
							}
							else
							{
								htmltext = "08";
							}
						}
						break;
					
					case CYBELLIN:
						if ((_bracelet_1 == 0) && (_bracelet_2 == 0))
						{
							htmltext = "01";
						}
						else if ((_bracelet_1 > 0) && (_bracelet_2 > 0))
						{
							if (st.getQuestItemsCount(CYB_REQ) == 0)
							{
								htmltext = "02";
							}
							else if (st.getQuestItemsCount(3698) > 0)
							{
								htmltext = "05";
							}
							else if (st.getQuestItemsCount(3707) > 0)
							{
								htmltext = "07";
								st.takeItems(3707, -1);
								st.giveItems(57, CYB_REWARDS.get(3707));
							}
							else if (st.getQuestItemsCount(3708) > 0)
							{
								htmltext = "11";
								st.takeItems(3708, -1);
							}
							else if ((st.getQuestItemsCount(3699) > 0) || (st.getQuestItemsCount(3700) > 0) || (st.getQuestItemsCount(3701) > 0) || (st.getQuestItemsCount(3702) > 0) || (st.getQuestItemsCount(3703) > 0) || (st.getQuestItemsCount(3704) > 0) || (st.getQuestItemsCount(3705) > 0) || (st.getQuestItemsCount(3706) > 0))
							{
								htmltext = "06";
							}
							else
							{
								htmltext = "10";
							}
						}
						break;
				}
				break;
		}
		
		if (MathUtil.isDigit(htmltext))
		{
			htmltext = _npc + "-" + htmltext + ".htm";
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		QuestState st = checkPlayerState(player, npc, STATE_STARTED);
		if (st == null)
		{
			return null;
		}
		
		int _npc = npc.getNpcId();
		int _cond = st.getInt("cond");
		int _rnd = Rnd.get(100);
		
		int _instructions_1 = st.getQuestItemsCount(TEST_INSTRUCTIONS_1);
		int _instructions_2 = st.getQuestItemsCount(TEST_INSTRUCTIONS_2);
		
		if ((_cond == 1) && (_instructions_1 == 1))
		{
			if (LEVEL_1.containsKey(_npc))
			{
				int _item = LEVEL_1.get(_npc)[0];
				int _amount = LEVEL_1.get(_npc)[1];
				int _chance = LEVEL_1.get(_npc)[2];
				
				if ((_rnd < _chance) && (st.getQuestItemsCount(_item) < _amount))
				{
					st.giveItems(_item, 1);
					
					if (st.getQuestItemsCount(_item) == _amount)
					{
						st.playSound(Sound.SOUND_MIDDLE);
					}
					else
					{
						st.playSound(Sound.SOUND_ITEMGET);
					}
				}
			}
			else if ((_npc == BREKA_ORC_WARRIOR) && (_rnd < 10))
			{
				if (st.getQuestItemsCount(3711) == 0)
				{
					addSpawn(27140, player, false, 300000, true); // Breka Overlord Haka
				}
				else if (st.getQuestItemsCount(3712) == 0)
				{
					addSpawn(27141, player, false, 300000, true); // Breka Overlord Jaka
				}
				else if (st.getQuestItemsCount(3713) == 0)
				{
					addSpawn(27142, player, false, 300000, true); // Breka Overlord Marka
				}
			}
			else if ((_npc == WINDSUS) && (st.getQuestItemsCount(3714) == 0) && (_rnd < 10))
			{
				addSpawn(27143, player, false, 300000, true); // Windsus Aleph
			}
		}
		else if (_cond == 2)
		{
			if (_instructions_2 == 1)
			{
				if (LEVEL_2.containsKey(_npc))
				{
					int _item = LEVEL_2.get(_npc)[0];
					int _amount = LEVEL_2.get(_npc)[1];
					int _chance = LEVEL_2.get(_npc)[2];
					
					if ((_rnd < _chance) && (st.getQuestItemsCount(_item) < _amount))
					{
						st.giveItems(_item, 1);
						
						if (st.getQuestItemsCount(_item) == _amount)
						{
							st.playSound(Sound.SOUND_MIDDLE);
						}
						else
						{
							st.playSound(Sound.SOUND_ITEMGET);
						}
					}
				}
				else if ((_npc == TARLK_BUGBEAR_WARRIOR) && (_rnd < 10))
				{
					if (st.getQuestItemsCount(3722) == 0)
					{
						addSpawn(27144, player, false, 300000, true); // Tarlk Raider Athu
					}
					else if (st.getQuestItemsCount(3723) == 0)
					{
						addSpawn(27145, player, false, 300000, true); // Tarlk Raider Lanka
					}
					else if (st.getQuestItemsCount(3724) == 0)
					{
						addSpawn(27146, player, false, 300000, true); // Tarlk Raider Triska
					}
					else if (st.getQuestItemsCount(3725) == 0)
					{
						addSpawn(27147, player, false, 300000, true); // Tarlk Raider Motura
					}
					else if (st.getQuestItemsCount(3726) == 0)
					{
						addSpawn(27148, player, false, 300000, true); // Tarlk Raider Kalath
					}
				}
				else if (TOR_REQUESTS_1.containsKey(_npc))
				{
					int _itemRequired = TOR_REQUESTS_1.get(_npc)[0];
					int _itemGive = TOR_REQUESTS_1.get(_npc)[1];
					int _itemToGiveAmount = TOR_REQUESTS_1.get(_npc)[2];
					int _itemAmount = TOR_REQUESTS_1.get(_npc)[3];
					int _chance = TOR_REQUESTS_1.get(_npc)[4];
					
					if ((_rnd < _chance) && (st.getQuestItemsCount(_itemRequired) > 0) && (st.getQuestItemsCount(_itemGive) < _itemAmount))
					{
						st.giveItems(_itemGive, _itemToGiveAmount);
						
						if (st.getQuestItemsCount(_itemGive) == _itemAmount)
						{
							st.playSound(Sound.SOUND_MIDDLE);
						}
						else
						{
							st.playSound(Sound.SOUND_ITEMGET);
						}
						
						if (Rnd.nextBoolean() && ((_npc >= 27160) && (_npc <= 27164)))
						{
							// FIXME: Maybe need add auto attack the player
							addSpawn(27150, player, false, 300000, true);
							addSpawn(27150, player, false, 300000, true);
							autoChat(npc, "We will destroy the legacy of the ancient empire!");
						}
					}
				}
			}
		}
		else if (_cond == 3)
		{
			if (TOR_REQUESTS_2.containsKey(_npc))
			{
				int _itemRequired = TOR_REQUESTS_2.get(_npc)[0];
				int _itemGive = TOR_REQUESTS_2.get(_npc)[1];
				int _itemAmount = TOR_REQUESTS_2.get(_npc)[2];
				int _chance = TOR_REQUESTS_1.get(_npc)[3];
				
				if ((st.getQuestItemsCount(_itemRequired) > 0) && (st.getQuestItemsCount(_itemGive) < _itemAmount))
				{
					if (_rnd < _chance)
					{
						st.giveItems(_itemGive, 1);
						
						if (st.getQuestItemsCount(_itemGive) == _itemAmount)
						{
							st.playSound(Sound.SOUND_MIDDLE);
						}
						else
						{
							st.playSound(Sound.SOUND_ITEMGET);
						}
						
						if (Rnd.nextBoolean() && (_npc == 27162))
						{
							// FIXME: Maybe need add auto attack the player
							addSpawn(27150, player, false, 300000, true);
							addSpawn(27150, player, false, 300000, true);
							autoChat(npc, "We will destroy the legacy of the ancient empire!");
						}
					}
					if ((_rnd > Rnd.get(20)) && ((_npc == 20661) || (_npc == 20662) || (_npc == 20589) || (_npc == 20590) || (_npc == 20639)))
					{
						// FIXME: Maybe need add auto attack the player
						addSpawn(GREMLIN_FILCHER, player, false, 300000, true);
						autoChat(npc, "Get out! The jewels are mine!");
					}
				}
				else if (_npc == GREMLIN_FILCHER)
				{
					int _request = 0;
					
					for (int[] i : FLICHER.values())
					{
						if (st.getQuestItemsCount(FLICHER.get(i)[0]) > 0)
						{
							_request = FLICHER.get(i)[0];
						}
					}
					
					if (_request > 0)
					{
						int _item = FLICHER.get(_request)[0];
						int _amount = FLICHER.get(_request)[1];
						int _bonus = FLICHER.get(_request)[2];
						
						if (st.getQuestItemsCount(_item) < _amount)
						{
							st.giveItems(_item, _bonus);
							
							if (st.getQuestItemsCount(_item) == _amount)
							{
								st.playSound(Sound.SOUND_MIDDLE);
							}
							else
							{
								st.playSound(Sound.SOUND_ITEMGET);
							}
							
							autoChat(npc, "What!");
						}
					}
				}
			}
		}
		else if ((_rnd < 10) && TOR_REQUESTS_SPAWN.containsKey(_npc))
		{
			int _item1 = TOR_REQUESTS_SPAWN.get(_npc)[0];
			int _item2 = TOR_REQUESTS_SPAWN.get(_npc)[1];
			int _npcId = TOR_REQUESTS_SPAWN.get(_npc)[2];
			
			if ((st.getQuestItemsCount(_item1) > 0) && (st.getQuestItemsCount(_item2) == 0))
			{
				// FIXME: Maybe need add auto attack the player
				addSpawn(_npcId, player, false, 300000, true);
			}
		}
		
		if (LIZARDMAN.equals(_npc) && (player.getActiveWeaponItem().getItemId() == CYB_DAGGER) && player.getActiveWeaponItem().isEquipable() && (st.getQuestItemsCount(CYB_REQ) > 0) && (st.getQuestItemsCount(3708) == 0))
		{
			if (Rnd.nextBoolean())
			{
				if ((_cond == 2) || (_cond == 3))
				{
					for (int i = 3698; i < 3707; i++)
					{
						if (st.getQuestItemsCount(i) > 0)
						{
							st.takeItems(i, -1);
							st.giveItems(i + 1, 1);
							
							if (i >= 3703)
							{
								st.playSound(Sound.SOUND_JACKPOT);
							}
							
							break;
						}
						
					}
				}
			}
			else
			{
				for (int i = 3698; i < 3707; i++)
				{
					st.takeItems(i, -1);
				}
				
				st.giveItems(3708, 1);
			}
		}
		
		return null;
	}
	
	public boolean hasItem(QuestState st, int check)
	{
		int _count = 0;
		
		for (int[][] i : GREY_ADVANCE)
		{
			for (int[] j : i)
			{
				int _count2 = 0;
				
				for (int k : j)
				{
					if (st.getQuestItemsCount(k) >= i[1][0])
					{
						break;
					}
					
					_count2 += 1;
				}
				
				if (_count2 == j.length)
				{
					_count += 1;
				}
			}
		}
		
		if (_count >= check)
		{
			return true;
		}
		
		return false;
	}
	
	public int hasRequest(Player player, int level)
	{
		QuestState st = player.getQuestState(qn);
		
		Map<Integer, int[]> _rewards = TOR_REWARDS_1;
		
		if (level == 2)
		{
			_rewards = TOR_REWARDS_2;
		}
		
		for (int i : _rewards.keySet())
		{
			if (st.getQuestItemsCount(i) > 0)
			{
				if (st.getQuestItemsCount(_rewards.get(i)[0]) >= _rewards.get(i)[1])
				{
					return i;
				}
			}
		}
		
		return 0;
	}
	
	public void addKill(Map<Integer, int[]> map)
	{
		for (int[] i : map.values())
		{
			if (!NPCS.contains(i[0]))
			{
				addKillId(i[0]);
				NPCS.add(i[0]);
			}
		}
	}
	
	public static void autoChat(Npc npc, String text)
	{
		npc.broadcastPacket(new CreatureSay(npc.getObjectId(), Say2.ALL, npc.getName(), text));
	}
}