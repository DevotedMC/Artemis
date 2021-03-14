package com.github.maxopoly.artemis.rabbit.incoming;

import com.github.maxopoly.zeus.rabbit.incoming.StaticRabbitCommand;
import com.github.maxopoly.zeus.servers.ConnectedServer;
import java.util.UUID;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONObject;

public class SendPlayerTextComponent extends StaticRabbitCommand {
    @Override
    public String getIdentifier() {
        return "art_send_player_text_comp";
    }

    @Override
    public void handleRequest(ConnectedServer sendingServer, JSONObject data) {
        BaseComponent[] message = TextComponent.fromLegacyText(data.getString("message"));
        UUID receiver = UUID.fromString(data.getString("receiver"));
        Player player = Bukkit.getPlayer(receiver);
        if (player == null) {
            return;
        }
        player.sendMessage(message);
    }
}
