package model;

import lombok.Data;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.ui.Skill;

import java.util.Map;

@Data
public class PlayerDetails {

	private String name;
	private int combatLevel;
	private int skull;
	private int attackLevel;
	private int strengthLevel;
	private int defenseLevel;
	private int rangedLevel;
	private int magicLevel;
	private int prayerLevel;

	// TODO gear

	public static PlayerDetails of(Map<Skill, Stat> stats, Player player) {
		final PlayerDetails details = new PlayerDetails();
		details.setName(player.getName());
		details.setCombatLevel(player.getCombatLevel());
		details.setSkull(player.getSkullIcon());
		details.setAttackLevel(stats.get(Skill.ATTACK).getLevel());
		details.setStrengthLevel(stats.get(Skill.STRENGTH).getLevel());
		details.setDefenseLevel(stats.get(Skill.DEFENCE).getLevel());
		details.setRangedLevel(stats.get(Skill.RANGED).getLevel());
		details.setMagicLevel(stats.get(Skill.MAGIC).getLevel());
		details.setPrayerLevel(stats.get(Skill.PRAYER).getLevel());
		return details;
	}

}
