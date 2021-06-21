package model.filter;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import model.Symbol;
import org.osbot.rs07.api.model.Player;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@RequiredArgsConstructor
public class CombatLevelFilter implements PlayerFilter {

	@NonNull
	private final Symbol symbol;

	@NonNull
	private final int lowerBound;

	private int upperBound;

	@Override
	public List<Player> doFilter(final List<Player> players) {
		if(upperBound != 0) {
			return players.stream()
					.filter(p -> p.getCombatLevel() > lowerBound && p.getCombatLevel() <= upperBound)
					.collect(Collectors.toList());
		}
		switch(symbol.name()) {
			case "LESS_THAN":
				return players.stream().filter(p -> p.getCombatLevel() < lowerBound).collect(Collectors.toList());
			case "EQUAL":
				return players.stream().filter(p -> p.getCombatLevel() == lowerBound).collect(Collectors.toList());
			case "GREATER_THAN":
				return players.stream().filter(p -> p.getCombatLevel() > lowerBound).collect(Collectors.toList());
			default:
				System.out.println("[WARN] Unknown symbol: " + symbol.name() + " returning unfiltered list of players.");
				return players;
		}
	}
}
