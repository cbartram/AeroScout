package task;

import model.PlayerDetails;
import model.Stat;
import org.osbot.rs07.api.Players;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.script.MethodProvider;
import util.Util;

import java.util.List;
import java.util.Map;

public class FindPlayers extends Task {
	private final MethodProvider ctx;

	public FindPlayers(final MethodProvider ctx, final String status) {
		super(status);
		this.ctx = ctx;
	}

	@Override
	public boolean activate() {
		return true;
	}

	@Override
	public void execute() {
		Players p = ctx.getPlayers();

		if(p != null) {
			List<Player> players = p.getAll();
	//		.stream().filter(player -> player != null && !player.getName().equals(myPlayer().getName()))
	//				.collect(Collectors.toList());
			for(Player player : players) {
				Map<String, Stat> playerStats = Util.getStats(player.getName());
//				ctx.log("Found nearby player: " + player.getName() + " with stats: ");
//				for(String key : playerStats.keySet()) {
//					ctx.log(key + " - " + playerStats.get(key));
//				}
				PlayerDetails details = PlayerDetails.of(playerStats, player);
				ctx.log(details);
			}
		} else {
			setStatus("No players found.");
		}
	}
}
