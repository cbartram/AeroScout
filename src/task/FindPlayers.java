package task;

import model.PlayerDetails;
import model.Stat;
import model.Symbol;
import org.osbot.rs07.api.Players;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.script.MethodProvider;
import service.DiscordService;
import ui.Configuration;
import util.Util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FindPlayers extends Task {
	private final MethodProvider ctx;
	private final DiscordService discordService = new DiscordService();
	private final Configuration configuration;

	public FindPlayers(final MethodProvider ctx, final String status) {
		super(status);
		this.ctx = ctx;
		this.configuration = Configuration.getInstance();
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

			for(Player player : players) {
				Map<String, Stat> playerStats = Util.getStats(player.getName());
				PlayerDetails details = PlayerDetails.of(playerStats, player);
				details.setWorld(ctx.getWorlds().getCurrentWorld());
				ctx.log(details);

				// TODO Apply various filters from GUI
				final Symbol combatFilterSymbol = Symbol.of(configuration.get("filter.combat.symbol"));
//				final int[] combatFilterValue = GUI.getInstance().getCombatFilterValue();

				ctx.log("Combat filter symbol: " + combatFilterSymbol);
//				ctx.log("Combat filter value: " + Arrays.toString(combatFilterValue));

				discordService.postMessage(details);
			}
		} else {
			setStatus("No players found.");
		}
	}



}
