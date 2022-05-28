package org.asf.emuferal.networking.chatserver.packets;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.asf.emuferal.dms.DMManager;
import org.asf.emuferal.dms.PrivateChatMessage;
import org.asf.emuferal.networking.chatserver.ChatClient;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class HistoryPacket extends AbstractChatPacket {

	private String convo;

	@Override
	public String id() {
		return "conversations.history";
	}

	@Override
	public AbstractChatPacket instantiate() {
		return new HistoryPacket();
	}

	@Override
	public void parse(JsonObject data) {
		convo = data.get("conversationId").getAsString();
	}

	@Override
	public void build(JsonObject data) {
	}

	@Override
	public boolean handle(ChatClient client) {

		// Send response
		JsonObject res = new JsonObject();

		// Load messages
		DMManager manager = DMManager.getInstance();
		if (client.isInRoom(convo) && client.isRoomPrivate(convo) && manager.dmExists(convo)) {
			JsonArray msgs = new JsonArray();
			for (PrivateChatMessage msg : manager.getDMHistory(convo)) {
				// Build member list
				JsonArray members = new JsonArray();
				for (ChatClient cl : client.getServer().getClients()) {
					if (cl.isInRoom(convo) && cl != client)
						members.add(cl.getPlayer().getAccountID());
				}

				// Build message object
				JsonObject obj = new JsonObject();
				obj.addProperty("body", msg.content);
				obj.addProperty("conversation_id", convo);
				obj.addProperty("conversation_type", "private");
				obj.add("mask", null);
				try {
					obj.addProperty("message_id", "msg-" + UUID.nameUUIDFromBytes(msg.sentAt.getBytes("UTF-8")));
				} catch (UnsupportedEncodingException e) {
				}
				obj.add("participants", members);
				obj.addProperty("sent_at", msg.sentAt);
				obj.addProperty("source", msg.sentAt);
				msgs.add(obj);
			}
			res.add("messages", msgs);
		} else
			res.add("messages", new JsonArray());

		res.addProperty("eventId", "conversations.history");
		res.addProperty("success", true);
		client.sendPacket(res);
		return true;
	}

}