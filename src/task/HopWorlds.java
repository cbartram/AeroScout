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
	public boolean activate() throws InterruptedException {
		return state.getCurrentState().equals("HOP_WORLDS");
	}

	@Override
	public void execute() throws InterruptedException {
		int nextWorld = nextWorldInOrder(false);
		ctx.log("Hopping to world: " + nextWorld);
		ctx.worlds.hop(nextWorld);
		state.setState("SCOUT_PLAYERS");
	}

	/**
	 * Determines the next world to hop to.
	 * @param members Boolean true if members only worlds should be used and false if F2P worlds only should be used.
	 * @return int the world number to hop to
	 */
	private int nextWorldInOrder(final boolean members) {
		return this.ctx.getWorlds().getAvailableWorlds(true)
				.stream()
				.filter(world -> !world.isPvpWorld() && world.isMembers() == members && !world.getActivity().contains("skill") &&
						!world.getActivity().contains("Deadman") && world.getId() > this.ctx.getWorlds().getCurrentWorld())
				.min(Comparator.comparingInt(World::getId))
				.map(World::getId)
				.orElse(members ? 302 : 301);
	}
}
