package task;

import lombok.Getter;
import model.Configuration;
import model.PlayerDetails;
import model.Stat;
import model.State;
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
	private final Configuration configuration = Configuration.getInstance();
	private final State state = State.getInstance();

	@Getter
	private String status;

	public FindPlayers(final MethodProvider ctx, final String status) {
		this.ctx = ctx;
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

			ctx.log("Found " + players.size() + " players before any filtering.");

			// Apply filters collectively (not independently of the initial player list)
			// TODO perhaps in the future their should be a GUI radio button which allows users to define how
			// collective filters operate. i.e should each filter be applied to the master list of players or
			// should each filter be applied to a subset of players returned from the previous filter.
			for(PlayerFilter filter : configuration.getFilters()) {
				players = filter.doFilter(players);
				ctx.log("There are " + players.size() + " players after filter: " + filter.getClass().getName());
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
		state.setState("HOP_WORLDS");
	}
}
