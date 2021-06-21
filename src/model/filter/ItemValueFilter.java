package model.filter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.osbot.rs07.api.model.Player;
import util.Util;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ItemValueFilter implements PlayerFilter {

	@NonNull
	private final int minValueThreshold;

	@Override
	public List<Player> doFilter(List<Player> players) {
		for(Player player : players) {
			int totalValue = 0;
			int[] equipment = player.getDefinition().getAppearance();
			for (int j : equipment) {
				int itemId = j - 512;
				if (itemId > 0) {
					System.out.println("Player " + player.getName() + " is wearing Item id: " + itemId + " which is worth: " + Util.getPrice(String.valueOf(itemId)));
					totalValue += Util.getPrice(String.valueOf(itemId));
				}
			}

			System.out.println("Total value for player: " + player.getName() + "'s equipment is: " + totalValue);
		}
		return players.stream()
				.filter(player -> this.equipmentValueFor(player) >= minValueThreshold)
				.collect(Collectors.toList());
	}


	/**
	 * Determines the total value of all equipment a player is wearing (excluding ring slot)
	 * @param player Player the player to identify and valuate equipment for.
	 * @return Integer the total value of all equipment the player is wearing.
	 */
	private int equipmentValueFor(final Player player) {
		int totalValue = 0;
		int[] equipment = player.getDefinition().getAppearance();
		for (int j : equipment) {
			int itemId = j - 512;
			if (itemId > 0) {
				totalValue += Util.getPrice(String.valueOf(itemId));
			}
		}
		return totalValue;
	}
}
