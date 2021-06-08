package model.validation;

import lombok.Getter;
import ui.GUI;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ValueFilterValidator implements Validation {
	private final Map<String, Integer> validDenominations = new HashMap<>();

	public ValueFilterValidator() {
		validDenominations.put("K", 1000);
		validDenominations.put("M", 1000000);
		validDenominations.put("B", 1000000000);
	}

	@Getter
	public final String errorMessage = "Value must be a number but can include denominations like: M (million), B (billion), or K (thousand).\n Example: 100M, 10K, 3B, or 500";

	@Override
	public boolean validate(final GUI gui) {
		final String rawValue = gui.getItemValueFilterTextField().getText();
		if(rawValue.length() == 0) return true;
		if(rawValue.length() >= 2) {
			// Check the last char for M, B or K
			final String denomination = rawValue.substring(rawValue.length() - 1);
			try {
				if (validDenominations.containsKey(denomination.toUpperCase(Locale.ROOT))) {
					// Multiply the parsed integer with the denomination
					Integer.parseInt(rawValue.substring(0, rawValue.length() - 1));
				} else {
					// There is no denomination assume the parsed integer is the value itself
					Integer.parseInt(rawValue);
				}
			} catch(NumberFormatException e) {
				System.err.println("NumberFormatException thrown while attempting to parse value " + rawValue + " from item value filter.");
				return false;
			}
			return true;
		}
		return false;
	}
}
