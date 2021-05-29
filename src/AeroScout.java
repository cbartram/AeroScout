import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import task.FindPlayers;
import task.Task;
import ui.GUI;
import util.Util;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ScriptManifest(author = "Aerodude",
		info = "Scouting script which locates players and identifies metadata about them.",
		logo = "",
		name = "AeroScout",
		version=1
)
public class AeroScout extends Script {

	public static void main(String[] args) {
		GUI gui = new GUI();
		gui.open();
	}

	private static final List<Task> tasks = new ArrayList<>();
	private static final GUI gui = new GUI();
	private String status = "Initializing Script";
	private long startTime;

	@Override
	public final void onStart() {
		startTime = System.currentTimeMillis();

		// Add all our tasks to the task list
		tasks.addAll(Collections.singletonList(
			new FindPlayers(this, "Locating Nearby Players...", gui)
		));

		try {
			SwingUtilities.invokeAndWait(() -> {
				gui.open();
				status = "Waiting for GUI Input";
			});
		} catch (InterruptedException | InvocationTargetException e) {
			e.printStackTrace();
			stop();
		}

		// Stop the script if the user never clicked the start button
		// but closed the GUI
		if (!gui.isStarted()) {
			stop();
		}
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
		stop(false);
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
