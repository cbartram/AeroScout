package task;

import model.PlayerDetails;
import model.Stat;
import org.osbot.rs07.api.Players;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.script.MethodProvider;
import service.DiscordService;
import ui.GUI;
import util.Sleep;
import util.Util;

import java.util.List;
import java.util.Map;

public class FindPlayers extends Task {
	private final MethodProvider ctx;
	private final GUI gui;
	private final DiscordService discordService = new DiscordService();

	public FindPlayers(final MethodProvider ctx, final String status, final GUI gui) {
		super(status);
		this.ctx = ctx;
		this.gui = gui;
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
			// TODO filter out current player by name
	//		.stream().filter(player -> player != null && !player.getName().equals(myPlayer().getName()))
	//				.collect(Collectors.toList());
			for(Player player : players) {
				Map<String, Stat> playerStats = Util.getStats(player.getName());
				PlayerDetails details = PlayerDetails.of(playerStats, player);
				details.setWorld(ctx.getWorlds().getCurrentWorld());
				ctx.log(details);

				// TODO Apply various filters from GUI

				discordService.postMessage(gui.getDiscordUrl(), details);
				Sleep.sleepFor(50); // We get rate limited by the Discord API this gives a little pause in between requests
			}
		} else {
			setStatus("No players found.");
		}
	}
}
