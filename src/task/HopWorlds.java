package task;

import lombok.Getter;
import model.Configuration;
import model.State;
import org.osbot.rs07.api.ui.World;
import org.osbot.rs07.script.MethodProvider;

import java.util.Comparator;

public class HopWorlds implements Task {
	private final MethodProvider ctx;
	private final Configuration configuration = Configuration.getInstance();
	private final State state = State.getInstance();

	@Getter
	private String status;

	public HopWorlds(final MethodProvider ctx, final String status) {
		this.ctx = ctx;
		this.status = status;
	}


	@Override
	public boolean activate() {
		return state.getCurrentState().equals("HOP_WORLDS");
	}

	@Override
	public void execute() {
		int nextWorld = nextWorldInOrder();
		final String status = "Hopping to world " + nextWorld;
		this.status = status;
		ctx.log(status);
		ctx.worlds.hop(nextWorld);
		state.setState("SCOUT_PLAYERS");
	}

	/**
	 * Determines the next world to hop to. This method will take a players preference specified in the GUI for
	 * weather or not to use members / pvp worlds.
	 * @return int the world number to hop to
	 */
	private int nextWorldInOrder() {
		final boolean usePvpWorlds = Boolean.parseBoolean(configuration.get("world.hopper.pvp"));
		final boolean useMembers = Boolean.parseBoolean(configuration.get("world.hopper.members"));
		return this.ctx.getWorlds().getAvailableWorlds(true)
				.stream()
				.filter(world -> world.isPvpWorld() == usePvpWorlds &&
						world.isMembers() == useMembers && !world.getActivity().contains("skill") &&
						!world.getActivity().contains("Deadman") && world.getId() > this.ctx.getWorlds().getCurrentWorld())
				.min(Comparator.comparingInt(World::getId))
				.map(World::getId)
				.orElse(useMembers ? 302 : 301);
	}
}
