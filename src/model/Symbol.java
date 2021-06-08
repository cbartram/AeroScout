package model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;

@AllArgsConstructor
public enum Symbol {
	LESS_THAN("Less Than"),
	GREATER_THAN("Greater Than"),
	EQUAL("Equal To"),
	BETWEEN("Between");

	@Getter
	private final String text;

	public static Symbol of(String text) {
		switch(text.toUpperCase(Locale.ROOT)) {
			case "LESS THAN":
				return Symbol.LESS_THAN;
			case "GREATER THAN":
				return Symbol.GREATER_THAN;
			case "EQUAL TO":
				return Symbol.EQUAL;
			case "BETWEEN":
				return Symbol.BETWEEN;
			default:
				System.out.println("[WARN] Unknown Symbol value given as input: " + text + ". Defaulting to Symbol.GREATER_THAN");
				return Symbol.GREATER_THAN;
		}
	}
}