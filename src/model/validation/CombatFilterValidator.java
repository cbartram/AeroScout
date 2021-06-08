package model.validation;

import lombok.Getter;
import ui.GUI;

public class CombatFilterValidator implements Validation {

	@Getter
	private final String errorMessage = "The combat filter you entered is invalid. Combat filters must a single number be between 3 and 126.";

	/**
	 * Validates that the numerical data entered into the combat filter combo box is a valid Old
	 * School RuneScape combat level.
	 * @return Boolean true if the combat filter is valid and false otherwise
	 */
	@Override
	public boolean validate(final GUI gui) {
		if (gui.getCombatFilterTextField().getText().length() == 0 && gui.getCombatFilterBetweenTextField().getText().length() == 0)
			return true;
		try {
			final int combatFilterValue = Integer.parseInt(gui.getCombatFilterTextField().getText());
			final int combatFilterBetweenValue = Integer.parseInt(gui.getCombatFilterBetweenTextField().getText());

			if (combatFilterValue < 3 || combatFilterValue > 126) {
				return false;
			}

			if (combatFilterBetweenValue < 3 || combatFilterBetweenValue > 126) {
				return false;
			}
		} catch (NumberFormatException e) {
			System.err.println("NumberFormatException thrown while attempting to parse text from the combat filter text fields. Combat filter text = " + gui.getCombatFilterTextField().getText() + " Combat filter between field = " + gui.getCombatFilterBetweenTextField().getText());
			return false;
		}
		return true;
	}
}
