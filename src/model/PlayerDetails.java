package model;

import lombok.Data;
import org.osbot.rs07.api.def.ItemDefinition;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.ui.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;

@Data
public class PlayerDetails {

	private String name;
	private int world;
	private int combatLevel;
	private int skull;
	private int attackLevel = -1;
	private int strengthLevel = -1;
	private int defenseLevel = -1;
	private int rangedLevel = -1;
	private int magicLevel = -1;
	private int hitpointsLevel = -1;
	private int prayerLevel = -1;
	private Map<EquipmentSlot, String> equipment;

	public static PlayerDetails of(final Map<String, Stat> stats, final Player player) {
		final PlayerDetails details = new PlayerDetails();
		details.setName(player.getName());
		details.setCombatLevel(player.getCombatLevel());
		details.setSkull(player.getSkullIcon());

		if(stats.containsKey("Attack")) {
			details.setAttackLevel(stats.get("Attack").getLevel());
		}

		if(stats.containsKey("Strength")) {
			details.setStrengthLevel(stats.get("Strength").getLevel());
		}

		if(stats.containsKey("Defence")) {
			details.setDefenseLevel(stats.get("Defence").getLevel());
		}

		if(stats.containsKey("Ranged")) {
			details.setRangedLevel(stats.get("Ranged").getLevel());
		}

		if(stats.containsKey("Magic")) {
			details.setMagicLevel(stats.get("Magic").getLevel());
		}

		if(stats.containsKey("Prayer")) {
			details.setPrayerLevel(stats.get("Prayer").getLevel());
		}

		if(stats.containsKey("Hitpoints")) {
			details.setAttackLevel(stats.get("Hitpoints").getLevel());
		}
		details.setEquipment(identifyEquipment(player));
		return details;
	}

	/**
	 * Identifies a players equipment and stores it within a Map accessible by the equipment slot.
	 * @param p Player The player to identify the equipment for
	 * @return Map of players equipment. Keys will be the specific equipment enum from the class EquipmentSlot and values
	 * are the String name of the equipment
	 */
	private static Map<EquipmentSlot, String> identifyEquipment(final Player p) {
		Map<EquipmentSlot, String> equipmentList = new HashMap<>();
		if (p != null) {
			int[] equipment = p.getDefinition().getAppearance();
			for (int i = 0; i < equipment.length; i++) {
				if(equipment[i] - 512 > 0) {
					switch(i) {
						case 0:
							equipmentList.put(EquipmentSlot.HAT, ItemDefinition.forId(equipment[i] - 512).getName());
							break;
						case 1:
							equipmentList.put(EquipmentSlot.CAPE, ItemDefinition.forId(equipment[i] - 512).getName());
							break;
						case 2:
							equipmentList.put(EquipmentSlot.AMULET, ItemDefinition.forId(equipment[i] - 512).getName());
							break;
						case 3:
							equipmentList.put(EquipmentSlot.WEAPON, ItemDefinition.forId(equipment[i] - 512).getName());
							break;
						case 4:
							equipmentList.put(EquipmentSlot.CHEST, ItemDefinition.forId(equipment[i] - 512).getName());
							break;
						case 5:
							equipmentList.put(EquipmentSlot.SHIELD, ItemDefinition.forId(equipment[i] - 512).getName());
							break;
						case 7:
							equipmentList.put(EquipmentSlot.LEGS, ItemDefinition.forId(equipment[i] - 512).getName());
							break;
						case 9:
							equipmentList.put(EquipmentSlot.HANDS, ItemDefinition.forId(equipment[i] - 512).getName());
							break;
						case 10:
							equipmentList.put(EquipmentSlot.FEET, ItemDefinition.forId(equipment[i] - 512).getName());
							break;
					}
				}
			}
		}
		return equipmentList;
	}
}
