package task;

import model.PlayerDetails;
import model.Stat;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.ui.Skill;
import util.Util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FindPlayers extends Task {

	public FindPlayers(final String status) {
		super(status);
	}

	@Override
	public boolean activate() {
		return true;
	}

	@Override
	public void execute() {
		List<Player> players = getPlayers().getAll().stream().filter(player -> !player.getName()
				.equals(myPlayer().getName()))
				.collect(Collectors.toList());
		for(Player p : players) {
			log("Found nearby player: " + p.getName());
			Map<Skill, Stat> playerStats = Util.getStats(p.getName());
			PlayerDetails details = PlayerDetails.of(playerStats, p);
			log(details);
		}
	}
}
