package model;

import lombok.Getter;

public class State {
	private static volatile State instance;
	private static final Object mutex = new Object();

	@Getter
	private String currentState = "INITIALIZATION";


	public void setState(final String newState) {
		this.currentState = newState;
	}

	/**
	 * Configuration is implemented as a singleton instance which cannot be instantiated directly. This ensures all
	 * reads and writes to the properties file are performed on the same instance of the class (and subsequently the
	 * same instance of the Properties object). There should never be different properties that are written to disk
	 * from what is loaded in memory.
	 *
	 * i.e The script should always use an edit to a loaded property via the GUI rather than a possibly outdated property
	 * loaded from disk.
	 * @return Configuration instance.
	 */
	public static State getInstance() {
		State result = instance;
		if (result == null) {
			synchronized (mutex) {
				result = instance;
				if (result == null)
					instance = result = new State();
			}
		}
		return result;
	}
}
