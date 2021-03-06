package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Stat;
import org.osbot.rs07.api.ui.Skill;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * util.Util.java
 * Basic Utility class which holds static methods
 * for things like time formatting, money formatting and
 * retrieving GE prices for specific items
 * Created by cbartram on 2019-08-13.
 * http://github.com/cbartram
 */
public class Util {

	private static final Map<String, Map<String, Stat>> playerStatsCache = new ConcurrentHashMap<>();
	private static final Map<String, Integer> validDenominations = new HashMap<>();
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final Map<String, Integer> itemPriceMap = new HashMap<>();

	static {
		validDenominations.put("K", 1000);
		validDenominations.put("M", 1000000);
		validDenominations.put("B", 1000000000);
	}

	public static int getPrice(final String itemId) {
		return itemPriceMap.getOrDefault(itemId, 0);
	}

	public static Map<String, Integer> cacheItemPrices() {
		try {
			URL url = new URL("https://prices.runescape.wiki/api/v1/osrs/latest");
			URLConnection con = url.openConnection();
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
			con.setUseCaches(true);
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			final StringBuilder rawData = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				rawData.append(line);
			}
			JsonNode node = mapper.readTree(rawData.toString());
			JsonNode parsedData = node.get("data");
			Map<String, Map<String, Integer>> data = mapper.treeToValue(parsedData, Map.class);
			for(String itemId : data.keySet()) {
				// Compute avg value of high and low price if they exist otherwise just take one or the other.
				Integer high = data.get(itemId).get("high");
				Integer low = data.get(itemId).get("low");
				if(high == null && low == null)  {
					continue;
				}

				if(high == null) {
					itemPriceMap.put(itemId, low);
					continue;
				}
				if(low == null) {
					itemPriceMap.put(itemId, high);
					continue;
				}

				int average = (high + low) / 2;
				itemPriceMap.put(itemId, average);
			}
			br.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return itemPriceMap;
	}

	public static Map<String, Stat> getStats(final String playerName) {

		if(playerStatsCache.containsKey(playerName)) {
			return playerStatsCache.get(playerName);
		}

		final Map<String, Stat> stats = new HashMap<>(24);

		try {
			URL url = new URL("https://secure.runescape.com/m=hiscore_oldschool/index_lite.ws?player=" + URLEncoder.encode(playerName, StandardCharsets.UTF_8.toString()));
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
			con.setUseCaches(true);

			if(con.getResponseCode() >= 400) {
				return stats;
			}

			try(InputStreamReader inputStreamReader = new InputStreamReader(con.getInputStream());
				BufferedReader br = new BufferedReader(inputStreamReader)) {
				String[] splitValues = br.readLine().split(",");
//				stats.put("Overall", new Stat(Integer.parseInt(splitValues[0]), Integer.parseInt(splitValues[1]), Integer.parseInt(splitValues[2])));

				for (final Skill skill : Skill.values()) {
					splitValues = br.readLine().split(",");
					stats.put(skill.toString(), new Stat(Integer.parseInt(splitValues[0]), Integer.parseInt(splitValues[1]), Integer.parseInt(splitValues[2])));
				}
			}

		} catch(final Exception e) {
			e.printStackTrace();
		}
		playerStatsCache.put(playerName, stats);
		return stats;
	}

	/**
	 * Converts a string value like 100M, 10k, 50B, or 1000 to its appropriate integer value. i.e 10k would be
	 * converted into 10,000 (without the comma).
	 * @param rawValue String the raw value which includes or does not include a denomination
	 * @return Integer the integer value represented by the denomination
	 */
	public static int parseDenominatedGold(final String rawValue) {
		if(rawValue.length() == 0 || rawValue.length() == 1) return 0;
		// Check the last char for M, B or K
		final String denomination = rawValue.substring(rawValue.length() - 1);
		int value;
		try {
			if (validDenominations.containsKey(denomination.toUpperCase(Locale.ROOT))) {
				// Multiply the parsed integer with the denomination
				value = Integer.parseInt(rawValue.substring(0, rawValue.length() - 1)) * validDenominations.get(denomination.toUpperCase(Locale.ROOT));
			} else {
				// There is no denomination assume the parsed integer is the value itself
				value = Integer.parseInt(rawValue);
			}
			return value;
		} catch(NumberFormatException e) {
			System.err.println("NumberFormatException thrown while attempting to parse value " + rawValue + " from item value filter.");
			return 0;
		}
	}
	

	/**
	 * Returns a random integer between the specified low and high integers
	 * @param low Integer lowest possible value returned from the random number generator
	 * @param high Integer highest possible value returned from the random number generator
	 * @return Integer between the specified range
	 */
	public static int rand(int low, int high) {
		Random r = new Random();
		return r.nextInt(high - low) + low;
	}

	/**
	 * Computes the total amount of gold gained from the script each
	 * hour that is has been run. Note: this also has the added convenience of
	 * formatting the value returned to be a string like 1k, 19k, 20m etc...
	 * @param totalGold Integer
	 * @param ms Long the time in milliseconds the script has been running
	 * @return String the amount of gold gained each hour pre-formatted
	 */
	public static String goldPerHour(int totalGold, long ms) {
		return formatValue(totalGold / ((ms / 1000) / 60) / 60);
	}

	/**
	 * Formats time in a human readable way hh:mm:ss
	 * @param ms long Time im milliseconds to format
	 * @return String the correctly formatted time in hh:mm:ss
	 */
	public static String formatTime(final long ms){
		long s = ms / 1000, m = s / 60, h = m / 60;
		s %= 60; m %= 60; h %= 24;
		return String.format("%02d:%02d:%02d", h, m, s);
	}

	/**
	 * Formats a monetary value from its raw long to a string like 1m 10m, 1k, 20k etc...
	 * @param l Long the input value given
	 * @return String the value returned after it has been formatted
	 */
	public static String formatValue(final long l) {
		return (l > 1_000_000) ? String.format("%.2fm", ((double) l / 1_000_000))
				: (l > 1000) ? String.format("%.1fk", ((double) l / 1000))
				: l + "";
	}
}
