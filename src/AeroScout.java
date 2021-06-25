import model.Configuration;
import model.State;
import model.filter.PlayerFilter;
import org.osbot.core.api.Wrapper;
import org.osbot.rs07.Bot;
import org.osbot.rs07.accessor.XName;
import org.osbot.rs07.accessor.XNode;
import org.osbot.rs07.accessor.XNodeDequeI;
import org.osbot.rs07.accessor.XPlayer;
import org.osbot.rs07.accessor.XPlayerDefinition;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import task.FindPlayers;
import task.HopWorlds;
import task.Task;
import ui.GUI;
import util.Util;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ScriptManifest(author = "Aerodude",
		info = "Scouting script which locates players and identifies metadata about them.",
		logo = "",
		name = "AeroScout",
		version=1
)
public class AeroScout extends Script {
	private static final List<Task> tasks = new ArrayList<>();
	private static final GUI gui = GUI.getInstance();
	private static final Configuration configuration = Configuration.getInstance();
	private String status = "Initializing Script";
	private long startTime;

	public static void main(String[] args) {
		gui.pack();
		gui.setVisible(true);

		List<Player> players = Collections.singletonList(new Player(new XPlayer() {
			@Override
			public int getPrayerIcon() {
				return 0;
			}

			@Override
			public int getCombatLevel() {
				return 15;
			}

			@Override
			public int getTeam() {
				return 0;
			}

			@Override
			public XName getName() {
				return new XName() {
					@Override
					public String getOriginal() {
						return "ExamplePlayer";
					}

					@Override
					public String getFormatted() {
						return "Example Player";
					}

					@Override
					public Bot getBot() {
						return null;
					}
				};
			}

			@Override
			public int getSkullIcon() {
				return 0;
			}

			@Override
			public XPlayerDefinition getDefinition() {
				return new XPlayerDefinition() {
					@Override
					public int[] getAppearance() {
						return new int[0];
					}

					@Override
					public long getModelHash() {
						return 0;
					}
				};
			}

			@Override
			public int getAnimation2() {
				return 0;
			}

			@Override
			public int getAnimationStanding() {
				return 0;
			}

			@Override
			public int getHeight() {
				return 0;
			}

			@Override
			public int[] getWalkingQueueX() {
				return new int[0];
			}

			@Override
			public int[] getWalkingQueueY() {
				return new int[0];
			}

			@Override
			public int getGridY() {
				return 0;
			}

			@Override
			public int[] getSplatSecondaryDamage() {
				return new int[0];
			}

			@Override
			public String getHeadMessage() {
				return null;
			}

			@Override
			public int getAnimationSpot() {
				return 0;
			}

			@Override
			public int getCharacterFacingUid() {
				return 0;
			}

			@Override
			public int[] getSplatType() {
				return new int[0];
			}

			@Override
			public int[] getSplatDamage() {
				return new int[0];
			}

			@Override
			public int[] getSplatSecondaryType() {
				return new int[0];
			}

			@Override
			public int getRotation() {
				return 0;
			}

			@Override
			public int[] getSplatTime() {
				return new int[0];
			}

			@Override
			public int getGridX() {
				return 0;
			}

			@Override
			public int getWalkingQueueSize() {
				return 0;
			}

			@Override
			public int getAnimationDelay() {
				return 0;
			}

			@Override
			public XNodeDequeI getHitBars() {
				return null;
			}

			@Override
			public int getAnimation() {
				return 0;
			}

			@Override
			public int getModelHeight() {
				return 0;
			}

			@Override
			public void setWrapper(Wrapper wrapper) {

			}

			@Override
			public Wrapper getWrapper() {
				return null;
			}

			@Override
			public long getHash() {
				return 0;
			}

			@Override
			public XNode getPrevious() {
				return null;
			}

			@Override
			public XNode getNext() {
				return null;
			}

			@Override
			public Bot getBot() {
				return null;
			}
		}));

		for(PlayerFilter filter : configuration.getFilters()) {
			filter.doFilter(players);
		}

	}

	@Override
	public final void onStart() {
		startTime = System.currentTimeMillis();
		State.getInstance();
		Util.cacheItemPrices();
		try {
			SwingUtilities.invokeAndWait(() -> {
				gui.open();
				status = "Waiting for GUI Input";
			});
		} catch (InterruptedException | InvocationTargetException e) {
			e.printStackTrace();
			stop();
		}

		// Add all our tasks to the task list
		tasks.addAll(Arrays.asList(
			new FindPlayers(this, "Locating Nearby Players..."),
			new HopWorlds(this, "Hopping worlds...")
		));

	}

	@Override
	public int onLoop() throws InterruptedException {
		try {
			for (Task task : tasks) {
				if (task.activate()) {
					status = task.getStatus();
					task.execute();
				}
			}
		} catch(InterruptedException e) {
			log("There has been an error!");
			e.printStackTrace();
		}
		return random(150, 200);
	}


	@Override
	public final void onExit() throws InterruptedException {
		if(gui.isOpen()) gui.close();
		log("Closing Script...");
	}

	@Override
	public void onPaint(final Graphics2D g) {
		final long runTime = System.currentTimeMillis() - startTime;

		// Fill the background color rectangle
		g.setColor(new Color(120, 111, 100, 150));
		g.fillRect(0,0,200,150);
		g.setColor(Color.RED);

		// Create a border
		g.setColor(Color.CYAN);
		g.drawRect(0, 0, 200, 150);

		g.setFont(g.getFont().deriveFont(12.0f));

		// Draw the main text and space is out bc its blurry
		g.drawString("A e r o  S c o u t", 10, 20);

		g.setColor(Color.WHITE);
		g.drawString("S t a t u s: " + status, 10, 40);
		g.drawString("R u n t i m e: " + Util.formatTime(runTime), 10, 60);

		// Draw the mouse cursor
		Point pos = getMouse().getPosition();

		g.drawLine(pos.x - 5, pos.y + 5, pos.x + 5, pos.y - 5);
		g.drawLine(pos.x + 5, pos.y + 5, pos.x - 5, pos.y - 5);
	}

}
