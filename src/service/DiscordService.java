package service;

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
	 * @param  discordUrl String the discord webhook URL to post messages to.
	 * @param playerDetails PlayerDetails A player details instance containing information about player levels,
	 *                      gear, combat, and skull status.
	 */
	public void postMessage(final String discordUrl, final PlayerDetails playerDetails) {
		final String formattedPlayerDetails = String.format("W-%d --- %s (%d) --- Skull: %d --- Lvls A: %d D: %d S: %d H: %d R: %d M: %d P: %s --- Gear Head: %s Neck: %s Cape: %s Weapon: %s Chest: %s Shield: %s Legs: %s Gloves: %s Boots: %s ",
				playerDetails.getWorld(),
				playerDetails.getName(),
				playerDetails.getCombatLevel(),
				playerDetails.getSkull(),
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
		final String jsonPayload = "{ \"content\": \"" + formattedPlayerDetails + "\"}";
		try {
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
			StringBuffer response = new StringBuffer();

			while ((output = in.readLine()) != null) {
				response.append(output);
			}

			in.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
