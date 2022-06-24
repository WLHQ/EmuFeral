package org.asf.emuferal.discord.handlers.discord.interactions.buttons.auth2fa;

import org.asf.emuferal.discord.TimedActions;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.MessageReferenceData;
import reactor.core.publisher.Mono;

public class WhitelistHandler2fa {

	/**
	 * Confirm button event (2fa)
	 * 
	 * @param event   Button event
	 * @param gateway Discord client
	 * @return Result Mono object
	 */
	public static Mono<?> handle(String id, ButtonInteractionEvent event, GatewayDiscordClient gateway) {
		// Parse request
		String uid = id.split("/")[1];
		String action = id.split("/")[2];

		// Verify interaction owner
		String str = event.getInteraction().getUser().getId().asString();
		if (uid.equals(str)) {
			// Disable the buttons
			if (!event.getMessage().get().getData().messageReference().isAbsent()) {
				try {
					MessageReferenceData ref = event.getMessage().get().getData().messageReference().get();
					Message oMsg = gateway.getMessageById(Snowflake.of(ref.channelId().get().asString()),
							Snowflake.of(ref.messageId().get())).block();
					oMsg.delete().subscribe();
				} catch (Exception e) {
				}
			}
			event.getMessage().get().edit().withComponents().subscribe();

			// Run the action (or attempt to)
			if (TimedActions.runAction(action)) {
				// Send success
				return event.reply("Account login has been accepted.\n"
						+ "\nThe IP used to log into EmuFeral has been saved, further logins from this address will not be verified and login will be confirmed automatically.\n"
						+ "\nYou can remove whitelisted IPs via ingame commands.");
			} else {
				// Send failure
				return event.reply("Login request has expired.");
			}
		}

		// Default response
		return Mono.empty();
	}
}
