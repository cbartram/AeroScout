package model;

import lombok.Data;
import org.osbot.rs07.api.model.Player;

import java.util.Map;

@Data
public class PlayerDetails {

	private String name;
	private int combatLevel;
	private int skull;
	private int attackLevel = -1;
	private int strengthLevel = -1;
	private int defenseLevel = -1;
	private int rangedLevel = -1;
	private int magicLevel = -1;
	private int prayerLevel = -1;

	// TODO gear

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

		return details;
	}

}
