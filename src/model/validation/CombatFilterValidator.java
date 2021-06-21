package model.validation;

import lombok.Getter;
import model.Symbol;
import ui.GUI;

public class CombatFilterValidator implements Validation {

	@Getter
	private String errorMessage = "The combat filter you entered is invalid. Combat filters must a single number be between 3 and 126.";

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

			if(Symbol.of((String) gui.getCombatFilterComboBox().getSelectedItem()) == Symbol.BETWEEN) {
				final int combatFilterBetweenValue = Integer.parseInt(gui.getCombatFilterBetweenTextField().getText());
				if (combatFilterBetweenValue < 3 || combatFilterBetweenValue > 126) {
					return false;
				}

				if(combatFilterValue >= combatFilterBetweenValue) {
					errorMessage = "The combat filters lower bound is greater than the upper bound. Specify a valid combat level range.";
					return false;
				}

			}

			if (combatFilterValue < 3 || combatFilterValue > 126) {
				return false;
			}

		} catch (NumberFormatException e) {
			System.err.println("NumberFormatException thrown while attempting to parse text from the combat filter text fields. Combat filter text = " + gui.getCombatFilterTextField().getText() + " Combat filter between field = " + gui.getCombatFilterBetweenTextField().getText());
			return false;
		}
		return true;
	}
}
