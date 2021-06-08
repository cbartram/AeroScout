package model.validation;

import ui.GUI;

public interface Validation {
	boolean validate(final GUI gui);
	String getErrorMessage();
}
