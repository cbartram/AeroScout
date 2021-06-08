package model.validation;

import lombok.Getter;
import ui.GUI;

public class DiscordUrlValidator implements Validation {

	@Getter
	private final String errorMessage = "The URL entered is not a valid Discord Webhook URL.";

	@Override
	public boolean validate(final GUI gui) {
		final String url = gui.getDiscordUrlTextField().getText();
		final String validUrlHost = "https://discord.com/api/webhooks/";
		if(url.length() == 0 || url.length() < validUrlHost.length()) return false;
		return url.startsWith(validUrlHost);
	}
}
