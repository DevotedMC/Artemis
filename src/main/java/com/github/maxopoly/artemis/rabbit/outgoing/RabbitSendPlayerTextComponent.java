package com.github.maxopoly.artemis.rabbit.outgoing;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.artemis.rabbit.MCStandardRequest;
import com.github.maxopoly.artemis.rabbit.requests.SendPlayerTextComponent;
import com.google.common.base.Preconditions;
import java.util.UUID;
import net.md_5.bungee.api.chat.TextComponent;
import org.json.JSONObject;

public class RabbitSendPlayerTextComponent extends MCStandardRequest {

    private UUID sender;
    private UUID receiver;
    private TextComponent message;

    public RabbitSendPlayerTextComponent(UUID executor, UUID receiver, TextComponent message) {
        Preconditions.checkNotNull(executor);
        Preconditions.checkNotNull(receiver);
        Preconditions.checkNotNull(message);
        this.sender = executor;
        this.receiver = receiver;
        this.message = message;
    }

    @Override
    protected void enrichJson(JSONObject json) {
        json.put("sender", sender.toString());
        json.put("receiver", receiver.toString());
        json.put("message", message.toLegacyText());
    }

    @Override
    public String getIdentifier() {
        return SendPlayerTextComponent.REQUEST_ID;
    }

    @Override
    public void handleReply(JSONObject reply) {
        boolean success = reply.getBoolean("success");
        if (success) {
            return;
        }
        SendPlayerTextComponent.FailureReason reason = SendPlayerTextComponent.FailureReason.valueOf(reply.getString("reason"));
        switch (reason) {
            case PLAYER_DOES_NOT_EXIST:
                ArtemisPlugin.getInstance().getLogger().info("Tried to send message to " + receiver.toString() + " but they went offline");
                return;
            default:
                break;
        }
    }
}
