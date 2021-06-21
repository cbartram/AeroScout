package task;

import lombok.Getter;
import model.Configuration;
import model.PlayerDetails;
import model.Stat;
import model.filter.PlayerFilter;
import org.osbot.rs07.api.Players;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.script.MethodProvider;
import service.DiscordService;
import util.Util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FindPlayers implements Task {
	private final MethodProvider ctx;
	private final DiscordService discordService = new DiscordService();
	private final Configuration configuration;

	@Getter
	private String status;

	public FindPlayers(final MethodProvider ctx, final String status) {
		this.ctx = ctx;
		this.configuration = Configuration.getInstance();
		this.status = status;
	}

	@Override
	public boolean activate() {
		return true;
	}

	@Override
	public void execute() {
		Players p = ctx.getPlayers();

		if(p != null) {
			List<Player> players = p.getAll()
					.stream()
					.filter(player -> !player.getName().equals(ctx.myPlayer().getName()))
					.collect(Collectors.toList());

			// Apply filters collectively (not independently of the initial player list)
			for(PlayerFilter filter : configuration.getFilters()) {
				players = filter.doFilter(players);
			}

			for(Player player : players) {
				Map<String, Stat> playerStats = Util.getStats(player.getName());
				PlayerDetails details = PlayerDetails.of(playerStats, player);
				details.setWorld(ctx.getWorlds().getCurrentWorld());
				ctx.log(details);
				discordService.postMessage(details);
			}
		} else {
			this.status = "No players found.";
		}
	}
}
