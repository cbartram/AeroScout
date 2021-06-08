package ui;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Configuration {

	private static final String FILE_PATH = System.getProperty("user.home")
			+ File.separator + "OSBot" + File.separator + "Data"
			+ File.separator + "AeroScout.properties";


	private static volatile Configuration instance;
	private static final Object mutex = new Object();

	@Getter
	private final Properties properties = new Properties();


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
	public static Configuration getInstance() {
		Configuration result = instance;
		if (result == null) {
			synchronized (mutex) {
				result = instance;
				if (result == null)
					instance = result = new Configuration();
					if(new File(FILE_PATH).exists()) {
						instance.load();
					}
			}
		}
		return result;
	}

	public String get(final String key) {
		return this.properties.containsKey(key) ? this.properties.getProperty(key) : "No property for key: " + key;
	}

	/**
	 * Loads properties from the AeroScout properties file into a java.util.Properties object.
	 */
	public void load() {
		try {
			System.out.println("[INFO] Loading Configuration file from: " + FILE_PATH);
			InputStream stream = new FileInputStream(FILE_PATH);
			this.properties.load(stream);
		} catch(IOException e) {
			System.err.println("[ERROR] IOException thrown while attempting to read configuration properties from: " + FILE_PATH);
			e.printStackTrace();
		}
	}

	/**
	 * Persists a set of AeroScout properties to disk so that subsequent runs can store profiles.
	 * @param gui GUI Instance to use to retrieve the property values to save.
	 */
	public void save(final GUI gui) {
		try {
			System.out.println("[INFO] Saving Configuration file to: " + FILE_PATH);
			OutputStream stream = new FileOutputStream(FILE_PATH);
			this.properties.setProperty("discord.url", gui.getDiscordUrlTextField().getText());
			this.properties.setProperty("filter.combat.symbol", (String) gui.getCombatFilterComboBox().getSelectedItem());
			this.properties.setProperty("filter.combat.text.simple", gui.getCombatFilterTextField().getText());
			this.properties.setProperty("filter.combat.text.between", gui.getCombatFilterBetweenTextField().getText());
//			this.properties.setProperty("filter.item", gui.getCombatFilterValue());
//			this.properties.setProperty("filter.equipment", gui.getCombatFilterValue());
			this.properties.store(stream, "");
		} catch (IOException e) {
			System.err.println("[ERROR] IOException thrown while attempting to write configuration properties to: " + FILE_PATH);
			e.printStackTrace();
		}
	}
}