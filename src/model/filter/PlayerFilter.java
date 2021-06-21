package model.filter;

import org.osbot.rs07.api.model.Player;

import java.util.List;

public interface PlayerFilter {
	List<Player> doFilter(final List<Player> players);
}
