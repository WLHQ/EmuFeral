package org.asf.emuferal.packets.xt.gameserver.interactions;

import java.io.IOException;

import org.asf.emuferal.data.XtReader;
import org.asf.emuferal.data.XtWriter;
import org.asf.emuferal.networking.smartfox.SmartfoxClient;
import org.asf.emuferal.packets.xt.IXtPacket;
import org.asf.emuferal.players.Player;

public class InteractionCancel implements IXtPacket<InteractionCancel> {

	private String target;

	@Override
	public InteractionCancel instantiate() {
		return new InteractionCancel();
	}

	@Override
	public String id() {
		return "oac";
	}

	@Override
	public void parse(XtReader reader) throws IOException {
		target = reader.readRemaining();
	}

	@Override
	public void build(XtWriter writer) throws IOException {
	}

	@Override
	public boolean handle(SmartfoxClient client) throws IOException {
		// Interaction cancel
		Player plr = (Player) client.container;

		// TODO
		return true;
	}

}