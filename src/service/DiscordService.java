package service;

import model.Configuration;
import model.PlayerDetails;
import org.osbot.rs07.api.ui.EquipmentSlot;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * DiscordService - Contains methods used to interface with the Discord HTTP API. This service is primarily respondible
 * for sending messages to specific Discord channels.
 */
public class DiscordService {

	/**
	 * Uses an Http POST request to relay player details to a discord server given the unique URL of the discord
	 * webhook.
	 * @param playerDetails PlayerDetails A player details instance containing information about player levels,
	 *                      gear, combat, and skull status.
	 */
	public void postMessage(final PlayerDetails playerDetails) {
		final String discordUrl = Configuration.getInstance().get("discord.url");
		final String formattedPlayerDetails = String.format("W-%d --- %s (%d) --- Skulled: %s --- Lvls Attack: %d Defence: %d Strength: %d Hitpoints: %d Range: %d Magic: %d Prayer: %s --- Gear Head: %s Neck: %s Cape: %s Weapon: %s Chest: %s Shield: %s Legs: %s Gloves: %s Boots: %s ",
				playerDetails.getWorld(),
				playerDetails.getName().replaceAll(" ", "_"),
				playerDetails.getCombatLevel(),
				playerDetails.getSkull() == -1 ? "No" : "Yes",
				playerDetails.getAttackLevel(),
				playerDetails.getDefenseLevel(),
				playerDetails.getStrengthLevel(),
				playerDetails.getHitpointsLevel(),
				playerDetails.getRangedLevel(),
				playerDetails.getMagicLevel(),
				playerDetails.getPrayerLevel(),
				playerDetails.getEquipment().getOrDefault(EquipmentSlot.HAT, "None"),
				playerDetails.getEquipment().getOrDefault(EquipmentSlot.AMULET, "None"),
				playerDetails.getEquipment().getOrDefault(EquipmentSlot.CAPE, "None"),
				playerDetails.getEquipment().getOrDefault(EquipmentSlot.WEAPON, "None"),
				playerDetails.getEquipment().getOrDefault(EquipmentSlot.CHEST, "None"),
				playerDetails.getEquipment().getOrDefault(EquipmentSlot.SHIELD, "None"),
				playerDetails.getEquipment().getOrDefault(EquipmentSlot.LEGS, "None"),
				playerDetails.getEquipment().getOrDefault(EquipmentSlot.HANDS, "None"),
				playerDetails.getEquipment().getOrDefault(EquipmentSlot.FEET, "None")
		);
		try {
			final String jsonPayload = "{ \"content\": \"" + formattedPlayerDetails + "\"}";
			HttpURLConnection con = (HttpURLConnection) new URL(discordUrl).openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
			con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestMethod("POST");

			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(jsonPayload);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			System.out.println("[DEBUG] POST to Discord Webhook URL: " + discordUrl + " with response code: " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String output;
			StringBuilder response = new StringBuilder();

			while ((output = in.readLine()) != null) {
				response.append(output);
			}

			System.out.println("[INFO] Http Response: " + response);
			in.close();
		} catch(IOException e) {
			System.err.println("[ERROR] IOException thrown while attempting to contact Discord Webhook URL. Message = " + e.getMessage() + " Player Details = " + playerDetails);
			e.printStackTrace();
		}
	}
}
