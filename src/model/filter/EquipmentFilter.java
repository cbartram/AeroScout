package model.filter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.osbot.rs07.api.def.ItemDefinition;
import org.osbot.rs07.api.model.Player;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EquipmentFilter implements PlayerFilter {

	@NonNull
	private final List<String> items;

	@Override
	public List<Player> doFilter(List<Player> players) {
		if(items.size() == 0)  return players;
		System.out.println("Filtering for players with at least 1 of the following items: " + items);
		return players.stream().filter(this::hasItem).collect(Collectors.toList());
	}

	/**
	 * Determines if a player is wearing a specific item identified in the items list through the GUI
	 * @param player Player the player entity to search for
	 * @return Boolean true if the player is wearing at least 1 item from the list and false otherwise.
	 */
	public boolean hasItem(final Player player) {
		int[] equipment = player.getDefinition().getAppearance();
		for (int j : equipment) {
			int itemId = j - 512;
			if (itemId > 0) {
				ItemDefinition item = ItemDefinition.forId(itemId);
				return items.contains(item.getName().toUpperCase(Locale.ROOT).replaceAll(" ", "_"));
			}
		}
		return false;
	}
}
